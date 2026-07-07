<template>
  <div class="page-container dashboard-page">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.key">
        <div class="stat-card" :class="'stat-' + card.key">
          <div class="stat-inner">
            <div class="stat-icon" :style="{ background: card.bg, color: card.color }">
              <el-icon :size="22"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-body">
              <span class="stat-label">{{ card.label }}</span>
              <span class="stat-value">
                <span v-if="loading" class="stat-skeleton"></span>
                <template v-else>{{ card.value }}</template>
              </span>
            </div>
          </div>
          <div class="stat-glow" :style="{ background: card.color }"></div>
        </div>
      </el-col>
    </el-row>

    <div class="section-heading">数据概览</div>
    <el-row :gutter="20" class="chart-row">
      <el-col :span="15">
        <div class="chart-card">
          <div class="card-header-row">
            <span class="card-title">销售趋势</span>
            <span class="card-sub">最近7天</span>
          </div>
          <div class="chart-box" v-loading="chartLoading">
            <div ref="saleChartRef" class="chart-box-inner"></div>
          </div>
        </div>
      </el-col>
      <el-col :span="9">
        <div class="chart-card">
          <div class="card-header-row">
            <span class="card-title">快捷操作</span>
          </div>
          <div class="action-grid">
            <div
              v-for="a in quickActions"
              :key="a.route"
              class="action-card"
              @click="$router.push(a.route)"
            >
              <div class="action-icon" :style="{ background: a.bg, color: a.color }">
                <el-icon :size="24"><component :is="a.icon" /></el-icon>
              </div>
              <span class="action-label">{{ a.label }}</span>
              <span class="action-desc">{{ a.desc }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { Coin, List, Clock, Warning, Document, Goods, ShoppingCartFull, Box, User } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard } from '../api/auth'

const saleChartRef = ref(null)
const loading = ref(true)
const chartLoading = ref(true)
const dashboardData = ref({})
let saleChart = null

const cards = reactive([
  { key: 'totalSales', label: '总销售额', value: '--', icon: Coin, color: '#6366f1', bg: '#eef2ff' },
  { key: 'totalOrders', label: '总订单数', value: '--', icon: List, color: '#10b981', bg: '#ecfdf5' },
  { key: 'todayOrders', label: '今日订单', value: '--', icon: Clock, color: '#f59e0b', bg: '#fffbeb' },
  { key: 'warningCount', label: '库存预警', value: '--', icon: Warning, color: '#ef4444', bg: '#fef2f2' }
])

const quickActions = reactive([
  { label: '订单管理', desc: '处理顾客订单', icon: Document, route: '/orders', color: '#6366f1', bg: '#eef2ff' },
  { label: '商品管理', desc: '管理商品信息', icon: Goods, route: '/products', color: '#10b981', bg: '#ecfdf5' },
  { label: '采购管理', desc: '向厂家下单采购', icon: ShoppingCartFull, route: '/purchases', color: '#f59e0b', bg: '#fffbeb' },
  { label: '库存管理', desc: '查看库存流水', icon: Box, route: '/stock/logs', color: '#3b82f6', bg: '#dbeafe' },
  { label: '用户管理', desc: '管理客户与厂家', icon: User, route: '/users', color: '#8b5cf6', bg: '#ede9fe' }
])

const loadDashboard = async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    const d = res.data
    dashboardData.value = d
    cards[0].value = d.totalSales != null ? '¥' + Number(d.totalSales).toFixed(2) : '¥0'
    cards[1].value = d.totalOrders ?? 0
    cards[2].value = d.todayOrders ?? 0
    cards[3].value = d.warningCount ?? 0
    if (saleChart) updateChart()
  } catch {
    cards.forEach(c => c.value = '--')
  } finally {
    loading.value = false
  }
}

const initSaleChart = () => {
  if (!saleChartRef.value) return
  const now = new Date()
  const days = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    days.push((d.getMonth() + 1) + '月' + d.getDate() + '日')
  }
  const data = new Array(7).fill(0)
  const salesData = dashboardData.value.last7DaysSales
  if (salesData) {
    for (let i = 0; i < Math.min(7, salesData.length); i++) {
      data[i] = Number(salesData[i]) || 0
    }
  }
  saleChart = echarts.init(saleChartRef.value)
  chartLoading.value = false
  saleChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      borderColor: '#e2e8f0',
      textStyle: { color: '#1e293b', fontSize: 13 },
      boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
      padding: [12, 16],
      extraCssText: 'border-radius:8px'
    },
    grid: { left: '2%', right: '3%', bottom: '2%', top: '16px', containLabel: true },
    xAxis: {
      type: 'category', boundaryGap: false, data: days,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#94a3b8', fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } },
      axisLabel: { color: '#94a3b8', fontSize: 12 }
    },
    series: [{
      name: '销售额', type: 'line', data,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      itemStyle: { color: '#6366f1' },
      lineStyle: { color: '#6366f1', width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(99,102,241,0.3)' },
          { offset: 0.5, color: 'rgba(99,102,241,0.08)' },
          { offset: 1, color: 'rgba(99,102,241,0.0)' }
        ])
      }
    }]
  })
}

const updateChart = () => {
  if (!saleChart) return
  const salesData = dashboardData.value.last7DaysSales
  if (!salesData) return
  const data = []
  for (let i = 0; i < 7; i++) {
    data.push(Number(salesData[i]) || 0)
  }
  saleChart.setOption({ series: [{ data }] })
}

const handleResize = () => { saleChart?.resize() }

onMounted(async () => {
  await loadDashboard()
  nextTick(() => { setTimeout(initSaleChart, 150) })
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  saleChart?.dispose()
})
</script>

<style scoped>
.dashboard-page { padding: 24px 28px; }

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 24px 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  position: relative;
  overflow: hidden;
  cursor: default;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.04), 0 12px 28px rgba(0,0,0,0.08);
}
.stat-glow {
  position: absolute;
  top: -30px; right: -30px;
  width: 80px; height: 80px;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.12;
  pointer-events: none;
  transition: opacity 0.3s;
}
.stat-card:hover .stat-glow { opacity: 0.2; }

.stat-inner {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.stat-icon {
  width: 48px; height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-body { display: flex; flex-direction: column; min-width: 0; }
.stat-label { font-size: 13px; color: #94a3b8; font-weight: 500; white-space: nowrap; }
.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.3;
  letter-spacing: -0.5px;
}

.stat-skeleton {
  display: inline-block;
  width: 80px; height: 28px;
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
  border-radius: 6px;
  margin-top: 2px;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.chart-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  padding: 24px;
  border: 1px solid #e2e8f0;
  height: 380px;
  display: flex;
  flex-direction: column;
}

.section-heading {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 24px 0 12px;
}

.chart-row {
  margin-top: 0;
}

.chart-box-inner {
  width: 100%;
  height: 100%;
}

.card-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  flex-shrink: 0;
}
.card-title { font-size: 15px; font-weight: 700; color: #1e293b; }
.card-sub { font-size: 12px; color: #94a3b8; }

.chart-box { flex: 1; min-height: 0; }

.action-grid { display: flex; flex-direction: column; gap: 8px; flex: 1; }
.action-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}
.action-card:hover {
  background: #fff;
  border-color: #e0e7ff;
  box-shadow: 0 2px 8px rgba(99,102,241,0.08);
  transform: translateX(4px);
}

.action-icon {
  width: 44px; height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-label { font-size: 14px; font-weight: 600; color: #334155; }
.action-desc { font-size: 12px; color: #94a3b8; margin-left: auto; }
</style>
