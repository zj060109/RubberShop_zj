import api from './index'

export function getCustomizationList(params) {
  return api.get('/customizations', { params })
}

export function getCustomizationDetail(id) {
  return api.get(`/customizations/${id}`)
}

export function createCustomization(data) {
  return api.post('/customizations', data)
}

export function quoteCustomization(id, data) {
  return api.put(`/customizations/${id}/quote`, data)
}

export function confirmCustomization(id, data) {
  return api.put(`/customizations/${id}/confirm`, data)
}

export function convertToProduct(id, categoryId) {
  return api.post(`/customizations/${id}/convert-to-product?categoryId=${categoryId}`)
}

export function cancelCustomization(id) {
  return api.post(`/customizations/${id}/cancel`)
}
