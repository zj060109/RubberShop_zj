<template>
  <div class="page">
    <div class="form-wrapper">
      <el-card class="form-card">
        <div class="card-title">
          <span class="title-text">手动库存调整</span>
          <span class="title-desc">通过本功能对库存进行入库或出库操作</span>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="adjust-form">
          <el-form-item label="商品分类" prop="categoryId">
            <el-tree-select v-model="form.categoryId" :data="categoryTree"
              :props="{ label: 'name_zj', value: 'id_zj', children: 'children' }"
              placeholder="请选择商品分类" check-strictly class="form-select-260" @change="onCategoryChange" />
          </el-form-item>
          <el-form-item label="尺寸规格" prop="spec">
            <div class="spec-search-row">
              <el-select v-model="form.spec" filterable allow-create clearable
                placeholder="选择或输入尺寸" class="spec-select" @change="onSpecChange">
                <el-option v-for="s in availableSpecs" :key="s" :label="s" :value="s" />
              </el-select>
              <el-button type="info" plain size="default" @click="loadSpecs">刷新尺寸列表</el-button>
            </div>
            <div v-if="productInfo" class="product-info-tag">
              <span class="product-name">{{ productInfo.name }}</span>
              <span class="product-stock">当前库存: {{ productInfo.stock }}</span>
            </div>
          </el-form-item>
          <el-form-item label="变动类型" prop="type">
            <el-radio-group v-model="form.type" class="type-radio-group">
              <el-radio-button value="manual_in">手动入库</el-radio-button>
              <el-radio-button value="manual_out">手动出库</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" class="quantity-input" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
          </el-form-item>
          <el-form-item>
            <div class="form-actions">
              <el-button type="primary" :loading="submitting" @click="handleSubmit" class="submit-btn">提交调整</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
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

const handleProductIdChange = async (val) => {
  if (!val) { productInfo.value = null; return }
  try {
    const res = await getProductDetail(val)
    productInfo.value = { name: res.data.name_zj || res.data.name || '未知', stock: res.data.stock_zj || 0 }
  } catch {
    productInfo.value = null
  }
}

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
.page {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 60px);
}

.form-wrapper {
  width: 100%;
}

.form-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #e2e8f0;
  padding: 24px;
  margin-bottom: 16px;
}

.card-title {
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.title-text {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.title-desc {
  font-size: 13px;
  color: #909399;
}

.adjust-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
  padding-bottom: 4px;
}

.type-radio-group :deep(.el-radio-button__inner) {
  border-radius: 6px;
  padding: 8px 28px;
  border: 1px solid #dcdfe6;
  box-shadow: none;
  transition: all 0.2s;
}

.type-radio-group :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-right: none;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.type-radio-group :deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.type-radio-group :deep(.el-radio-button.is-active .el-radio-button__inner) {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.35);
}

.quantity-input {
  width: 100%;
}

.quantity-input :deep(.el-input-number) {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.submit-btn {
  background: var(--primary);
  border-color: var(--primary);
}

.submit-btn:hover {
  background: #5558e6;
  border-color: #5558e6;
}

.form-select-260 {
  width: 260px;
}

.spec-search-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.spec-select {
  width: 200px;
}

.product-info-tag {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-name {
  font-size: 14px;
  font-weight: 600;
  color: #166534;
}

.product-stock {
  font-size: 13px;
  color: #15803d;
  margin-left: auto;
}
</style>
