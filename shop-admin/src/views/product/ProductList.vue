<template>
  <div class="page-container">
    <div class="card-toolbar">
      <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索商品尺寸" clearable style="width:220px" @keyup.enter="search">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        <el-tree-select v-model="categoryId" :data="categoryTree" :props="{ label: 'name_zj', value: 'id_zj', children: 'children' }"
          placeholder="全部分类" clearable check-strictly style="width:180px;margin-left:12px" @change="search" />
        <el-button type="primary" :loading="loading" @click="search" style="margin-left:12px">搜索</el-button>
      </div>
      <el-button type="primary" @click="$router.push('/products/form')">新增商品</el-button>
    </div>

    <div class="card-table">
      <el-table :data="tableData" v-loading="loading" stripe>
        <template #empty>
          <el-empty description="暂无商品数据" :image-size="80" />
        </template>
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image v-if="firstImage(row)" :src="firstImage(row)" style="width:50px;height:50px;border-radius:6px" fit="cover" lazy />
            <div v-else class="no-image">--</div>
          </template>
        </el-table-column>
        <el-table-column prop="spec_zj" label="尺寸" width="140" show-overflow-tooltip />
        <el-table-column label="分类" width="130">
          <template #default="{ row }">{{ getCategoryName(row.category_id_zj) }}</template>
        </el-table-column>
        <el-table-column label="价格" width="110" sortable :sort-method="(a,b) => a.price_zj - b.price_zj">
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
            <el-button size="small" text type="primary" @click="$router.push('/products/form/' + row.id_zj)">编辑</el-button>
            <el-button size="small" text type="danger" @click="handleDelete(row)">删除</el-button>
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
import { Search } from '@element-plus/icons-vue'
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
      if (item.id_zj === id) return item.name_zj
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
  ElMessageBox.confirm('确定删除「' + getCategoryName(row.category_id_zj) + ' - ' + (row.spec_zj || row.name_zj) + '」？', '确认删除', { type: 'warning' })
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
.toolbar-left { display: flex; align-items: center; }

.no-image {
  width: 50px; height: 50px;
  display: flex; align-items: center; justify-content: center;
  color: #c0c4cc; font-size: 12px;
  background: #f8fafc; border-radius: 8px;
}

.price-cell { color: var(--primary); font-weight: 700; font-size: 14px; }
.stock-cell { font-weight: 600; }
.stock-warn { color: #dc2626; font-weight: 700; }
.stock-warn::before { content: '⚠ '; font-size: 12px; }

.pagination-wrap {
  display: flex; justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid var(--border-light);
}
</style>
