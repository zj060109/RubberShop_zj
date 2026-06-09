package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_config_zj")
public class SysConfig {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("config_key_zj")
    private String config_key_zj;

    @TableField("config_value_zj")
    private String config_value_zj;

    @TableField("remark_zj")
    private String remark_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
