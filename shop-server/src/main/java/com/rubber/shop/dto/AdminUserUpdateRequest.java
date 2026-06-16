package com.rubber.shop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminUserUpdateRequest {

    private BigDecimal balance;
    private BigDecimal creditLimit;
    private Integer status;
}
