package com.rubber.shop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String name;

    private String spec;

    private String description;

    private List<String> images;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    private Integer stock;

    private Integer warningStock;

    private Long factoryId;
}
