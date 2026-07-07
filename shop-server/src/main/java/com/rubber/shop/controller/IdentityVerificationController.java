package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.entity.IdentityVerification;
import com.rubber.shop.entity.User;
import com.rubber.shop.service.IdentityVerificationService;
import com.rubber.shop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/identity")
public class IdentityVerificationController {

    private final IdentityVerificationService verificationService;
    private final UserService userService;

    public IdentityVerificationController(IdentityVerificationService verificationService, UserService userService) {
        this.verificationService = verificationService;
        this.userService = userService;
    }

    @GetMapping("/status")
    public Result<?> myStatus() {
        Long userId = getCurrentUserId();
        LambdaQueryWrapper<IdentityVerification> w = new LambdaQueryWrapper<>();
        w.eq(IdentityVerification::getUser_id_zj, userId);
        IdentityVerification iv = verificationService.getOne(w);
        return Result.success(iv);
    }

    @PostMapping("/submit")
    @Transactional
    public Result<?> submit(@RequestParam String idCard, @RequestParam String realName,
                            @RequestParam(required = false) String faceImage) {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (!"customer".equals(user.getRole_zj())) {
            throw new BusinessException("仅顾客可提交实名认证");
        }

        LambdaQueryWrapper<IdentityVerification> ew = new LambdaQueryWrapper<>();
        ew.eq(IdentityVerification::getUser_id_zj, userId);
        IdentityVerification existing = verificationService.getOne(ew);

        if (existing != null) {
            existing.setId_card_zj(idCard);
            existing.setReal_name_zj(realName);
            existing.setFace_image_zj(faceImage);
            existing.setStatus_zj(1);
            existing.setUpdated_at_zj(LocalDateTime.now());
            verificationService.updateById(existing);
        } else {
            IdentityVerification iv = new IdentityVerification();
            iv.setUser_id_zj(userId);
            iv.setId_card_zj(idCard);
            iv.setReal_name_zj(realName);
            iv.setFace_image_zj(faceImage);
            iv.setStatus_zj(1);
            iv.setCreated_at_zj(LocalDateTime.now());
            verificationService.save(iv);
        }

        return Result.success("实名认证提交成功", null);
    }

    private Long getCurrentUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null)
            throw new BusinessException("请先登录");
        return (Long) a.getPrincipal();
    }
}
