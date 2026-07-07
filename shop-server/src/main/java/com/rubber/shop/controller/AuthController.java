package com.rubber.shop.controller;

import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.config.JwtUtil;
import com.rubber.shop.dto.LoginRequest;
import com.rubber.shop.dto.LoginResponse;
import com.rubber.shop.dto.PasswordUpdateRequest;
import com.rubber.shop.dto.RegisterRequest;
import com.rubber.shop.entity.User;
import com.rubber.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        User user = userService.getByPhone(req.getPhone());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword_zj())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (user.getStatus_zj() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId_zj(), user.getRole_zj());
        LoginResponse resp = new LoginResponse(token, user.getId_zj(), user.getRole_zj());
        return Result.success("登录成功", resp);
    }

    @PostMapping("/register")
    @Transactional
    public Result<?> register(@RequestBody @Valid RegisterRequest req) {
        if (req.getPassword() == null || req.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        User existUser = userService.getByPhone(req.getPhone());
        if (existUser != null) {
            throw new BusinessException("该手机号已注册");
        }
        User user = new User();
        user.setPhone_zj(req.getPhone());
        user.setPassword_zj(passwordEncoder.encode(req.getPassword()));
        user.setRole_zj("customer");
        user.setReal_name_zj(req.getRealName());
        user.setBalance_zj(java.math.BigDecimal.ZERO);
        user.setCredit_limit_zj(java.math.BigDecimal.ZERO);
        user.setStatus_zj(1);
        user.setCreated_at_zj(java.time.LocalDateTime.now());
        user.setUpdated_at_zj(java.time.LocalDateTime.now());
        userService.save(user);
        String token = jwtUtil.generateToken(user.getId_zj(), user.getRole_zj());
        LoginResponse resp = new LoginResponse(token, user.getId_zj(), user.getRole_zj());
        return Result.success("注册成功", resp);
    }

    @PutMapping("/password")
    @Transactional
    public Result<?> changePassword(@RequestBody @Valid PasswordUpdateRequest req) {
        Long userId = getCurrentUserId();
        if (req.getNewPassword() == null || req.getNewPassword().length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }
        if (req.getOldPassword().equals(req.getNewPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword_zj())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword_zj(passwordEncoder.encode(req.getNewPassword()));
        user.setUpdated_at_zj(java.time.LocalDateTime.now());
        userService.updateById(user);
        return Result.success("密码修改成功", null);
    }

    private Long getCurrentUserId() {
        return AuthUtils.getCurrentUserId();
    }
}
