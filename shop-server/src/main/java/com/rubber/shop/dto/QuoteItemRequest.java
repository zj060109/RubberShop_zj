package com.rubber.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteItemRequest {

    @NotNull(message = "规格不能为空")
    private String spec;

    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;
}
