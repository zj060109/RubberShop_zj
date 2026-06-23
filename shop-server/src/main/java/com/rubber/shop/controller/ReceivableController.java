package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
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
import java.util.List;

@RestController
@RequestMapping("/api/receivables")
public class ReceivableController {

    private final ReceivableService receivableService;
    private final ReceiptService receiptService;
    private final UserService userService;
    private final BalanceLogService balanceLogService;

    public ReceivableController(ReceivableService rs, ReceiptService rps,
            UserService us, BalanceLogService bls) {
        this.receivableService = rs; this.receiptService = rps;
        this.userService = us; this.balanceLogService = bls;
    }

    @GetMapping
    public Result<Page<Receivable>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        LambdaQueryWrapper<Receivable> w = new LambdaQueryWrapper<>();
        if (!isAdmin()) w.eq(Receivable::getUser_id_zj, getCurrentUserId());
        w.orderByDesc(Receivable::getCreated_at_zj);
        return Result.success(receivableService.page(new Page<>(page, pageSize), w));
    }

    @GetMapping("/{id}")
    public Result<Receivable> detail(@PathVariable Long id) {
        Receivable r = receivableService.getById(id);
        if (r == null) throw new BusinessException("应收记录不存在");
        if (!isAdmin() && !r.getUser_id_zj().equals(getCurrentUserId()))
            throw new BusinessException("无权查看");
        return Result.success(r);
    }

    @PostMapping("/{id}/receipts")
    @Transactional
    public Result<?> repay(@PathVariable Long id, @RequestBody @Valid RepaymentRequest req) {
        Receivable r = receivableService.getById(id);
        if (r == null) throw new BusinessException("应收记录不存在");
        if ("paid".equals(r.getStatus_zj()) || "void".equals(r.getStatus_zj()))
            throw new BusinessException("该应收已结清或已作废");

        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("还款金额必须大于0");

        BigDecimal remaining = r.getAmount_owed_zj().subtract(r.getAmount_paid_zj());
        if (req.getAmount().compareTo(remaining) > 0)
            throw new BusinessException("还款金额不能超过欠款余额：" + remaining);

        String pm = req.getPaymentMethod();
        Long userId = getCurrentUserId();
        boolean isMerchantUser = isAdmin();

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
        } else if (!"cash".equals(pm) && !"bank_transfer".equals(pm) && !"balance".equals(pm)) {
            throw new BusinessException("还款方式无效");
        }

        Receipt receipt = new Receipt();
        receipt.setReceivable_id_zj(id);
        receipt.setAmount_zj(req.getAmount());
        receipt.setPayment_method_zj(pm);
        receipt.setOperator_id_zj(userId);
        receipt.setCreated_at_zj(LocalDateTime.now());
        receiptService.save(receipt);

        r.setAmount_paid_zj(r.getAmount_paid_zj().add(req.getAmount()));
        if (r.getAmount_paid_zj().compareTo(r.getAmount_owed_zj()) >= 0) {
            r.setStatus_zj("paid");
        } else {
            r.setStatus_zj("partially_paid");
        }
        r.setUpdated_at_zj(LocalDateTime.now());
        receivableService.updateById(r);

        return Result.success("还款成功", null);
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
