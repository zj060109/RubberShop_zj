import api from './index'

export function login(phone, password) {
  return api.post('/auth/login', { phone, password })
}

export function getProfile() {
  return api.get('/user/profile')
}

export function changePassword(oldPassword, newPassword) {
  return api.put('/auth/password', { oldPassword, newPassword })
}

export function getDashboard() {
  return api.get('/admin/statistics/dashboard')
}
