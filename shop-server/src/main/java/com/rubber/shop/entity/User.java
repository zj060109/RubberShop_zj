package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_zj")
public class User {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("phone_zj")
    private String phone_zj;

    @TableField("password_zj")
    private String password_zj;

    @TableField("role_zj")
    private String role_zj;

    @TableField("real_name_zj")
    private String real_name_zj;

    @TableField("avatar_zj")
    private String avatar_zj;

    @TableField("balance_zj")
    private BigDecimal balance_zj;

    @TableField("credit_limit_zj")
    private BigDecimal credit_limit_zj;

    @TableField("company_name_zj")
    private String company_name_zj;

    @TableField("default_receiver_name_zj")
    private String default_receiver_name_zj;

    @TableField("default_receiver_phone_zj")
    private String default_receiver_phone_zj;

    @TableField("default_province_zj")
    private String default_province_zj;

    @TableField("default_city_zj")
    private String default_city_zj;

    @TableField("default_district_zj")
    private String default_district_zj;

    @TableField("default_detail_address_zj")
    private String default_detail_address_zj;

    @TableField("status_zj")
    private Integer status_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
