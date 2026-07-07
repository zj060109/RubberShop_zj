package com.rubber.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rubber.shop.entity.ChatConversation;
import com.rubber.shop.mapper.ChatConversationMapper;
import com.rubber.shop.service.ChatConversationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversation> implements ChatConversationService {

    @Override
    public ChatConversation getOrCreate(Long customerId, Long merchantId) {
        LambdaQueryWrapper<ChatConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatConversation::getCustomer_id_zj, customerId);
        ChatConversation conv = this.getOne(wrapper);
        if (conv == null) {
            conv = new ChatConversation();
            conv.setCustomer_id_zj(customerId);
            conv.setMerchant_id_zj(merchantId != null ? merchantId : 1L);
            conv.setStatus_zj("active");
            conv.setUnread_merchant_zj(0);
            conv.setUnread_customer_zj(0);
            conv.setCreated_at_zj(LocalDateTime.now());
            conv.setUpdated_at_zj(LocalDateTime.now());
            this.save(conv);
        }
        return conv;
    }
}
