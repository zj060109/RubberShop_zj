package com.rubber.shop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminUserUpdateRequest {

    private BigDecimal balance;

    @DecimalMin(value = "0.00", message = "赊账额度不能为负数")
    private BigDecimal creditLimit;

    @Min(value = 0, message = "状态值只能为0或1")
    @Max(value = 1, message = "状态值只能为0或1")
    private Integer status;
}
