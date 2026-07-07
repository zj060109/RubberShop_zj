package com.rubber.shop.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.AdminUserDetailResponse;
import com.rubber.shop.dto.AdminUserUpdateRequest;
import com.rubber.shop.entity.BalanceLog;
import com.rubber.shop.entity.User;
import com.rubber.shop.service.BalanceLogService;
import com.rubber.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final BalanceLogService balanceLogService;

    public AdminUserController(UserService userService, BalanceLogService balanceLogService) {
        this.userService = userService;
        this.balanceLogService = balanceLogService;
    }

    @GetMapping
    public Result<Page<AdminUserDetailResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String role) {

        AuthUtils.requireMerchant();

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(User::getPhone_zj, phone);
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole_zj, role);
        }
        wrapper.orderByDesc(User::getCreated_at_zj);

        Page<User> userPage = userService.page(new Page<>(page, pageSize), wrapper);

        Page<AdminUserDetailResponse> respPage = new Page<>(page, pageSize, userPage.getTotal());
        respPage.setRecords(userPage.getRecords().stream().map(this::toDetail).toList());
        return Result.success(respPage);
    }

    @GetMapping("/{id}")
    public Result<AdminUserDetailResponse> detail(@PathVariable Long id) {
        AuthUtils.requireMerchant();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(toDetail(user));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody @Valid AdminUserUpdateRequest req) {
        AuthUtils.requireMerchant();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (req.getBalance() != null && req.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal amount = req.getBalance();
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(User::getId_zj, id);
            wrapper.setSql("balance_zj = balance_zj + {0}", amount);
            wrapper.setSql("updated_at_zj = NOW()");
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                wrapper.ge(User::getBalance_zj, amount.abs());
            }
            boolean success = userService.update(wrapper);
            if (!success) {
                throw new BusinessException("余额不足，调整失败");
            }
            User updated = userService.getById(id);
            BalanceLog log = new BalanceLog();
            log.setUser_id_zj(id);
            log.setChange_amount_zj(amount);
            log.setCurrent_balance_zj(updated.getBalance_zj());
            log.setType_zj("admin_adjust");
            log.setRemark_zj("管理员调额");
            log.setCreated_at_zj(LocalDateTime.now());
            balanceLogService.save(log);
        }

        if (req.getPoints() != null) {
            LambdaUpdateWrapper<User> pw = new LambdaUpdateWrapper<>();
            pw.eq(User::getId_zj, id);
            pw.set(User::getPoints_zj, req.getPoints());
            pw.set(User::getUpdated_at_zj, LocalDateTime.now());
            userService.update(pw);
        }

        if (req.getStatus() != null && req.getStatus() != 0 && req.getStatus() != 1) {
            throw new BusinessException("状态值无效，仅支持0或1");
        }
        if (req.getStatus() != null) {
            LambdaUpdateWrapper<User> fieldWrapper = new LambdaUpdateWrapper<>();
            fieldWrapper.eq(User::getId_zj, id);
            fieldWrapper.set(User::getStatus_zj, req.getStatus());
            fieldWrapper.set(User::getUpdated_at_zj, LocalDateTime.now());
            userService.update(fieldWrapper);
        }

        if (req.getPoints() != null && req.getPoints() >= 10) {
            User afterPoints = userService.getById(id);
            if (afterPoints.getCredit_limit_zj() == null || afterPoints.getCredit_limit_zj().compareTo(BigDecimal.ZERO) <= 0) {
                LambdaUpdateWrapper<User> cw = new LambdaUpdateWrapper<>();
                cw.eq(User::getId_zj, id)
                  .set(User::getCredit_limit_zj, new BigDecimal("5000.00"));
                userService.update(cw);
            }
        }

        return Result.success("修改成功", null);
    }

    private AdminUserDetailResponse toDetail(User user) {
        AdminUserDetailResponse dto = new AdminUserDetailResponse();
        dto.setId(user.getId_zj());
        dto.setPhone(user.getPhone_zj());
        dto.setRole(user.getRole_zj());
        dto.setRealName(user.getReal_name_zj());
        dto.setAvatar(user.getAvatar_zj());
        dto.setBalance(user.getBalance_zj());
        dto.setPoints(user.getPoints_zj());
        dto.setCreditLimit(user.getCredit_limit_zj());
        dto.setCompanyName(user.getCompany_name_zj());
        dto.setDefaultReceiverName(user.getDefault_receiver_name_zj());
        dto.setDefaultReceiverPhone(user.getDefault_receiver_phone_zj());
        dto.setDefaultProvince(user.getDefault_province_zj());
        dto.setDefaultCity(user.getDefault_city_zj());
        dto.setDefaultDistrict(user.getDefault_district_zj());
        dto.setDefaultDetailAddress(user.getDefault_detail_address_zj());
        dto.setStatus(user.getStatus_zj());
        dto.setCreatedAt(user.getCreated_at_zj());
        dto.setUpdatedAt(user.getUpdated_at_zj());
        return dto;
    }
}
