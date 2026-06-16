package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.ProfileUpdateRequest;
import com.rubber.shop.dto.RechargeRequest;
import com.rubber.shop.dto.UserProfileResponse;
import com.rubber.shop.entity.BalanceLog;
import com.rubber.shop.entity.User;
import com.rubber.shop.service.BalanceLogService;
import com.rubber.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final BalanceLogService balanceLogService;

    public UserController(UserService userService, BalanceLogService balanceLogService) {
        this.userService = userService;
        this.balanceLogService = balanceLogService;
    }

    @GetMapping("/profile")
    public Result<UserProfileResponse> profile() {
        User user = getCurrentUser();
        return Result.success(toProfileResponse(user));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody ProfileUpdateRequest req) {
        User user = getCurrentUser();
        if (req.getRealName() != null) {
            user.setReal_name_zj(req.getRealName());
        }
        if (req.getAvatar() != null) {
            user.setAvatar_zj(req.getAvatar());
        }
        if (req.getReceiverName() != null) {
            user.setDefault_receiver_name_zj(req.getReceiverName());
        }
        if (req.getReceiverPhone() != null) {
            user.setDefault_receiver_phone_zj(req.getReceiverPhone());
        }
        if (req.getProvince() != null) {
            user.setDefault_province_zj(req.getProvince());
        }
        if (req.getCity() != null) {
            user.setDefault_city_zj(req.getCity());
        }
        if (req.getDistrict() != null) {
            user.setDefault_district_zj(req.getDistrict());
        }
        if (req.getDetailAddress() != null) {
            user.setDefault_detail_address_zj(req.getDetailAddress());
        }
        userService.updateById(user);
        return Result.success("修改成功", null);
    }

    @PostMapping("/recharge")
    @Transactional
    public Result<?> recharge(@RequestBody @Valid RechargeRequest req) {
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        User user = getCurrentUser();
        user.setBalance_zj(user.getBalance_zj().add(req.getAmount()));
        userService.updateById(user);

        BalanceLog log = new BalanceLog();
        log.setUser_id_zj(user.getId_zj());
        log.setChange_amount_zj(req.getAmount());
        log.setCurrent_balance_zj(user.getBalance_zj());
        log.setType_zj("recharge");
        log.setRemark_zj("余额充值");
        log.setCreated_at_zj(LocalDateTime.now());
        balanceLogService.save(log);

        return Result.success("充值成功", null);
    }

    @GetMapping("/balance_logs")
    public Result<Page<BalanceLog>> balanceLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId();
        LambdaQueryWrapper<BalanceLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BalanceLog::getUser_id_zj, userId)
               .orderByDesc(BalanceLog::getCreated_at_zj);
        return Result.success(balanceLogService.page(new Page<>(page, pageSize), wrapper));
    }

    private User getCurrentUser() {
        User user = userService.getById(getCurrentUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private UserProfileResponse toProfileResponse(User user) {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setId(user.getId_zj());
        resp.setPhone(user.getPhone_zj());
        resp.setRole(user.getRole_zj());
        resp.setRealName(user.getReal_name_zj());
        resp.setAvatar(user.getAvatar_zj());
        resp.setBalance(user.getBalance_zj());
        resp.setCreditLimit(user.getCredit_limit_zj());
        resp.setCompanyName(user.getCompany_name_zj());
        resp.setDefaultReceiverName(user.getDefault_receiver_name_zj());
        resp.setDefaultReceiverPhone(user.getDefault_receiver_phone_zj());
        resp.setDefaultProvince(user.getDefault_province_zj());
        resp.setDefaultCity(user.getDefault_city_zj());
        resp.setDefaultDistrict(user.getDefault_district_zj());
        resp.setDefaultDetailAddress(user.getDefault_detail_address_zj());
        resp.setStatus(user.getStatus_zj());
        resp.setCreatedAt(user.getCreated_at_zj());
        return resp;
    }
}
