package com.rubber.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rubber.shop.entity.ChatMessage;

public interface ChatMessageService extends IService<ChatMessage> {

    Page<ChatMessage> getMessages(Long conversationId, int page, int pageSize);
}
