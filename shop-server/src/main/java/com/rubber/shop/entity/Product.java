package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_zj")
public class Product {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("category_id_zj")
    private Long category_id_zj;

    @TableField("name_zj")
    private String name_zj;

    @TableField("spec_zj")
    private String spec_zj;

    @TableField("brand_zj")
    private String brand_zj;

    @TableField("model_zj")
    private String model_zj;

    @TableField("material_zj")
    private String material_zj;

    @TableField("description_zj")
    private String description_zj;

    @TableField("images_zj")
    private String images_zj;

    @TableField("price_zj")
    private BigDecimal price_zj;

    @TableField("stock_zj")
    private Integer stock_zj;

    @TableField("warning_stock_zj")
    private Integer warning_stock_zj;

    @TableField("status_zj")
    private String status_zj;

    @TableField("factory_id_zj")
    private Long factory_id_zj;

    @TableField("is_customized_zj")
    private Integer is_customized_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
