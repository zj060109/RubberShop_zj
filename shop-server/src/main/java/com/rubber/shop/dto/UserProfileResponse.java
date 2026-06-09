package com.rubber.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserProfileResponse {

    private Long id;
    private String phone;
    private String role;
    private String realName;
    private String avatar;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private String companyName;
    private String defaultReceiverName;
    private String defaultReceiverPhone;
    private String defaultProvince;
    private String defaultCity;
    private String defaultDistrict;
    private String defaultDetailAddress;
    private Integer status;
    private LocalDateTime createdAt;
}
