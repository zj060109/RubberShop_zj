package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("purchase_item_zj")
public class PurchaseItem {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("purchase_id_zj")
    private Long purchase_id_zj;

    @TableField("product_id_zj")
    private Long product_id_zj;

    @TableField("product_name_zj")
    private String product_name_zj;

    @TableField("spec_zj")
    private String spec_zj;

    @TableField("quantity_zj")
    private Integer quantity_zj;

    @TableField("unit_price_zj")
    private BigDecimal unit_price_zj;

    @TableField("subtotal_zj")
    private BigDecimal subtotal_zj;
}
