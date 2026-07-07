import api from './index'

export function getOrderList(params) {
  return api.get('/orders', { params })
}

export function getOrderDetail(id) {
  return api.get(`/orders/${id}`)
}

export function createOrder(data) {
  return api.post('/orders', data)
}

export function updateOrderStatus(id, status) {
  return api.put(`/orders/${id}/status?status=${status}`)
}

export function shipOrder(id, expressCompany, trackingNo) {
  return api.put(`/orders/${id}/ship`, null, { params: { expressCompany, trackingNo } })
}

export function receiveOrder(id) {
  return api.put(`/orders/${id}/receive`)
}

export function cancelOrder(id) {
  return api.post(`/orders/${id}/cancel`)
}

export function customerShip(id, expressCompany, trackingNo) {
  return api.put(`/orders/${id}/customer-ship`, null, { params: { expressCompany, trackingNo } })
}

export function merchantReceive(id) {
  return api.put(`/orders/${id}/merchant-receive`)
}

export function updateInstallation(id, params) {
  return api.put(`/orders/${id}/installation`, null, { params })
}

export function getInstallation(id) {
  return api.get(`/orders/${id}/installation`)
}

export function getMerchantAddress() {
  return api.get('/orders/merchant-address')
}

export function updateMerchantAddress(data) {
  return api.put('/orders/merchant-address', data)
}
