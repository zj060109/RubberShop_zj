package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private final ObjectMapper objectMapper;

    public OrderController(OrderService orderService, OrderItemService orderItemService,
            OrderStatusLogService orderStatusLogService, ProductService productService,
            UserService userService, BalanceLogService balanceLogService,
            StockLogService stockLogService, ReceivableService receivableService,
            ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.orderStatusLogService = orderStatusLogService;
        this.productService = productService;
        this.userService = userService;
        this.balanceLogService = balanceLogService;
        this.stockLogService = stockLogService;
        this.receivableService = receivableService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Transactional
    public Result<Order> create(@RequestBody @Valid OrderCreateRequest req) {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        String paymentMethod = req.getPaymentMethod();
        if (!"balance".equals(paymentMethod) && !"credit".equals(paymentMethod)) {
            throw new BusinessException("支付方式无效，仅支持 balance 或 credit");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : req.getItems()) {
            Product product = productService.getById(itemReq.getProductId());
            if (product == null) throw new BusinessException("商品不存在");
            if (!"on".equals(product.getStatus_zj())) throw new BusinessException("商品[" + product.getName_zj() + "]已下架");
            if (product.getStock_zj() < itemReq.getQuantity()) throw new BusinessException("商品[" + product.getName_zj() + "]库存不足");

            BigDecimal subtotal = product.getPrice_zj().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem oi = new OrderItem();
            oi.setProduct_id_zj(product.getId_zj());
            oi.setProduct_name_zj(product.getName_zj());
            oi.setProduct_image_zj(extractFirstImage(product.getImages_zj()));
            oi.setPrice_zj(product.getPrice_zj());
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
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin()) {
            wrapper.eq(Order::getUser_id_zj, userId);
        }
        wrapper.orderByDesc(Order::getCreated_at_zj);
        return Result.success(orderService.page(new Page<>(page, pageSize), wrapper));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailResponse> detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        Long userId = getCurrentUserId();
        if (!isAdmin() && !order.getUser_id_zj().equals(userId)) {
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
        if (!isAdmin()) throw new BusinessException("权限不足");
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
        if (!isAdmin()) throw new BusinessException("权限不足");
        Order order = orderService.getById(id);
        if (order == null) throw new BusinessException("订单不存在");

        String oldStatus = order.getStatus_zj();
        if (!"paid".equals(oldStatus) && !"accepted".equals(oldStatus)) {
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
        if (!isAdmin() && !order.getUser_id_zj().equals(userId)) {
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
        if (!"paid".equals(status) && !"accepted".equals(status)) {
            throw new BusinessException("当前订单状态不允许取消");
        }

        Long userId = getCurrentUserId();
        if (!isAdmin() && !order.getUser_id_zj().equals(userId)) {
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
                    userService.update(uw);

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

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            String r = a.getAuthority();
            if ("ROLE_MERCHANT".equals(r)) return true;
        }
        return false;
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private String extractFirstImage(String imagesJson) {
        if (imagesJson == null || imagesJson.isEmpty()) return null;
        try {
            java.util.List<String> list = objectMapper.readValue(imagesJson, objectMapper.getTypeFactory()
                    .constructCollectionType(java.util.List.class, String.class));
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
