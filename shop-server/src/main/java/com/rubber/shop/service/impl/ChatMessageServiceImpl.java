package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.ChatMessage;
import com.rubber.shop.mapper.ChatMessageMapper;
import com.rubber.shop.service.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Override
    public Page<ChatMessage> getMessages(Long conversationId, int page, int pageSize) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversation_id_zj, conversationId)
               .orderByDesc(ChatMessage::getCreated_at_zj);
        return this.page(new Page<>(page, pageSize), wrapper);
    }
}
