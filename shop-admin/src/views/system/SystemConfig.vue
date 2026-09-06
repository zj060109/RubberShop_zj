<template>
  <div class="page">
    <div class="toolbar">
      <h2 class="page-title">系统配置</h2>
    </div>

    <div class="card-surface">
      <el-table :data="configs" v-loading="loading" class="data-table">
        <el-table-column prop="config_key_zj" label="配置键" width="240">
          <template #default="{ row }">
            <span class="key-mono">{{ row.config_key_zj }}</span>
          </template>
        </el-table-column>
        <el-table-column label="配置值" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="value-mono">{{ row.config_value_zj || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="remark-text">{{ row.remark_zj || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="180">
          <template #default="{ row }">{{ formatTime(row.updated_at_zj) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <button class="edit-link" @click="showEdit(row)">编辑</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="编辑配置" v-model="editVisible" width="480px" destroy-on-close @closed="editRow = null" class="edit-dialog">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-position="top" v-if="editRow">
        <el-form-item label="配置键">
          <el-input :model-value="editRow.config_key_zj" disabled class="disabled-input" />
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
        <el-button type="primary" :loading="saving" @click="handleSave" class="primary-btn">保存</el-button>
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
.page { padding: 32px 40px; }

.page-title { font-size: 20px; font-weight: 700; color: var(--c-text); margin: 0; }
.toolbar { margin-bottom: 16px; }

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

.key-mono {
  font-family: 'SF Mono','Menlo',monospace; font-size: 13px;
  font-weight: 600; color: var(--c-text);
}
.value-mono {
  font-family: 'SF Mono','Menlo',monospace; font-size: 13px;
  color: var(--c-text);
}
.remark-text { color: var(--c-text-secondary); font-size: 13px; }

.edit-link {
  padding: 4px 12px; border-radius: 6px; border: none;
  background: transparent; color: var(--c-primary);
  font-size: 13px; font-weight: 500; cursor: pointer; transition: all .15s;
}
.edit-link:hover { background: #e0e7ff; }

.edit-dialog :deep(.el-form-item__label) {
  font-size: 12px; font-weight: 600; color: var(--c-text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}

.disabled-input :deep(.el-input__wrapper) {
  background: var(--c-bg); box-shadow: none;
}

.primary-btn { border-radius: 8px; }
</style>
