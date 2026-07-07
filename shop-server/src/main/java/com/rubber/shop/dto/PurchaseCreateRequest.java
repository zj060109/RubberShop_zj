package com.rubber.shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseCreateRequest {

    @NotNull(message = "厂家不能为空")
    private Long factoryId;

    @Valid
    @NotEmpty(message = "采购明细不能为空")
    private List<PurchaseItemRequest> items;

    private String expectedDeliveryDate;
}
