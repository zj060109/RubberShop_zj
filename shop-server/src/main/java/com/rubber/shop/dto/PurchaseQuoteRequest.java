package com.rubber.shop.dto;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseQuoteRequest {
    private List<PurchaseQuoteItem> items;
}
