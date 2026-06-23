package com.rubber.shop.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(toDetail(user));
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<?> update(@PathVariable Long id, @RequestBody @Valid AdminUserUpdateRequest req) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId_zj, id);

        if (req.getBalance() != null && req.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal amount = req.getBalance();
            wrapper.setSql("balance_zj = balance_zj + {0}", amount);
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

        if (req.getCreditLimit() != null || req.getStatus() != null) {
            LambdaUpdateWrapper<User> fieldWrapper = new LambdaUpdateWrapper<>();
            fieldWrapper.eq(User::getId_zj, id);
            if (req.getCreditLimit() != null) {
                fieldWrapper.set(User::getCredit_limit_zj, req.getCreditLimit());
            }
            if (req.getStatus() != null) {
                fieldWrapper.set(User::getStatus_zj, req.getStatus());
            }
            fieldWrapper.set(User::getUpdated_at_zj, LocalDateTime.now());
            userService.update(fieldWrapper);
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
