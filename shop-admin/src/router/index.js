import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'DataAnalysis' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('../views/product/ProductList.vue'),
        meta: { title: '商品管理', icon: 'Goods' }
      },
      {
        path: 'products/form/:id?',
        name: 'ProductForm',
        component: () => import('../views/product/ProductForm.vue'),
        meta: { title: '商品编辑', hidden: true }
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('../views/category/CategoryList.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('../views/order/OrderList.vue'),
        meta: { title: '订单管理', icon: 'Document' }
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('../views/order/OrderDetail.vue'),
        meta: { title: '订单详情', hidden: true }
      },
      {
        path: 'customizations',
        name: 'CustomizationList',
        component: () => import('../views/customization/CustomizationList.vue'),
        meta: { title: '定制管理', icon: 'Brush' }
      },
      {
        path: 'customizations/:id',
        name: 'CustomizationDetail',
        component: () => import('../views/customization/CustomizationDetail.vue'),
        meta: { title: '定制详情', hidden: true }
      },
      {
        path: 'purchases',
        name: 'PurchaseList',
        component: () => import('../views/purchase/PurchaseList.vue'),
        meta: { title: '采购管理', icon: 'ShoppingCart' }
      },
      {
        path: 'purchases/form/:id?',
        name: 'PurchaseForm',
        component: () => import('../views/purchase/PurchaseForm.vue'),
        meta: { title: '采购单编辑', hidden: true }
      },
      {
        path: 'purchases/detail/:id',
        name: 'PurchaseDetail',
        component: () => import('../views/purchase/PurchaseDetail.vue'),
        meta: { title: '采购详情', hidden: true }
      },
      {
        path: 'stock/logs',
        name: 'StockLogs',
        component: () => import('../views/stock/StockLogs.vue'),
        meta: { title: '库存流水', icon: 'List' }
      },
      {
        path: 'stock/manual',
        name: 'StockManual',
        component: () => import('../views/stock/StockManual.vue'),
        meta: { title: '手动调整', icon: 'Edit' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('../views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'credit',
        name: 'CreditList',
        component: () => import('../views/credit/CreditList.vue'),
        meta: { title: '赊账管理', icon: 'Money' }
      },
      {
        path: 'chat',
        name: 'ChatView',
        component: () => import('../views/chat/ChatView.vue'),
        meta: { title: '客服聊天', icon: 'ChatDotRound' }
      },
      {
        path: 'system/config',
        name: 'SystemConfig',
        component: () => import('../views/system/SystemConfig.vue'),
        meta: { title: '系统配置', icon: 'Setting' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next(token ? '/dashboard' : undefined)
  } else if (!token) {
    next('/login')
  } else if (to.path === '/') {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
