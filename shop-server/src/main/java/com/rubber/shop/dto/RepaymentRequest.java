package com.rubber.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepaymentRequest {

    @NotNull(message = "还款金额不能为空")
    private BigDecimal amount;

    @NotBlank(message = "还款方式不能为空")
    private String paymentMethod;

    private String remark;
}
