<template>
  <div class="page-container category-page">
    <div class="card-toolbar">
      <el-button type="primary" @click="handleAdd(null)">
        <el-icon style="margin-right:4px"><Plus /></el-icon>添加顶级分类
      </el-button>
    </div>

    <div class="category-card" v-loading="loading">
      <template v-if="!loading && treeData.length === 0">
        <el-empty description="暂无分类，请添加" :image-size="80" />
      </template>
      <el-tree
        v-else
        :data="treeData"
        :props="{ label: 'name_zj', children: 'children' }"
        node-key="id_zj"
        default-expand-all
        highlight-current
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="tree-node-info">
              <span class="tree-node-name">{{ node.label }}</span>
              <span class="tree-node-sort">排序：{{ data.sort_zj || 0 }}</span>
            </div>
            <div class="tree-node-actions">
              <el-button size="small" type="primary" text @click.stop="handleAdd(data)">添加子分类</el-button>
              <el-button size="small" text @click.stop="handleEdit(data)">编辑</el-button>
              <el-button size="small" type="danger" text @click.stop="handleDelete(data)">删除</el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="440px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="80px" class="category-form">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dialogForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialogForm.sort" :min="0" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '../../api/category'

const treeData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogFormRef = ref(null)
const saving = ref(false)
const editingNode = ref(null)
const parentNode = ref(null)

const dialogForm = reactive({ name: '', sort: 0 })
const dialogRules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }
const dialogTitle = computed(() => editingNode.value ? '编辑分类' : '新增分类')

const loadTree = async () => {
  loading.value = true
  try {
    const res = await getCategoryTree()
    treeData.value = res.data || []
  } catch {} finally { loading.value = false }
}

const handleAdd = (parent) => {
  editingNode.value = null
  parentNode.value = parent
  dialogForm.name = ''
  dialogForm.sort = 0
  dialogVisible.value = true
}

const handleEdit = (node) => {
  editingNode.value = node
  parentNode.value = null
  dialogForm.name = node.name_zj
  dialogForm.sort = node.sort_zj || 0
  dialogVisible.value = true
}

const resetForm = () => {
  editingNode.value = null
  parentNode.value = null
}

const handleSave = async () => {
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (editingNode.value) {
      await updateCategory(editingNode.value.id_zj, {
        name: dialogForm.name,
        sort: dialogForm.sort,
        parentId: editingNode.value.parent_id_zj
      })
      ElMessage.success('修改成功')
    } else {
      await createCategory({
        name: dialogForm.name,
        sort: dialogForm.sort,
        parentId: parentNode.value ? parentNode.value.id_zj : 0
      })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadTree()
  } catch {} finally {
    saving.value = false
  }
}

const handleDelete = (node) => {
  ElMessageBox.confirm(
    '确定删除分类「' + node.name_zj + '」？如果存在子分类或关联商品将无法删除。',
    '确认删除',
    { type: 'warning' }
  ).then(async () => {
    try {
      await deleteCategory(node.id_zj)
      ElMessage.success('删除成功')
      loadTree()
    } catch {}
  }).catch(() => {})
}

onMounted(loadTree)
</script>

<style scoped>
.category-page { padding: 24px; }

.category-card {
  background: var(--bg-card);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
  padding: 24px;
}

.el-tree :deep(.el-tree-node__content) {
  height: 44px; border-radius: 8px; transition: background 0.15s; padding-right: 8px;
}
.el-tree :deep(.el-tree-node__content:hover) { background: #f8fafc; }
.el-tree :deep(.el-tree-node.is-current > .el-tree-node__content) { background: var(--primary-bg); }

.tree-node {
  flex: 1; display: flex; align-items: center;
  justify-content: space-between; min-width: 0;
}
.tree-node-info { display: flex; align-items: center; gap: 12px; min-width: 0; }
.tree-node-name { font-weight: 600; color: var(--text); font-size: 14px; }
.tree-node-sort {
  font-size: 12px; color: var(--text-muted);
  background: #f1f5f9; padding: 2px 10px; border-radius: 20px;
}
.tree-node-actions {
  display: flex; gap: 2px; flex-shrink: 0;
}

.category-form { padding-top: 8px; }
</style>
