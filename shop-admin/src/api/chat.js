import api from './index'

export function getConversations() {
  return api.get('/chat/conversations')
}

export function getMessages(conversationId, page = 1, pageSize = 50) {
  return api.get(`/chat/conversations/${conversationId}/messages`, { params: { page, pageSize } })
}

export function sendMessage(data) {
  return api.post('/chat/messages', data)
}

export function markRead(conversationId) {
  return api.put(`/chat/conversations/${conversationId}/read`)
}
