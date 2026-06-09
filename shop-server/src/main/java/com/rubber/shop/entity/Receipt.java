package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("receipt_zj")
public class Receipt {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("receivable_id_zj")
    private Long receivable_id_zj;

    @TableField("amount_zj")
    private BigDecimal amount_zj;

    @TableField("payment_method_zj")
    private String payment_method_zj;

    @TableField("operator_id_zj")
    private Long operator_id_zj;

    @TableField("remark_zj")
    private String remark_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;
}
