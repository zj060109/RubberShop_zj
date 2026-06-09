package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("order_item_zj")
public class OrderItem {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("order_id_zj")
    private Long order_id_zj;

    @TableField("product_id_zj")
    private Long product_id_zj;

    @TableField("product_name_zj")
    private String product_name_zj;

    @TableField("product_image_zj")
    private String product_image_zj;

    @TableField("price_zj")
    private BigDecimal price_zj;

    @TableField("quantity_zj")
    private Integer quantity_zj;

    @TableField("subtotal_zj")
    private BigDecimal subtotal_zj;
}
