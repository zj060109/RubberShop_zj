package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_log_zj")
public class StockLog {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("product_id_zj")
    private Long product_id_zj;

    @TableField("change_quantity_zj")
    private Integer change_quantity_zj;

    @TableField("current_stock_zj")
    private Integer current_stock_zj;

    @TableField("type_zj")
    private String type_zj;

    @TableField("reference_id_zj")
    private Long reference_id_zj;

    @TableField("remark_zj")
    private String remark_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;
}
