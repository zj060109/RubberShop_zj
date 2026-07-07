<template>
  <div class="page-container detail-page" v-loading="loading">
    <div class="card-toolbar">
      <el-button text @click="$router.push('/orders')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回订单列表
      </el-button>
      <div class="header-actions">
        <el-button v-if="order.status_zj === 'paid'" type="success" @click="handleAccept">接单</el-button>
        <el-button v-if="order.status_zj === 'paid' || order.status_zj === 'accepted'" type="warning" @click="shipDialogVisible = true">发货</el-button>
        <el-button v-if="order.status_zj === 'shipped'" type="success" @click="handleReceive">确认收货</el-button>
        <el-button v-if="order.status_zj === 'paid' || order.status_zj === 'accepted' || order.status_zj === 'shipped_to_merchant'" type="danger" plain @click="handleCancel">取消订单</el-button>
        <el-button v-if="order.need_installation_zj && order.status_zj === 'paid'" type="primary" @click="installShipDialog = true">登记寄送商户</el-button>
        <el-button v-if="order.need_installation_zj && order.status_zj === 'shipped_to_merchant'" type="success" @click="handleMerchantReceive">商户收货</el-button>
        <el-button v-if="order.need_installation_zj && order.status_zj === 'installing'" type="primary" @click="installDialog = true">安装管理</el-button>
      </div>
    </div>

    <div class="cards-row">
      <div class="card-item">
        <div class="card-header">
          <span class="card-header-icon order-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg></span>
          订单信息
        </div>
        <div class="card-body">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">订单号</span>
              <span class="info-value order-no">{{ order.order_no_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">状态</span>
              <span class="info-value">
                <el-tag :type="statusTagType(order.status_zj)" effect="light" size="small">{{ statusLabel(order.status_zj) }}</el-tag>
                <el-tag v-if="order.need_installation_zj" type="danger" effect="plain" size="small" style="margin-left:6px">代安装</el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">顾客ID</span>
              <span class="info-value">{{ order.user_id_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">支付方式</span>
              <span class="info-value">
                <el-tag :type="order.payment_method_zj === 'balance' ? '' : 'warning'" effect="light" size="small">
                  {{ order.payment_method_zj === 'balance' ? '余额' : '赊账' }}
                </el-tag>
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">实付金额</span>
              <span class="info-value price">¥{{ order.actual_amount_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">下单时间</span>
              <span class="info-value">{{ order.created_at_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">支付时间</span>
              <span class="info-value">{{ order.paid_at_zj || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">发货时间</span>
              <span class="info-value">{{ order.shipped_at_zj || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">完成时间</span>
              <span class="info-value">{{ order.completed_at_zj || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="card-item">
        <div class="card-header">
          <span class="card-header-icon addr-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg></span>
          收货地址
        </div>
        <div class="card-body">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">收货人</span>
              <span class="info-value">{{ order.receiver_name_zj }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">联系电话</span>
              <span class="info-value">{{ order.receiver_phone_zj }}</span>
            </div>
            <div class="info-item full-width">
              <span class="info-label">详细地址</span>
              <span class="info-value">{{ order.province_zj }} {{ order.city_zj }} {{ order.district_zj }} {{ order.detail_address_zj }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="order.status_zj === 'shipped' || order.status_zj === 'completed' || order.express_company_zj" class="card-item logistics-card">
      <div class="card-header">
        <span class="card-header-icon logistics-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg></span>
        物流信息
      </div>
      <div class="card-body">
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">快递公司</span>
            <span class="info-value">{{ order.express_company_zj || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">快递单号</span>
            <span class="info-value tracking-no">{{ order.tracking_no_zj || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">发货时间</span>
            <span class="info-value">{{ order.shipped_at_zj || '-' }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="order.need_installation_zj" class="card-item install-card">
      <div class="card-header">
        <span class="card-header-icon install-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg></span>
        代安装服务
      </div>
      <div class="card-body">
        <div class="install-timeline">
          <div class="install-step" :class="{ active: order.status_zj === 'paid', done: order.status_zj !== 'paid' && order.customer_tracking_no_zj }">
            <div class="step-dot"></div>
            <div class="step-text">
              <div class="step-title">顾客寄送</div>
              <div class="step-detail" v-if="order.customer_express_company_zj">
                快递：{{ order.customer_express_company_zj }} {{ order.customer_tracking_no_zj }}
              </div>
              <div class="step-detail" v-else-if="order.need_installation_zj && order.status_zj === 'paid'">等待顾客寄送商品至商户</div>
            </div>
          </div>
          <div class="install-step" :class="{ active: order.status_zj === 'shipped_to_merchant', done: ['installing','installed','shipped','completed'].includes(order.status_zj) }">
            <div class="step-dot"></div>
            <div class="step-text">
              <div class="step-title">商户收货</div>
              <div class="step-detail" v-if="['installing','installed','shipped','completed'].includes(order.status_zj)">已收货</div>
            </div>
          </div>
          <div class="install-step" :class="{ active: order.status_zj === 'installing', done: ['installed','shipped','completed'].includes(order.status_zj) }">
            <div class="step-dot"></div>
            <div class="step-text">
              <div class="step-title">安装中</div>
            </div>
          </div>
          <div class="install-step" :class="{ active: order.status_zj === 'installed', done: ['shipped','completed'].includes(order.status_zj) }">
            <div class="step-dot"></div>
            <div class="step-text">
              <div class="step-title">安装完成</div>
            </div>
          </div>
        </div>

        <div v-if="installationData.video || installationData.images" class="install-media">
          <div class="section-title">安装成果</div>
          <div class="media-row">
            <div v-if="installationData.video" class="media-item">
              <span class="media-label">安装视频：</span>
              <a :href="installationData.video" target="_blank" class="media-link">{{ installationData.video }}</a>
            </div>
            <div v-if="installationData.images" class="media-item">
              <span class="media-label">安装图片：</span>
              <span class="media-link-text">{{ installationData.images }}</span>
            </div>
          </div>
          <div v-if="installationData.remark" class="install-remark">
            <span class="media-label">备注：</span>{{ installationData.remark }}
          </div>
        </div>
      </div>
    </div>

    <div class="card-item">
      <div class="card-header">
        <span class="card-header-icon items-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg></span>
        商品明细
      </div>
      <div class="card-body">
        <el-table :data="items" stripe class="items-table" size="large">
          <el-table-column prop="product_name_zj" label="商品名" min-width="200" />
          <el-table-column label="单价" width="120" align="right">
            <template #default="{ row }">¥{{ row.price_zj }}</template>
          </el-table-column>
          <el-table-column prop="quantity_zj" label="数量" width="100" align="center" />
          <el-table-column label="小计" width="130" align="right">
            <template #default="{ row }">¥{{ row.subtotal_zj }}</template>
          </el-table-column>
        </el-table>
        <div class="total-row">
          订单总金额：<span class="total-price">¥{{ order.actual_amount_zj }}</span>
        </div>
      </div>
    </div>

    <div class="card-item">
      <div class="card-header">
        <span class="card-header-icon timeline-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg></span>
        状态时间线
      </div>
      <div class="card-body">
        <el-timeline v-if="statusLogs.length" class="status-timeline">
          <el-timeline-item
            v-for="log in statusLogs"
            :key="log.id_zj"
            :timestamp="log.created_at_zj"
            placement="top"
            size="large"
          >
            <div class="timeline-item-content">
              <span class="timeline-from">{{ statusLabel(log.from_status_zj) }}</span>
              <span class="timeline-arrow">→</span>
              <span class="timeline-to">{{ statusLabel(log.to_status_zj) }}</span>
              <span v-if="log.remark_zj" class="timeline-remark">备注：{{ log.remark_zj }}</span>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无状态变更记录" :image-size="80" />
      </div>
    </div>

    <el-dialog v-model="shipDialogVisible" title="发货" width="460px" destroy-on-close>
      <el-form :model="shipForm" label-width="80px" class="ship-form">
        <el-form-item label="快递公司">
          <el-input v-model="shipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShip" :disabled="!shipForm.expressCompany || !shipForm.trackingNo">确认发货</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="installShipDialog" title="登记寄送商户" width="460px" destroy-on-close>
      <el-form :model="installShipForm" label-width="80px">
        <el-form-item label="快递公司">
          <el-input v-model="installShipForm.expressCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="installShipForm.trackingNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="installShipDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCustomerShip" :disabled="!installShipForm.expressCompany || !installShipForm.trackingNo">确认寄送</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="installDialog" title="安装管理" width="500px" destroy-on-close>
      <el-form :model="installForm" label-width="80px">
        <el-form-item label="安装视频">
          <el-input v-model="installForm.video" placeholder="视频链接" />
        </el-form-item>
        <el-form-item label="安装图片">
          <el-input v-model="installForm.images" placeholder="图片链接，逗号分隔" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="installForm.remark" type="textarea" :rows="2" placeholder="安装备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="installDialog = false">取消</el-button>
        <el-button type="success" @click="handleInstallComplete">标记安装完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getOrderDetail, updateOrderStatus, shipOrder, receiveOrder, cancelOrder, customerShip, merchantReceive, updateInstallation, getInstallation } from '../../api/order'

const route = useRoute()

const order = ref({})
const items = ref([])
const statusLogs = ref([])
const loading = ref(false)

const shipDialogVisible = ref(false)
const shipForm = ref({ expressCompany: '', trackingNo: '' })

const installationData = ref({})

const statusMap = {
  paid: '已支付',
  accepted: '已接单',
  shipped: '已发货',
  completed: '已完成',
  cancelled: '已取消',
  refunding: '退款中',
  refunded: '已退款',
  shipped_to_merchant: '已寄送商户',
  installing: '安装中',
  installed: '已安装'
}

const statusTagType = (status) => {
  const map = {
    paid: 'info',
    accepted: 'primary',
    shipped: 'warning',
    completed: 'success',
    cancelled: 'danger',
    refunding: 'danger',
    refunded: 'danger',
    shipped_to_merchant: 'warning',
    installing: '',
    installed: 'success'
  }
  return map[status] || 'info'
}

const statusLabel = (status) => statusMap[status] || status

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data.order || {}
    items.value = res.data.items || []
    statusLogs.value = res.data.statusLogs || []
    if (order.value.need_installation_zj) {
      loadInstallation()
    }
  } finally {
    loading.value = false
  }
}

const loadInstallation = async () => {
  try {
    const res = await getInstallation(order.value.id_zj)
    installationData.value = res.data || {}
  } catch {}
}

const handleAccept = async () => {
  try {
    await updateOrderStatus(order.value.id_zj, 'accepted')
    ElMessage.success('已接单')
    loadDetail()
  } catch {}
}

const handleShip = async () => {
  try {
    await shipOrder(order.value.id_zj, shipForm.value.expressCompany, shipForm.value.trackingNo)
    ElMessage.success('已发货')
    shipDialogVisible.value = false
    loadDetail()
  } catch {}
}

const handleReceive = async () => {
  try {
    await receiveOrder(order.value.id_zj)
    ElMessage.success('已确认收货')
    loadDetail()
  } catch {}
}

const handleCancel = () => {
  ElMessageBox.confirm('确定取消该订单？', '确认取消', { type: 'warning' }).then(async () => {
    try {
      await cancelOrder(order.value.id_zj)
      ElMessage.success('已取消')
      loadDetail()
    } catch {}
  }).catch(() => {})
}

const installShipDialog = ref(false)
const installShipForm = ref({ expressCompany: '', trackingNo: '' })

const handleCustomerShip = async () => {
  try {
    await customerShip(order.value.id_zj, installShipForm.value.expressCompany, installShipForm.value.trackingNo)
    ElMessage.success('已登记寄送')
    installShipDialog.value = false
    loadDetail()
  } catch {}
}

const handleMerchantReceive = async () => {
  try {
    await merchantReceive(order.value.id_zj)
    ElMessage.success('已收货，进入安装')
    loadDetail()
  } catch {}
}

const installDialog = ref(false)
const installForm = ref({ video: '', images: '', remark: '' })

const handleInstallComplete = async () => {
  try {
    await updateInstallation(order.value.id_zj, {
      video: installForm.value.video,
      images: installForm.value.images,
      remark: installForm.value.remark,
      status: 'installed'
    })
    ElMessage.success('安装完成')
    installDialog.value = false
    loadDetail()
  } catch {}
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.detail-page { padding: 24px; }

.back-btn { font-size: 14px; font-weight: 500; }
.back-btn .el-icon { margin-right: 4px; }

.header-actions { display: flex; gap: 8px; }

.cards-row { display: flex; gap: 16px; flex-wrap: wrap; }
.cards-row .card-item { flex: 1; }

.card-item {
  background: var(--bg-card);
  border-radius: var(--radius);
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border);
  overflow: hidden;
}

.card-header {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 24px; font-size: 15px; font-weight: 700; color: var(--text);
  border-bottom: 1px solid var(--border-light);
  background: #fafbff;
}
.card-header-icon {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 10px;
}
.card-header-icon svg { width: 18px; height: 18px; }
.order-icon { background: var(--primary-bg); color: var(--primary); }
.addr-icon { background: var(--success-bg); color: var(--success); }
.logistics-icon { background: var(--info-bg); color: var(--info); }
.items-icon { background: var(--warning-bg); color: var(--warning); }
.timeline-icon { background: #fce7f3; color: #ec4899; }

.card-body { padding: 20px 24px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px 40px; }
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-item.full-width { grid-column: 1 / -1; }
.info-label { font-size: 12px; color: var(--text-muted); font-weight: 500; letter-spacing: 0.3px; }
.info-value { font-size: 14px; color: var(--text); }
.info-value.order-no { font-family: monospace; font-weight: 600; }
.info-value.price { font-weight: 700; color: var(--primary); font-size: 18px; }
.info-value.tracking-no { font-family: monospace; font-weight: 500; color: var(--info); }

.total-row { margin-top: 20px; text-align: right; font-size: 14px; color: var(--text-secondary); padding-right: 4px; }
.total-price { color: var(--danger); font-size: 22px; font-weight: 800; margin-left: 8px; }

.status-timeline { padding: 8px 0; }
.timeline-item-content { font-size: 14px; color: var(--text); }
.timeline-from { color: var(--text-muted); }
.timeline-arrow { margin: 0 6px; color: var(--primary); }
.timeline-to { font-weight: 600; }
.timeline-remark { margin-left: 12px; color: var(--text-muted); font-size: 13px; }

.ship-form { padding-top: 8px; }

.install-card .install-icon { background: #fce7f3; color: #ec4899; }

.install-timeline {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  position: relative;
}
.install-timeline::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 30px;
  right: 30px;
  height: 2px;
  background: #e5e7eb;
  z-index: 0;
}
.install-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
  z-index: 1;
  flex: 1;
}
.step-dot {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #e5e7eb;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #e5e7eb;
}
.install-step.active .step-dot {
  background: #6366f1;
  box-shadow: 0 0 0 2px #c7d2fe;
  animation: pulse 1.5s infinite;
}
.install-step.done .step-dot {
  background: #10b981;
  box-shadow: 0 0 0 2px #a7f3d0;
}
.step-text { text-align: center; }
.step-title { font-size: 12px; font-weight: 600; color: #374151; }
.step-detail { font-size: 11px; color: #6b7280; margin-top: 2px; }
.install-step.done .step-title { color: #059669; }

@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 2px #c7d2fe; }
  50% { box-shadow: 0 0 0 6px rgba(99,102,241,0.1); }
}

.install-media { border-top: 1px solid #e5e7eb; padding-top: 16px; }
.section-title { font-size: 13px; font-weight: 600; color: #1e293b; margin-bottom: 10px; }
.media-row { display: flex; flex-direction: column; gap: 8px; }
.media-item { font-size: 13px; }
.media-label { color: #6b7280; }
.media-link { color: #6366f1; }
.media-link-text { color: #374151; }
.install-remark { margin-top: 10px; font-size: 13px; color: #374151; }
</style>
