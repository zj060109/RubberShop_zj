package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("purchase_zj")
public class Purchase {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("order_no_zj")
    private String order_no_zj;

    @TableField("factory_id_zj")
    private Long factory_id_zj;

    @TableField("total_amount_zj")
    private BigDecimal total_amount_zj;

    @TableField("status_zj")
    private String status_zj;

    @TableField("expected_delivery_date_zj")
    private LocalDate expected_delivery_date_zj;

    @TableField("express_company_zj")
    private String express_company_zj;

    @TableField("tracking_no_zj")
    private String tracking_no_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
