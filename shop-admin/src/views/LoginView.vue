<template>
  <div class="login-page">
    <div class="login-left">
      <div class="branding">
        <div class="logo-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none"><rect width="48" height="48" rx="14" fill="rgba(255,255,255,0.15)"/><path d="M14 24h20M18 17v14M30 17v14" stroke="#fff" stroke-width="3" stroke-linecap="round"/></svg>
        </div>
        <h1>橡胶进销存</h1>
        <p>专业橡胶密封件 · 油封 · 密封圈<br>一站式进销存管理平台</p>
      </div>
      <div class="features">
        <div class="feat"><span class="feat-dot"></span>商品管理 · 品牌/型号/材质</div>
        <div class="feat"><span class="feat-dot"></span>订单追踪 · 物流实时更新</div>
        <div class="feat"><span class="feat-dot"></span>库存预警 · 智能补货提醒</div>
      </div>
      <div class="decor-circle c1"></div>
      <div class="decor-circle c2"></div>
    </div>

    <div class="login-right">
      <div class="form-wrapper">
        <div class="form-header">
          <span class="greeting">欢迎回来 👋</span>
          <span class="hint">登录管理您的店铺</span>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号" :prefix-icon="Phone" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" class="submit-btn" @click="handleLogin">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span class="version">RubberShop v2.0</span>
        </div>
      </div>
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
const form = reactive({ phone: '', password: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try { await userStore.login(form.phone, form.password); ElMessage.success('登录成功'); router.push('/dashboard') }
  catch {} finally { loading.value = false }
}
</script>

<style scoped>
.login-page { display:flex; height:100vh; }

.login-left {
  flex:0 0 480px; background:linear-gradient(155deg,#312e81 0%,#4f46e5 40%,#6366f1 70%,#818cf8 100%);
  display:flex; flex-direction:column; justify-content:space-between; padding:60px 56px;
  position:relative; overflow:hidden; color:#fff;
}
.branding { position:relative; z-index:1; }
.logo-icon { margin-bottom:28px; }
.branding h1 { font-size:32px; font-weight:800; letter-spacing:1px; margin-bottom:16px; }
.branding p { font-size:14px; color:rgba(255,255,255,0.65); line-height:1.8; }
.features { position:relative; z-index:1; display:flex; flex-direction:column; gap:12px; }
.feat { font-size:13px; color:rgba(255,255,255,0.55); display:flex; align-items:center; gap:10px; }
.feat-dot { width:6px; height:6px; border-radius:50%; background:rgba(255,255,255,0.3); flex-shrink:0; }
.decor-circle { position:absolute; border-radius:50%; filter:blur(80px); opacity:0.15; }
.c1 { width:350px; height:350px; background:#818cf8; top:-80px; right:-80px; }
.c2 { width:250px; height:250px; background:#6366f1; bottom:-60px; left:-60px; }

.login-right {
  flex:1; display:flex; align-items:center; justify-content:center;
  background:var(--c-bg);
}
.form-wrapper { width:400px; }
.form-header { margin-bottom:36px; }
.greeting { display:block; font-size:24px; font-weight:700; color:var(--c-text); }
.hint { display:block; font-size:14px; color:var(--c-text-muted); margin-top:6px; }
.submit-btn { width:100%; height:48px; border-radius:var(--radius); font-size:15px; font-weight:600; letter-spacing:4px; }
.submit-btn :deep(.el-icon) { margin-right:6px; }
.form-footer { text-align:center; margin-top:40px; }
.version { font-size:12px; color:var(--c-text-muted); letter-spacing:1px; }
</style>
