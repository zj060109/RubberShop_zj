<template>
  <div class="product-form-page">
    <div class="page-header">
      <el-button text @click="$router.back()" class="back-btn">
        <el-icon :size="20"><ArrowLeft /></el-icon>
      </el-button>
      <h3 class="page-title">{{ isEdit ? '编辑商品' : '新增商品' }}</h3>
      <span v-if="isEdit" class="header-badge">ID: {{ route.params.id }}</span>
    </div>

    <el-card class="form-card" shadow="never" v-loading="editingLoading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" size="default" class="product-form">

        <h4 class="section-title">基本信息</h4>

        <el-form-item label="商品分类" prop="categoryId">
          <el-tree-select v-model="form.categoryId" :data="categoryTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择商品分类" check-strictly class="form-select-260" @change="onCategoryChange" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="如 NAK、NOK、Parker" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="如 TC-25-40-7、B3" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="材质" prop="material">
              <el-select v-model="form.material" placeholder="选择材质" clearable filterable allow-create class="form-full-width">
                <el-option label="NBR（丁腈橡胶）" value="NBR" />
                <el-option label="FKM（氟橡胶）" value="FKM" />
                <el-option label="EPDM（三元乙丙）" value="EPDM" />
                <el-option label="SIL（硅橡胶）" value="SIL" />
                <el-option label="PU（聚氨酯）" value="PU" />
                <el-option label="HNBR（氢化丁腈）" value="HNBR" />
                <el-option label="CR（氯丁橡胶）" value="CR" />
                <el-option label="PTFE（聚四氟乙烯）" value="PTFE" />
                <el-option label="NR（天然橡胶）" value="NR" />
                <el-option label="ACM（丙烯酸酯）" value="ACM" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尺寸规格">
              <el-input v-model="form.spec" placeholder="如 25x40x7、80x95x12">
                <template #prepend>{{ form.categoryName || '选择分类' }}</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="描述信息（选填）" maxlength="500" show-word-limit />
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
          <div class="form-actions">
            <el-button @click="$router.back()" class="btn-cancel">取消</el-button>
            <el-button type="primary" class="btn-save" :loading="saving" @click="handleSave">
              {{ isEdit ? '保存修改' : '立即创建' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled, Delete, ArrowLeft } from '@element-plus/icons-vue'
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
  brand: '',
  model: '',
  material: '',
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
  brand: [{ required: true, message: '请输入品牌', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
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
    form.brand = p.brand_zj || ''
    form.model = p.model_zj || ''
    form.material = p.material_zj || ''
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
      brand: form.brand,
      model: form.model,
      material: form.material || null,
      spec: form.spec || null,
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
  padding: 32px 40px;
  background: #fafafa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #171717;
  margin: 0;
}

.back-btn {
  font-size: 18px;
  color: #6b7280;
  padding: 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
}
.back-btn:hover {
  color: #5c6cf0;
  background: #eef0ff;
}

.header-badge {
  background: #eef0ff;
  color: #5c6cf0;
  padding: 2px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.form-card {
  border-radius: 12px;
  border: 1px solid #eaeaea;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}
.form-card :deep(.el-card__body) {
  padding: 24px;
}

.product-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #171717;
  margin: 28px 0 16px;
}
.section-title:first-child {
  margin-top: 0;
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
  border: 1px solid #eaeaea;
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
  border-color: #5c6cf0;
}
.upload-dragger :deep(.el-upload__text) {
  font-size: 13px;
  color: #6b7280;
  margin-top: 8px;
}
.upload-dragger :deep(.el-upload__text em) {
  color: #5c6cf0;
  font-style: normal;
}
.upload-dragger :deep(.el-upload__tip) {
  margin-top: 6px;
  font-size: 12px;
  color: #9ca3af;
  text-align: center;
}

.btn-save {
  border-radius: 8px;
  transition: all 0.2s ease;
  --el-button-bg-color: #5c6cf0;
  --el-button-border-color: #5c6cf0;
  --el-button-hover-bg-color: #4f5de0;
  --el-button-hover-border-color: #4f5de0;
  --el-button-active-bg-color: #4f5de0;
  --el-button-active-border-color: #4f5de0;
}

.btn-cancel {
  border-radius: 8px;
  transition: all 0.2s ease;
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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
  width: 100%;
}
</style>
