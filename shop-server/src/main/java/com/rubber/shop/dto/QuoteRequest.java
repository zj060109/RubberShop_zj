package com.rubber.shop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuoteRequest {

    @NotEmpty(message = "报价明细不能为空")
    private List<QuoteItemRequest> items;
}
