package com.rubber.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubber.shop.common.AuthUtils;
import com.rubber.shop.common.BusinessException;
import com.rubber.shop.common.Result;
import com.rubber.shop.dto.ChatMessageRequest;
import com.rubber.shop.entity.ChatConversation;
import com.rubber.shop.entity.ChatMessage;
import com.rubber.shop.entity.User;
import com.rubber.shop.service.ChatConversationService;
import com.rubber.shop.service.ChatMessageService;
import com.rubber.shop.service.UserService;
import com.rubber.shop.websocket.ChatWebSocketHandler;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatConversationService conversationService;
    private final ChatMessageService messageService;
    private final UserService userService;
    private final ChatWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public ChatController(ChatConversationService conversationService,
                          ChatMessageService messageService,
                          UserService userService,
                          ChatWebSocketHandler webSocketHandler,
                          ObjectMapper objectMapper) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.userService = userService;
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> conversations() {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        List<ChatConversation> list;
        if ("merchant".equals(user.getRole_zj())) {
            LambdaQueryWrapper<ChatConversation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ChatConversation::getMerchant_id_zj, userId)
                   .eq(ChatConversation::getStatus_zj, "active")
                   .orderByDesc(ChatConversation::getLast_message_time_zj);
            list = conversationService.list(wrapper);
        } else {
            LambdaQueryWrapper<ChatConversation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ChatConversation::getCustomer_id_zj, userId)
                   .eq(ChatConversation::getStatus_zj, "active")
                   .orderByDesc(ChatConversation::getLast_message_time_zj);
            list = conversationService.list(wrapper);
            if (list.isEmpty()) {
                ChatConversation conv = conversationService.getOrCreate(userId, null);
                list = List.of(conv);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatConversation conv : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", conv.getId_zj());
            map.put("customerId", conv.getCustomer_id_zj());
            map.put("merchantId", conv.getMerchant_id_zj());
            map.put("customerName", conv.getCustomer_name_zj());
            map.put("customerPhone", conv.getCustomer_phone_zj());
            map.put("customerAvatar", conv.getCustomer_avatar_zj());
            map.put("lastMessage", conv.getLast_message_zj());
            map.put("lastMessageTime", conv.getLast_message_time_zj());
            map.put("unreadCount", "merchant".equals(user.getRole_zj())
                    ? conv.getUnread_merchant_zj() : conv.getUnread_customer_zj());
            map.put("status", conv.getStatus_zj());
            result.add(map);
        }
        return Result.success(result);
    }

    @GetMapping("/conversations/{id}/messages")
    public Result<Map<String, Object>> messages(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "50") int pageSize) {
        Long userId = getCurrentUserId();
        ChatConversation conv = conversationService.getById(id);
        if (conv == null) throw new BusinessException("会话不存在");

        User user = userService.getById(userId);
        boolean isMerchant = user != null && "merchant".equals(user.getRole_zj());
        if (isMerchant && !conv.getMerchant_id_zj().equals(userId)) {
            throw new BusinessException("无权访问该会话");
        }
        if (!isMerchant && !conv.getCustomer_id_zj().equals(userId)) {
            throw new BusinessException("无权访问该会话");
        }

        Page<ChatMessage> msgPage = messageService.getMessages(id, page, pageSize);
        List<Map<String, Object>> records = new ArrayList<>();
        for (ChatMessage msg : msgPage.getRecords()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", msg.getId_zj());
            m.put("conversationId", msg.getConversation_id_zj());
            m.put("senderId", msg.getSender_id_zj());
            m.put("senderName", msg.getSender_name_zj());
            m.put("senderAvatar", msg.getSender_avatar_zj());
            m.put("senderRole", msg.getSender_role_zj());
            m.put("content", msg.getContent_zj());
            m.put("messageType", msg.getMessage_type_zj());
            m.put("isRead", msg.getIs_read_zj());
            m.put("createdAt", msg.getCreated_at_zj());
            records.add(m);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", msgPage.getTotal());
        result.put("page", msgPage.getCurrent());
        result.put("pageSize", msgPage.getSize());

        Map<String, Object> convMap = new LinkedHashMap<>();
        convMap.put("id", conv.getId_zj());
        convMap.put("customerId", conv.getCustomer_id_zj());
        convMap.put("merchantId", conv.getMerchant_id_zj());
        convMap.put("customerName", conv.getCustomer_name_zj());
        convMap.put("customerPhone", conv.getCustomer_phone_zj());
        convMap.put("status", conv.getStatus_zj());
        result.put("conversation", convMap);

        return Result.success(result);
    }

    @PostMapping("/messages")
    @Transactional
    public Result<Map<String, Object>> sendMessage(@RequestBody @Valid ChatMessageRequest req) {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        if (req.getContent() == null || req.getContent().trim().isEmpty()) {
            throw new BusinessException("消息内容不能为空");
        }

        ChatConversation conv;
        if (req.getConversationId() != null) {
            conv = conversationService.getById(req.getConversationId());
            if (conv == null) throw new BusinessException("会话不存在");
        } else {
            conv = conversationService.getOrCreate(userId, null);
        }

        String senderRole = user.getRole_zj();
        if ("merchant".equals(senderRole) && !conv.getMerchant_id_zj().equals(userId)) {
            throw new BusinessException("无权在该会话发消息");
        }
        if (!"merchant".equals(senderRole) && !conv.getCustomer_id_zj().equals(userId)) {
            throw new BusinessException("无权在该会话发消息");
        }

        Long receiverId;
        if ("merchant".equals(senderRole)) {
            receiverId = conv.getCustomer_id_zj();
        } else {
            receiverId = conv.getMerchant_id_zj();
        }

        ChatMessage msg = new ChatMessage();
        msg.setConversation_id_zj(conv.getId_zj());
        msg.setSender_id_zj(userId);
        msg.setSender_name_zj(user.getReal_name_zj() != null ? user.getReal_name_zj()
                : ("merchant".equals(senderRole) ? user.getCompany_name_zj() : user.getPhone_zj()));
        msg.setSender_avatar_zj(user.getAvatar_zj());
        msg.setSender_role_zj(senderRole);
        msg.setContent_zj(req.getContent());
        msg.setMessage_type_zj(req.getMessageType() != null ? req.getMessageType() : "text");
        msg.setIs_read_zj(0);
        msg.setCreated_at_zj(LocalDateTime.now());
        messageService.save(msg);

        LambdaUpdateWrapper<ChatConversation> convUpdate = new LambdaUpdateWrapper<>();
        convUpdate.eq(ChatConversation::getId_zj, conv.getId_zj());
        convUpdate.set(ChatConversation::getLast_message_zj,
                req.getContent().length() > 100 ? req.getContent().substring(0, 100) : req.getContent());
        convUpdate.set(ChatConversation::getLast_message_time_zj, msg.getCreated_at_zj());
        if (conv.getCustomer_name_zj() == null && !"merchant".equals(senderRole)) {
            convUpdate.set(ChatConversation::getCustomer_name_zj, user.getReal_name_zj());
            convUpdate.set(ChatConversation::getCustomer_phone_zj, user.getPhone_zj());
            convUpdate.set(ChatConversation::getCustomer_avatar_zj, user.getAvatar_zj());
        }
        if ("merchant".equals(senderRole)) {
            convUpdate.setSql("unread_customer_zj = unread_customer_zj + 1");
        } else {
            convUpdate.setSql("unread_merchant_zj = unread_merchant_zj + 1");
        }
        conversationService.update(convUpdate);

        Map<String, Object> pushMsg = new LinkedHashMap<>();
        pushMsg.put("type", "new_message");
        pushMsg.put("data", buildMessageMap(msg));
        pushMsg.put("conversationId", conv.getId_zj());

        try {
            String json = objectMapper.writeValueAsString(pushMsg);
            webSocketHandler.sendToUser(receiverId, json);
        } catch (Exception e) {
            System.err.println("WebSocket push failed: " + e.getMessage());
        }

        return Result.success(buildMessageMap(msg));
    }

    @Transactional
    @PutMapping("/conversations/{id}/read")
    public Result<?> markRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        ChatConversation conv = conversationService.getById(id);
        if (conv == null) throw new BusinessException("会话不存在");

        boolean isMerchant = "merchant".equals(user.getRole_zj());
        if (isMerchant && !conv.getMerchant_id_zj().equals(userId)) {
            throw new BusinessException("无权操作该会话");
        }
        if (!isMerchant && !conv.getCustomer_id_zj().equals(userId)) {
            throw new BusinessException("无权操作该会话");
        }

        LambdaUpdateWrapper<ChatConversation> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatConversation::getId_zj, id);
        if (isMerchant) {
            wrapper.set(ChatConversation::getUnread_merchant_zj, 0);
        } else {
            wrapper.set(ChatConversation::getUnread_customer_zj, 0);
        }
        conversationService.update(wrapper);

        LambdaUpdateWrapper<ChatMessage> msgWrapper = new LambdaUpdateWrapper<>();
        msgWrapper.eq(ChatMessage::getConversation_id_zj, id)
                  .ne(ChatMessage::getSender_id_zj, userId)
                  .set(ChatMessage::getIs_read_zj, 1);
        messageService.update(msgWrapper);

        return Result.success("ok", null);
    }

    private Long getCurrentUserId() {
        return AuthUtils.getCurrentUserId();
    }

    private Map<String, Object> buildMessageMap(ChatMessage msg) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", msg.getId_zj());
        m.put("conversationId", msg.getConversation_id_zj());
        m.put("senderId", msg.getSender_id_zj());
        m.put("senderName", msg.getSender_name_zj());
        m.put("senderAvatar", msg.getSender_avatar_zj());
        m.put("senderRole", msg.getSender_role_zj());
        m.put("content", msg.getContent_zj());
        m.put("messageType", msg.getMessage_type_zj());
        m.put("isRead", msg.getIs_read_zj());
        m.put("createdAt", msg.getCreated_at_zj() != null ? msg.getCreated_at_zj().toString() : null);
        return m;
    }
}
