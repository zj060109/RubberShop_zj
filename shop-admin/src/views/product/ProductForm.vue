<template>
  <div class="product-form-page">
    <div class="page-header">
      <h3 class="page-title">{{ isEdit ? '编辑商品' : '新增商品' }}</h3>
    </div>

    <el-card class="form-card" shadow="never" v-loading="editingLoading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" size="default" class="product-form">

        <h4 class="section-title">基本信息</h4>

        <el-form-item label="商品分类" prop="categoryId">
          <el-tree-select v-model="form.categoryId" :data="categoryTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择商品分类" check-strictly class="form-select-260" @change="onCategoryChange" />
        </el-form-item>
        <el-form-item label="尺寸规格" prop="spec">
          <el-input v-model="form.spec" placeholder="请输入尺寸规格，如 80mm、M3、大号" maxlength="50" show-word-limit class="form-select-260">
            <template #prepend>{{ form.categoryName || '请先选择分类' }}</template>
          </el-input>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述信息" maxlength="500" show-word-limit />
        </el-form-item>

        <h4 class="section-title">图片管理</h4>

        <el-form-item label="商品图片">
          <div class="upload-section">
            <div class="image-gallery">
              <div v-for="(url, i) in form.images" :key="i" class="image-item">
                <el-image :src="url" fit="cover" class="gallery-img" :preview-src-list="form.images" :initial-index="i" />
                <div class="image-mask" @click="removeImage(i)">
                  <el-icon :size="18"><Delete /></el-icon>
                </div>
              </div>
            </div>
            <el-upload v-if="form.images.length < 5" drag :show-file-list="false" :before-upload="handleUpload"
              accept="image/*" class="upload-dragger">
              <el-icon class="el-icon--upload" :size="32"><UploadFilled /></el-icon>
              <div class="el-upload__text">拖拽图片到此处 或 <em>点击上传</em></div>
              <template #tip>
                <div class="el-upload__tip">支持 jpg/png 格式，最多 5 张</div>
              </template>
            </el-upload>
          </div>
        </el-form-item>

        <h4 class="section-title">价格库存</h4>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="1" class="form-full-width" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存">
              <el-input-number v-model="form.stock" :min="0" class="form-full-width" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预警阈值">
              <el-input-number v-model="form.warningStock" :min="0" class="form-full-width" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>

        <h4 class="section-title">其他</h4>

        <el-form-item label="关联厂家">
          <el-select v-model="form.factoryId" placeholder="请选择厂家（可选）" clearable filterable class="form-select-260">
            <el-option v-for="f in factories" :key="f.id" :label="f.companyName || f.realName || f.username" :value="f.id" />
          </el-select>
        </el-form-item>

        <el-divider />

        <el-form-item>
          <el-button type="primary" class="btn-save" :loading="saving" @click="handleSave">
            {{ isEdit ? '保存修改' : '立即创建' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, Delete } from '@element-plus/icons-vue'
import { getProductDetail, createProduct, updateProduct } from '../../api/product'
import { getCategoryTree } from '../../api/category'
import { uploadFile } from '../../api/upload'
import api from '../../api/index'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const isEdit = ref(false)
const editingLoading = ref(false)
const categoryTree = ref([])
const factories = ref([])

const form = reactive({
  categoryName: '',
  categoryId: null,
  spec: '',
  description: '',
  images: [],
  price: 0.01,
  stock: 0,
  warningStock: 10,
  factoryId: null
})

const rules = {
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  spec: [{ required: true, message: '请输入尺寸规格', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || []
  } catch {}
}

const findCategoryName = (id, tree) => {
  for (const node of tree) {
    if (node.id === id) return node.name
    if (node.children) {
      const found = findCategoryName(id, node.children)
      if (found) return found
    }
  }
  return ''
}

const onCategoryChange = () => {
  form.categoryName = findCategoryName(form.categoryId, categoryTree.value)
}

const loadFactories = async () => {
  try {
    const res = await api.get('/admin/users', { params: { page: 1, pageSize: 200, role: 'factory' } })
    factories.value = res.data?.records || []
  } catch {}
}

const loadProduct = async (id) => {
  editingLoading.value = true
  try {
    const res = await getProductDetail(id)
    const p = res.data
    form.categoryId = p.category_id_zj
    form.spec = p.spec_zj || ''
    form.description = p.description_zj || ''
    form.images = parseImages(p.images_zj)
    form.price = p.price_zj || 0.01
    form.stock = p.stock_zj || 0
    form.warningStock = p.warning_stock_zj || 10
    form.factoryId = p.factory_id_zj
  } catch {} finally {
    editingLoading.value = false
  }
}

const parseImages = (val) => {
  if (!val) return []
  if (Array.isArray(val)) return val
  try { return JSON.parse(val) } catch { return [] }
}

const removeImage = (i) => { form.images.splice(i, 1) }

const handleUpload = async (file) => {
  try {
    const res = await uploadFile(file)
    form.images.push(res.data)
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error(e?.message || '上传失败，请重试')
  }
  return false
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const data = {
      categoryId: form.categoryId,
      spec: form.spec,
      description: form.description,
      images: form.images,
      price: form.price,
      stock: form.stock,
      warningStock: form.warningStock,
      factoryId: form.factoryId
    }
    if (isEdit.value) {
      await updateProduct(route.params.id, data)
      ElMessage.success('修改成功')
    } else {
      await createProduct(data)
      ElMessage.success('新增成功')
    }
    router.push('/products')
  } catch {} finally {
    saving.value = false
  }
}

onMounted(() => {
  loadCategories()
  loadFactories()
  if (route.params.id) {
    isEdit.value = true
    loadProduct(route.params.id)
  }
})
</script>

<style scoped>
.product-form-page {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.form-card {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.form-card :deep(.el-card__body) {
  padding: 24px 32px 16px;
}

.product-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #6366f1;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eef2ff;
}

.upload-section {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.image-gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-item {
  position: relative;
  width: 88px;
  height: 88px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}
.gallery-img {
  width: 100%;
  height: 100%;
  display: block;
}
.image-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}
.image-item:hover .image-mask {
  opacity: 1;
}

.upload-dragger {
  width: 200px;
}
.upload-dragger :deep(.el-upload) {
  width: 100%;
}
.upload-dragger :deep(.el-upload-dragger) {
  border-radius: 8px;
  border: 1.5px dashed #d9d9d9;
  padding: 28px 16px;
  width: 200px;
  min-height: auto;
  transition: border-color 0.2s;
}
.upload-dragger :deep(.el-upload-dragger:hover) {
  border-color: #6366f1;
}
.upload-dragger :deep(.el-upload__text) {
  font-size: 13px;
  color: #606266;
  margin-top: 8px;
}
.upload-dragger :deep(.el-upload__text em) {
  color: #6366f1;
  font-style: normal;
}
.upload-dragger :deep(.el-upload__tip) {
  margin-top: 6px;
  font-size: 12px;
  color: #c0c4cc;
  text-align: center;
}

.btn-save {
  --el-button-bg-color: #6366f1;
  --el-button-border-color: #6366f1;
  --el-button-hover-bg-color: #5558e6;
  --el-button-hover-border-color: #5558e6;
  --el-button-active-bg-color: #4f52d6;
  --el-button-active-border-color: #4f52d6;
}

.form-full-width {
  width: 100%;
}

.form-full-width :deep(.el-input-number) {
  width: 100%;
}

.form-select-260 {
  width: 260px;
}
</style>
