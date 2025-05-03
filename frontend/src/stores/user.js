import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {

  // JWT token
  const token = ref(localStorage.getItem('token') || null)
  // 是否登入
  const isLoggedIn = computed(() => !!token.value)

  // 將 JWT token 存進localStorage
  function setUser({ token: newToken }) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 登出
  function logout() {
    token.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    isLoggedIn,
    setUser,
    logout
  }
})
