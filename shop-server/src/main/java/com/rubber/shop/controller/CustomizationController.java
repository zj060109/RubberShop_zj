package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.*;
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/customizations")
public class CustomizationController {

    private final CustomizationService customizationService;
    private final CustomizationItemService itemService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderStatusLogService orderStatusLogService;
    private final UserService userService;
    private final BalanceLogService balanceLogService;
    private final ReceivableService receivableService;
    private final ObjectMapper objectMapper;

    public CustomizationController(CustomizationService cs, CustomizationItemService is,
            ProductService ps, OrderService os, OrderItemService ois, OrderStatusLogService osls,
            UserService us, BalanceLogService bls, ReceivableService rs, ObjectMapper om) {
        this.customizationService = cs; this.itemService = is;
        this.productService = ps; this.orderService = os; this.orderItemService = ois;
        this.orderStatusLogService = osls; this.userService = us;
        this.balanceLogService = bls; this.receivableService = rs; this.objectMapper = om;
    }

    @PostMapping
    public Result<?> create(@RequestBody CustomizationCreateRequest req) {
        Customization c = new Customization();
        c.setUser_id_zj(getCurrentUserId());
        c.setStatus_zj("pending_quote");
        c.setDescription_zj(req.getDescription());
        if (req.getReferenceImages() != null && !req.getReferenceImages().isEmpty()) {
            try { c.setReference_images_zj(objectMapper.writeValueAsString(req.getReferenceImages())); }
            catch (Exception e) { throw new BusinessException("图片数据格式错误"); }
        }
        c.setCreated_at_zj(LocalDateTime.now());
        customizationService.save(c);
        return Result.success("定制提交成功", null);
    }

    @GetMapping
    public Result<Page<Customization>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<Customization> w = new LambdaQueryWrapper<>();
        if (!isAdmin()) w.eq(Customization::getUser_id_zj, getCurrentUserId());
        w.orderByDesc(Customization::getCreated_at_zj);
        return Result.success(customizationService.page(new Page<>(page, pageSize), w));
    }

    @GetMapping("/{id}")
    public Result<Customization> detail(@PathVariable Long id) {
        Customization c = customizationService.getById(id);
        if (c == null) throw new BusinessException("定制不存在");
        if (!isAdmin() && !c.getUser_id_zj().equals(getCurrentUserId()))
            throw new BusinessException("无权查看");
        return Result.success(c);
    }

    @PutMapping("/{id}/quote")
    @Transactional
    public Result<?> quote(@PathVariable Long id, @RequestBody @Valid QuoteRequest req) {
        if (!isAdmin()) throw new BusinessException("权限不足");
        Customization c = customizationService.getById(id);
        if (c == null) throw new BusinessException("定制不存在");
        if (!"pending_quote".equals(c.getStatus_zj())) throw new BusinessException("当前状态不允许报价");

        LambdaQueryWrapper<CustomizationItem> iw = new LambdaQueryWrapper<>();
        iw.eq(CustomizationItem::getCustomization_id_zj, id);
        itemService.remove(iw);

        BigDecimal total = BigDecimal.ZERO;
        for (QuoteItemRequest qi : req.getItems()) {
            CustomizationItem ci = new CustomizationItem();
            ci.setCustomization_id_zj(id);
            ci.setProduct_spec_zj(qi.getSpec());
            ci.setQuantity_zj(qi.getQuantity());
            ci.setUnit_price_zj(qi.getUnitPrice());
            itemService.save(ci);
            total = total.add(qi.getUnitPrice().multiply(new BigDecimal(qi.getQuantity())));
        }

        c.setTotal_quoted_price_zj(total);
        c.setStatus_zj("quoted");
        c.setUpdated_at_zj(LocalDateTime.now());
        customizationService.updateById(c);
        return Result.success("报价成功", null);
    }

    @PutMapping("/{id}/confirm")
    @Transactional
    public Result<?> confirm(@PathVariable Long id, @RequestBody @Valid ConfirmCustomizationRequest req) {
        Long userId = getCurrentUserId();
        Customization c = customizationService.getById(id);
        if (c == null) throw new BusinessException("定制不存在");
        if (!c.getUser_id_zj().equals(userId)) throw new BusinessException("无权操作");
        if (!"quoted".equals(c.getStatus_zj())) throw new BusinessException("当前状态不允许确认");

        String pm = req.getPaymentMethod();
        if (!"balance".equals(pm) && !"credit".equals(pm)) throw new BusinessException("支付方式无效");

        List<CustomizationItem> items = itemService.list(
                new LambdaQueryWrapper<CustomizationItem>().eq(CustomizationItem::getCustomization_id_zj, id));

        Product p = new Product();
        p.setCategory_id_zj(0L);
        p.setName_zj("定制商品_" + id);
        p.setStatus_zj("off");
        p.setIs_customized_zj(1);
        p.setPrice_zj(c.getTotal_quoted_price_zj());
        p.setStock_zj(0);
        p.setWarning_stock_zj(0);
        p.setCreated_at_zj(LocalDateTime.now());
        productService.save(p);

        Order order = new Order();
        order.setOrder_no_zj("CUS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ThreadLocalRandom.current().nextInt(100, 999));
        order.setUser_id_zj(userId);
        order.setTotal_amount_zj(c.getTotal_quoted_price_zj());
        order.setActual_amount_zj(c.getTotal_quoted_price_zj());
        order.setPayment_method_zj(pm);
        order.setStatus_zj("paid");
        order.setPaid_at_zj(LocalDateTime.now());
        order.setCreated_at_zj(LocalDateTime.now());
        setAddress(order);
        orderService.save(order);

        for (CustomizationItem ci : items) {
            OrderItem oi = new OrderItem();
            oi.setOrder_id_zj(order.getId_zj());
            oi.setProduct_id_zj(p.getId_zj());
            oi.setProduct_name_zj(p.getName_zj());
            oi.setPrice_zj(ci.getUnit_price_zj());
            oi.setQuantity_zj(ci.getQuantity_zj());
            oi.setSubtotal_zj(ci.getUnit_price_zj().multiply(new BigDecimal(ci.getQuantity_zj())));
            orderItemService.save(oi);
        }

        User user = userService.getById(userId);
        if ("balance".equals(pm)) {
            if (user.getBalance_zj().compareTo(c.getTotal_quoted_price_zj()) < 0)
                throw new BusinessException("余额不足");
            LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
            uw.eq(User::getId_zj, userId)
              .ge(User::getBalance_zj, c.getTotal_quoted_price_zj())
              .setSql("balance_zj = balance_zj - {0}", c.getTotal_quoted_price_zj());
            if (!userService.update(uw)) throw new BusinessException("余额不足");

            User refreshed = userService.getById(userId);
            BalanceLog bl = new BalanceLog();
            bl.setUser_id_zj(userId);
            bl.setChange_amount_zj(c.getTotal_quoted_price_zj().negate());
            bl.setCurrent_balance_zj(refreshed.getBalance_zj());
            bl.setType_zj("consume");
            bl.setReference_id_zj(order.getId_zj());
            bl.setRemark_zj("定制订单支付");
            bl.setCreated_at_zj(LocalDateTime.now());
            balanceLogService.save(bl);
        } else {
            BigDecimal unpaidTotal = BigDecimal.ZERO;
            LambdaQueryWrapper<Receivable> rw2 = new LambdaQueryWrapper<>();
            rw2.eq(Receivable::getUser_id_zj, userId)
               .in(Receivable::getStatus_zj, "unpaid", "partially_paid");
            for (Receivable r : receivableService.list(rw2)) {
                unpaidTotal = unpaidTotal.add(r.getAmount_owed_zj().subtract(r.getAmount_paid_zj()));
            }
            BigDecimal availableCredit = user.getCredit_limit_zj().subtract(unpaidTotal);
            if (availableCredit.compareTo(c.getTotal_quoted_price_zj()) < 0)
                throw new BusinessException("赊账额度不足，可用额度：" + availableCredit);
            Receivable rec = new Receivable();
            rec.setOrder_id_zj(order.getId_zj());
            rec.setUser_id_zj(userId);
            rec.setAmount_owed_zj(c.getTotal_quoted_price_zj());
            rec.setAmount_paid_zj(BigDecimal.ZERO);
            rec.setStatus_zj("unpaid");
            rec.setCreated_at_zj(LocalDateTime.now());
            receivableService.save(rec);
        }

        c.setOrder_id_zj(order.getId_zj());
        c.setStatus_zj("confirmed");
        c.setUpdated_at_zj(LocalDateTime.now());
        customizationService.updateById(c);

        OrderStatusLog sl = new OrderStatusLog();
        sl.setOrder_id_zj(order.getId_zj());
        sl.setTo_status_zj("paid");
        sl.setOperator_id_zj(userId);
        sl.setRemark_zj("定制确认生成订单");
        sl.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(sl);

        return Result.success("确认成功", order);
    }

    @PostMapping("/{id}/convert-to-product")
    public Result<?> convertToProduct(@PathVariable Long id, @RequestParam Long categoryId) {
        if (!isAdmin()) throw new BusinessException("权限不足");
        Customization c = customizationService.getById(id);
        if (c == null) throw new BusinessException("定制不存在");
        if (!"confirmed".equals(c.getStatus_zj()) && !"converted".equals(c.getStatus_zj()))
            throw new BusinessException("当前状态不允许转换");

        if (c.getOrder_id_zj() == null) throw new BusinessException("未关联订单");
        LambdaQueryWrapper<OrderItem> iw = new LambdaQueryWrapper<>();
        iw.eq(OrderItem::getOrder_id_zj, c.getOrder_id_zj());
        OrderItem oi = orderItemService.getOne(iw);
        if (oi == null) throw new BusinessException("未找到关联商品");

        Product p = productService.getById(oi.getProduct_id_zj());
        if (p == null) throw new BusinessException("商品不存在");
        p.setCategory_id_zj(categoryId);
        p.setStatus_zj("on");
        productService.updateById(p);

        c.setStatus_zj("converted");
        c.setUpdated_at_zj(LocalDateTime.now());
        customizationService.updateById(c);
        return Result.success("转换成功", null);
    }

    private void setAddress(Order order) {
        User user = userService.getById(getCurrentUserId());
        String name = user.getDefault_receiver_name_zj();
        String phone = user.getDefault_receiver_phone_zj();
        String province = user.getDefault_province_zj();
        String city = user.getDefault_city_zj();
        String district = user.getDefault_district_zj();
        String detail = user.getDefault_detail_address_zj();
        if (name == null || name.isEmpty() || phone == null || phone.isEmpty()
                || province == null || province.isEmpty() || city == null || city.isEmpty()
                || district == null || district.isEmpty() || detail == null || detail.isEmpty()) {
            throw new BusinessException("请先完善收货地址（省市区+详细地址）");
        }
        order.setReceiver_name_zj(name);
        order.setReceiver_phone_zj(phone);
        order.setProvince_zj(province);
        order.setCity_zj(city);
        order.setDistrict_zj(district);
        order.setDetail_address_zj(detail);
    }

    private boolean isAdmin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated()) return false;
        for (GrantedAuthority g : a.getAuthorities()) {
            if ("ROLE_MERCHANT".equals(g.getAuthority())) return true;
        }
        return false;
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
