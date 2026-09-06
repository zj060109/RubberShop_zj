<template>
  <div class="page" v-loading="loading">
    <div class="toolbar">
      <button class="back-link" @click="$router.push('/purchases')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        返回采购列表
      </button>
      <div class="toolbar-actions">
        <el-button v-if="isMerchant && detail.status_zj === 'pending'" type="primary" size="small" @click="$router.push('/purchases/form/' + detail.id_zj)">修改</el-button>
        <el-button v-if="isMerchant && detail.status_zj === 'quoted'" type="success" size="small" @click="handlePay">确认付款</el-button>
        <el-button v-if="isMerchant && detail.status_zj === 'shipped'" type="success" size="small" @click="handleReceive">确认收货</el-button>
        <el-button v-if="isFactory && detail.status_zj === 'pending'" type="warning" size="small" @click="openQuoteDialog">报价</el-button>
        <el-button v-if="isFactory && detail.status_zj === 'paid'" type="warning" size="small" @click="openShipDialog">发货</el-button>
        <el-button v-if="detail.status_zj === 'pending' && isMerchant" type="danger" size="small" @click="handleCancel">取消</el-button>
        <el-button v-if="detail.status_zj === 'quoted' && isFactory" type="danger" size="small" @click="handleCancel">拒绝</el-button>
      </div>
    </div>

    <div class="info-cards-row">
      <div class="info-card">
        <div class="info-card-header">
          <div class="info-card-icon icon-order">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>
          </div>
          <span>采购单信息</span>
        </div>
        <div class="info-card-body">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">采购单号</span>
              <span class="info-value mono">{{ detail.order_no_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">状态</span>
              <span class="info-value">
                <span class="status-tag" :class="'status-' + detail.status_zj">{{ statusLabel(detail.status_zj) }}</span>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">厂家</span>
              <span class="info-value">{{ factoryName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">金额</span>
              <span class="info-value price-value">{{ detail.total_amount_zj > 0 ? '¥' + Number(detail.total_amount_zj).toFixed(2) : '待报价' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">预计交货</span>
              <span class="info-value">{{ detail.expected_delivery_date_zj || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ detail.created_at_zj }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="detail.express_company_zj || detail.tracking_no_zj" class="info-card">
        <div class="info-card-header">
          <div class="info-card-icon icon-logistics">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
          </div>
          <span>物流信息</span>
        </div>
        <div class="info-card-body">
          <div class="info-grid">
            <div class="info-item"><span class="info-label">快递公司</span><span class="info-value">{{ detail.express_company_zj }}</span></div>
            <div class="info-item"><span class="info-label">快递单号</span><span class="info-value mono tracking">{{ detail.tracking_no_zj }}</span></div>
          </div>
        </div>
      </div>
    </div>

    <div class="info-card">
      <div class="info-card-header">
        <div class="info-card-icon icon-items">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/></svg>
        </div>
        <span>采购明细</span>
      </div>
      <div class="info-card-body no-padding">
        <el-table :data="items" class="items-table">
          <el-table-column label="商品名称" min-width="140">
            <template #default="{ row }">{{ row.product_name_zj }}</template>
          </el-table-column>
          <el-table-column prop="spec_zj" label="规格" width="130" />
          <el-table-column prop="quantity_zj" label="数量" width="80" />
          <el-table-column label="单价" width="130" align="right">
            <template #default="{ row }">{{ row.unit_price_zj > 0 ? '¥' + Number(row.unit_price_zj).toFixed(2) : '待报价' }}</template>
          </el-table-column>
          <el-table-column label="小计" width="130" align="right">
            <template #default="{ row }">{{ row.subtotal_zj > 0 ? '¥' + Number(row.subtotal_zj).toFixed(2) : '-' }}</template>
          </el-table-column>
        </el-table>
        <div class="total-summary">
          采购总金额<span class="total-amount">{{ detail.total_amount_zj > 0 ? '¥' + Number(detail.total_amount_zj).toFixed(2) : '待厂家报价' }}</span>
        </div>
      </div>
    </div>

    <el-dialog v-model="quoteDialogVisible" title="报价" width="680px" destroy-on-close>
      <el-table :data="quoteItems" class="dialog-table">
        <el-table-column label="商品" min-width="120">
          <template #default="{ row }">{{ row.product_name_zj }}</template>
        </el-table-column>
        <el-table-column prop="spec_zj" label="规格" width="100" />
        <el-table-column prop="quantity_zj" label="数量" width="70" />
        <el-table-column label="单价" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row._quotePrice" :min="0.01" :precision="2" size="small" controls-position="right" class="full-width" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="110" align="right">
          <template #default="{ row }">¥{{ ((row._quotePrice||0) * row.quantity_zj).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="dialog-total">
        总金额：<span class="dialog-total-price">¥{{ quoteTotal.toFixed(2) }}</span>
      </div>
      <template #footer>
        <el-button @click="quoteDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="quoting" @click="handleQuote">提交报价</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="shipDialogVisible" title="发货" width="480px" destroy-on-close>
      <el-form :model="shipForm" label-position="top">
        <el-form-item label="快递公司">
          <el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getPurchaseDetail, quotePurchase, payPurchase, receivePurchase, cancelPurchase, shipPurchase } from '../../api/purchase'
import { useUserStore } from '../../stores/user'
import api from '../../api/index'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref({})
const items = ref([])
const factoryName = ref('')

const isMerchant = computed(() => userStore.role === 'merchant')
const isFactory = computed(() => userStore.role === 'factory')

const statusMap = { pending:'待报价', quoted:'已报价', paid:'已付款', shipped:'已发货', received:'已收货', cancelled:'已取消' }
const statusTypeMap = { pending:'info', quoted:'warning', paid:'primary', shipped:'', received:'success', cancelled:'danger' }
const statusLabel = (s) => statusMap[s] || s
const statusType = (s) => statusTypeMap[s] || 'info'

const loadFactoryName = async (factoryId) => {
  if (!factoryId) return
  try {
    const res = await api.get('/admin/users', { params: { page:1, pageSize:200, role:'factory' } })
    const list = res.data?.records || (Array.isArray(res.data) ? res.data : [])
    const f = list.find(x => x.id === factoryId)
    factoryName.value = f ? (f.companyName || f.realName || '厂家#'+factoryId) : '厂家#'+factoryId
  } catch {}
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getPurchaseDetail(route.params.id)
    const d = res.data || {}
    detail.value = d.purchase || {}
    items.value = d.items || []
    loadFactoryName(detail.value.factory_id_zj)
  } finally { loading.value = false }
}

const handlePay = async () => {
  try {
    await ElMessageBox.confirm(`确认支付 ¥${Number(detail.value.total_amount_zj).toFixed(2)}？`, '确认付款', { type: 'warning' })
    await payPurchase(detail.value.id_zj)
    ElMessage.success('已付款，等待厂家发货')
    loadDetail()
  } catch {}
}

const handleReceive = async () => {
  try {
    await ElMessageBox.confirm('确认收到货物？商品将自动入库。', '确认收货', { type: 'success' })
    await receivePurchase(detail.value.id_zj)
    ElMessage.success('已收货，商品已入库')
    loadDetail()
  } catch {}
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定取消该采购单？', '确认', { type: 'warning' })
    await cancelPurchase(detail.value.id_zj)
    ElMessage.success('已取消')
    loadDetail()
  } catch {}
}

const quoteDialogVisible = ref(false)
const quoting = ref(false)
const quoteItems = ref([])
const quoteTotal = computed(() => quoteItems.value.reduce((s, i) => s + (i._quotePrice||0)*i.quantity_zj, 0))

const openQuoteDialog = () => {
  quoteItems.value = items.value.map(i => ({ ...i, _quotePrice: i.unit_price_zj > 0 ? i.unit_price_zj : null }))
  quoteDialogVisible.value = true
}

const handleQuote = async () => {
  for (const item of quoteItems.value) {
    if (!item._quotePrice || item._quotePrice <= 0) { ElMessage.warning('请填写所有明细的单价'); return }
  }
  quoting.value = true
  try {
    await quotePurchase(detail.value.id_zj, {
      items: quoteItems.value.map(i => ({ itemId: i.id_zj, unitPrice: i._quotePrice }))
    })
    ElMessage.success('报价成功')
    quoteDialogVisible.value = false
    loadDetail()
  } catch {} finally { quoting.value = false }
}

const shipDialogVisible = ref(false)
const shipping = ref(false)
const shipForm = ref({ expressCompany: '', trackingNo: '' })

const openShipDialog = () => {
  shipForm.value = { expressCompany: '', trackingNo: '' }
  shipDialogVisible.value = true
}

const handleShip = async () => {
  const { expressCompany, trackingNo } = shipForm.value
  if (!expressCompany || !trackingNo) { ElMessage.warning('请填写完整物流信息'); return }
  shipping.value = true
  try {
    await shipPurchase(detail.value.id_zj, expressCompany, trackingNo)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    loadDetail()
  } catch {} finally { shipping.value = false }
}

onMounted(loadDetail)
</script>

<style scoped>
.page { padding: 32px 40px; }

.toolbar {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 24px;
}

.back-link {
  display: inline-flex; align-items: center; gap: 6px;
  background: none; border: none; color: var(--c-text-secondary);
  font-size: 14px; font-weight: 500; cursor: pointer; padding: 6px 0;
  transition: color .15s;
}
.back-link:hover { color: var(--c-text); }

.toolbar-actions { display: flex; gap: 8px; }

.info-cards-row {
  display: grid; grid-template-columns: 1fr 1fr; gap: 16px;
  margin-bottom: 16px;
}

.info-card {
  background: var(--c-surface); border-radius: 12px;
  border: 1px solid var(--c-border); overflow: hidden;
}

.info-card-header {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 24px; font-size: 14px; font-weight: 600;
  color: var(--c-text); border-bottom: 1px solid var(--c-border);
  background: var(--c-bg);
}

.info-card-icon {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 8px;
}
.icon-order { background: #e0e7ff; color: var(--c-primary); }
.icon-logistics { background: #dbeafe; color: #2563eb; }
.icon-items { background: #fef3c7; color: #92400e; }

.info-card-body { padding: 20px 24px; }
.info-card-body.no-padding { padding: 0; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px 40px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 12px; color: var(--c-text-muted); font-weight: 500; text-transform: uppercase; letter-spacing: 0.3px; }
.info-value { font-size: 14px; color: var(--c-text); }
.info-value.mono { font-family: 'SF Mono','Menlo',monospace; font-weight: 600; }
.info-value.price-value { font-weight: 700; font-size: 18px; }
.info-value.tracking { color: #2563eb; }

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

.items-table :deep(.el-table__header th) {
  background: var(--c-bg); color: var(--c-text-secondary);
  font-weight: 600; font-size: 12px; text-transform: uppercase;
  letter-spacing: 0.5px; border-bottom: 1px solid var(--c-border);
}
.items-table :deep(.el-table__body td) { border-color: var(--c-border); }

.total-summary {
  text-align: right; padding: 16px 24px;
  font-size: 14px; color: var(--c-text-secondary);
}
.total-amount { font-size: 22px; font-weight: 800; color: var(--c-text); margin-left: 12px; }

.dialog-table :deep(.el-table__header th) {
  background: var(--c-bg); color: var(--c-text-secondary);
  font-weight: 600; font-size: 12px;
}
.full-width { width: 100%; }

.dialog-total { text-align: right; margin-top: 16px; font-size: 16px; font-weight: 600; }
.dialog-total-price { font-size: 24px; font-weight: 700; margin-left: 8px; }
</style>
