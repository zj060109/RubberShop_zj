<template>
  <el-container class="layout-wrap">
    <el-aside width="240px" class="sidebar">
      <div class="logo-area">
        <div class="logo-icon">
          <svg viewBox="0 0 32 32" width="28" height="28" fill="none">
            <rect width="32" height="32" rx="8" fill="url(#logo-grad)"/>
            <path d="M8 16h16M12 10v12M20 10v12" stroke="#fff" stroke-width="2.5" stroke-linecap="round"/>
            <defs><linearGradient id="logo-grad" x1="0" y1="0" x2="32" y2="32"><stop offset="0" stop-color="#818cf8"/><stop offset="1" stop-color="#4f46e5"/></linearGradient></defs>
          </svg>
        </div>
        <span class="logo-text">橡胶进销存</span>
      </div>
      <div class="nav-scroll">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="transparent"
          text-color="rgba(255,255,255,0.55)"
          active-text-color="#ffffff"
          class="side-menu"
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.path"
            :index="'/' + item.path"
          >
            <el-icon class="menu-icon"><component :is="item.meta.icon" /></el-icon>
            <span class="menu-label">{{ item.meta.title }}</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-aside>
    <el-container class="main-area">
      <el-header class="top-header">
        <div class="header-left">
          <span class="breadcrumb-label">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-trigger">
              <el-avatar :size="32" class="user-avatar">{{ (userStore.userInfo?.name || '管').charAt(0) }}</el-avatar>
              <span class="user-name">{{ userStore.userInfo?.name || '管理员' }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleChangePassword">
                  <el-icon><Key /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { ArrowDown, Key, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const matched = route.matched.filter(r => r.meta?.title)
  return matched.map(r => r.meta.title).join(' / ') || ''
})

const menuItems = (router.options.routes
  .find(r => r.path === '/')
  ?.children
  ?.filter(r => !r.meta?.hidden)) || []

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const handleChangePassword = () => {
  ElMessage.info('修改密码功能：请在个人中心操作')
}
</script>

<style scoped>
.layout-wrap { height: 100vh; overflow: hidden; }

.sidebar {
  background: linear-gradient(180deg, #0f172a 0%, #1e1b4b 100%);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 20px rgba(0,0,0,0.3);
  position: relative;
  z-index: 2;
  overflow: hidden;
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
  background: rgba(0,0,0,0.15);
}

.logo-icon { display: flex; align-items: center; flex-shrink: 0; }

.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
  user-select: none;
}

.nav-scroll { flex: 1; overflow-y: auto; overflow-x: hidden; }

.side-menu {
  border-right: none;
  padding: 12px 0;
  background: transparent !important;
}

.side-menu :deep(.el-menu-item) {
  margin: 2px 10px;
  border-radius: 10px;
  height: 44px;
  line-height: 44px;
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1);
  font-size: 14px;
  position: relative;
  overflow: hidden;
}

.side-menu :deep(.el-menu-item::before) {
  content: '';
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 3px;
  background: #818cf8;
  border-radius: 0 2px 2px 0;
  transform: scaleY(0);
  transition: transform 0.25s ease;
}

.side-menu :deep(.el-menu-item:hover) {
  background-color: rgba(99,102,241,0.15);
  color: #e0e5f0;
}

.side-menu :deep(.el-menu-item:hover::before) {
  transform: scaleY(1);
}

.side-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(99,102,241,0.35), rgba(99,102,241,0.15));
  color: #fff;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(99,102,241,0.25);
}

.side-menu :deep(.el-menu-item.is-active::before) {
  transform: scaleY(1);
  background: #a5b4fc;
}

.menu-icon { font-size: 18px; margin-right: 8px; }
.menu-label { letter-spacing: 0.02em; }

.main-area { background: #f8fafc; min-width: 0; }

.top-header {
  background: #fff;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 0 rgba(0,0,0,0.04), 0 2px 8px rgba(0,0,0,0.03);
  position: relative;
  z-index: 1;
}

.header-left { display: flex; align-items: center; }
.breadcrumb-label { font-size: 14px; font-weight: 500; color: var(--text); }

.header-right { display: flex; align-items: center; gap: 12px; }
.notice-badge { cursor: pointer; }

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 10px;
  transition: all var(--transition);
}
.user-trigger:hover { background: #f1f5f9; }

.user-avatar {
  background: linear-gradient(135deg, #818cf8, #6366f1);
  color: #fff;
  font-weight: 700;
  font-size: 14px;
}
.user-name { color: var(--text-secondary); font-size: 14px; font-weight: 500; }
.arrow-icon { font-size: 12px; color: var(--text-muted); transition: transform var(--transition); }
.el-dropdown:hover .arrow-icon { transform: rotate(180deg); }

.main-content {
  background: #f8fafc;
  padding: 0;
  min-height: 0;
  overflow-y: auto;
}

.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.25s cubic-bezier(0.4,0,0.2,1);
}
.fade-slide-enter-from { opacity: 0; transform: translateY(8px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-8px); }
</style>
