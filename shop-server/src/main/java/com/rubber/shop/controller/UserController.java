package com.rubber.shop.controller;

import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.UserProfileResponse;
import com.rubber.shop.entity.User;
import com.rubber.shop.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<UserProfileResponse> profile() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
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
        return Result.success(resp);
    }
}
