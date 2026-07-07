<template>
  <div class="login-page">
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
    </div>
    <div class="login-card">
      <div class="card-header">
        <div class="logo-wrap">
          <svg viewBox="0 0 40 40" width="40" height="40" fill="none">
            <rect width="40" height="40" rx="10" fill="url(#login-logo)"/>
            <path d="M10 20h20M15 12v16M25 12v16" stroke="#fff" stroke-width="3" stroke-linecap="round"/>
            <defs><linearGradient id="login-logo" x1="0" y1="0" x2="40" y2="40"><stop offset="0" stop-color="#818cf8"/><stop offset="1" stop-color="#4f46e5"/></linearGradient></defs>
          </svg>
        </div>
        <h1 class="app-title">橡胶进销存管理系统</h1>
        <p class="app-subtitle">Rubber Inventory Management</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="login-form">
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            class="login-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            class="login-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '验证中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="login-footer">
      <span>RubberShop v1.0</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Phone, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form.phone, form.password)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 40%, #312e81 70%, #6366f1 100%);
  position: relative;
  overflow: hidden;
}

.bg-shapes { position: absolute; inset: 0; overflow: hidden; }
.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.25;
  animation: floatShape 12s ease-in-out infinite;
}
.shape-1 { width: 400px; height: 400px; background: #818cf8; top: -100px; left: -100px; animation-delay: 0s; }
.shape-2 { width: 300px; height: 300px; background: #6366f1; bottom: -80px; right: -60px; animation-delay: 3s; }
.shape-3 { width: 250px; height: 250px; background: #a78bfa; top: 40%; right: -80px; animation-delay: 6s; }
.shape-4 { width: 200px; height: 200px; background: #4f46e5; bottom: 30%; left: -60px; animation-delay: 9s; }

@keyframes floatShape {
  0%, 100% { transform: translate(0,0) scale(1); }
  33% { transform: translate(30px,-30px) scale(1.05); }
  66% { transform: translate(-20px,20px) scale(0.95); }
}

.login-card {
  width: 420px;
  padding: 48px 44px;
  background: rgba(255,255,255,0.97);
  border-radius: 20px;
  box-shadow: 0 24px 80px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.08);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20px);
}

.card-header { text-align: center; margin-bottom: 36px; }
.logo-wrap { margin-bottom: 16px; display: inline-flex; }

.app-title {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 6px;
  letter-spacing: 1px;
}

.app-subtitle {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
  font-weight: 400;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.login-form { margin-top: 8px; }

.login-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  height: 48px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.25s ease;
  background: #f8fafc;
}
.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #cbd5e1;
  background: #fff;
}
.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(99,102,241,0.3), 0 0 0 1px #6366f1;
  background: #fff;
}

.login-input :deep(.el-input__prefix) { color: #94a3b8; }
.login-input :deep(.el-input__prefix .el-icon) { font-size: 18px; }
.login-input :deep(.el-input__inner) { font-size: 15px; }

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  border: none;
  margin-top: 8px;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(99,102,241,0.35);
}
.login-btn:not(.is-disabled):hover {
  background: linear-gradient(135deg, #5558e6, #4338ca);
  box-shadow: 0 6px 24px rgba(99,102,241,0.45);
  transform: translateY(-1px);
}
.login-btn:not(.is-disabled):active {
  transform: translateY(0);
}

.login-footer {
  position: absolute;
  bottom: 24px;
  z-index: 1;
  color: rgba(255,255,255,0.35);
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
