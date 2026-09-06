<template>
  <div class="page">
    <div class="card-surface">
      <div class="card-header-block">
        <h2 class="card-title">手动库存调整</h2>
        <p class="card-desc">通过本功能对库存进行入库或出库操作</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="adjust-form">
        <el-form-item label="商品分类" prop="categoryId">
          <el-tree-select v-model="form.categoryId" :data="categoryTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择商品分类" check-strictly class="field-sm" @change="onCategoryChange" />
        </el-form-item>

        <el-form-item label="尺寸规格" prop="spec">
          <div class="spec-row">
            <el-select v-model="form.spec" filterable allow-create clearable
              placeholder="选择或输入尺寸" class="field-sm" @change="onSpecChange">
              <el-option v-for="s in availableSpecs" :key="s" :label="s" :value="s" />
            </el-select>
            <el-button plain @click="loadSpecs" class="refresh-btn">刷新尺寸列表</el-button>
          </div>
          <div v-if="productInfo" class="product-badge">
            <span class="product-badge-name">{{ productInfo.name }}</span>
            <span class="product-badge-stock">当前库存: {{ productInfo.stock }}</span>
          </div>
        </el-form-item>

        <el-form-item label="变动类型" prop="type">
          <div class="type-toggle">
            <button
              class="toggle-btn"
              :class="{ active: form.type === 'manual_in' }"
              @click="form.type = 'manual_in'"
            >手动入库</button>
            <button
              class="toggle-btn"
              :class="{ active: form.type === 'manual_out' }"
              @click="form.type = 'manual_out'"
            >手动出库</button>
          </div>
        </el-form-item>

        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" class="field-full" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" :loading="submitting" @click="handleSubmit" class="primary-btn">提交调整</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adjustStock } from '../../api/stock'
import { getProductList } from '../../api/product'
import { getCategoryTree } from '../../api/category'

const formRef = ref(null)
const submitting = ref(false)
const productInfo = ref(null)
const categoryTree = ref([])
const availableSpecs = ref([])
const matchedProductId = ref(null)

const form = reactive({
  categoryId: null,
  spec: '',
  type: 'manual_in',
  quantity: 1,
  remark: ''
})

const rules = {
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  spec: [{ required: true, message: '请选择或输入尺寸', trigger: 'change' }],
  type: [{ required: true, message: '请选择变动类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || []
  } catch {}
}

const loadSpecs = async () => {
  if (!form.categoryId) return
  try {
    const res = await getProductList({ categoryId: form.categoryId, pageSize: 200 })
    const products = res.data?.records || []
    availableSpecs.value = [...new Set(products.map(p => p.spec_zj).filter(Boolean))].sort()
  } catch {}
}

const findProduct = async (categoryId, spec) => {
  try {
    const res = await getProductList({ categoryId, keyword: spec, pageSize: 5 })
    const products = res.data?.records || []
    const match = products.find(p => p.spec_zj === spec)
    if (match) {
      matchedProductId.value = match.id_zj
      productInfo.value = { name: match.name_zj || match.name || '未知', stock: match.stock_zj || 0 }
    } else {
      matchedProductId.value = null
      productInfo.value = null
    }
  } catch {
    matchedProductId.value = null
    productInfo.value = null
  }
}

const onCategoryChange = () => {
  form.spec = ''
  productInfo.value = null
  loadSpecs()
}

const onSpecChange = (val) => {
  if (val && form.categoryId) {
    findProduct(form.categoryId, val)
  } else {
    productInfo.value = null
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (!matchedProductId.value) {
    ElMessage.warning('请先找到匹配的商品')
    return
  }
  submitting.value = true
  try {
    await adjustStock({ categoryId: form.categoryId, spec: form.spec, productId: matchedProductId.value, type: form.type, quantity: form.quantity, remark: form.remark })
    ElMessage.success('库存调整成功')
    handleReset()
  } catch (e) {
    ElMessage.error(e?.message || '调整失败')
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
  productInfo.value = null
  matchedProductId.value = null
  form.categoryId = null
  form.spec = ''
  form.type = 'manual_in'
  form.quantity = 1
  form.remark = ''
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.page { padding: 32px 40px; }

.card-surface {
  background: var(--c-surface); border-radius: 12px;
  border: 1px solid var(--c-border); padding: 32px;
  max-width: 560px;
}

.card-header-block {
  margin-bottom: 32px; padding-bottom: 24px;
  border-bottom: 1px solid var(--c-border);
}

.card-title { font-size: 20px; font-weight: 700; color: var(--c-text); margin: 0 0 6px; }
.card-desc { font-size: 13px; color: var(--c-text-muted); margin: 0; }

.adjust-form :deep(.el-form-item__label) {
  font-size: 12px; font-weight: 600; color: var(--c-text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}

.field-sm { width: 240px; }
.field-full { width: 100%; }
.field-full :deep(.el-input-number) { width: 100%; }

.spec-row { display: flex; gap: 10px; align-items: center; }
.refresh-btn { border-radius: 8px; }

.product-badge {
  margin-top: 10px; padding: 10px 14px;
  background: #dcfce7; border: 1px solid #bbf7d0;
  border-radius: 8px; display: flex; align-items: center; gap: 16px;
}
.product-badge-name { font-size: 14px; font-weight: 600; color: #166534; }
.product-badge-stock { font-size: 13px; color: var(--c-success); margin-left: auto; }

.type-toggle {
  display: inline-flex; border-radius: 8px; border: 1px solid var(--c-border);
  overflow: hidden;
}

.toggle-btn {
  padding: 8px 28px; border: none; border-right: 1px solid var(--c-border);
  background: var(--c-surface); color: var(--c-text-secondary);
  font-size: 14px; font-weight: 500; cursor: pointer; transition: all .15s;
}
.toggle-btn:last-child { border-right: none; }
.toggle-btn.active { background: var(--c-primary); color: #fff; }
.toggle-btn:not(.active):hover { background: var(--c-bg); }

.form-actions { display: flex; gap: 12px; padding-top: 8px; }
.primary-btn { border-radius: 8px; }
</style>
