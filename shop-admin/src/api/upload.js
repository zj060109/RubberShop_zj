import api from './index'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/upload', formData)
}
