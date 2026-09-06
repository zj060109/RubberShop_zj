<template>
  <div class="page">
    <div class="toolbar">
      <el-radio-group v-model="statusFilter" @change="onStatusChange" class="status-pills">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="paid">已支付</el-radio-button>
        <el-radio-button value="accepted">已接单</el-radio-button>
        <el-radio-button value="shipped">已发货</el-radio-button>
        <el-radio-button value="completed">已完成</el-radio-button>
        <el-radio-button value="cancelled">已取消</el-radio-button>
      </el-radio-group>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe row-key="id_zj">
        <el-table-column prop="order_no_zj" label="订单号" min-width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.order_no_zj }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="user_id_zj" label="顾客ID" width="100" />
        <el-table-column prop="receiver_name_zj" label="收货人" width="120" />
        <el-table-column label="金额" width="110" align="right">
          <template #default="{ row }">
            <span class="price-tag">&yen;{{ (row.actual_amount_zj ?? 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.payment_method_zj === 'balance' ? 'primary' : 'warning'" effect="light" size="small">
              {{ row.payment_method_zj === 'balance' ? '余额' : '赊账' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status_zj)" effect="light" size="small">
              {{ statusLabel(row.status_zj) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="175">
          <template #default="{ row }">{{ row.created_at_zj }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button size="small" type="primary" text @click="$router.push('/orders/' + row.id_zj)">详情</el-button>
              <el-button v-if="row.status_zj === 'paid'" size="small" type="success" plain @click="handleAccept(row)">接单</el-button>
              <el-button v-if="row.status_zj === 'paid' || row.status_zj === 'accepted'" size="small" type="warning" plain @click="openShipDialog(row)">发货</el-button>
              <el-button v-if="row.status_zj === 'paid' || row.status_zj === 'accepted'" size="small" type="danger" plain @click="handleCancel(row)">取消</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          background
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </div>

    <el-dialog v-model="shipDialogVisible" title="发货" width="460px" destroy-on-close>
      <el-form :model="shipForm" label-width="80px" class="ship-form">
        <el-form-item label="快递公司">
          <el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShip" :disabled="!shipForm.expressCompany || !shipForm.trackingNo">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, updateOrderStatus, shipOrder, cancelOrder } from '../../api/order'

const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

const shipDialogVisible = ref(false)
const shipForm = ref({ id: null, expressCompany: '', trackingNo: '' })

const statusMap = {
  paid: '已支付',
  accepted: '已接单',
  shipped: '已发货',
  completed: '已完成',
  cancelled: '已取消',
  refunding: '退款中',
  refunded: '已退款'
}

const statusTagType = (status) => {
  const map = {
    paid: 'info',
    accepted: 'primary',
    shipped: 'warning',
    completed: 'success',
    cancelled: 'danger',
    refunding: 'danger',
    refunded: 'danger'
  }
  return map[status] || 'info'
}

const statusLabel = (status) => statusMap[status] || status

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ page: page.value, pageSize: pageSize.value, status: statusFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const onStatusChange = () => {
  page.value = 1
  loadData()
}

const handleAccept = async (row) => {
  try {
    await updateOrderStatus(row.id_zj, 'accepted')
    ElMessage.success('已接单')
    loadData()
  } catch {}
}

const openShipDialog = (row) => {
  shipForm.value = { id: row.id_zj, expressCompany: '', trackingNo: '' }
  shipDialogVisible.value = true
}

const handleShip = async () => {
  try {
    await shipOrder(shipForm.value.id, shipForm.value.expressCompany, shipForm.value.trackingNo)
    ElMessage.success('已发货')
    shipDialogVisible.value = false
    loadData()
  } catch {}
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定取消该订单？', '确认取消', { type: 'warning' }).then(async () => {
    try {
      await cancelOrder(row.id_zj)
      ElMessage.success('已取消')
      loadData()
    } catch {}
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page {
  padding: 32px 40px;
  background: var(--c-bg, #fafafa);
  min-height: calc(100vh - 60px);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.status-pills :deep(.el-radio-button__inner) {
  border-radius: 8px !important;
  padding: 7px 18px;
  border: 1px solid var(--c-border, #eaeaea) !important;
  box-shadow: none;
  transition: all 0.2s;
  background: var(--c-surface, #fff);
  color: var(--c-text-secondary, #6b7280);
  font-size: 13px;
}
.status-pills :deep(.el-radio-button:not(:last-child) .el-radio-button__inner) {
  margin-right: 8px;
}
.status-pills :deep(.el-radio-button.is-active .el-radio-button__inner) {
  background: var(--c-primary, #5c6cf0);
  border-color: var(--c-primary, #5c6cf0) !important;
  color: #fff;
  box-shadow: 0 2px 8px rgba(92, 108, 240, 0.3);
}

.table-card {
  background: var(--c-surface, #fff);
  border-radius: 12px;
  border: 1px solid var(--c-border, #eaeaea);
  overflow: hidden;
}

.table-card :deep(.el-table__header th) {
  background: var(--c-bg, #fafafa);
  font-weight: 600;
  color: var(--c-text, #171717);
  border-bottom: 1px solid var(--c-border, #eaeaea);
}

.table-card :deep(.el-table td) {
  color: var(--c-text, #171717);
}

.table-card :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: var(--c-bg, #fafafa);
}

.order-no {
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', 'Consolas', monospace;
  font-size: 13px;
  letter-spacing: 0.02em;
  color: var(--c-text, #171717);
}

.price-tag {
  font-weight: 700;
  color: var(--c-primary, #5c6cf0);
  font-size: 14px;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', 'Consolas', monospace;
}

.action-btns {
  display: flex;
  gap: 6px;
  justify-content: center;
  flex-wrap: wrap;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 0 20px 20px;
}

.ship-form {
  padding-top: 8px;
}
</style>
