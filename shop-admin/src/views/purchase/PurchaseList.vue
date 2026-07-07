<template>
  <div class="page-container purchase-page">
    <div class="card-toolbar">
      <div class="toolbar-left">
        <el-radio-group v-model="statusFilter" @change="onStatusChange" size="small">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="pending">待报价</el-radio-button>
          <el-radio-button value="quoted">已报价</el-radio-button>
          <el-radio-button value="paid">已付款</el-radio-button>
          <el-radio-button value="shipped">已发货</el-radio-button>
          <el-radio-button value="received">已收货</el-radio-button>
          <el-radio-button value="cancelled">已取消</el-radio-button>
        </el-radio-group>
      </div>
      <el-button type="primary" @click="$router.push('/purchases/form')" :icon="Plus">新增采购单</el-button>
    </div>

    <div class="card-table">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="order_no_zj" label="采购单号" min-width="180" />
        <el-table-column label="厂家" min-width="140">
          <template #default="{ row }">{{ getFactoryName(row.factory_id_zj) || '-' }}</template>
        </el-table-column>
        <el-table-column label="金额" width="130" align="right">
          <template #default="{ row }">
            <span class="amount-text">{{ row.total_amount_zj > 0 ? '¥' + Number(row.total_amount_zj).toFixed(2) : '待报价' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status_zj)" effect="light" size="small">{{ statusLabel(row.status_zj) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ row.created_at_zj }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="$router.push('/purchases/detail/' + row.id_zj)">详情</el-button>
            <el-button v-if="row.status_zj === 'pending'" size="small" text type="warning" @click="$router.push('/purchases/form/' + row.id_zj)">修改</el-button>
            <el-button v-if="row.status_zj === 'quoted'" size="small" text type="success" @click="handlePay(row)">付款</el-button>
            <el-button v-if="row.status_zj === 'shipped'" size="small" text type="success" @click="handleReceive(row)">收货</el-button>
            <el-button v-if="row.status_zj === 'pending'" size="small" text type="danger" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData"
        @size-change="loadData"
        class="pagination-wrap"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPurchaseList, payPurchase, receivePurchase, cancelPurchase } from '../../api/purchase'
import api from '../../api/index'

const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)
const factories = ref([])

const statusMap = {
  pending: '待报价', quoted: '已报价', paid: '已付款',
  shipped: '已发货', received: '已收货', cancelled: '已取消'
}
const statusTypeMap = {
  pending: 'info', quoted: 'warning', paid: 'primary',
  shipped: '', received: 'success', cancelled: 'danger'
}
const statusLabel = (s) => statusMap[s] || s
const statusType = (s) => statusTypeMap[s] || 'info'

const loadFactories = async () => {
  try {
    const res = await api.get('/admin/users', { params: { page: 1, pageSize: 200, role: 'factory' } })
    factories.value = (res.data?.records || res.data || [])
  } catch {}
}

const getFactoryName = (id) => {
  const f = factories.value.find(x => x.id === id)
  return f ? (f.companyName || f.realName || '') : ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPurchaseList({ page: page.value, pageSize: pageSize.value, status: statusFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const onStatusChange = () => { page.value = 1; loadData() }

const handlePay = (row) => {
  ElMessageBox.confirm('确认已向厂家支付 ¥' + Number(row.total_amount_zj).toFixed(2) + '？', '确认付款', { type: 'warning' })
    .then(async () => {
      try { await payPurchase(row.id_zj); ElMessage.success('已付款，等待厂家发货'); loadData() } catch {}
    }).catch(() => {})
}

const handleReceive = (row) => {
  ElMessageBox.confirm('确认收到货物？确认后商品将自动入库。', '确认收货', { type: 'success' })
    .then(async () => {
      try { await receivePurchase(row.id_zj); ElMessage.success('已收货，商品已入库'); loadData() } catch {}
    }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定取消该采购单？', '确认取消', { type: 'warning' })
    .then(async () => {
      try { await cancelPurchase(row.id_zj); ElMessage.success('已取消'); loadData() } catch {}
    }).catch(() => {})
}

onMounted(() => { loadFactories(); loadData() })
</script>

<style scoped>
.purchase-page { padding: 24px; }

.toolbar-left { display: flex; align-items: center; }

.amount-text { font-weight: 600; color: var(--primary); font-size: 13px; }

.pagination-wrap { margin-top: 20px; justify-content: flex-end; }

:deep(.el-radio-button__inner) { border-radius: 6px !important; }
:deep(.el-table th.el-table__cell) { background: #f8f9fb; color: #4a5568; font-weight: 600; font-size: 13px; }
</style>
