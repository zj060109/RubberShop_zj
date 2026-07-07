package com.rubber.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_conversation_zj")
public class ChatConversation {

    @TableId(value = "id_zj", type = IdType.AUTO)
    private Long id_zj;

    @TableField("customer_id_zj")
    private Long customer_id_zj;

    @TableField("merchant_id_zj")
    private Long merchant_id_zj;

    @TableField("customer_name_zj")
    private String customer_name_zj;

    @TableField("customer_phone_zj")
    private String customer_phone_zj;

    @TableField("customer_avatar_zj")
    private String customer_avatar_zj;

    @TableField("last_message_zj")
    private String last_message_zj;

    @TableField("last_message_time_zj")
    private LocalDateTime last_message_time_zj;

    @TableField("unread_merchant_zj")
    private Integer unread_merchant_zj;

    @TableField("unread_customer_zj")
    private Integer unread_customer_zj;

    @TableField("status_zj")
    private String status_zj;

    @TableField("created_at_zj")
    private LocalDateTime created_at_zj;

    @TableField("updated_at_zj")
    private LocalDateTime updated_at_zj;
}
