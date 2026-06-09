package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_status_log_zj")
public class OrderStatusLog {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("order_id_zj")
    private Long order_id_zj;

    @TableField("from_status_zj")
    private String from_status_zj;

    @TableField("to_status_zj")
    private String to_status_zj;

    @TableField("operator_id_zj")
    private Long operator_id_zj;

    @TableField("remark_zj")
    private String remark_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;
}
