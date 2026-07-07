import api from './index'

export function getPurchaseList(params) {
  return api.get('/purchases', { params })
}

export function getPurchaseDetail(id) {
  return api.get(`/purchases/${id}`)
}

export function createPurchase(data) {
  return api.post('/purchases', data)
}

export function updatePurchase(id, data) {
  return api.put(`/purchases/${id}`, data)
}

export function quotePurchase(id, data) {
  return api.put(`/purchases/${id}/quote`, data)
}

export function payPurchase(id) {
  return api.put(`/purchases/${id}/pay`)
}

export function shipPurchase(id, expressCompany, trackingNo) {
  return api.put(`/purchases/${id}/logistics`, null, { params: { expressCompany, trackingNo } })
}

export function receivePurchase(id) {
  return api.put(`/purchases/${id}/received`)
}

export function cancelPurchase(id) {
  return api.put(`/purchases/${id}/cancel`)
}
