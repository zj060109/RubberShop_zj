<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <span class="brand-icon"><svg width="28" height="28" viewBox="0 0 28 28" fill="none"><rect width="28" height="28" rx="7" fill="#5c6cf0"/><path d="M8 14h12M10 10v8M18 10v8" stroke="#fff" stroke-width="2" stroke-linecap="round"/></svg></span>
        <span class="brand-name">橡胶进销存</span>
      </div>

      <nav class="sidebar-nav">
        <router-link v-for="item in menuItems" :key="item.path" :to="'/' + item.path" class="nav-item" :class="{ active: isActive('/' + item.path) }">
          <el-icon class="nav-icon"><component :is="item.meta.icon" /></el-icon>
          <span class="nav-label">{{ item.meta.title }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-card">
          <div class="user-avatar">{{ (userStore.userInfo?.name || '管').charAt(0) }}</div>
          <div class="user-info">
            <span class="user-name">{{ userStore.userInfo?.name || '管理员' }}</span>
            <span class="user-role">系统管理员</span>
          </div>
          <el-dropdown trigger="click" placement="top-start">
            <span class="user-more"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="3" cy="8" r="1.5" fill="currentColor"/><circle cx="8" cy="8" r="1.5" fill="currentColor"/><circle cx="13" cy="8" r="1.5" fill="currentColor"/></svg></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout"><el-icon><SwitchButton /></el-icon> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </aside>

    <main class="main">
      <header class="topbar">
        <div class="breadcrumb">
          <span class="bc-item">{{ currentTitle }}</span>
        </div>
      </header>
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentTitle = computed(() => {
  const matched = route.matched.filter(r => r.meta?.title)
  return matched.map(r => r.meta.title).join(' · ') || ''
})

const isActive = (path) => route.path === path

const menuItems = (router.options.routes.find(r => r.path === '/')?.children?.filter(r => !r.meta?.hidden)) || []

const handleLogout = () => { userStore.logout(); router.push('/login') }
</script>

<style scoped>
.app-shell { display:flex; height:100vh; background:var(--c-bg); }

.sidebar {
  width:240px; flex-shrink:0;
  background:#fff;
  border-right:1px solid var(--c-border);
  display:flex; flex-direction:column;
}

.sidebar-brand {
  height:56px; display:flex; align-items:center; gap:10px;
  padding:0 20px; border-bottom:1px solid var(--c-border);
}
.brand-icon { display:flex; }
.brand-name { font-size:15px; font-weight:700; color:var(--c-text); letter-spacing:0.5px; }

.sidebar-nav { flex:1; padding:12px 10px; overflow-y:auto; display:flex; flex-direction:column; gap:2px; }

.nav-item {
  display:flex; align-items:center; gap:10px;
  padding:10px 12px; border-radius:var(--radius-sm);
  font-size:13px; font-weight:500; color:var(--c-text-secondary);
  text-decoration:none; transition:all var(--transition);
}
.nav-item:hover { background:#f5f5f5; color:var(--c-text); }
.nav-item.active { background:var(--c-primary-light); color:var(--c-primary); font-weight:600; }
.nav-icon { font-size:18px; flex-shrink:0; }
.nav-label { white-space:nowrap; }

.sidebar-footer { padding:12px 14px; border-top:1px solid var(--c-border); }
.user-card { display:flex; align-items:center; gap:10px; }
.user-avatar {
  width:32px; height:32px; border-radius:8px;
  background:linear-gradient(135deg,#818cf8,#6366f1);
  display:flex; align-items:center; justify-content:center;
  color:#fff; font-weight:700; font-size:13px; flex-shrink:0;
}
.user-info { flex:1; min-width:0; display:flex; flex-direction:column; }
.user-name { font-size:13px; font-weight:600; color:var(--c-text); white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.user-role { font-size:11px; color:var(--c-text-muted); }
.user-more { color:var(--c-text-muted); cursor:pointer; padding:4px; border-radius:4px; }
.user-more:hover { background:#f5f5f5; color:var(--c-text); }

.main { flex:1; display:flex; flex-direction:column; min-width:0; }
.topbar {
  height:56px; flex-shrink:0;
  background:#fff; border-bottom:1px solid var(--c-border);
  display:flex; align-items:center; padding:0 28px;
}
.bc-item { font-size:13px; font-weight:500; color:var(--c-text-secondary); }

.content { flex:1; overflow-y:auto; }

.page-fade-enter-active,.page-fade-leave-active { transition:opacity 0.15s ease,transform 0.15s ease; }
.page-fade-enter-from { opacity:0; transform:translateY(4px); }
.page-fade-leave-to { opacity:0; }
</style>
