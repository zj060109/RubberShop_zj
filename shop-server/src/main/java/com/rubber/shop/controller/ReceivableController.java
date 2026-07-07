package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.ReceivableListDTO;
import com.rubber.shop.dto.RepaymentRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/receivables")
public class ReceivableController {

    private final ReceivableService receivableService;
    private final ReceiptService receiptService;
    private final UserService userService;
    private final BalanceLogService balanceLogService;
    private final OrderService orderService;

    public ReceivableController(ReceivableService rs, ReceiptService rps,
            UserService us, BalanceLogService bls, OrderService os) {
        this.receivableService = rs; this.receiptService = rps;
        this.userService = us; this.balanceLogService = bls;
        this.orderService = os;
    }

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String phone) {
        Long currentUserId = getCurrentUserId();
        boolean isMerchantUser = isMerchant();

        Set<Long> phoneUserIds = null;
        if (isMerchantUser && phone != null && !phone.isEmpty()) {
            LambdaQueryWrapper<User> uqw = new LambdaQueryWrapper<>();
            uqw.like(User::getPhone_zj, phone);
            phoneUserIds = userService.list(uqw).stream().map(User::getId_zj).collect(Collectors.toSet());
        }

        LambdaQueryWrapper<Receivable> w = new LambdaQueryWrapper<>();
        if (!isMerchantUser) {
            w.eq(Receivable::getUser_id_zj, currentUserId);
        } else if (phoneUserIds != null) {
            if (phoneUserIds.isEmpty()) {
                Page<ReceivableListDTO> emptyPage = new Page<>(page, pageSize, 0);
                return Result.success(emptyPage);
            }
            w.in(Receivable::getUser_id_zj, phoneUserIds);
        }
        if (status != null && !status.isEmpty()) {
            w.eq(Receivable::getStatus_zj, status);
        }
        w.orderByDesc(Receivable::getCreated_at_zj);

        Page<Receivable> resultPage = receivableService.page(new Page<>(page, pageSize), w);

        if (isMerchantUser) {
            List<Receivable> records = resultPage.getRecords();
            Set<Long> userIds = records.stream().map(Receivable::getUser_id_zj).collect(Collectors.toSet());
            Set<Long> orderIds = records.stream().map(Receivable::getOrder_id_zj).collect(Collectors.toSet());

            Map<Long, User> userMap = new HashMap<>();
            if (!userIds.isEmpty()) {
                LambdaQueryWrapper<User> usw = new LambdaQueryWrapper<>();
                usw.in(User::getId_zj, userIds);
                for (User u : userService.list(usw)) userMap.put(u.getId_zj(), u);
            }

            Map<Long, String> orderNoMap = new HashMap<>();
            if (!orderIds.isEmpty()) {
                for (Long oid : orderIds) {
                    Order order = orderService.getById(oid);
                    if (order != null) orderNoMap.put(oid, order.getOrder_no_zj());
                }
            }

            List<ReceivableListDTO> dtos = new ArrayList<>();
            for (Receivable r : records) {
                User u = userMap.get(r.getUser_id_zj());
                String orderNo = orderNoMap.getOrDefault(r.getOrder_id_zj(), "");
                dtos.add(ReceivableListDTO.from(r,
                    u != null ? u.getReal_name_zj() : null,
                    u != null ? u.getPhone_zj() : null,
                    u != null ? u.getCompany_name_zj() : null,
                    orderNo));
            }

            Page<ReceivableListDTO> dtoPage = new Page<>(page, pageSize, resultPage.getTotal());
            dtoPage.setRecords(dtos);
            return Result.success(dtoPage);
        }

        return Result.success(resultPage);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Receivable r = receivableService.getById(id);
        if (r == null) throw new BusinessException("应收记录不存在");
        if (!isMerchant() && !r.getUser_id_zj().equals(getCurrentUserId()))
            throw new BusinessException("无权查看");

        LambdaQueryWrapper<Receipt> rw = new LambdaQueryWrapper<>();
        rw.eq(Receipt::getReceivable_id_zj, id).orderByDesc(Receipt::getCreated_at_zj);
        List<Receipt> receipts = receiptService.list(rw);

        Map<String, Object> result = new HashMap<>();
        result.put("receivable", r);
        result.put("receipts", receipts);

        if (isMerchant()) {
            User customer = userService.getById(r.getUser_id_zj());
            Order order = orderService.getById(r.getOrder_id_zj());
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("customerName", customer != null ? customer.getReal_name_zj() : null);
            userInfo.put("customerPhone", customer != null ? customer.getPhone_zj() : null);
            userInfo.put("customerCompany", customer != null ? customer.getCompany_name_zj() : null);
            userInfo.put("customerPoints", customer != null ? customer.getPoints_zj() : null);
            userInfo.put("customerCreditLimit", customer != null ? customer.getCredit_limit_zj() : null);
            userInfo.put("orderNo", order != null ? order.getOrder_no_zj() : null);
            result.put("customerInfo", userInfo);
        }

        return Result.success(result);
    }

    @PostMapping("/{id}/receipts")
    @Transactional
    public Result<?> repay(@PathVariable Long id, @RequestBody @Valid RepaymentRequest req) {
        Receivable r = receivableService.getById(id);
        if (r == null) throw new BusinessException("应收记录不存在");
        if (!isMerchant() && !r.getUser_id_zj().equals(getCurrentUserId()))
            throw new BusinessException("无权操作该应收记录");
        if ("paid".equals(r.getStatus_zj()) || "void".equals(r.getStatus_zj()))
            throw new BusinessException("该应收已结清或已作废");

        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("还款金额必须大于0");

        BigDecimal remaining = r.getAmount_owed_zj().subtract(r.getAmount_paid_zj());
        if (req.getAmount().compareTo(remaining) > 0)
            throw new BusinessException("还款金额不能超过欠款余额：" + remaining);

        String pm = req.getPaymentMethod();
        Long userId = getCurrentUserId();
        boolean isMerchantUser = isMerchant();

        if ("balance".equals(pm)) {
            if (isMerchantUser) throw new BusinessException("商家不能使用余额还款，请使用现金或转账方式");
            LambdaUpdateWrapper<User> uw = new LambdaUpdateWrapper<>();
            uw.eq(User::getId_zj, userId)
              .ge(User::getBalance_zj, req.getAmount())
              .setSql("balance_zj = balance_zj - {0}", req.getAmount());
            if (!userService.update(uw)) throw new BusinessException("余额不足");

            User refreshed = userService.getById(userId);
            BalanceLog bl = new BalanceLog();
            bl.setUser_id_zj(userId);
            bl.setChange_amount_zj(req.getAmount().negate());
            bl.setCurrent_balance_zj(refreshed.getBalance_zj());
            bl.setType_zj("repay");
            bl.setReference_id_zj(id);
            bl.setRemark_zj("赊账还款");
            bl.setCreated_at_zj(LocalDateTime.now());
            balanceLogService.save(bl);
        } else if (!isMerchantUser) {
            throw new BusinessException("顾客仅支持余额还款，商家可登记线下收款");
        } else if (!"cash".equals(pm) && !"bank_transfer".equals(pm)) {
            throw new BusinessException("还款方式无效");
        }

        Receipt receipt = new Receipt();
        receipt.setReceivable_id_zj(id);
        receipt.setAmount_zj(req.getAmount());
        receipt.setPayment_method_zj(pm);
        receipt.setOperator_id_zj(userId);
        receipt.setRemark_zj(req.getRemark());
        receipt.setCreated_at_zj(LocalDateTime.now());
        receiptService.save(receipt);

        LambdaUpdateWrapper<Receivable> rw = new LambdaUpdateWrapper<>();
        rw.eq(Receivable::getId_zj, id)
          .setSql("amount_paid_zj = amount_paid_zj + {0}", req.getAmount())
          .setSql("updated_at_zj = NOW()");
        if (r.getAmount_paid_zj().add(req.getAmount()).compareTo(r.getAmount_owed_zj()) >= 0) {
            rw.set(Receivable::getStatus_zj, "paid");
        } else {
            rw.set(Receivable::getStatus_zj, "partially_paid");
        }
        receivableService.update(rw);

        return Result.success("还款成功", null);
    }

    private boolean isMerchant() { return AuthUtils.isMerchant(); }
    private Long getCurrentUserId() { return AuthUtils.getCurrentUserId(); }
}
