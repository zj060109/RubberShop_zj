<template>
  <div class="page-container">
    <div class="card-toolbar">
      <div class="toolbar-left">
        <el-radio-group v-model="statusFilter" @change="onStatusChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="unpaid">待还</el-radio-button>
          <el-radio-button value="partially_paid">部分还</el-radio-button>
          <el-radio-button value="paid">已结清</el-radio-button>
          <el-radio-button value="void">已作废</el-radio-button>
        </el-radio-group>
      </div>
      <div class="toolbar-right">
        <el-input v-model="phoneSearch" placeholder="搜索顾客手机号" clearable class="search-input" @keyup.enter="search" />
        <el-button type="primary" class="search-btn" @click="search">搜索</el-button>
      </div>
    </div>

    <div class="card-table">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="顾客" min-width="120">
          <template #default="{ row }">
            <div>{{ row.customerName || '-' }}</div>
            <div class="sub-text">{{ row.customerPhone || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="订单号" min-width="180">
          <template #default="{ row }">
            <el-button v-if="row.orderNo" type="primary" link size="small" @click="$router.push('/orders/' + row.orderId)">
              {{ row.orderNo }}
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="应收金额" width="130" align="right">
          <template #default="{ row }">
            <span class="amount-primary">&yen;{{ formatNum(row.amountOwed) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="已还金额" width="130" align="right">
          <template #default="{ row }">
            <span class="amount-primary">&yen;{{ formatNum(row.amountPaid) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="剩余欠款" width="130" align="right">
          <template #default="{ row }">
            <span class="amount-primary">&yen;{{ formatNum(row.remaining) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ row.createdAt }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="showDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'unpaid' || row.status === 'partially_paid'" size="small" text type="success" @click="showRepay(row)">收款</el-button>
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

    <el-dialog v-model="detailVisible" title="应收账款详情" width="620px" destroy-on-close>
      <div v-if="detailData" class="detail-wrap">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="应收ID">{{ detailData.id }}</el-descriptions-item>
          <el-descriptions-item label="订单号">
            <el-button v-if="detailData.orderNo" type="primary" link size="small" @click="$router.push('/orders/' + detailData.orderId)">{{ detailData.orderNo }}</el-button>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="顾客姓名">{{ detailData.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailData.customerPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="公司名">{{ detailData.customerCompany || '-' }}</el-descriptions-item>
          <el-descriptions-item label="积分">{{ detailData.customerPoints ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="赊账额度">&yen;{{ detailData.customerCreditLimit ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detailData.status)" effect="light" size="small">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="应收金额">&yen;{{ formatNum(detailData.amountOwed) }}</el-descriptions-item>
          <el-descriptions-item label="已还金额">&yen;{{ formatNum(detailData.amountPaid) }}</el-descriptions-item>
          <el-descriptions-item label="剩余欠款">
            <span class="amount-owed">&yen;{{ formatNum(detailData.remaining) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailData.createdAt }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="detailData.receipts && detailData.receipts.length" class="receipts-section">
          <div class="section-title">收款记录</div>
          <el-table :data="detailData.receipts" size="small" stripe>
            <el-table-column prop="id_zj" label="ID" width="60" />
            <el-table-column label="金额" width="120" align="right">
              <template #default="{ row }">
                <span class="amount-primary">&yen;{{ formatNum(row.amount_zj) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="方式" width="110">
              <template #default="{ row }">
                <el-tag v-if="row.payment_method_zj === 'balance'" type="primary" size="small">余额</el-tag>
                <el-tag v-else-if="row.payment_method_zj === 'cash'" type="success" size="small">现金</el-tag>
                <el-tag v-else-if="row.payment_method_zj === 'bank_transfer'" type="warning" size="small">转账</el-tag>
                <span v-else>{{ row.payment_method_zj }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark_zj" label="备注" min-width="120" />
            <el-table-column prop="created_at_zj" label="收款时间" width="170" />
          </el-table>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="repayVisible" title="登记收款" width="440px" destroy-on-close>
      <el-form :model="repayForm" label-width="80px" v-if="repayTarget" class="repay-form">
        <el-form-item label="顾客">
          <span class="repay-info">{{ repayTarget.customerName || '-' }} ({{ repayTarget.customerPhone || '-' }})</span>
        </el-form-item>
        <el-form-item label="剩余欠款">
          <span class="repay-remaining">&yen;{{ formatNum(repayTarget.remaining) }}</span>
        </el-form-item>
        <el-form-item label="收款金额">
          <el-input-number v-model="repayForm.amount" :min="0" :step="0.01" :max="repayTarget.remaining" :precision="2" controls-position="right" class="full-input" />
        </el-form-item>
        <el-form-item label="收款方式">
          <el-select v-model="repayForm.paymentMethod" class="full-input">
            <el-option label="现金" value="cash" />
            <el-option label="银行转账" value="bank_transfer" />
            <el-option label="余额" value="balance" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="repayForm.remark" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="repayVisible = false">取消</el-button>
        <el-button type="primary" :loading="repaying" @click="handleRepay">确认收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReceivableList, getReceivableDetail, repayReceivable } from '../../api/credit'

const statusFilter = ref('')
const phoneSearch = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

const detailVisible = ref(false)
const detailData = ref(null)

const repayVisible = ref(false)
const repayTarget = ref(null)
const repaying = ref(false)
const repayForm = reactive({ amount: 0, paymentMethod: 'cash', remark: '' })

const statusMap = { unpaid: '待还', partially_paid: '部分还', paid: '已结清', void: '已作废' }
const statusTypeMap = { unpaid: 'danger', partially_paid: 'warning', paid: 'success', void: 'info' }

const statusLabel = (s) => statusMap[s] || s
const statusType = (s) => statusTypeMap[s] || 'info'
const formatNum = (v) => (v != null ? Number(v).toFixed(2) : '0.00')

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value, status: statusFilter.value }
    if (phoneSearch.value) params.phone = phoneSearch.value
    const res = await getReceivableList(params)
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

const search = () => {
  page.value = 1
  loadData()
}

const showDetail = async (row) => {
  try {
    const res = await getReceivableDetail(row.id)
    const d = res.data || {}
    const r = d.receivable || {}
    const ci = d.customerInfo || {}
    detailData.value = {
      id: r.id_zj,
      orderNo: ci.orderNo || null,
      orderId: r.order_id_zj,
      customerName: ci.customerName || row.customerName || null,
      customerPhone: ci.customerPhone || row.customerPhone || null,
      customerCompany: ci.customerCompany || row.customerCompany || null,
      customerPoints: ci.customerPoints,
      customerCreditLimit: ci.customerCreditLimit,
      status: r.status_zj,
      amountOwed: r.amount_owed_zj,
      amountPaid: r.amount_paid_zj,
      remaining: (r.amount_owed_zj || 0) - (r.amount_paid_zj || 0),
      createdAt: r.created_at_zj,
      receipts: d.receipts || []
    }
    detailVisible.value = true
  } catch {}
}

const showRepay = (row) => {
  repayTarget.value = row
  repayForm.amount = row.remaining
  repayForm.paymentMethod = 'cash'
  repayForm.remark = ''
  repayVisible.value = true
}

const handleRepay = async () => {
  if (!repayForm.amount || repayForm.amount <= 0) {
    ElMessage.warning('请输入收款金额')
    return
  }
  if (repayForm.amount > repayTarget.value.remaining) {
    ElMessage.warning('收款金额不能超过剩余欠款')
    return
  }
  repaying.value = true
  try {
    await repayReceivable(repayTarget.value.id, {
      amount: repayForm.amount,
      paymentMethod: repayForm.paymentMethod,
      remark: repayForm.remark
    })
    ElMessage.success('收款成功')
    repayVisible.value = false
    loadData()
  } catch {} finally {
    repaying.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 200px;
}

.sub-text {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.amount-primary {
  color: var(--primary);
  font-weight: 600;
}

.amount-owed {
  color: var(--primary-dark);
  font-weight: 700;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-wrap {
  max-height: 70vh;
  overflow-y: auto;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 20px 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.receipts-section {
  margin-top: 8px;
}

.repay-form .repay-info {
  font-size: 14px;
  color: #374151;
}

.repay-form .repay-remaining {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary);
}

.full-input {
  width: 100%;
}
.full-input :deep(.el-select) {
  width: 100%;
}
</style>
