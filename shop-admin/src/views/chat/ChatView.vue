<template>
  <div class="chat-container">
    <div class="conversation-panel">
      <div class="panel-header">客服消息</div>
      <div class="conv-search">
        <el-input v-model="searchText" placeholder="搜索会话..." clearable size="small" />
      </div>
      <div class="conv-list">
        <div
          v-for="conv in filteredConversations"
          :key="conv.id"
          :class="['conv-item', { active: currentConv?.id === conv.id }]"
          @click="selectConversation(conv)"
        >
          <el-avatar :size="40" class="conv-avatar">{{ (conv.customerName || '?')[0] }}</el-avatar>
          <div class="conv-info">
            <div class="conv-top">
              <span class="conv-name">{{ conv.customerName || conv.customerPhone || '未知客户' }}</span>
              <span class="conv-time">{{ formatTime(conv.lastMessageTime) }}</span>
            </div>
            <div class="conv-bottom">
              <span class="conv-preview">{{ conv.lastMessage || '暂无消息' }}</span>
              <el-badge v-if="conv.unreadCount > 0" :value="conv.unreadCount" :max="99" class="conv-badge" />
            </div>
          </div>
        </div>
        <div v-if="conversations.length === 0" class="conv-empty">暂无客户消息</div>
      </div>
    </div>

    <div class="chat-panel">
      <template v-if="currentConv">
        <div class="chat-header">
          <el-avatar :size="36">{{ (currentConv.customerName || '?')[0] }}</el-avatar>
          <div class="chat-header-info">
            <div class="chat-header-name">{{ currentConv.customerName || currentConv.customerPhone || '未知客户' }}</div>
            <div class="chat-header-phone" v-if="currentConv.customerPhone">{{ currentConv.customerPhone }}</div>
          </div>
        </div>

        <div class="chat-messages" ref="msgContainer" @scroll="onMessagesScroll">
          <div v-if="loading" class="msg-loading"><el-icon class="is-loading" :size="20"><Loading /></el-icon></div>
          <div
            v-for="msg in messages"
            :key="msg.id"
            :class="['msg-item', msg.senderRole === 'merchant' ? 'msg-self' : 'msg-other']"
          >
            <div class="msg-bubble">
              <div class="msg-text">{{ msg.content }}</div>
              <div class="msg-time">{{ formatTime(msg.createdAt) }}</div>
            </div>
          </div>
        </div>

        <transition name="el-fade-in">
          <div v-show="isScrolledUp" class="scroll-bottom-btn" @click="scrollToBottom">
            <el-icon :size="18"><ArrowDown /></el-icon>
          </div>
        </transition>

        <div class="chat-input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="3"
            placeholder="输入消息，按 Enter 发送，Shift+Enter 换行"
            resize="none"
            @keydown.enter.exact="handleSend"
          />
          <el-button type="primary" :loading="sending" @click="handleSend" class="send-btn">发送</el-button>
        </div>
      </template>
      <div v-else class="chat-placeholder">
        <el-icon :size="48" color="#cbd5e1"><ChatDotRound /></el-icon>
        <p>选择一个会话开始聊天</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Loading, ArrowDown } from '@element-plus/icons-vue'
import { getConversations, getMessages, sendMessage, markRead } from '../../api/chat'

const conversations = ref([])
const currentConv = ref(null)
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const sending = ref(false)
const msgContainer = ref(null)
const searchText = ref('')
const isScrolledUp = ref(false)
let ws = null
let wsTimer = null

const filteredConversations = computed(() => {
  if (!searchText.value) return conversations.value
  const kw = searchText.value.toLowerCase()
  return conversations.value.filter(c =>
    (c.customerName || '').toLowerCase().includes(kw) ||
    (c.customerPhone || '').toLowerCase().includes(kw) ||
    (c.lastMessage || '').toLowerCase().includes(kw)
  )
})

const loadConversations = async () => {
  try {
    const res = await getConversations()
    const list = (res.data || res || [])
    conversations.value = list.map(c => ({
      ...c,
      lastMessage: c.lastMessage || '',
      lastMessageTime: c.lastMessageTime || null,
      unreadCount: c.unreadCount || 0
    }))
  } catch {}
}

const loadMessages = async () => {
  if (!currentConv.value) return
  loading.value = true
  try {
    const res = await getMessages(currentConv.value.id)
    const data = res.data || {}
    const records = data.records || []
    messages.value = records.reverse()
  } catch {} finally {
    loading.value = false
  }
}

const selectConversation = async (conv) => {
  currentConv.value = conv
  await loadMessages()
  if (conv.unreadCount > 0) {
    try { await markRead(conv.id) } catch {}
    conv.unreadCount = 0
  }
  nextTick(scrollToBottom)
}

const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || !currentConv.value) return
  sending.value = true
  try {
    const res = await sendMessage({ conversationId: currentConv.value.id, content: text })
    const msg = res.data
    messages.value.push(msg)
    inputText.value = ''
    currentConv.value.lastMessage = text
    currentConv.value.lastMessageTime = msg.createdAt
    nextTick(scrollToBottom)
  } catch {} finally {
    sending.value = false
  }
}

const scrollToBottom = () => {
  const el = msgContainer.value
  if (el) {
    el.scrollTop = el.scrollHeight
    isScrolledUp.value = false
  }
}

const onMessagesScroll = () => {
  const el = msgContainer.value
  if (!el) return
  isScrolledUp.value = el.scrollHeight - el.scrollTop - el.clientHeight > 80
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = n => String(n).padStart(2, '0')
  const time = `${pad(d.getHours())}:${pad(d.getMinutes())}`
  if (d.toDateString() === now.toDateString()) return time
  return `${pad(d.getMonth() + 1)}/${pad(d.getDate())} ${time}`
}

const onWsMessage = (event) => {
  try {
    const data = JSON.parse(event.data)
    if (data.type === 'new_message' && data.data) {
      const msg = data.data
      const convId = data.conversationId
      const conv = conversations.value.find(c => c.id === convId)
      if (conv) {
        conv.lastMessage = msg.content
        conv.lastMessageTime = msg.createdAt
        if (currentConv.value?.id !== convId) {
          conv.unreadCount = (conv.unreadCount || 0) + 1
        }
      }
      if (currentConv.value?.id === convId) {
        messages.value.push(msg)
        if (conv) {
          try { markRead(convId) } catch {}
          conv.unreadCount = 0
        }
        nextTick(scrollToBottom)
      } else {
        conversations.value = [...conversations.value]
      }
    }
  } catch {}
}

const connectWs = () => {
  const token = localStorage.getItem('token')
  if (!token) return
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  const url = `${protocol}//${location.host}/ws/chat?token=${encodeURIComponent(token)}`
  try {
    ws = new WebSocket(url)
    ws.onmessage = onWsMessage
    ws.onclose = () => {
      wsTimer = setTimeout(connectWs, 5000)
    }
    ws.onerror = () => {
      ws?.close()
    }
  } catch {
    wsTimer = setTimeout(connectWs, 5000)
  }
}

const disconnectWs = () => {
  clearTimeout(wsTimer)
  if (ws) {
    ws.onclose = null
    ws.close()
    ws = null
  }
}

onMounted(() => {
  loadConversations()
  connectWs()
})

onUnmounted(() => {
  disconnectWs()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 116px);
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  margin: 24px;
}

.conversation-panel {
  width: 320px;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.panel-header {
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.conv-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.conv-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
}

.conv-item:hover {
  background: #f1f5f9;
}

.conv-item.active {
  background: #eef2ff;
}

.conv-avatar {
  background: linear-gradient(135deg, #818cf8, #6366f1);
  color: #fff;
  flex-shrink: 0;
}

.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conv-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-time {
  font-size: 11px;
  color: #94a3b8;
  flex-shrink: 0;
}

.conv-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.conv-preview {
  font-size: 12px;
  color: #64748b;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-badge {
  flex-shrink: 0;
}

.conv-empty {
  text-align: center;
  padding: 40px 20px;
  color: #94a3b8;
  font-size: 14px;
}

.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.chat-header-info {
  display: flex;
  flex-direction: column;
}

.chat-header-name {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.chat-header-phone {
  font-size: 12px;
  color: #94a3b8;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
}

.msg-loading {
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  padding: 20px;
}

.msg-item {
  display: flex;
  max-width: 70%;
}

.msg-other {
  align-self: flex-start;
}

.msg-self {
  align-self: flex-end;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  position: relative;
}

.msg-other .msg-bubble {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-bottom-left-radius: 4px;
}

.msg-self .msg-bubble {
  background: linear-gradient(135deg, #818cf8, #6366f1);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.msg-text {
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.msg-time {
  font-size: 10px;
  margin-top: 4px;
  opacity: 0.7;
  text-align: right;
}

.chat-input-area {
  padding: 12px 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 10px;
  align-items: flex-end;
  flex-shrink: 0;
}

.chat-input-area :deep(.el-textarea__inner) {
  border-radius: 10px;
  font-size: 14px;
}

.send-btn {
  flex-shrink: 0;
  background: linear-gradient(135deg, #818cf8, #6366f1);
  border: none;
  border-radius: 10px;
  height: 40px;
}

.chat-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  gap: 12px;
}

.chat-placeholder p {
  font-size: 14px;
}

.conv-search {
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.scroll-bottom-btn {
  position: absolute;
  bottom: 12px;
  right: 24px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  color: #6366f1;
  transition: all 0.2s;
}

.scroll-bottom-btn:hover {
  background: #eef2ff;
  border-color: #6366f1;
}
</style>
