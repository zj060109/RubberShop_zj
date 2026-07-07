<template>
  <div class="page-container">
    <div class="form-header">
      <h2>{{ isEdit ? '编辑采购单' : '新增采购单 — 发送给厂家报价' }}</h2>
    </div>

    <el-card shadow="never" class="form-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" label-position="right">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="厂家" prop="factoryId">
              <el-select v-model="form.factoryId" placeholder="选择厂家" filterable clearable style="width:100%">
                <el-option v-for="f in factoryList" :key="f.id" :label="f.companyName || f.realName" :value="f.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计交货">
              <el-date-picker v-model="form.estimatedDelivery" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card shadow="never" class="form-card" style="margin-top:16px">
      <template #header>
        <div class="card-header">
          <span>采购明细（厂家将为您报价）</span>
          <el-button type="primary" plain :icon="Plus" size="small" @click="addItem">添加明细</el-button>
        </div>
      </template>

      <el-table :data="form.items" stripe v-if="form.items.length > 0">
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
            <el-button type="danger" text :icon="Delete" size="small" @click="removeItem($index)" />
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无明细，请点击添加" :image-size="80" />

      <div class="total-bar">
        <span class="total-label">共 {{ form.items.length }} 项</span>
        <span class="total-hint">价格由厂家填写</span>
      </div>
    </el-card>

    <div class="form-footer">
      <el-button @click="router.push('/purchases')">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">发送给厂家报价</el-button>
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
.form-header { margin-bottom: 16px; }
.form-header h2 { font-size: 18px; font-weight: 700; color: var(--text); margin: 0; }

.form-card { border: 1px solid var(--border); }
.form-card .card-header { display: flex; align-items: center; justify-content: space-between; font-weight: 600; }

.total-bar {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 16px; margin-top: 16px;
  border-top: 1px solid var(--border-light);
}
.total-label { font-size: 14px; color: var(--text-secondary); }
.total-hint { font-size: 13px; color: var(--text-muted); }

.form-footer {
  display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px;
}
</style>
