<template>
  <div class="page">
    <div class="toolbar">
      <h2 class="page-title">{{ isEdit ? '编辑采购单' : '新增采购单 — 发送给厂家报价' }}</h2>
    </div>

    <div class="card-surface">
      <div class="card-section-title">基本信息</div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="form-grid">
        <el-form-item label="厂家" prop="factoryId">
          <el-select v-model="form.factoryId" placeholder="选择厂家" filterable clearable class="full-width">
            <el-option v-for="f in factoryList" :key="f.id" :label="f.companyName || f.realName" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计交货">
          <el-date-picker v-model="form.estimatedDelivery" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" class="full-width" />
        </el-form-item>
      </el-form>
    </div>

    <div class="card-surface" style="margin-top:16px">
      <div class="card-section-header">
        <span class="card-section-title">采购明细（厂家将为您报价）</span>
        <el-button type="primary" plain :icon="Plus" size="small" @click="addItem" class="add-item-btn">添加明细</el-button>
      </div>

      <el-table :data="form.items" class="items-table" v-if="form.items.length > 0">
        <el-table-column label="商品名称" min-width="160">
          <template #default="{ row }">
            <el-input v-model="row.productName" placeholder="商品名称（必填）" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="商品ID" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.productId" :min="0" size="small" controls-position="right" placeholder="选填" />
          </template>
        </el-table-column>
        <el-table-column label="规格" width="130">
          <template #default="{ row }">
            <el-input v-model="row.spec" placeholder="规格型号" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="数量" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" size="small" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="" width="60" align="center">
          <template #default="{ $index }">
            <button class="remove-btn" @click="removeItem($index)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M3 6h18"/><path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/></svg>
            </button>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="empty-state">暂无明细，请点击"添加明细"</div>

      <div class="table-footer">
        <span class="footer-label">共 {{ form.items.length }} 项</span>
        <span class="footer-hint">价格由厂家填写</span>
      </div>
    </div>

    <div class="form-actions">
      <el-button @click="router.push('/purchases')">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave" class="primary-btn">发送给厂家报价</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { createPurchase, updatePurchase, getPurchaseDetail } from '../../api/purchase'
import api from '../../api/index'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref(null)
const factoryList = ref([])
const form = reactive({ factoryId: null, estimatedDelivery: null, items: [] })

const rules = {
  factoryId: [{ required: true, message: '请选择厂家', trigger: 'change' }]
}

const loadFactories = async () => {
  try {
    const res = await api.get('/admin/users', { params: { page:1, pageSize:200, role:'factory' } })
    factoryList.value = (res.data?.records || res.data || [])
  } catch {}
}

const addItem = () => { form.items.push({ productName:'', productId:0, spec:'', quantity:1 }) }
const removeItem = (i) => { form.items.splice(i,1) }

const loadPurchase = async (id) => {
  try {
    const res = await getPurchaseDetail(id)
    const p = (res.data || {}).purchase || {}
    if (p.status_zj !== 'pending') {
      ElMessage.warning('该采购单已报价，不可修改')
      router.push('/purchases')
      return
    }
    form.factoryId = p.factory_id_zj
    form.estimatedDelivery = p.expected_delivery_date_zj || null
    form.items = ((res.data||{}).items||[]).map(i => ({
      productName: i.product_name_zj||'', productId: i.product_id_zj||0,
      spec: i.spec_zj||'', quantity: i.quantity_zj||1
    }))
  } catch {}
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (form.items.length === 0) { ElMessage.warning('请至少添加一个采购明细'); return }
  for (const item of form.items) {
    if (!item.productName) { ElMessage.warning('商品名称不能为空'); return }
    if (!item.quantity || item.quantity < 1) { ElMessage.warning('数量必须大于0'); return }
  }
  saving.value = true
  try {
    const data = {
      factoryId: form.factoryId,
      expectedDeliveryDate: form.estimatedDelivery || null,
      items: form.items.map(i => ({
        productName: i.productName, productId: i.productId || null,
        spec: i.spec, quantity: i.quantity
      }))
    }
    if (isEdit.value) { await updatePurchase(route.params.id, data) }
    else { await createPurchase(data) }
    ElMessage.success(isEdit.value ? '修改成功' : '已发送给厂家，等待报价')
    router.push('/purchases')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

onMounted(async () => {
  loadFactories()
  if (route.params.id) { isEdit.value = true; await loadPurchase(route.params.id) }
  else { addItem() }
})
</script>

<style scoped>
.page { padding: 32px 40px; }

.page-title { font-size: 20px; font-weight: 700; color: var(--c-text); margin: 0; }

.card-surface {
  background: var(--c-surface); border-radius: 12px;
  border: 1px solid var(--c-border); padding: 24px;
}

.card-section-title {
  font-size: 14px; font-weight: 600; color: var(--c-text); margin-bottom: 20px;
}

.card-section-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.card-section-header .card-section-title { margin-bottom: 0; }
.add-item-btn { border-radius: 8px; }

.form-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 0 32px;
}

.full-width { width: 100%; }

.form-grid :deep(.el-form-item__label) {
  font-size: 12px; font-weight: 600; color: var(--c-text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}

.items-table :deep(.el-table__header th) {
  background: var(--c-bg); color: var(--c-text-secondary);
  font-weight: 600; font-size: 12px; text-transform: uppercase;
  letter-spacing: 0.5px; border-bottom: 1px solid var(--c-border);
}
.items-table :deep(.el-table__body td) { border-color: var(--c-border); }

.remove-btn {
  display: inline-flex; align-items: center; justify-content: center;
  width: 28px; height: 28px; border-radius: 6px; border: none;
  background: transparent; color: var(--c-text-muted); cursor: pointer;
  transition: all .15s;
}
.remove-btn:hover { background: #fee2e2; color: #dc2626; }

.empty-state {
  padding: 40px 0; text-align: center; color: var(--c-text-muted);
  font-size: 14px;
}

.table-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 16px; margin-top: 16px;
  border-top: 1px solid var(--c-border);
}
.footer-label { font-size: 14px; color: var(--c-text-secondary); }
.footer-hint { font-size: 13px; color: var(--c-text-muted); }

.form-actions {
  display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;
}
.primary-btn { border-radius: 8px; }
</style>
