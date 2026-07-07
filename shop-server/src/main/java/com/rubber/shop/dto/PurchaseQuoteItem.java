package com.rubber.shop.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseQuoteItem {
    private Long itemId;
    private BigDecimal unitPrice;
}
