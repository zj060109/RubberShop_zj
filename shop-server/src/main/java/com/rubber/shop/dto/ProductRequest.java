package com.rubber.shop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    private Long categoryId;
    private String name;
    private String description;
    private List<String> images;
    private BigDecimal price;
    private Integer stock;
    private Integer warningStock;
    private Long factoryId;
}
