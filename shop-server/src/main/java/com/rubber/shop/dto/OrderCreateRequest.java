package com.rubber.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    @NotEmpty(message = "订单明细不能为空")
    private List<OrderItemRequest> items;

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
}
