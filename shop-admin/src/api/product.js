import api from './index'

export function getProductList(params) {
  return api.get('/products', { params })
}

export function getProductDetail(id) {
  return api.get(`/products/${id}`)
}

export function createProduct(data) {
  return api.post('/products', data)
}

export function updateProduct(id, data) {
  return api.put(`/products/${id}`, data)
}

export function deleteProduct(id) {
  return api.delete(`/products/${id}`)
}

export function toggleProductStatus(id, status) {
  return api.put(`/products/${id}/status`, null, { params: { status } })
}
