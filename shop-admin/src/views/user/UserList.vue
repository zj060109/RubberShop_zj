<template>
  <div class="page">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="phone" placeholder="搜索手机号" clearable class="input-field" @keyup.enter="search" />
        <el-select v-model="roleFilter" placeholder="角色筛选" clearable class="select-field" @change="search">
          <el-option label="客户" value="customer" />
          <el-option label="商户" value="merchant" />
          <el-option label="工厂" value="factory" />
        </el-select>
        <el-button type="primary" class="search-btn" @click="search">搜索</el-button>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe class="data-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="姓名" width="140">
          <template #default="{ row }">
            <span class="user-name">{{ row.role === 'factory' ? (row.companyName || row.realName || '-') : (row.realName || '-') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="roleTag(row.role)" size="small" effect="light">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="积分" width="80" align="center">
          <template #default="{ row }">
            <span :class="{ 'points-unlock': row.points >= 10 }">{{ row.points ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="余额" width="110" align="right">
          <template #default="{ row }">
            <span class="currency">&yen;{{ row.balance ?? 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              active-text="启用"
              inactive-text="禁用"
              inline-prompt
              size="small"
              class="status-switch"
              @change="(val) => toggleStatus(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="175">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text @click="showDetail(row)">详情</el-button>
            <el-button size="small" type="primary" plain @click="showAdjust(row)">调整</el-button>
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
    </div>

    <el-dialog title="用户详情" v-model="detailVisible" width="500px" class="detail-dialog" destroy-on-close>
      <el-descriptions v-if="detailUser" :column="1" border>
        <el-descriptions-item label="ID">{{ detailUser.id }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailUser.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel(detailUser.role) }}</el-descriptions-item>
        <el-descriptions-item label="余额">&yen;{{ detailUser.balance ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="积分">{{ detailUser.points ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="公司名称">{{ detailUser.companyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailUser.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatTime(detailUser.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="调整用户" v-model="adjustVisible" width="460px" class="adjust-dialog" destroy-on-close @closed="adjustUser = null">
      <el-form ref="adjustFormRef" :model="adjustForm" label-width="100px" v-if="adjustUser" class="adjust-form">
        <el-form-item label="余额变动">
          <el-input-number v-model="adjustForm.balance" placeholder="正数增加 / 负数减少" controls-position="right" class="full-input" />
          <div class="form-hint">正数增加余额，负数减少余额，0 表示不变</div>
        </el-form-item>
        <el-form-item label="积分调整">
          <el-input-number v-model="adjustForm.points" :min="0" placeholder="直接设置积分" controls-position="right" class="full-input" />
          <div class="form-hint">直接设置积分值，达到10分自动解锁赊账</div>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="adjustForm.status" active-text="启用" inactive-text="禁用" class="status-switch" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjusting" @click="handleAdjust" class="confirm-btn">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, updateUser } from '../../api/user'

const phone = ref('')
const roleFilter = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const detailUser = ref(null)

const adjustVisible = ref(false)
const adjustUser = ref(null)
const adjustFormRef = ref(null)
const adjusting = ref(false)
const adjustForm = reactive({ balance: 0, points: 0, status: true })

const roleMap = { customer: '客户', merchant: '商户', factory: '工厂' }
const roleTagMap = { customer: 'info', merchant: 'success', factory: 'warning' }

const roleLabel = (role) => roleMap[role] || role
const roleTag = (role) => roleTagMap[role] || ''

const formatTime = (val) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, pageSize: pageSize.value, phone: phone.value, role: roleFilter.value })
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

const toggleStatus = async (row, val) => {
  try {
    await updateUser(row.id, { status: val ? 1 : 0 })
    row.status = val ? 1 : 0
    ElMessage.success('状态更新成功')
  } catch {}
}

const showDetail = (row) => {
  detailUser.value = row
  detailVisible.value = true
}

const showAdjust = (row) => {
  adjustUser.value = row
  adjustForm.balance = 0
  adjustForm.points = row.points ?? 0
  adjustForm.status = row.status === 1
  adjustVisible.value = true
}

const handleAdjust = async () => {
  adjusting.value = true
  try {
    const data = {}
    if (adjustForm.balance !== 0) {
      data.balance = adjustForm.balance
    }
    data.points = adjustForm.points
    data.status = adjustForm.status ? 1 : 0
    await updateUser(adjustUser.value.id, data)
    ElMessage.success('调整成功')
    adjustVisible.value = false
    loadData()
  } catch {} finally {
    adjusting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page {
  padding: 32px 40px;
  background: var(--c-bg, #fafafa);
  min-height: calc(100vh - 60px);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  background: var(--c-surface, #fff);
  border-radius: 12px;
  border: 1px solid var(--c-border, #eaeaea);
  padding: 16px 20px;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.input-field {
  width: 200px;
}

.select-field {
  width: 140px;
}

.search-btn {
  background: var(--c-primary, #5c6cf0);
  border-color: var(--c-primary, #5c6cf0);
}

.search-btn:hover {
  background: #4b55d9;
  border-color: #4b55d9;
}

.table-card {
  background: var(--c-surface, #fff);
  border-radius: 12px;
  border: 1px solid var(--c-border, #eaeaea);
  overflow: hidden;
}

.data-table :deep(.el-table__header th) {
  background: var(--c-bg, #fafafa);
  font-weight: 600;
  color: var(--c-text, #171717);
  border-bottom: 1px solid var(--c-border, #eaeaea);
}

.data-table :deep(.el-table td) {
  color: var(--c-text, #171717);
}

.data-table :deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: var(--c-bg, #fafafa);
}

.user-name {
  font-weight: 500;
  color: var(--c-text, #171717);
}

.currency {
  color: var(--c-primary, #5c6cf0);
  font-weight: 600;
  font-family: 'SF Mono', 'Fira Code', 'Cascadia Code', 'Consolas', monospace;
}

.points-unlock {
  color: var(--c-success, #16a34a);
  font-weight: 700;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 0 20px 20px;
}

.status-switch :deep(.el-switch__core) {
  --el-switch-on-color: var(--c-success, #16a34a);
}

.status-switch :deep(.el-switch.is-checked .el-switch__core) {
  background: var(--c-success, #16a34a);
  border-color: var(--c-success, #16a34a);
}

.detail-dialog :deep(.el-descriptions__label) {
  font-weight: 500;
  color: var(--c-text-secondary, #6b7280);
  background: var(--c-bg, #fafafa);
}

.adjust-form .form-hint {
  font-size: 12px;
  color: var(--c-text-muted, #9ca3af);
  margin-top: 4px;
}

.full-input {
  width: 100%;
}

.full-input :deep(.el-input-number) {
  width: 100%;
}

.confirm-btn {
  background: var(--c-primary, #5c6cf0);
  border-color: var(--c-primary, #5c6cf0);
}

.confirm-btn:hover {
  background: #4b55d9;
  border-color: #4b55d9;
}
</style>
