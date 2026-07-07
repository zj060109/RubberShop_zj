package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_message_zj")
public class ChatMessage {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("conversation_id_zj")
    private Long conversation_id_zj;

    @TableField("sender_id_zj")
    private Long sender_id_zj;

    @TableField("sender_name_zj")
    private String sender_name_zj;

    @TableField("sender_avatar_zj")
    private String sender_avatar_zj;

    @TableField("sender_role_zj")
    private String sender_role_zj;

    @TableField("content_zj")
    private String content_zj;

    @TableField("message_type_zj")
    private String message_type_zj;

    @TableField("is_read_zj")
    private Integer is_read_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;
}
