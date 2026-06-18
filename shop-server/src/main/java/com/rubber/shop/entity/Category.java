package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category_zj")
public class Category {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("name_zj")
    private String name_zj;

    @TableField("parent_id_zj")
    private Long parent_id_zj = 0L;

    @TableField("sort_zj")
    private Integer sort_zj;

    @TableField("icon_zj")
    private String icon_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;
}
