<template>
  <div class="page">
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="search-group">
          <el-input v-model="keyword" placeholder="搜索品牌、型号、材质、规格" clearable class="search-input" @keyup.enter="search">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-tree-select v-model="categoryId" :data="categoryTree" :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="全部分类" clearable check-strictly class="category-select" @change="search" />
          <el-button type="primary" :loading="loading" @click="search" class="btn-search">搜索</el-button>
        </div>
      </div>
      <el-button type="primary" class="btn-create" @click="$router.push('/products/form')">新增商品</el-button>
    </div>

    <div class="card-table">
      <el-table :data="tableData" v-loading="loading" class="product-table">
        <template #empty>
          <el-empty description="暂无商品数据" :image-size="80" />
        </template>
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image v-if="firstImage(row)" :src="firstImage(row)" class="thumb-img" fit="cover" lazy />
            <div v-else class="no-image">--</div>
          </template>
        </el-table-column>
        <el-table-column label="规格" width="130">
          <template #default="{ row }">
            <span v-if="row.spec_zj" class="spec-tag">{{ row.spec_zj }}</span>
            <span v-else class="text-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column label="品牌 / 型号" width="170">
          <template #default="{ row }">
            <div class="brand-model-cell">
              <span v-if="row.brand_zj" class="brand-tag">{{ row.brand_zj }}</span>
              <span v-if="row.model_zj" class="model-text">{{ row.model_zj }}</span>
              <span v-if="!row.brand_zj && !row.model_zj" class="text-muted">--</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="材质" width="100">
          <template #default="{ row }">
            <span v-if="row.material_zj" :class="['mat-tag', 'mat-' + row.material_zj.toLowerCase()]">
              <span class="mat-dot"></span>
              {{ row.material_zj }}
            </span>
            <span v-else class="text-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="110">
          <template #default="{ row }">{{ getCategoryName(row.category_id_zj) }}</template>
        </el-table-column>
        <el-table-column label="价格" width="105" sortable :sort-method="(a,b) => a.price_zj - b.price_zj">
          <template #default="{ row }">
            <span class="price-cell">&yen;{{ Number(row.price_zj || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="90" sortable>
          <template #default="{ row }">
            <span :class="['stock-cell', { 'stock-warn': row.stock_zj <= row.warning_stock_zj }]">
              {{ row.stock_zj ?? 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status_zj === 'on'" active-text="上架" inactive-text="下架"
              inline-prompt size="small" @change="(val) => toggleStatus(row, val)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" class="action-btn" @click="$router.push('/products/form/' + row.id_zj)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button size="small" text type="danger" class="action-btn" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" :total="total"
          layout="total, sizes, prev, pager, next" background @current-change="loadData" @size-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Edit, Delete } from '@element-plus/icons-vue'
import { getProductList, toggleProductStatus, deleteProduct } from '../../api/product'
import { getCategoryTree } from '../../api/category'

const keyword = ref('')
const categoryId = ref(null)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)
const categoryTree = ref([])

const firstImage = (row) => {
  if (!row.images_zj) return ''
  let images = row.images_zj
  if (typeof images === 'string') {
    try { images = JSON.parse(images) } catch { return '' }
  }
  return Array.isArray(images) && images.length ? images[0] : ''
}

const getCategoryName = (id) => {
  const find = (list) => {
    for (const item of list) {
      if (item.id === id) return item.name
      if (item.children) {
        const found = find(item.children)
        if (found) return found
      }
    }
    return ''
  }
  return find(categoryTree.value) || '-'
}

const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    categoryTree.value = res.data || []
  } catch {}
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getProductList({ page: page.value, pageSize: pageSize.value, keyword: keyword.value, categoryId: categoryId.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const search = () => {
  page.value = 1
  loadData()
}

const toggleStatus = async (row, val) => {
  const status = val ? 'on' : 'off'
  try {
    await toggleProductStatus(row.id_zj, status)
    row.status_zj = status
    ElMessage.success('状态更新成功')
  } catch {}
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除「' + (row.brand_zj || '') + ' ' + (row.model_zj || '') + ' ' + (row.spec_zj || '') + '」？', '确认删除', { type: 'warning' })
    .then(async () => {
      try {
        await deleteProduct(row.id_zj)
        ElMessage.success('删除成功')
        if (tableData.value.length === 1 && page.value > 1) {
          page.value--
        }
        loadData()
      } catch {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.page {
  padding: 32px 40px;
  min-height: calc(100vh - 60px);
  background: #fafafa;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.search-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 240px;
}

.category-select {
  width: 180px;
}

.btn-search {
  border-radius: 8px;
  transition: all 0.2s ease;
  --el-button-bg-color: #5c6cf0;
  --el-button-border-color: #5c6cf0;
  --el-button-hover-bg-color: #4f5de0;
  --el-button-hover-border-color: #4f5de0;
}

.btn-create {
  border-radius: 8px;
  transition: all 0.2s ease;
  --el-button-bg-color: #5c6cf0;
  --el-button-border-color: #5c6cf0;
  --el-button-hover-bg-color: #4f5de0;
  --el-button-hover-border-color: #4f5de0;
  padding: 8px 20px;
  font-weight: 500;
}

.card-table {
  background: #fff;
  border: 1px solid #eaeaea;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.product-table {
  --el-table-border-color: transparent;
}

.product-table :deep(.el-table__header th) {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #6b7280;
  background: #fafafa;
  border-bottom: 1px solid #eaeaea;
  padding: 12px 0;
  font-weight: 600;
}

.product-table :deep(.el-table__body td) {
  border-bottom: 1px solid #f5f5f5;
  color: #171717;
  font-size: 13px;
  padding: 14px 0;
}

.product-table :deep(.el-table__row:hover > td) {
  background: #fafafa;
}

.thumb-img {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: block;
}

.no-image {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 12px;
  background: #f5f5f5;
  border-radius: 8px;
}

.text-muted {
  color: #9ca3af;
  font-size: 12px;
}

.spec-tag {
  display: inline-block;
  background: #f5f5f5;
  color: #171717;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  font-family: 'Courier New', monospace;
}

.brand-model-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.brand-tag {
  display: inline-block;
  background: #eef0ff;
  color: #5c6cf0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.model-text {
  color: #6b7280;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mat-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.mat-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  flex-shrink: 0;
}

.mat-nbr   { background: #eff6ff; color: #2563eb; }
.mat-fkm   { background: #fef2f2; color: #dc2626; }
.mat-epdm  { background: #ecfdf5; color: #059669; }
.mat-sil   { background: #f0fdf4; color: #16a34a; }
.mat-pu    { background: #faf5ff; color: #9333ea; }
.mat-hnbr  { background: #fffbeb; color: #d97706; }
.mat-cr    { background: #fefce8; color: #ca8a04; }
.mat-ptfe  { background: #f8fafc; color: #475569; }
.mat-nr    { background: #fef7ed; color: #ea580c; }
.mat-acm   { background: #fdf2f8; color: #db2777; }

.price-cell {
  color: #5c6cf0;
  font-weight: 700;
  font-size: 14px;
}

.stock-cell {
  font-weight: 600;
  font-size: 13px;
}

.stock-warn {
  color: #dc2626;
  font-weight: 700;
}

.action-btn {
  font-size: 13px;
  gap: 4px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #eaeaea;
}
</style>
