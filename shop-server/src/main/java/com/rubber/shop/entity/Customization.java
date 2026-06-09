package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("customization_zj")
public class Customization {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("user_id_zj")
    private Long user_id_zj;

    @TableField("status_zj")
    private String status_zj;

    @TableField("description_zj")
    private String description_zj;

    @TableField("reference_images_zj")
    private String reference_images_zj;

    @TableField("total_quoted_price_zj")
    private BigDecimal total_quoted_price_zj;

    @TableField("order_id_zj")
    private Long order_id_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
