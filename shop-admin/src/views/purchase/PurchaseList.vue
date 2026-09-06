<template>
  <div class="page">
    <div class="toolbar">
      <div class="status-pills">
        <button
          v-for="opt in statusOptions"
          :key="opt.value"
          class="pill"
          :class="{ active: statusFilter === opt.value }"
          @click="statusFilter = opt.value; onStatusChange()"
        >{{ opt.label }}</button>
      </div>
      <el-button type="primary" @click="$router.push('/purchases/form')" :icon="Plus" class="add-btn">新增采购单</el-button>
    </div>

    <div class="card-surface">
      <el-table :data="tableData" v-loading="loading" class="data-table">
        <el-table-column prop="order_no_zj" label="采购单号" min-width="180" />
        <el-table-column label="厂家" min-width="140">
          <template #default="{ row }">{{ getFactoryName(row.factory_id_zj) || '-' }}</template>
        </el-table-column>
        <el-table-column label="金额" width="150" align="right">
          <template #default="{ row }">
            <span v-if="row.total_amount_zj > 0" class="money">¥{{ Number(row.total_amount_zj).toFixed(2) }}</span>
            <span v-else class="money-placeholder">待报价</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="status-tag" :class="'status-' + row.status_zj">{{ statusLabel(row.status_zj) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ row.created_at_zj }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <button class="action-link" @click="$router.push('/purchases/detail/' + row.id_zj)">详情</button>
              <button v-if="row.status_zj === 'pending'" class="action-link" @click="$router.push('/purchases/form/' + row.id_zj)">修改</button>
              <button v-if="row.status_zj === 'quoted'" class="action-link action-success" @click="handlePay(row)">付款</button>
              <button v-if="row.status_zj === 'shipped'" class="action-link action-success" @click="handleReceive(row)">收货</button>
              <button v-if="row.status_zj === 'pending'" class="action-link action-danger" @click="handleCancel(row)">取消</button>
            </div>
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
        class="pagination"
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

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待报价', value: 'pending' },
  { label: '已报价', value: 'quoted' },
  { label: '已付款', value: 'paid' },
  { label: '已发货', value: 'shipped' },
  { label: '已收货', value: 'received' },
  { label: '已取消', value: 'cancelled' }
]

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
.page { padding: 32px 40px; }

.toolbar {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 20px;
}

.status-pills { display: flex; gap: 6px; flex-wrap: wrap; }

.pill {
  padding: 6px 16px; border-radius: 8px; border: 1px solid var(--c-border);
  background: var(--c-surface); color: var(--c-text-secondary);
  font-size: 13px; cursor: pointer; transition: all .15s;
}
.pill:hover { border-color: var(--c-primary); color: var(--c-primary); }
.pill.active { background: var(--c-primary); border-color: var(--c-primary); color: #fff; }

.add-btn { border-radius: 8px; }

.card-surface {
  background: var(--c-surface); border-radius: 12px;
  border: 1px solid var(--c-border); overflow: hidden;
}

.data-table :deep(.el-table__header th) {
  background: var(--c-bg); color: var(--c-text-secondary);
  font-weight: 600; font-size: 12px; text-transform: uppercase;
  letter-spacing: 0.5px; border-bottom: 1px solid var(--c-border);
}
.data-table :deep(.el-table__body td) { border-color: var(--c-border); }
.data-table :deep(.el-table__row:hover > td) { background: var(--c-bg); }

.money { font-weight: 700; color: var(--c-text); font-size: 14px; }
.money-placeholder { color: var(--c-text-muted); font-size: 13px; }

.status-tag {
  display: inline-block; padding: 2px 10px; border-radius: 6px;
  font-size: 12px; font-weight: 500;
}
.status-pending { background: #f3f4f6; color: var(--c-text-secondary); }
.status-quoted { background: #fef3c7; color: #92400e; }
.status-paid { background: #e0e7ff; color: var(--c-primary); }
.status-shipped { background: #dbeafe; color: #2563eb; }
.status-received { background: #dcfce7; color: var(--c-success); }
.status-cancelled { background: #fee2e2; color: #dc2626; }

.actions { display: flex; gap: 4px; flex-wrap: wrap; }
.action-link {
  padding: 4px 10px; border-radius: 6px; border: none; background: transparent;
  color: var(--c-text-secondary); font-size: 13px; cursor: pointer; transition: all .15s;
}
.action-link:hover { background: var(--c-bg); color: var(--c-text); }
.action-success { color: var(--c-success); }
.action-success:hover { background: #dcfce7; }
.action-danger { color: #dc2626; }
.action-danger:hover { background: #fee2e2; }

.pagination { margin-top: 20px; justify-content: flex-end; }
</style>
