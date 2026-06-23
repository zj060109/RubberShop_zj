package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.PurchaseCreateRequest;
import com.rubber.shop.dto.PurchaseDetailResponse;
import com.rubber.shop.dto.PurchaseItemRequest;
import com.rubber.shop.entity.*;
import com.rubber.shop.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchaseItemService itemService;
    private final ProductService productService;
    private final StockLogService stockLogService;
    private final UserService userService;

    public PurchaseController(PurchaseService ps, PurchaseItemService is,
            ProductService prs, StockLogService sls, UserService us) {
        this.purchaseService = ps; this.itemService = is;
        this.productService = prs; this.stockLogService = sls; this.userService = us;
    }

    @PostMapping
    @Transactional
    public Result<?> create(@RequestBody @Valid PurchaseCreateRequest req) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        User factory = userService.getById(req.getFactoryId());
        if (factory == null || !"factory".equals(factory.getRole_zj()))
            throw new BusinessException("厂家不存在");

        Purchase p = new Purchase();
        p.setOrder_no_zj("PUR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ThreadLocalRandom.current().nextInt(100, 999));
        p.setFactory_id_zj(req.getFactoryId());
        p.setStatus_zj("pending");
        p.setCreated_at_zj(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<PurchaseItem> items = new ArrayList<>();
        for (PurchaseItemRequest ir : req.getItems()) {
            if (ir.getQuantity() <= 0) throw new BusinessException("数量必须大于0");
            if (ir.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("单价必须大于0");
            BigDecimal subtotal = ir.getUnitPrice().multiply(new BigDecimal(ir.getQuantity()));
            total = total.add(subtotal);
            PurchaseItem pi = new PurchaseItem();
            pi.setProduct_id_zj(ir.getProductId());
            pi.setProduct_name_zj(ir.getProductName());
            pi.setSpec_zj(ir.getSpec());
            pi.setQuantity_zj(ir.getQuantity());
            pi.setUnit_price_zj(ir.getUnitPrice());
            pi.setSubtotal_zj(subtotal);
            items.add(pi);
        }
        p.setTotal_amount_zj(total);
        purchaseService.save(p);

        for (PurchaseItem pi : items) {
            pi.setPurchase_id_zj(p.getId_zj());
            itemService.save(pi);
        }
        return Result.success("采购单创建成功", p);
    }

    @GetMapping
    public Result<Page<Purchase>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<Purchase> w = new LambdaQueryWrapper<>();
        Long userId = getCurrentUserId();
        boolean isMerch = isMerchant();
        boolean isFact = isFactory();
        if (!isMerch && isFact) w.eq(Purchase::getFactory_id_zj, userId);
        else if (!isMerch) throw new BusinessException("权限不足");
        w.orderByDesc(Purchase::getCreated_at_zj);
        return Result.success(purchaseService.page(new Page<>(page, pageSize), w));
    }

    @GetMapping("/{id}")
    public Result<PurchaseDetailResponse> detail(@PathVariable Long id) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        Long userId = getCurrentUserId();
        if (!isMerchant() && !p.getFactory_id_zj().equals(userId))
            throw new BusinessException("无权查看");

        LambdaQueryWrapper<PurchaseItem> iw = new LambdaQueryWrapper<>();
        iw.eq(PurchaseItem::getPurchase_id_zj, id);
        List<PurchaseItem> items = itemService.list(iw);
        return Result.success(new PurchaseDetailResponse(p, items));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody @Valid PurchaseCreateRequest req) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"pending".equals(p.getStatus_zj())) throw new BusinessException("仅pending状态可修改");

        LambdaQueryWrapper<PurchaseItem> iw = new LambdaQueryWrapper<>();
        iw.eq(PurchaseItem::getPurchase_id_zj, id);
        itemService.remove(iw);

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItemRequest ir : req.getItems()) {
            BigDecimal subtotal = ir.getUnitPrice().multiply(new BigDecimal(ir.getQuantity()));
            total = total.add(subtotal);
            PurchaseItem pi = new PurchaseItem();
            pi.setPurchase_id_zj(id);
            pi.setProduct_id_zj(ir.getProductId());
            pi.setProduct_name_zj(ir.getProductName());
            pi.setSpec_zj(ir.getSpec());
            pi.setQuantity_zj(ir.getQuantity());
            pi.setUnit_price_zj(ir.getUnitPrice());
            pi.setSubtotal_zj(subtotal);
            itemService.save(pi);
        }
        p.setTotal_amount_zj(total);
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("修改成功", null);
    }

    @PutMapping("/{id}/status")
    @Transactional
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");

        String old = p.getStatus_zj();
        Long userId = getCurrentUserId();
        boolean valid = false;

        if ("confirmed".equals(status) && "pending".equals(old) && (isMerchant() || p.getFactory_id_zj().equals(userId))) valid = true;
        if ("shipped".equals(status) && "confirmed".equals(old) && (isMerchant() || p.getFactory_id_zj().equals(userId))) valid = true;
        if ("cancelled".equals(status) && ("pending".equals(old) || "confirmed".equals(old)) && isMerchant()) valid = true;

        if (!valid) throw new BusinessException("不允许此操作");

        p.setStatus_zj(status);
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("操作成功", null);
    }

    @PutMapping("/{id}/logistics")
    public Result<?> logistics(@PathVariable Long id, @RequestParam String expressCompany, @RequestParam String trackingNo) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!p.getFactory_id_zj().equals(getCurrentUserId())) throw new BusinessException("无权操作");

        p.setExpress_company_zj(expressCompany);
        p.setTracking_no_zj(trackingNo);
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("物流信息填写成功", null);
    }

    @PutMapping("/{id}/received")
    @Transactional
    public Result<?> received(@PathVariable Long id) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"shipped".equals(p.getStatus_zj())) throw new BusinessException("当前状态不允许收货");

        LambdaQueryWrapper<PurchaseItem> iw = new LambdaQueryWrapper<>();
        iw.eq(PurchaseItem::getPurchase_id_zj, id);
        List<PurchaseItem> items = itemService.list(iw);
        for (PurchaseItem pi : items) {
            if (pi.getProduct_id_zj() == null) throw new BusinessException("明细[" + pi.getProduct_name_zj() + "]未关联商品，无法入库");
            LambdaUpdateWrapper<Product> pw = new LambdaUpdateWrapper<>();
            pw.eq(Product::getId_zj, pi.getProduct_id_zj())
              .setSql("stock_zj = stock_zj + {0}", pi.getQuantity_zj());
            productService.update(pw);

            Product updated = productService.getById(pi.getProduct_id_zj());
            StockLog sl = new StockLog();
            sl.setProduct_id_zj(pi.getProduct_id_zj());
            sl.setChange_quantity_zj(pi.getQuantity_zj());
            sl.setCurrent_stock_zj(updated.getStock_zj());
            sl.setType_zj("purchase_in");
            sl.setReference_id_zj(id);
            sl.setRemark_zj("采购入库");
            sl.setCreated_at_zj(LocalDateTime.now());
            stockLogService.save(sl);
        }

        p.setStatus_zj("received");
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("收货入库成功", null);
    }

    private boolean isMerchant() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated()) return false;
        for (GrantedAuthority g : a.getAuthorities()) {
            if ("ROLE_MERCHANT".equals(g.getAuthority())) return true;
        }
        return false;
    }

    private boolean isFactory() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated()) return false;
        for (GrantedAuthority g : a.getAuthorities()) {
            if ("ROLE_FACTORY".equals(g.getAuthority())) return true;
        }
        return false;
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
