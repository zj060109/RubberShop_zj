package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.OrderCreateRequest;
import com.rubber.shop.dto.OrderDetailResponse;
import com.rubber.shop.dto.OrderItemRequest;
import com.rubber.shop.entity.*;
import com.rubber.shop.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderStatusLogService orderStatusLogService;
    private final ProductService productService;
    private final UserService userService;
    private final BalanceLogService balanceLogService;
    private final StockLogService stockLogService;
    private final ReceivableService receivableService;
    private final IdentityVerificationService identityVerificationService;
    private final ObjectMapper objectMapper;

    public OrderController(OrderService orderService, OrderItemService orderItemService,
            OrderStatusLogService orderStatusLogService, ProductService productService,
            UserService userService, BalanceLogService balanceLogService,
            StockLogService stockLogService, ReceivableService receivableService,
            IdentityVerificationService identityVerificationService,
            ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.orderStatusLogService = orderStatusLogService;
        this.productService = productService;
        this.userService = userService;
        this.balanceLogService = balanceLogService;
        this.stockLogService = stockLogService;
        this.receivableService = receivableService;
        this.identityVerificationService = identityVerificationService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Transactional
    public Result<Order> create(@RequestBody @Valid OrderCreateRequest req) {
        if (req.getNeedInstallation() == null) req.setNeedInstallation(0);
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        String paymentMethod = req.getPaymentMethod();
        if (!"balance".equals(paymentMethod) && !"credit".equals(paymentMethod)) {
            throw new BusinessException("支付方式无效，仅支持 balance 或 credit");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<Long> productIds = req.getItems().stream().map(OrderItemRequest::getProductId).distinct().toList();
        List<Product> products = productService.listByIds(productIds);
        java.util.Map<Long, Product> productMap = new java.util.HashMap<>();
        for (Product p : products) productMap.put(p.getId_zj(), p);
        
        for (OrderItemRequest itemReq : req.getItems()) {
            Product p = productMap.get(itemReq.getProductId());
            if (p == null) throw new BusinessException("商品(ID:" + itemReq.getProductId() + ")不存在");
            if (!"on".equals(p.getStatus_zj())) throw new BusinessException("商品「" + p.getName_zj() + "」已下架");
            if (p.getStock_zj() < itemReq.getQuantity()) throw new BusinessException("商品[" + p.getName_zj() + "]库存不足");

            BigDecimal subtotal = p.getPrice_zj().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem oi = new OrderItem();
            oi.setProduct_id_zj(p.getId_zj());
            oi.setProduct_name_zj(p.getName_zj());
            oi.setProduct_image_zj(extractFirstImage(p.getImages_zj()));
            oi.setPrice_zj(p.getPrice_zj());
            oi.setQuantity_zj(itemReq.getQuantity());
            oi.setSubtotal_zj(subtotal);
            orderItems.add(oi);
        }

        Order order = new Order();
        order.setOrder_no_zj(generateOrderNo());
        order.setUser_id_zj(userId);
        order.setTotal_amount_zj(totalAmount);
        order.setActual_amount_zj(totalAmount);
        order.setPayment_method_zj(paymentMethod);
        order.setNeed_installation_zj(req.getNeedInstallation());
        order.setStatus_zj("paid");
        order.setPaid_at_zj(LocalDateTime.now());
        order.setCreated_at_zj(LocalDateTime.now());
        setAddress(order, user, req);
        orderService.save(order);

        for (OrderItemRequest itemReq : req.getItems()) {
            LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Product::getId_zj, itemReq.getProductId())
                   .ge(Product::getStock_zj, itemReq.getQuantity())
                    .setSql("stock_zj = stock_zj - {0}", itemReq.getQuantity());
            boolean stockSuccess = productService.update(wrapper);
            if (!stockSuccess) throw new BusinessException("商品[" + itemReq.getProductId() + "]库存不足，下单失败");

            Product updated = productService.getById(itemReq.getProductId());
            StockLog sl = new StockLog();
            sl.setProduct_id_zj(itemReq.getProductId());
            sl.setChange_quantity_zj(-itemReq.getQuantity());
            sl.setCurrent_stock_zj(updated.getStock_zj());
            sl.setType_zj("sale_out");
            sl.setReference_id_zj(order.getId_zj());
            sl.setRemark_zj("订单销售出库");
            sl.setCreated_at_zj(LocalDateTime.now());
            stockLogService.save(sl);
        }

        for (OrderItem oi : orderItems) {
            oi.setOrder_id_zj(order.getId_zj());
            orderItemService.save(oi);
        }

        if ("balance".equals(paymentMethod)) {
            if (user.getBalance_zj().compareTo(totalAmount) < 0) {
                throw new BusinessException("余额不足");
            }
            LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
            uw.eq(User::getId_zj, userId)
              .ge(User::getBalance_zj, totalAmount)
              .setSql("balance_zj = balance_zj - {0}", totalAmount);
            if (!userService.update(uw)) throw new BusinessException("余额不足");

            User refreshed = userService.getById(userId);
            BalanceLog bl = new BalanceLog();
            bl.setUser_id_zj(userId);
            bl.setChange_amount_zj(totalAmount.negate());
            bl.setCurrent_balance_zj(refreshed.getBalance_zj());
            bl.setType_zj("consume");
            bl.setReference_id_zj(order.getId_zj());
            bl.setRemark_zj("订单支付");
            bl.setCreated_at_zj(LocalDateTime.now());
            balanceLogService.save(bl);
        } else {
            if (user.getPoints_zj() == null || user.getPoints_zj() < 10) {
                throw new BusinessException("积分不足（需满10积分），无法使用赊账");
            }
            if (user.getCredit_limit_zj() == null || user.getCredit_limit_zj().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("赊账功能未解锁，请先达到10积分");
            }
            LambdaQueryWrapper<IdentityVerification> ivw = new LambdaQueryWrapper<>();
            ivw.eq(IdentityVerification::getUser_id_zj, userId)
               .eq(IdentityVerification::getStatus_zj, 1);
            if (identityVerificationService.count(ivw) == 0) {
                throw new BusinessException("未完成实名认证，无法使用赊账");
            }
            BigDecimal unpaidTotal = getUnpaidReceivableTotal(userId);
            BigDecimal availableCredit = user.getCredit_limit_zj().subtract(unpaidTotal);
            if (availableCredit.compareTo(totalAmount) < 0) {
                throw new BusinessException("赊账额度不足，可用额度：" + availableCredit);
            }
            Receivable r = new Receivable();
            r.setOrder_id_zj(order.getId_zj());
            r.setUser_id_zj(userId);
            r.setAmount_owed_zj(totalAmount);
            r.setAmount_paid_zj(BigDecimal.ZERO);
            r.setStatus_zj("unpaid");
            r.setCreated_at_zj(LocalDateTime.now());
            receivableService.save(r);
        }

        if ("customer".equals(user.getRole_zj())) {
            LambdaUpdateWrapper<User> pointsWrapper = new LambdaUpdateWrapper<>();
            pointsWrapper.eq(User::getId_zj, userId)
                .setSql("points_zj = points_zj + 1");
            userService.update(pointsWrapper);

            User afterPoints = userService.getById(userId);
            if (afterPoints.getPoints_zj() != null && afterPoints.getPoints_zj() >= 10
                    && (afterPoints.getCredit_limit_zj() == null || afterPoints.getCredit_limit_zj().compareTo(BigDecimal.ZERO) <= 0)) {
                LambdaUpdateWrapper<User> creditWrapper = new LambdaUpdateWrapper<>();
                creditWrapper.eq(User::getId_zj, userId)
                    .set(User::getCredit_limit_zj, new BigDecimal("5000.00"));
                userService.update(creditWrapper);
            }
        }

        OrderStatusLog sl = new OrderStatusLog();
        sl.setOrder_id_zj(order.getId_zj());
        sl.setTo_status_zj("paid");
        sl.setOperator_id_zj(userId);
        sl.setRemark_zj("顾客下单");
        sl.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(sl);

        return Result.success("下单成功", order);
    }

    @GetMapping
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        Long userId = getCurrentUserId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (!isMerchant()) {
            wrapper.eq(Order::getUser_id_zj, userId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus_zj, status);
        }
        wrapper.orderByDesc(Order::getCreated_at_zj);
        return Result.success(orderService.page(new Page<>(page, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailResponse> detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        Long userId = getCurrentUserId();
        if (!isMerchant() && !order.getUser_id_zj().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrder_id_zj, id);
        List<OrderItem> items = orderItemService.list(itemWrapper);

        LambdaQueryWrapper<OrderStatusLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(OrderStatusLog::getOrder_id_zj, id).orderByAsc(OrderStatusLog::getCreated_at_zj);
        List<OrderStatusLog> logs = orderStatusLogService.list(logWrapper);

        return Result.success(new OrderDetailResponse(order, items, logs));
    }

    @PutMapping("/{id}/status")
    @Transactional
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        String oldStatus = order.getStatus_zj();
        boolean valid = false;

        if ("accepted".equals(status) && "paid".equals(oldStatus)) valid = true;

        if (!valid) throw new BusinessException("当前状态不允许此操作，请使用 /ship 端点进行发货操作");

        order.setStatus_zj(status);
        order.setUpdated_at_zj(LocalDateTime.now());
        orderService.updateById(order);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrder_id_zj(id);
        log.setFrom_status_zj(oldStatus);
        log.setTo_status_zj(status);
        log.setOperator_id_zj(getCurrentUserId());
        log.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(log);

        return Result.success("操作成功", null);
    }

    @PutMapping("/{id}/ship")
    @Transactional
    public Result<?> ship(@PathVariable Long id, @RequestParam String expressCompany, @RequestParam String trackingNo) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        String oldStatus = order.getStatus_zj();
        if (!"paid".equals(oldStatus) && !"accepted".equals(oldStatus) && !"installed".equals(oldStatus)) {
            throw new BusinessException("当前状态不允许发货");
        }

        order.setStatus_zj("shipped");
        order.setExpress_company_zj(expressCompany);
        order.setTracking_no_zj(trackingNo);
        order.setShipped_at_zj(LocalDateTime.now());
        order.setUpdated_at_zj(LocalDateTime.now());
        orderService.updateById(order);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrder_id_zj(id);
        log.setFrom_status_zj(oldStatus);
        log.setTo_status_zj("shipped");
        log.setOperator_id_zj(getCurrentUserId());
        log.setRemark_zj("物流：" + expressCompany + " " + trackingNo);
        log.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(log);

        return Result.success("发货成功", null);
    }

    @PutMapping("/{id}/receive")
    @Transactional
    public Result<?> receive(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        Long userId = getCurrentUserId();
        if (!isMerchant() && !order.getUser_id_zj().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        if (!"shipped".equals(order.getStatus_zj())) {
            throw new BusinessException("当前状态不允许确认收货");
        }

        String oldStatus = order.getStatus_zj();
        order.setStatus_zj("completed");
        order.setFinished_at_zj(LocalDateTime.now());
        order.setUpdated_at_zj(LocalDateTime.now());
        orderService.updateById(order);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrder_id_zj(id);
        log.setFrom_status_zj(oldStatus);
        log.setTo_status_zj("completed");
        log.setOperator_id_zj(getCurrentUserId());
        log.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(log);

        return Result.success("确认收货成功", null);
    }

    @PostMapping("/{id}/cancel")
    @Transactional
    public Result<?> cancel(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        String status = order.getStatus_zj();
        if (!"paid".equals(status) && !"accepted".equals(status) && !"shipped_to_merchant".equals(status)) {
            throw new BusinessException("当前订单状态不允许取消");
        }

        Long userId = getCurrentUserId();
        if (!isMerchant() && !order.getUser_id_zj().equals(userId)) {
            throw new BusinessException("无权取消该订单");
        }

        LambdaUpdateWrapper<Order> ow = new LambdaUpdateWrapper<>();
        ow.eq(Order::getId_zj, id)
          .eq(Order::getStatus_zj, status)
          .set(Order::getStatus_zj, "refunded")
          .set(Order::getUpdated_at_zj, LocalDateTime.now());
        if (!orderService.update(ow)) throw new BusinessException("订单状态已变更，无法取消");

        boolean refundFailed = false;

        if ("balance".equals(order.getPayment_method_zj())) {
            LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
            uw.eq(User::getId_zj, order.getUser_id_zj())
              .setSql("balance_zj = balance_zj + {0}", order.getActual_amount_zj());
            if (!userService.update(uw)) refundFailed = true;
            else {
                User refreshed = userService.getById(order.getUser_id_zj());
                if (refreshed == null) { refundFailed = true; }
                else {
                    BalanceLog bl = new BalanceLog();
                    bl.setUser_id_zj(order.getUser_id_zj());
                    bl.setChange_amount_zj(order.getActual_amount_zj());
                    bl.setCurrent_balance_zj(refreshed.getBalance_zj());
                    bl.setType_zj("refund");
                    bl.setReference_id_zj(id);
                    bl.setRemark_zj("订单退款");
                    bl.setCreated_at_zj(LocalDateTime.now());
                    balanceLogService.save(bl);
                }
            }
        } else {
            LambdaQueryWrapper<Receivable> rw = new LambdaQueryWrapper<>();
            rw.eq(Receivable::getOrder_id_zj, id);
            Receivable receivable = receivableService.getOne(rw);
            if (receivable != null) {
                if (receivable.getAmount_paid_zj().compareTo(BigDecimal.ZERO) > 0) {
                    LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
                    uw.eq(User::getId_zj, order.getUser_id_zj())
                      .setSql("balance_zj = balance_zj + {0}", receivable.getAmount_paid_zj());
                    if (!userService.update(uw)) refundFailed = true;
                    else {
                        User refundedUser = userService.getById(order.getUser_id_zj());
                        if (refundedUser != null) {
                            BalanceLog bl2 = new BalanceLog();
                    bl2.setUser_id_zj(order.getUser_id_zj());
                    bl2.setChange_amount_zj(receivable.getAmount_paid_zj());
                    bl2.setCurrent_balance_zj(refundedUser.getBalance_zj());
                    bl2.setType_zj("refund");
                    bl2.setReference_id_zj(id);
                    bl2.setRemark_zj("赊账订单退款-已还金额退回");
                    bl2.setCreated_at_zj(LocalDateTime.now());
                    balanceLogService.save(bl2);
                        }
                    }
                }
                receivable.setStatus_zj("void");
                receivable.setUpdated_at_zj(LocalDateTime.now());
                receivableService.updateById(receivable);
            }
        }

        LambdaQueryWrapper<OrderItem> iw = new LambdaQueryWrapper<>();
        iw.eq(OrderItem::getOrder_id_zj, id);
        List<OrderItem> items = orderItemService.list(iw);
        for (OrderItem item : items) {
            LambdaUpdateWrapper<Product> pw = new LambdaUpdateWrapper<>();
            pw.eq(Product::getId_zj, item.getProduct_id_zj())
              .setSql("stock_zj = stock_zj + {0}", item.getQuantity_zj());
            productService.update(pw);

            Product updated = productService.getById(item.getProduct_id_zj());
            StockLog sl = new StockLog();
            sl.setProduct_id_zj(item.getProduct_id_zj());
            sl.setChange_quantity_zj(item.getQuantity_zj());
            sl.setCurrent_stock_zj(updated.getStock_zj());
            sl.setType_zj("refund_in");
            sl.setReference_id_zj(id);
            sl.setRemark_zj("订单退款入库");
            sl.setCreated_at_zj(LocalDateTime.now());
            stockLogService.save(sl);
        }

        if (refundFailed) throw new BusinessException("退款处理异常，请联系管理员");

        OrderStatusLog log = new OrderStatusLog();
        log.setOrder_id_zj(id);
        log.setFrom_status_zj(status);
        log.setTo_status_zj("refunded");
        log.setOperator_id_zj(getCurrentUserId());
        log.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(log);

        return Result.success("取消成功，已退款", null);
    }

    @PutMapping("/{id}/customer-ship")
    @Transactional
    public Result<?> customerShip(@PathVariable Long id,
                                   @RequestParam String expressCompany,
                                   @RequestParam String trackingNo) {
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        Long userId = getCurrentUserId();
        if (!order.getUser_id_zj().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        if (!"paid".equals(order.getStatus_zj())) {
            throw new BusinessException("当前状态不允许寄送");
        }
        if (order.getNeed_installation_zj() == null || order.getNeed_installation_zj().intValue() != 1) {
            throw new BusinessException("该订单不需要代安装服务");
        }

        String oldStatus = order.getStatus_zj();
        order.setStatus_zj("shipped_to_merchant");
        order.setCustomer_express_company_zj(expressCompany);
        order.setCustomer_tracking_no_zj(trackingNo);
        order.setUpdated_at_zj(LocalDateTime.now());
        orderService.updateById(order);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrder_id_zj(id);
        log.setFrom_status_zj(oldStatus);
        log.setTo_status_zj("shipped_to_merchant");
        log.setOperator_id_zj(userId);
        log.setRemark_zj("顾客寄送商品至商户：" + expressCompany + " " + trackingNo);
        log.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(log);

        return Result.success("已登记寄送信息，等待商户收货", null);
    }

    @PutMapping("/{id}/merchant-receive")
    @Transactional
    public Result<?> merchantReceive(@PathVariable Long id) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        if (!"shipped_to_merchant".equals(order.getStatus_zj())) {
            throw new BusinessException("当前状态不允许收货");
        }

        String oldStatus = order.getStatus_zj();
        order.setStatus_zj("installing");
        order.setUpdated_at_zj(LocalDateTime.now());
        orderService.updateById(order);

        OrderStatusLog log = new OrderStatusLog();
        log.setOrder_id_zj(id);
        log.setFrom_status_zj(oldStatus);
        log.setTo_status_zj("installing");
        log.setOperator_id_zj(getCurrentUserId());
        log.setRemark_zj("商户已收到顾客寄送的商品，开始安装");
        log.setCreated_at_zj(LocalDateTime.now());
        orderStatusLogService.save(log);

        return Result.success("已收货，进入待安装状态", null);
    }

    @PutMapping("/{id}/installation")
    @Transactional
    public Result<?> updateInstallation(@PathVariable Long id,
                                         @RequestParam(required = false) String video,
                                         @RequestParam(required = false) String images,
                                         @RequestParam(required = false) String remark,
                                         @RequestParam(required = false) String status) {
        if (!isMerchant()) throw new BusinessException("权限不足");
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        if (!"installing".equals(order.getStatus_zj())) {
            throw new BusinessException("当前状态不允许操作安装");
        }

        boolean changed = false;
        if (video != null) { order.setInstallation_video_zj(video); changed = true; }
        if (images != null) { order.setInstallation_images_zj(images); changed = true; }
        if (remark != null) { order.setInstallation_remark_zj(remark); changed = true; }

        String oldStatus = order.getStatus_zj();
        if ("completed".equals(status)) {
            order.setStatus_zj("installed");
            order.setInstallation_completed_at_zj(LocalDateTime.now());
            changed = true;

            OrderStatusLog log = new OrderStatusLog();
            log.setOrder_id_zj(id);
            log.setFrom_status_zj(oldStatus);
            log.setTo_status_zj("installed");
            log.setOperator_id_zj(getCurrentUserId());
            log.setRemark_zj("安装完成" + (remark != null ? "：" + remark : ""));
            log.setCreated_at_zj(LocalDateTime.now());
            orderStatusLogService.save(log);
        }

        if (changed) {
            order.setUpdated_at_zj(LocalDateTime.now());
            orderService.updateById(order);
        }

        return Result.success("安装状态更新成功", null);
    }

    @GetMapping("/{id}/installation")
    public Result<?> getInstallation(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        Long userId = getCurrentUserId();
        if (!isMerchant() && !order.getUser_id_zj().equals(userId)) {
            throw new BusinessException("无权查看");
        }

        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("needInstallation", order.getNeed_installation_zj());
        result.put("status", order.getStatus_zj());
        result.put("customerExpressCompany", order.getCustomer_express_company_zj());
        result.put("customerTrackingNo", order.getCustomer_tracking_no_zj());
        result.put("video", order.getInstallation_video_zj());
        result.put("images", order.getInstallation_images_zj());
        result.put("remark", order.getInstallation_remark_zj());
        result.put("completedAt", order.getInstallation_completed_at_zj());
        return Result.success(result);
    }

    @GetMapping("/merchant-address")
    public Result<?> getMerchantAddress() {
        User merchant = userService.getById(1L);
        if (merchant == null) throw new BusinessException("商户不存在");

        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("receiverName", merchant.getMch_receiver_name_zj());
        map.put("receiverPhone", merchant.getMch_receiver_phone_zj());
        map.put("province", merchant.getMch_province_zj());
        map.put("city", merchant.getMch_city_zj());
        map.put("district", merchant.getMch_district_zj());
        map.put("detailAddress", merchant.getMch_detail_address_zj());
        return Result.success(map);
    }

    @PutMapping("/merchant-address")
    @Transactional
    public Result<?> updateMerchantAddress(@RequestBody java.util.Map<String, String> body) {
        if (!isMerchant()) throw new BusinessException("权限不足");

        User user = userService.getById(getCurrentUserId());
        LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
        uw.eq(User::getId_zj, user.getId_zj());
        if (body.containsKey("receiverName")) uw.set(User::getMch_receiver_name_zj, body.get("receiverName"));
        if (body.containsKey("receiverPhone")) uw.set(User::getMch_receiver_phone_zj, body.get("receiverPhone"));
        if (body.containsKey("province")) uw.set(User::getMch_province_zj, body.get("province"));
        if (body.containsKey("city")) uw.set(User::getMch_city_zj, body.get("city"));
        if (body.containsKey("district")) uw.set(User::getMch_district_zj, body.get("district"));
        if (body.containsKey("detailAddress")) uw.set(User::getMch_detail_address_zj, body.get("detailAddress"));
        uw.set(User::getUpdated_at_zj, LocalDateTime.now());
        userService.update(uw);

        return Result.success("商户地址更新成功", null);
    }

    private void setAddress(Order order, User user, OrderCreateRequest req) {
        String name = req.getReceiverName() != null ? req.getReceiverName() : user.getDefault_receiver_name_zj();
        String phone = req.getReceiverPhone() != null ? req.getReceiverPhone() : user.getDefault_receiver_phone_zj();
        String province = req.getProvince() != null ? req.getProvince() : user.getDefault_province_zj();
        String city = req.getCity() != null ? req.getCity() : user.getDefault_city_zj();
        String district = req.getDistrict() != null ? req.getDistrict() : user.getDefault_district_zj();
        String detail = req.getDetailAddress() != null ? req.getDetailAddress() : user.getDefault_detail_address_zj();

        if (name == null || name.isEmpty() || phone == null || phone.isEmpty()
                || province == null || province.isEmpty() || city == null || city.isEmpty()
                || district == null || district.isEmpty() || detail == null || detail.isEmpty()) {
            throw new BusinessException("收货地址信息不完整，请先完善地址");
        }

        order.setReceiver_name_zj(name);
        order.setReceiver_phone_zj(phone);
        order.setProvince_zj(province);
        order.setCity_zj(city);
        order.setDistrict_zj(district);
        order.setDetail_address_zj(detail);
    }

    private BigDecimal getUnpaidReceivableTotal(Long userId) {
        LambdaQueryWrapper<Receivable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Receivable::getUser_id_zj, userId)
               .in(Receivable::getStatus_zj, "unpaid", "partially_paid");
        List<Receivable> list = receivableService.list(wrapper);
        BigDecimal total = BigDecimal.ZERO;
        for (Receivable r : list) {
            total = total.add(r.getAmount_owed_zj().subtract(r.getAmount_paid_zj()));
        }
        return total;
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + ThreadLocalRandom.current().nextInt(100, 999);
    }

    private boolean isMerchant() {
        return AuthUtils.isMerchant();
    }

    private Long getCurrentUserId() {
        return AuthUtils.getCurrentUserId();
    }

    private String extractFirstImage(String imagesJson) {
        if (imagesJson == null || imagesJson.isEmpty()) return null;
        try {
            java.util.List<String> list = objectMapper.readValue(imagesJson, objectMapper.getTypeFactory()
                    .constructCollectionType(java.util.List.class, String.class));
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            System.err.println("extractFirstImage failed: " + e.getMessage());
            return null;
        }
    }
}
