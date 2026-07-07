import api from './index'

export function getStockLogs(params) {
  return api.get('/stock/logs', { params })
}

export function adjustStock(data) {
  const params = new URLSearchParams(data)
  return api.post('/stock/adjust?' + params.toString())
}

export function getStockWarnings() {
  return api.get('/stock/warnings')
}
