package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_log_zj")
public class BalanceLog {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("user_id_zj")
    private Long user_id_zj;

    @TableField("change_amount_zj")
    private BigDecimal change_amount_zj;

    @TableField("current_balance_zj")
    private BigDecimal current_balance_zj;

    @TableField("type_zj")
    private String type_zj;

    @TableField("reference_id_zj")
    private Long reference_id_zj;

    @TableField("remark_zj")
    private String remark_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;
}
