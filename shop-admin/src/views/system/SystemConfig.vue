<template>
  <div class="page">
    <div class="toolbar-bar">
      <h2 class="page-title">系统配置</h2>
    </div>
    <el-card class="table-card">
      <el-table :data="configs" v-loading="loading" stripe class="data-table">
        <el-table-column prop="config_key_zj" label="配置键" width="220" />
        <el-table-column prop="config_value_zj" label="配置值" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="config-value">{{ row.config_value_zj || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark_zj" label="备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="config-remark">{{ row.remark_zj || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="175">
          <template #default="{ row }">
            {{ formatTime(row.updated_at_zj) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="showEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="编辑配置" v-model="editVisible" width="480px" class="edit-dialog" destroy-on-close @closed="editRow = null">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px" v-if="editRow" class="edit-form">
        <el-form-item label="配置键">
          <el-input :model-value="editRow.config_key_zj" disabled />
        </el-form-item>
        <el-form-item label="配置值" prop="value">
          <el-input v-model="editForm.value" type="textarea" :rows="3" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="editForm.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave" class="save-btn">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConfigs, updateConfig } from '../../api/config'

const configs = ref([])
const loading = ref(false)

const editVisible = ref(false)
const editRow = ref(null)
const editFormRef = ref(null)
const saving = ref(false)
const editForm = reactive({ value: '', remark: '' })
const editRules = {
  value: [{ required: true, message: '请输入配置值', trigger: 'blur' }]
}

const formatTime = (val) => {
  if (!val) return '-'
  return new Date(val).toLocaleString('zh-CN', { hour12: false })
}

const loadConfigs = async () => {
  loading.value = true
  try {
    const res = await getConfigs()
    configs.value = res.data || []
  } finally {
    loading.value = false
  }
}

const showEdit = (row) => {
  editRow.value = row
  editForm.value = row.config_value_zj || ''
  editForm.remark = row.remark_zj || ''
  editVisible.value = true
}

const handleSave = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await ElMessageBox.confirm('确定保存该配置更改？', '确认保存', { type: 'warning' })
  } catch {
    return
  }
  saving.value = true
  try {
    await updateConfig(editRow.value.config_key_zj, editForm.value, editForm.remark)
    ElMessage.success('配置更新成功')
    editVisible.value = false
    loadConfigs()
  } catch {} finally {
    saving.value = false
  }
}

onMounted(loadConfigs)
</script>

<style scoped>
.page {
  padding: 24px;
  background: #f8fafc;
  min-height: calc(100vh - 60px);
}

.toolbar-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
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
  border-bottom: 2px solid #e5e7eb;
}

.config-value {
  font-family: 'SF Mono', 'Menlo', 'Monaco', monospace;
  font-size: 13px;
  color: #374151;
}

.config-remark {
  color: #6b7280;
}

.edit-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
}

.save-btn {
  background: var(--primary);
  border-color: var(--primary);
}

.save-btn:hover {
  background: #5558e6;
  border-color: #5558e6;
}

.edit-dialog :deep(.el-input.is-disabled .el-input__wrapper) {
  background: #f5f5f5;
  box-shadow: none;
}
</style>
