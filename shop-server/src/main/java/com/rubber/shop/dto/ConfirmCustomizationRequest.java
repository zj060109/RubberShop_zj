package com.rubber.shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmCustomizationRequest {

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
}
