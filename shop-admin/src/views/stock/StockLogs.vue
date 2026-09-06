<template>
  <div class="page">
    <div class="toolbar">
      <div class="filter-group">
        <el-input v-model="productId" placeholder="商品ID" clearable class="input-sm" @keyup.enter="search" />
        <el-select v-model="typeFilter" placeholder="变动类型" clearable class="select-sm" @change="search">
          <el-option label="采购入库" value="purchase_in" />
          <el-option label="销售出库" value="sale_out" />
          <el-option label="手动入库" value="manual_in" />
          <el-option label="手动出库" value="manual_out" />
          <el-option label="退货入库" value="refund_in" />
        </el-select>
        <el-button type="primary" @click="search" class="btn-sm">搜索</el-button>
        <el-button type="warning" @click="showWarnings" class="btn-sm">库存预警</el-button>
      </div>
    </div>

    <div class="card-surface">
      <el-table :data="tableData" v-loading="loading" class="data-table">
        <el-table-column prop="product_id_zj" label="商品ID" width="90" />
        <el-table-column label="变动数量" width="110">
          <template #default="{ row }">
            <span class="qty" :class="row.change_quantity_zj > 0 ? 'qty-in' : 'qty-out'">
              {{ row.change_quantity_zj > 0 ? '+' : '' }}{{ row.change_quantity_zj }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="current_stock_zj" label="变动后库存" width="110" />
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <span class="type-tag" :class="'type-' + row.type_zj">{{ typeLabel(row.type_zj) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reference_id_zj" label="关联单号" width="130" />
        <el-table-column prop="remark_zj" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="时间" width="180">
          <template #default="{ row }">{{ formatTime(row.created_at_zj) }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @current-change="loadData"
          @size-change="loadData"
        />
      </div>
    </div>

    <el-dialog title="库存预警" v-model="warningVisible" width="680px" destroy-on-close>
      <el-table :data="warnings" v-loading="warningLoading" class="dialog-table">
        <template #empty>
          <div class="dialog-empty">暂无库存预警</div>
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
.page { padding: 32px 40px; }

.toolbar { display: flex; align-items: center; margin-bottom: 16px; }

.filter-group { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }

.input-sm { width: 160px; }
.select-sm { width: 150px; }
.btn-sm { border-radius: 8px; }

.card-surface {
  background: var(--c-surface); border-radius: 12px;
  border: 1px solid var(--c-border); overflow: hidden;
}

.data-table :deep(.el-table__header th) {
  background: var(--c-bg); color: var(--c-text-secondary);
  font-weight: 600; font-size: 12px; text-transform: uppercase;
  letter-spacing: 0.5px; border-bottom: 1px solid var(--c-border);
}
.data-table :deep(.el-table__body td) { border-color: var(--c-border); }
.data-table :deep(.el-table__row:hover > td) { background: var(--c-bg); }

.qty { font-weight: 600; font-size: 13px; }
.qty-in { color: var(--c-success); }
.qty-out { color: #dc2626; }

.type-tag {
  display: inline-block; padding: 2px 10px; border-radius: 6px;
  font-size: 12px; font-weight: 500;
}
.type-purchase_in, .type-manual_in, .type-refund_in { background: #dcfce7; color: var(--c-success); }
.type-sale_out { background: #fee2e2; color: #dc2626; }
.type-manual_out { background: #fef3c7; color: #92400e; }

.pagination-wrap { display: flex; justify-content: flex-end; padding: 16px; }

.dialog-table :deep(.el-table__header th) {
  background: var(--c-bg); color: var(--c-text-secondary);
  font-weight: 600; font-size: 12px;
}
.dialog-table :deep(.el-table__body td) { border-color: var(--c-border); }

.dialog-empty { padding: 40px 0; text-align: center; color: var(--c-text-muted); }
</style>
