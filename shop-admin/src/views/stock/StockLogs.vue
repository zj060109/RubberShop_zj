<template>
  <div class="page">
    <el-card class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="productId" placeholder="商品ID" clearable class="input-field" @keyup.enter="search" />
          <el-select v-model="typeFilter" placeholder="变动类型" clearable class="select-field" @change="search">
            <el-option label="采购入库" value="purchase_in" />
            <el-option label="销售出库" value="sale_out" />
            <el-option label="手动入库" value="manual_in" />
            <el-option label="手动出库" value="manual_out" />
            <el-option label="退货入库" value="refund_in" />
          </el-select>
          <el-button type="primary" class="search-btn" @click="search">搜索</el-button>
          <el-button type="warning" class="warn-btn" style="margin-left:8px" @click="showWarnings">库存预警</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="product_id_zj" label="商品ID" width="90" />
        <el-table-column label="变动数量" width="110">
          <template #default="{ row }">
            <span :class="row.change_quantity_zj > 0 ? 'qty-positive' : 'qty-negative'">
              {{ row.change_quantity_zj > 0 ? '+' : '' }}{{ row.change_quantity_zj }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="current_stock_zj" label="变动后库存" width="110" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <el-tag :type="tagType(row.type_zj)" size="small" effect="light">{{ typeLabel(row.type_zj) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reference_id_zj" label="关联单号" width="130" />
        <el-table-column prop="remark_zj" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="时间" width="175">
          <template #default="{ row }">
            {{ formatTime(row.created_at_zj) }}
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          background
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog title="库存预警" v-model="warningVisible" width="640px" class="warn-dialog" destroy-on-close>
      <el-table :data="warnings" v-loading="warningLoading" stripe class="data-table">
        <template #empty>
          <el-empty description="暂无库存预警" :image-size="80" />
        </template>
        <el-table-column prop="id_zj" label="商品ID" width="90" />
        <el-table-column prop="name_zj" label="商品名" min-width="140" />
        <el-table-column prop="stock_zj" label="当前库存" width="100" />
        <el-table-column prop="warning_stock_zj" label="预警阈值" width="100" />
      </el-table>
      <template #footer>
        <el-button type="primary" @click="warningVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStockLogs, getStockWarnings } from '../../api/stock'

const productId = ref('')
const typeFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

const warningVisible = ref(false)
const warningLoading = ref(false)
const warnings = ref([])

const typeMap = {
  purchase_in: '采购入库',
  sale_out: '销售出库',
  manual_in: '手动入库',
  manual_out: '手动出库',
  refund_in: '退货入库'
}

const tagMap = {
  purchase_in: 'success',
  sale_out: 'danger',
  manual_in: '',
  manual_out: 'warning',
  refund_in: 'info'
}

const typeLabel = (type) => typeMap[type] || type
const tagType = (type) => tagMap[type] || 'info'

const formatTime = (val) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStockLogs({ page: page.value, pageSize: pageSize.value, productId: productId.value, type: typeFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const search = () => {
  page.value = 1
  loadData()
}

const showWarnings = async () => {
  warningVisible.value = true
  warningLoading.value = true
  try {
    const res = await getStockWarnings()
    warnings.value = res.data || []
  } finally {
    warningLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 60px);
}

.toolbar-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
  border: 1px solid #e2e8f0;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.input-field {
  width: 180px;
}

.select-field {
  width: 160px;
}

.search-btn {
  background: var(--primary);
  border-color: var(--primary);
}

.search-btn:hover {
  background: #5558e6;
  border-color: #5558e6;
}

.warn-btn {
  background: #f59e0b;
  border-color: #f59e0b;
}

.warn-btn:hover {
  background: #d97706;
  border-color: #d97706;
}

.table-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #e2e8f0;
}

.data-table :deep(.el-table__header th) {
  background: #fafafa;
  font-weight: 600;
  color: #374151;
}

.qty-positive {
  color: #10b981;
  font-weight: 600;
}

.qty-negative {
  color: #ef4444;
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.warn-dialog :deep(.data-table .el-table__header th) {
  background: #fafafa;
  font-weight: 600;
  color: #374151;
}
</style>
