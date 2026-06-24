package com.rubber.shop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePurchaseRequest {

    @NotEmpty(message = "采购明细不能为空")
    private List<PurchaseItemRequest> items;
}
