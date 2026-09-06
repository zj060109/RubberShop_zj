<template>
  <div class="page">
    <div class="welcome-row">
      <div>
        <h2 class="page-title">数据概览</h2>
        <p class="page-sub">今日店铺运营数据总览</p>
      </div>
    </div>

    <div class="stat-grid">
      <div v-for="card in cards" :key="card.key" class="stat-card">
        <div class="stat-top">
          <div class="stat-icon" :style="{background:card.bg,color:card.color}">
            <el-icon :size="20"><component :is="card.icon" /></el-icon>
          </div>
          <span class="stat-trend" v-if="card.trend" :class="card.trendDir">↑ {{ card.trend }}%</span>
        </div>
        <div class="stat-value">
          <span v-if="loading" class="skeleton">--</span>
          <template v-else>{{ card.value }}</template>
        </div>
        <div class="stat-label">{{ card.label }}</div>
        <div class="stat-bar" :style="{background:card.color}"></div>
      </div>
    </div>

    <div class="section-row">
      <div class="section-card chart-card">
        <div class="sc-header">
          <span class="sc-title">销售趋势</span>
          <span class="sc-tag">近 7 天</span>
        </div>
        <div ref="chartEl" class="chart-area" v-loading="chartLoading"></div>
      </div>

      <div class="section-card actions-card">
        <div class="sc-header">
          <span class="sc-title">快捷入口</span>
        </div>
        <div class="action-list">
          <div v-for="a in quickActions" :key="a.route" class="action-item" @click="$router.push(a.route)">
            <div class="action-icon" :style="{background:a.bg,color:a.color}">
              <el-icon :size="20"><component :is="a.icon" /></el-icon>
            </div>
            <div class="action-info">
              <span class="action-label">{{ a.label }}</span>
              <span class="action-desc">{{ a.desc }}</span>
            </div>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { ArrowRight, Coin, List, Clock, Warning, Document, Goods, ShoppingCartFull, Box, User } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard } from '../api/auth'

const chartEl = ref(null)
const loading = ref(true)
const chartLoading = ref(true)
const dashboardData = ref({})
let chart = null

const cards = reactive([
  { key:'totalSales', label:'总销售额', value:'--', icon:Coin, color:'#5c6cf0', bg:'#eef0ff' },
  { key:'totalOrders', label:'总订单', value:'--', icon:List, color:'#16a34a', bg:'#dcfce7' },
  { key:'todayOrders', label:'今日订单', value:'--', icon:Clock, color:'#f59e0b', bg:'#fef3c7' },
  { key:'warningCount', label:'库存预警', value:'--', icon:Warning, color:'#dc2626', bg:'#fee2e2' }
])

const quickActions = [
  { label:'商品管理', desc:'管理品牌 型号 材质 规格', icon:Goods, route:'/products', color:'#5c6cf0', bg:'#eef0ff' },
  { label:'订单管理', desc:'处理顾客订单与发货', icon:Document, route:'/orders', color:'#16a34a', bg:'#dcfce7' },
  { label:'采购管理', desc:'向厂家下单采购商品', icon:ShoppingCartFull, route:'/purchases', color:'#f59e0b', bg:'#fef3c7' },
  { label:'库存管理', desc:'查看出入库流水记录', icon:Box, route:'/stock/logs', color:'#2563eb', bg:'#dbeafe' },
  { label:'用户管理', desc:'管理客户与厂家账号', icon:User, route:'/users', color:'#7c3aed', bg:'#ede9fe' }
]

const loadDashboard = async () => {
  loading.value = true
  try {
    const d = (await getDashboard()).data
    dashboardData.value = d
    cards[0].value = d.totalSales != null ? '¥'+Number(d.totalSales).toFixed(2) : '¥0'
    cards[1].value = d.totalOrders ?? 0
    cards[2].value = d.todayOrders ?? 0
    cards[3].value = d.warningCount ?? 0
    if (chart) updateChart()
  } catch { cards.forEach(c=>c.value='--') }
  finally { loading.value = false }
}

const initChart = () => {
  if (!chartEl.value) return
  chart = echarts.init(chartEl.value)
  chartLoading.value = false
  const days = []
  const now = new Date()
  for(let i=6;i>=0;i--) { const d=new Date(now); d.setDate(d.getDate()-i); days.push((d.getMonth()+1)+'/'+d.getDate()) }
  chart.setOption({
    tooltip:{ trigger:'axis', backgroundColor:'#fff', borderColor:'#eaeaea', textStyle:{color:'#171717',fontSize:12}, padding:[10,14], extraCssText:'border-radius:8px;box-shadow:0 4px 20px rgba(0,0,0,0.08)' },
    grid:{ left:8, right:8, bottom:0, top:24, containLabel:true },
    xAxis:{ type:'category', data:days, axisLine:{show:false}, axisTick:{show:false}, axisLabel:{color:'#9ca3af',fontSize:11} },
    yAxis:{ type:'value', splitLine:{lineStyle:{color:'#f3f4f6',type:'dashed'}}, axisLabel:{color:'#9ca3af',fontSize:11}, min:0 },
    series:[{
      name:'销售额', type:'bar', data:Array(7).fill(0),
      itemStyle:{ color:new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:'#818cf8'},{offset:1,color:'#5c6cf0'}]), borderRadius:[6,6,0,0] },
      barWidth:24,
      emphasis:{ itemStyle:{ color:'#5c6cf0' } }
    }]
  })
}

const updateChart = () => {
  if (!chart) return
  const d = dashboardData.value.last7DaysSales
  if (!d) return
  chart.setOption({ series:[{ data:d.slice(0,7).map(v=>Number(v)||0) }] })
}

const handleResize = () => chart?.resize()

onMounted(async () => {
  await loadDashboard()
  nextTick(() => setTimeout(initChart, 100))
  window.addEventListener('resize', handleResize)
})
onBeforeUnmount(() => { window.removeEventListener('resize', handleResize); chart?.dispose() })
</script>

<style scoped>
.page-title { font-size:22px; font-weight:700; color:var(--c-text); }
.page-sub { font-size:13px; color:var(--c-text-muted); margin-top:4px; }
.welcome-row { margin-bottom:28px; }

.stat-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16px; margin-bottom:28px; }

.stat-card {
  background:#fff; border-radius:var(--radius-lg); padding:20px 22px;
  border:1px solid var(--c-border); box-shadow:var(--shadow-xs);
  position:relative; overflow:hidden; cursor:default;
  transition:box-shadow var(--transition),transform var(--transition);
}
.stat-card:hover { box-shadow:var(--shadow-sm); transform:translateY(-1px); }
.stat-bar { position:absolute; bottom:0; left:0; right:0; height:3px; opacity:0.3; }
.stat-top { display:flex; align-items:center; justify-content:space-between; margin-bottom:14px; }
.stat-icon { width:36px; height:36px; border-radius:10px; display:flex; align-items:center; justify-content:center; }
.stat-value { font-size:28px; font-weight:800; color:var(--c-text); line-height:1.2; }
.stat-label { font-size:12px; color:var(--c-text-muted); margin-top:4px; font-weight:500; }
.stat-trend { font-size:11px; font-weight:600; padding:2px 8px; border-radius:20px; }
.stat-trend.up { color:#16a34a; background:#dcfce7; }
.skeleton { color:var(--c-border-hover); }

.section-row { display:flex; gap:16px; }
.section-card {
  background:#fff; border-radius:var(--radius-lg); padding:24px;
  border:1px solid var(--c-border); box-shadow:var(--shadow-xs);
}
.chart-card { flex:2; }
.actions-card { flex:1; }
.sc-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; }
.sc-title { font-size:14px; font-weight:700; color:var(--c-text); }
.sc-tag { font-size:11px; color:var(--c-text-muted); background:#f3f4f6; padding:3px 10px; border-radius:20px; }
.chart-area { width:100%; height:280px; }

.action-list { display:flex; flex-direction:column; gap:4px; }
.action-item {
  display:flex; align-items:center; gap:12px;
  padding:12px 14px; border-radius:var(--radius-sm);
  cursor:pointer; transition:all var(--transition);
}
.action-item:hover { background:#f9fafb; }
.action-icon { width:36px; height:36px; border-radius:10px; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.action-info { flex:1; min-width:0; }
.action-label { display:block; font-size:13px; font-weight:600; color:var(--c-text); }
.action-desc { display:block; font-size:11px; color:var(--c-text-muted); margin-top:1px; }
.action-arrow { color:var(--c-text-muted); font-size:14px; flex-shrink:0; transition:transform var(--transition); }
.action-item:hover .action-arrow { transform:translateX(3px); color:var(--c-primary); }
</style>
