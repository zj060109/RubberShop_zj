package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_zj")
public class Order {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("order_no_zj")
    private String order_no_zj;

    @TableField("user_id_zj")
    private Long user_id_zj;

    @TableField("total_amount_zj")
    private BigDecimal total_amount_zj;

    @TableField("actual_amount_zj")
    private BigDecimal actual_amount_zj;

    @TableField("payment_method_zj")
    private String payment_method_zj;

    @TableField("status_zj")
    private String status_zj;

    @TableField("receiver_name_zj")
    private String receiver_name_zj;

    @TableField("receiver_phone_zj")
    private String receiver_phone_zj;

    @TableField("province_zj")
    private String province_zj;

    @TableField("city_zj")
    private String city_zj;

    @TableField("district_zj")
    private String district_zj;

    @TableField("detail_address_zj")
    private String detail_address_zj;

    @TableField("express_company_zj")
    private String express_company_zj;

    @TableField("tracking_no_zj")
    private String tracking_no_zj;

    @TableField("paid_at_zj")
    private LocalDateTime paid_at_zj;

    @TableField("shipped_at_zj")
    private LocalDateTime shipped_at_zj;

    @TableField("finished_at_zj")
    private LocalDateTime finished_at_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
