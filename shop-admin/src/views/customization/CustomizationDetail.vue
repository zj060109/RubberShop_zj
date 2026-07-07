<template>
  <div class="page" v-loading="loading">
    <div class="header-bar">
      <el-button @click="$router.push('/customizations')" :icon="ArrowLeft">返回</el-button>
    </div>

    <el-card shadow="never" class="info-card">
      <template #header>
        <span class="card-title">基本信息</span>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="定制ID">{{ detail.id_zj }}</el-descriptions-item>
        <el-descriptions-item label="顾客ID">{{ detail.user_id_zj }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status_zj)" :color="statusTagColor(detail.status_zj)" effect="light">
            {{ statusLabel(detail.status_zj) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报价总额">
          <span class="amount-text">¥{{ detail.total_quoted_price_zj || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="关联订单号">{{ detail.order_id_zj || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.created_at_zj }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="desc-card">
      <template #header>
        <span class="card-title">需求描述</span>
      </template>
      <div class="desc-full">{{ detail.description_zj || '暂无描述' }}</div>
    </el-card>

    <el-card v-if="images.length" shadow="never" class="images-card">
      <template #header>
        <span class="card-title">参考图片</span>
      </template>
      <div class="image-gallery">
        <el-image
          v-for="(url, i) in images"
          :key="i"
          :src="url"
          :preview-src-list="images"
          :initial-index="i"
          fit="cover"
          class="gallery-image"
        />
      </div>
    </el-card>

    <el-card v-if="items.length" shadow="never" class="items-card">
      <template #header>
        <span class="card-title">报价明细</span>
      </template>
      <el-table :data="items" stripe>
        <el-table-column prop="product_spec_zj" label="规格" min-width="140" />
        <el-table-column prop="quantity_zj" label="数量" width="100" />
        <el-table-column label="单价" width="120" align="right">
          <template #default="{ row }">¥{{ row.unit_price_zj }}</template>
        </el-table-column>
        <el-table-column label="小计" width="120" align="right">
          <template #default="{ row }">¥{{ (row.subtotal_zj ?? (row.unit_price_zj * row.quantity_zj)).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="total-row">
        报价总金额：<span class="total-amount">¥{{ detail.total_quoted_price_zj || '0.00' }}</span>
      </div>
    </el-card>

    <el-card v-if="actionVisible" shadow="never" class="action-card">
      <template #header>
        <span class="card-title">操作</span>
      </template>

      <div v-if="detail.status_zj === 'pending_quote'" class="quote-form">
        <div class="quote-items">
          <div v-for="(item, i) in quoteItems" :key="i" class="quote-item-row">
            <el-input v-model="item.spec" placeholder="规格" style="width:180px" />
            <el-input-number v-model="item.quantity" :min="1" placeholder="数量" style="width:140px" />
            <el-input-number v-model="item.unitPrice" :min="0" :precision="2" placeholder="单价" style="width:160px" />
            <span class="item-subtotal">小计：¥{{ (item.unitPrice * item.quantity).toFixed(2) }}</span>
            <el-button type="danger" text :icon="Delete" @click="removeQuoteItem(i)" />
          </div>
        </div>
        <div class="quote-actions">
          <el-button type="primary" plain @click="addQuoteItem" :icon="Plus">添加明细</el-button>
          <div class="quote-total">
            报价总额：<span class="total-amount">¥{{ quoteTotal }}</span>
          </div>
        </div>
        <div class="quote-submit">
          <el-button type="primary" :loading="quoting" @click="handleQuote">提交报价</el-button>
        </div>
      </div>

      <div v-if="detail.status_zj !== 'pending_quote'" class="action-buttons">
        <el-button v-if="detail.status_zj === 'quoted'" type="success" @click="openConfirmDialog" :icon="Check">确认订单</el-button>
        <el-button v-if="detail.status_zj !== 'cancelled'" type="warning" @click="handleConvert" :icon="Switch">转为常规商品</el-button>
        <el-button v-if="detail.status_zj !== 'cancelled' && detail.status_zj !== 'converted'" type="danger" plain @click="handleCancel" :icon="Close">取消定制</el-button>
      </div>
    </el-card>

    <el-dialog v-model="confirmDialogVisible" title="确认订单" width="420px">
      <el-form :model="confirmForm" label-width="80px">
        <el-form-item label="支付方式">
          <el-select v-model="confirmForm.paymentMethod" placeholder="选择支付方式" style="width:100%">
            <el-option label="余额支付" value="balance" />
            <el-option label="赊账" value="credit" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="confirming" @click="handleConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Delete, Plus, Check, Switch, Close } from '@element-plus/icons-vue'
import { getCustomizationDetail, quoteCustomization, confirmCustomization, convertToProduct, cancelCustomization } from '../../api/customization'

const route = useRoute()

const loading = ref(false)
const detail = ref({})
const items = ref([])
const quoteItems = ref([])
const quoting = ref(false)
const confirmDialogVisible = ref(false)
const confirming = ref(false)
const confirmForm = ref({ paymentMethod: 'balance' })

const statusMap = {
  pending_quote: '待报价',
  quoted: '已报价',
  confirmed: '已确认',
  converted: '已转换',
  cancelled: '已取消'
}

const statusTagType = (status) => {
  const map = { pending_quote: 'info', quoted: 'primary', confirmed: 'success', converted: '', cancelled: 'danger' }
  return map[status] || 'info'
}

const statusTagColor = (status) => {
  return status === 'converted' ? '#8b5cf6' : ''
}

const statusLabel = (status) => statusMap[status] || status

const actionVisible = computed(() => {
  return detail.value.status_zj === 'pending_quote' || detail.value.status_zj === 'quoted' || detail.value.status_zj === 'confirmed'
})

const images = computed(() => {
  const val = detail.value.reference_images_zj
  if (!val) return []
  if (Array.isArray(val)) return val
  try { return JSON.parse(val) } catch { return [] }
})

const quoteTotal = computed(() => {
  return Number(quoteItems.value.reduce((sum, item) => sum + (item.unitPrice || 0) * (item.quantity || 0), 0).toFixed(2))
})

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getCustomizationDetail(route.params.id)
    detail.value = res.data || {}
    items.value = res.data?.items || []
  } finally {
    loading.value = false
  }
}

const addQuoteItem = () => {
  quoteItems.value.push({ spec: '', quantity: 1, unitPrice: 0 })
}

const removeQuoteItem = (i) => {
  quoteItems.value.splice(i, 1)
}

const handleQuote = async () => {
  if (quoteItems.value.length === 0) {
    ElMessage.warning('请至少添加一个报价项')
    return
  }
  quoting.value = true
  try {
    const body = {
      items: quoteItems.value.map(item => ({
        spec: item.spec,
        quantity: item.quantity,
        unitPrice: item.unitPrice
      }))
    }
    await quoteCustomization(route.params.id, body)
    ElMessage.success('报价成功')
    quoteItems.value = []
    loadDetail()
  } finally {
    quoting.value = false
  }
}

const openConfirmDialog = () => {
  confirmForm.value.paymentMethod = 'balance'
  confirmDialogVisible.value = true
}

const handleConfirm = async () => {
  confirming.value = true
  try {
    await confirmCustomization(route.params.id, { paymentMethod: confirmForm.value.paymentMethod })
    ElMessage.success('确认成功')
    confirmDialogVisible.value = false
    loadDetail()
  } finally {
    confirming.value = false
  }
}

const handleConvert = () => {
  ElMessageBox.prompt('请输入分类ID', '转为常规商品', {
    confirmButtonText: '确认',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效的分类ID'
  }).then(async ({ value }) => {
    try {
      await convertToProduct(route.params.id, value)
      ElMessage.success('转换成功')
      loadDetail()
    } catch {}
  }).catch(() => {})
}

const handleCancel = () => {
  ElMessageBox.confirm('确定取消该定制订单？', '确认取消', { type: 'warning' })
    .then(async () => {
      try {
        await cancelCustomization(route.params.id)
        ElMessage.success('已取消')
        loadDetail()
      } catch {}
    }).catch(() => {})
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.page {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 60px);
}

.header-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.info-card,
.desc-card,
.images-card,
.items-card,
.action-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #e2e8f0;
  padding: 24px;
  margin-bottom: 16px;
}
.info-card :deep(.el-card__body),
.desc-card :deep(.el-card__body),
.images-card :deep(.el-card__body),
.items-card :deep(.el-card__body),
.action-card :deep(.el-card__body) {
  padding: 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.amount-text {
  font-weight: 600;
  color: var(--primary);
}

.desc-full {
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.gallery-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  overflow: hidden;
}
.gallery-image :deep(img) {
  border-radius: 6px;
}

.total-row {
  margin-top: 16px;
  text-align: right;
  font-size: 14px;
  color: #64748b;
}

.total-amount {
  color: var(--primary);
  font-size: 20px;
  font-weight: 700;
  margin-left: 8px;
}

.quote-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quote-item-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-subtotal {
  font-size: 13px;
  color: var(--primary);
  font-weight: 600;
  min-width: 120px;
}

.quote-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
}

.quote-total {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.quote-submit {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

:deep(.el-table th.el-table__cell) {
  background: #f8f9fb;
  color: #4a5568;
  font-weight: 600;
  font-size: 13px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
}

:deep(.el-tag--default) {
  color: #fff;
  background-color: #8b5cf6;
  border-color: #8b5cf6;
}
</style>
