import api from './index'

export function getReceivableList(params) {
  return api.get('/receivables', { params })
}

export function getReceivableDetail(id) {
  return api.get(`/receivables/${id}`)
}

export function repayReceivable(id, data) {
  return api.post(`/receivables/${id}/receipts`, data)
}
