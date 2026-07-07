package com.rubber.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rubber.shop.entity.ChatConversation;

public interface ChatConversationService extends IService<ChatConversation> {

    ChatConversation getOrCreate(Long customerId, Long merchantId);
}
