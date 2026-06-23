package com.rubber.shop.dto;

import com.rubber.shop.entity.Purchase;
import com.rubber.shop.entity.PurchaseItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PurchaseDetailResponse {

    private Purchase purchase;
    private List<PurchaseItem> items;
}
