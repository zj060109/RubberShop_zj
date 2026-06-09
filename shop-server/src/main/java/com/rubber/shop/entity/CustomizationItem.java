package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("customization_item_zj")
public class CustomizationItem {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("customization_id_zj")
    private Long customization_id_zj;

    @TableField("product_spec_zj")
    private String product_spec_zj;

    @TableField("quantity_zj")
    private Integer quantity_zj;

    @TableField("unit_price_zj")
    private BigDecimal unit_price_zj;
}
