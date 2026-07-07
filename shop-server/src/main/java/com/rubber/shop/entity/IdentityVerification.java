package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("identity_verification_zj")
public class IdentityVerification {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("user_id_zj")
    private Long user_id_zj;

    @TableField("id_card_zj")
    private String id_card_zj;

    @TableField("real_name_zj")
    private String real_name_zj;

    @TableField("face_image_zj")
    private String face_image_zj;

    @TableField("status_zj")
    private Integer status_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
