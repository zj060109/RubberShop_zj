package com.rubber.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItemRequest {

    private Long productId;

    @NotNull(message = "商品名称不能为空")
    private String productName;

    private String spec;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    private BigDecimal unitPrice;
}
