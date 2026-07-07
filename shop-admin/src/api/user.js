import api from './index'

export function getUserList(params) {
  return api.get('/admin/users', { params })
}

export function getUserDetail(id) {
  return api.get(`/admin/users/${id}`)
}

export function updateUser(id, data) {
  return api.put(`/admin/users/${id}`, data)
}
