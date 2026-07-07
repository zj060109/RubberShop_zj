<template>
  <div class="page">
    <el-card shadow="never" class="status-filter-card">
      <el-radio-group v-model="statusFilter" @change="onStatusChange">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="pending_quote">待报价</el-radio-button>
        <el-radio-button value="quoted">已报价</el-radio-button>
        <el-radio-button value="confirmed">已确认</el-radio-button>
        <el-radio-button value="converted">已转换</el-radio-button>
        <el-radio-button value="cancelled">已取消</el-radio-button>
      </el-radio-group>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <template #empty>
          <el-empty description="暂无定制记录" :image-size="80" />
        </template>
        <el-table-column prop="id_zj" label="定制ID" width="100" />
        <el-table-column prop="user_id_zj" label="顾客ID" width="100" />
        <el-table-column label="描述" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip :content="row.description_zj" placement="top" :show-after="300" effect="light" :hide-after="0">
              <span class="desc-text">{{ row.description_zj }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="报价总额" width="120" align="right">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.total_quoted_price_zj || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status_zj)" effect="light" size="default">
              {{ statusLabel(row.status_zj) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ row.created_at_zj }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="$router.push('/customizations/' + row.id_zj)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        background
        @current-change="loadData"
        @size-change="loadData"
        class="pagination-wrap"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomizationList } from '../../api/customization'

const statusFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

const statusMap = {
  pending_quote: '待报价',
  quoted: '已报价',
  confirmed: '已确认',
  converted: '已转换',
  cancelled: '已取消'
}

const statusTagType = (status) => {
  const map = { pending_quote: 'info', quoted: 'primary', confirmed: 'success', converted: 'primary', cancelled: 'danger' }
  return map[status] || 'info'
}

const statusLabel = (status) => statusMap[status] || status

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCustomizationList({ page: page.value, pageSize: pageSize.value, status: statusFilter.value })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const onStatusChange = () => {
  page.value = 1
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 60px);
}

.status-filter-card {
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

.table-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 12px rgba(0,0,0,0.04);
  border: 1px solid #e2e8f0;
}
.table-card :deep(.el-card__body) {
  padding: 20px;
}

.desc-text {
  display: inline-block;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}

.amount-text {
  font-weight: 600;
  color: var(--primary);
}

.pagination-wrap {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-radio-button__inner) {
  border-radius: 6px !important;
  border: 1px solid #e4e7ed;
  background: #fff;
  color: #606266;
  padding: 6px 18px;
}
:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
  box-shadow: none;
}

:deep(.el-table th.el-table__cell) {
  background: #f8f9fb;
  color: #4a5568;
  font-weight: 600;
  font-size: 13px;
}

:deep(.el-tag--default) {
  color: #fff;
  background-color: #8b5cf6;
  border-color: #8b5cf6;
}
</style>
