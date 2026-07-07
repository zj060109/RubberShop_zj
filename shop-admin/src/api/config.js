import api from './index'

export function getConfigs() {
  return api.get('/configs')
}

export function updateConfig(key, value, remark) {
  return api.put(`/configs/${key}`, { value, remark })
}
