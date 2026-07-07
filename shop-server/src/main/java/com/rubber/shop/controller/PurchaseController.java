package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.PurchaseCreateRequest;
import com.rubber.shop.dto.PurchaseDetailResponse;
import com.rubber.shop.dto.PurchaseItemRequest;
import com.rubber.shop.dto.PurchaseQuoteRequest;
import com.rubber.shop.dto.PurchaseQuoteItem;
import com.rubber.shop.dto.UpdatePurchaseRequest;
import com.rubber.shop.entity.*;
import com.rubber.shop.service.*;
import jakarta.validation.Valid;
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

    // 1. 商户创建采购单（不含价格）
    @PostMapping
    @Transactional
    public Result<?> create(@RequestBody @Valid PurchaseCreateRequest req) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        User factory = userService.getById(req.getFactoryId());
        if (factory == null || !"factory".equals(factory.getRole_zj()))
            throw new BusinessException("厂家不存在");

        if (req.getItems() == null || req.getItems().isEmpty())
            throw new BusinessException("请至少添加一个采购明细");

        Purchase p = new Purchase();
        p.setOrder_no_zj("PUR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ThreadLocalRandom.current().nextInt(100, 999));
        p.setFactory_id_zj(req.getFactoryId());
        p.setStatus_zj("pending");
        p.setTotal_amount_zj(BigDecimal.ZERO);
        if (req.getExpectedDeliveryDate() != null) {
            try {
                p.setExpected_delivery_date_zj(java.time.LocalDate.parse(req.getExpectedDeliveryDate()));
            } catch (Exception ignored) {}
        }
        p.setCreated_at_zj(LocalDateTime.now());
        purchaseService.save(p);

        for (PurchaseItemRequest ir : req.getItems()) {
            if (ir.getProductName() == null || ir.getProductName().isEmpty())
                throw new BusinessException("商品名称不能为空");
            if (ir.getQuantity() == null || ir.getQuantity() <= 0)
                throw new BusinessException("数量必须大于0");
            PurchaseItem pi = new PurchaseItem();
            pi.setPurchase_id_zj(p.getId_zj());
            pi.setProduct_id_zj(ir.getProductId());
            pi.setProduct_name_zj(ir.getProductName());
            pi.setSpec_zj(ir.getSpec());
            pi.setQuantity_zj(ir.getQuantity());
            pi.setUnit_price_zj(BigDecimal.ZERO);
            pi.setSubtotal_zj(BigDecimal.ZERO);
            itemService.save(pi);
        }
        return Result.success("采购单已发送给厂家", p);
    }

    // 2. 列表（商户看全部，厂家看自己的）
    @GetMapping
    public Result<Page<Purchase>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<Purchase> w = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) w.eq(Purchase::getStatus_zj, status);
        boolean isMerch = isMerchant();
        boolean isFact = isFactory();
        if (!isMerch && isFact) w.eq(Purchase::getFactory_id_zj, getCurrentUserId());
        else if (!isMerch) throw new BusinessException("权限不足");
        w.orderByDesc(Purchase::getCreated_at_zj);
        return Result.success(purchaseService.page(new Page<>(page, Math.min(pageSize, 100)), w));
    }

    // 3. 详情
    @GetMapping("/{id}")
    public Result<PurchaseDetailResponse> detail(@PathVariable Long id) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        Long userId = getCurrentUserId();
        if (!isMerchant() && !p.getFactory_id_zj().equals(userId))
            throw new BusinessException("无权查看");

        List<PurchaseItem> items = itemService.list(
            new LambdaQueryWrapper<PurchaseItem>().eq(PurchaseItem::getPurchase_id_zj, id));
        return Result.success(new PurchaseDetailResponse(p, items));
    }

    // 4. 商户修改（仅pending状态，不含价格）
    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody @Valid UpdatePurchaseRequest req) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"pending".equals(p.getStatus_zj())) throw new BusinessException("仅待报价状态可修改");

        itemService.remove(new LambdaQueryWrapper<PurchaseItem>().eq(PurchaseItem::getPurchase_id_zj, id));

        for (PurchaseItemRequest ir : req.getItems()) {
            if (ir.getProductName() == null || ir.getProductName().isEmpty())
                throw new BusinessException("商品名称不能为空");
            if (ir.getQuantity() == null || ir.getQuantity() <= 0)
                throw new BusinessException("数量必须大于0");
            PurchaseItem pi = new PurchaseItem();
            pi.setPurchase_id_zj(id);
            pi.setProduct_id_zj(ir.getProductId());
            pi.setProduct_name_zj(ir.getProductName());
            pi.setSpec_zj(ir.getSpec());
            pi.setQuantity_zj(ir.getQuantity());
            pi.setUnit_price_zj(BigDecimal.ZERO);
            pi.setSubtotal_zj(BigDecimal.ZERO);
            itemService.save(pi);
        }
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("修改成功", null);
    }

    // 5. 厂家报价（设置每条明细的单价）
    @PutMapping("/{id}/quote")
    @Transactional
    public Result<?> quote(@PathVariable Long id, @RequestBody PurchaseQuoteRequest req) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"pending".equals(p.getStatus_zj())) throw new BusinessException("当前状态不允许报价");
        if (!p.getFactory_id_zj().equals(getCurrentUserId())) throw new BusinessException("无权操作");

        if (req.getItems() == null || req.getItems().isEmpty()) throw new BusinessException("请至少填写一项报价");

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseQuoteItem item : req.getItems()) {
            if (item.getItemId() == null) throw new BusinessException("明细ID不能为空");
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0)
                throw new BusinessException("单价必须大于0");

            PurchaseItem pi = itemService.getById(item.getItemId());
            if (pi == null || !pi.getPurchase_id_zj().equals(id))
                throw new BusinessException("明细不存在或不属于此采购单");

            BigDecimal subtotal = item.getUnitPrice().multiply(new BigDecimal(pi.getQuantity_zj()));
            pi.setUnit_price_zj(item.getUnitPrice());
            pi.setSubtotal_zj(subtotal);
            itemService.updateById(pi);
            total = total.add(subtotal);
        }

        p.setTotal_amount_zj(total);
        p.setStatus_zj("quoted");
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("报价成功", null);
    }

    // 6. 商户确认付款
    @PutMapping("/{id}/pay")
    @Transactional
    public Result<?> pay(@PathVariable Long id) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"quoted".equals(p.getStatus_zj())) throw new BusinessException("当前状态不允许付款");

        p.setStatus_zj("paid");
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("已确认付款，等待厂家发货", null);
    }

    // 7. 厂家发货
    @PutMapping("/{id}/logistics")
    @Transactional
    public Result<?> logistics(@PathVariable Long id,
            @RequestParam String expressCompany, @RequestParam String trackingNo) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"paid".equals(p.getStatus_zj())) throw new BusinessException("当前状态不允许发货");
        if (!p.getFactory_id_zj().equals(getCurrentUserId())) throw new BusinessException("无权操作");

        p.setExpress_company_zj(expressCompany);
        p.setTracking_no_zj(trackingNo);
        p.setStatus_zj("shipped");
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("发货成功", null);
    }

    // 8. 商户收货（自动入库）
    @PutMapping("/{id}/received")
    @Transactional
    public Result<?> received(@PathVariable Long id) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        if (!"shipped".equals(p.getStatus_zj())) throw new BusinessException("当前状态不允许收货");

        List<PurchaseItem> items = itemService.list(
            new LambdaQueryWrapper<PurchaseItem>().eq(PurchaseItem::getPurchase_id_zj, id));
        for (PurchaseItem pi : items) {
            if (pi.getProduct_id_zj() != null && pi.getProduct_id_zj() > 0) {
                Product product = productService.getById(pi.getProduct_id_zj());
                if (product != null) {
                    product.setStock_zj(product.getStock_zj() + pi.getQuantity_zj());
                    product.setUpdated_at_zj(LocalDateTime.now());
                    productService.updateById(product);

                    StockLog sl = new StockLog();
                    sl.setProduct_id_zj(pi.getProduct_id_zj());
                    sl.setChange_quantity_zj(pi.getQuantity_zj());
                    sl.setCurrent_stock_zj(product.getStock_zj());
                    sl.setType_zj("purchase_in");
                    sl.setReference_id_zj(id);
                    sl.setRemark_zj("采购入库");
                    sl.setCreated_at_zj(LocalDateTime.now());
                    stockLogService.save(sl);
                }
            }
        }

        p.setStatus_zj("received");
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("收货入库成功", null);
    }

    // 9. 取消（pending 商户可取消，quoted 厂家可取消）
    @PutMapping("/{id}/cancel")
    @Transactional
    public Result<?> cancel(@PathVariable Long id) {
        Purchase p = purchaseService.getById(id);
        if (p == null) throw new BusinessException("采购单不存在");
        String status = p.getStatus_zj();
        Long userId = getCurrentUserId();

        if ("pending".equals(status) && isMerchant()) {
            // ok
        } else if ("quoted".equals(status) && p.getFactory_id_zj().equals(userId)) {
            // ok
        } else {
            throw new BusinessException("无权取消此采购单");
        }

        p.setStatus_zj("cancelled");
        p.setUpdated_at_zj(LocalDateTime.now());
        purchaseService.updateById(p);
        return Result.success("已取消", null);
    }

    private boolean isMerchant() { return AuthUtils.isMerchant(); }
    private boolean isFactory() { return AuthUtils.isFactory(); }
    private Long getCurrentUserId() { return AuthUtils.getCurrentUserId(); }
}
