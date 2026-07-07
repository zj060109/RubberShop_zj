<template>
  <div class="page-container detail-page" v-loading="loading">
    <div class="card-toolbar">
      <el-button text @click="$router.push('/purchases')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回采购列表
      </el-button>
      <div class="header-actions">
        <el-button v-if="isMerchant && detail.status_zj === 'pending'" type="primary" size="small" @click="$router.push('/purchases/form/' + detail.id_zj)">修改</el-button>
        <el-button v-if="isMerchant && detail.status_zj === 'quoted'" type="success" size="small" @click="handlePay">确认付款</el-button>
        <el-button v-if="isMerchant && detail.status_zj === 'shipped'" type="success" size="small" @click="handleReceive">确认收货</el-button>
        <el-button v-if="isFactory && detail.status_zj === 'pending'" type="warning" size="small" @click="openQuoteDialog">报价</el-button>
        <el-button v-if="isFactory && detail.status_zj === 'paid'" type="warning" size="small" @click="openShipDialog">发货</el-button>
        <el-button v-if="detail.status_zj === 'pending' && isMerchant" type="danger" size="small" @click="handleCancel">取消</el-button>
        <el-button v-if="detail.status_zj === 'quoted' && isFactory" type="danger" size="small" @click="handleCancel">拒绝</el-button>
      </div>
    </div>

    <div class="cards-row">
      <div class="card-item">
        <div class="card-header">
          <span class="card-header-icon order-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg></span>
          采购单信息
        </div>
        <div class="card-body">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">采购单号</span>
              <span class="info-value order-no">{{ detail.order_no_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">状态</span>
              <span class="info-value"><el-tag :type="statusType(detail.status_zj)" effect="light" size="small">{{ statusLabel(detail.status_zj) }}</el-tag></span>
            </div>
            <div class="info-item">
              <span class="info-label">厂家</span>
              <span class="info-value">{{ factoryName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">金额</span>
              <span class="info-value price">{{ detail.total_amount_zj > 0 ? '¥' + Number(detail.total_amount_zj).toFixed(2) : '待报价' }}</span>
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

      <div v-if="detail.express_company_zj || detail.tracking_no_zj" class="card-item">
        <div class="card-header">
          <span class="card-header-icon logistics-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg></span>
          物流信息
        </div>
        <div class="card-body">
          <div class="info-grid">
            <div class="info-item"><span class="info-label">快递公司</span><span class="info-value">{{ detail.express_company_zj }}</span></div>
            <div class="info-item"><span class="info-label">快递单号</span><span class="info-value tracking-no">{{ detail.tracking_no_zj }}</span></div>
          </div>
        </div>
      </div>
    </div>

    <div class="card-item">
      <div class="card-header">
        <span class="card-header-icon items-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/></svg></span>
        采购明细
      </div>
      <div class="card-body">
        <el-table :data="items" stripe class="items-table">
          <el-table-column label="商品名称" min-width="140">
            <template #default="{ row }">{{ row.product_name_zj }}</template>
          </el-table-column>
          <el-table-column prop="spec_zj" label="规格" width="130" />
          <el-table-column prop="quantity_zj" label="数量" width="80" />
          <el-table-column label="单价" width="120" align="right">
            <template #default="{ row }">{{ row.unit_price_zj > 0 ? '¥' + Number(row.unit_price_zj).toFixed(2) : '待报价' }}</template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="right">
            <template #default="{ row }">{{ row.subtotal_zj > 0 ? '¥' + Number(row.subtotal_zj).toFixed(2) : '-' }}</template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          采购总金额：<span class="total-price">{{ detail.total_amount_zj > 0 ? '¥' + Number(detail.total_amount_zj).toFixed(2) : '待厂家报价' }}</span>
        </div>
      </div>
    </div>

    <!-- 报价弹窗（厂家专用） -->
    <el-dialog v-model="quoteDialogVisible" title="报价" width="600px" destroy-on-close>
      <el-table :data="quoteItems" stripe>
        <el-table-column label="商品" min-width="120">
          <template #default="{ row }">{{ row.product_name_zj }}</template>
        </el-table-column>
        <el-table-column prop="spec_zj" label="规格" width="100" />
        <el-table-column prop="quantity_zj" label="数量" width="70" />
        <el-table-column label="单价" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row._quotePrice" :min="0.01" :precision="2" size="small" controls-position="right" style="width:100%" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="100" align="right">
          <template #default="{ row }">¥{{ ((row._quotePrice||0) * row.quantity_zj).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="quote-total">
        总金额：<span class="quote-total-price">¥{{ quoteTotal.toFixed(2) }}</span>
      </div>
      <template #footer>
        <el-button @click="quoteDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="quoting" @click="handleQuote">提交报价</el-button>
      </template>
    </el-dialog>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialogVisible" title="发货" width="440px" destroy-on-close>
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="快递公司"><el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" /></el-form-item>
        <el-form-item label="快递单号"><el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" /></el-form-item>
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

// 报价
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

// 发货
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
.detail-page { padding: 24px; }

.back-btn { font-size: 14px; font-weight: 500; }
.header-actions { display: flex; gap: 8px; }

.cards-row { display: flex; gap: 16px; flex-wrap: wrap; }
.cards-row .card-item { flex: 1; min-width: 280px; }

.card-item {
  background: var(--bg-card); border-radius: var(--radius); margin-bottom: 16px;
  box-shadow: var(--shadow-sm); border: 1px solid var(--border); overflow: hidden;
}

.card-header {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 24px; font-size: 15px; font-weight: 700; color: var(--text);
  border-bottom: 1px solid var(--border-light); background: #fafbff;
}

.card-header-icon {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 10px;
}
.card-header-icon svg { width: 18px; height: 18px; }
.order-icon { background: var(--primary-bg); color: var(--primary); }
.logistics-icon { background: var(--info-bg); color: var(--info); }
.items-icon { background: var(--warning-bg); color: var(--warning); }

.card-body { padding: 20px 24px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px 40px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-label { font-size: 12px; color: var(--text-muted); font-weight: 500; }
.info-value { font-size: 14px; color: var(--text); }
.info-value.order-no { font-family: monospace; font-weight: 600; }
.info-value.price { font-weight: 700; color: var(--primary); font-size: 18px; }
.info-value.tracking-no { font-family: monospace; font-weight: 500; color: var(--info); }

.total-row { margin-top: 20px; text-align: right; font-size: 14px; color: var(--text-secondary); }
.total-price { color: var(--primary); font-size: 22px; font-weight: 800; margin-left: 8px; }

.quote-total { text-align: right; margin-top: 16px; font-size: 16px; font-weight: 600; }
.quote-total-price { color: var(--primary); font-size: 24px; font-weight: 700; margin-left: 8px; }
</style>
