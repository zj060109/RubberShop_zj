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
@TableName("receivable_zj")
public class Receivable {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("order_id_zj")
    private Long order_id_zj;

    @TableField("user_id_zj")
    private Long user_id_zj;

    @TableField("amount_owed_zj")
    private BigDecimal amount_owed_zj;

    @TableField("amount_paid_zj")
    private BigDecimal amount_paid_zj;

    @TableField("status_zj")
    private String status_zj;

    @TableField("due_date_zj")
    private LocalDate due_date_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
