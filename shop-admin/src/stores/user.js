import { defineStore } from 'pinia'
import { login as loginApi, getProfile } from '../api/auth'

export const useUserStore = defineStore('user', {
  state: () => {
    let parsedUser = null
    try {
      parsedUser = JSON.parse(localStorage.getItem('user') || 'null')
    } catch (e) {
      localStorage.removeItem('user')
    }
    return {
      token: localStorage.getItem('token') || '',
      userInfo: parsedUser
    }
  },
  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.userInfo?.role || ''
  },
  actions: {
    async login(phone, password) {
      const res = await loginApi(phone, password)
      this.token = res.data.token
      this.userInfo = {
        userId: res.data.userId,
        role: res.data.role
      }
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(this.userInfo))
    },
    async fetchProfile() {
      const res = await getProfile()
      const user = res.data
      this.userInfo = {
        ...this.userInfo,
        name: user.real_name_zj || user.realName || user.phone_zj,
        balance: user.balance_zj,
        phone: user.phone_zj,
        role: user.role_zj
      }
      localStorage.setItem('user', JSON.stringify(this.userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
