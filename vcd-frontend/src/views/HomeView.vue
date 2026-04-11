<template>
  <!-- 单一根节点：避免外层 router-view + transition 对多根片段渲染异常 -->
  <div class="home-view-root">
    <DashboardBigScreen v-if="showBigScreen" @close="showBigScreen = false" />

    <div v-else class="dashboard" v-loading="loading">
      <div class="dashboard-head">
        <h2 class="greeting">欢迎回来，{{ displayName }}</h2>
        <button type="button" class="big-screen-btn" @click="showBigScreen = true">
          <span class="big-screen-btn__shine" aria-hidden="true" />
          <span class="big-screen-btn__label">切换可视化大屏</span>
        </button>
      </div>

      <div class="grid-top">
        <div class="cards-col">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-head">
            <span class="stat-label">今日销售额</span>
            <el-tag type="primary" size="small" effect="plain">今天</el-tag>
          </div>
          <div class="stat-value blue">¥{{ todaySalesAmount.toFixed(2) }}</div>
          <div class="stat-meta">订单数: {{ todaySalesCount }}</div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-head">
            <span class="stat-label">今日租赁收入</span>
            <el-tag type="success" size="small" effect="plain">今天</el-tag>
          </div>
          <div class="stat-value blue">¥{{ todayRentalIncome.toFixed(2) }}</div>
          <div class="stat-meta">租赁数: {{ todayRentalCount }}</div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-head">
            <span class="stat-label">当前库存总量</span>
          </div>
          <div class="stat-value green">{{ totalStock }}</div>
          <div class="stat-meta">VCD种类: {{ inventoryKinds }}</div>
        </el-card>

        <el-card class="stat-card" shadow="hover">
          <div class="stat-head">
            <span class="stat-label">逾期租赁数量</span>
            <el-tag type="danger" size="small" effect="plain">需处理</el-tag>
          </div>
          <div class="stat-value red">{{ overdueCount }}</div>
          <div class="stat-meta">未归还VCD数量: {{ overdueCount }}</div>
        </el-card>
        </div>

        <el-card class="rank-card" shadow="hover">
          <div class="card-title-row">
            <span class="panel-title">热门VCD排行榜</span>
            <el-radio-group v-model="rankRange" size="small" class="rank-range-toggle">
              <el-radio-button value="month">本月</el-radio-button>
              <el-radio-button value="all">全部时间</el-radio-button>
            </el-radio-group>
          </div>
          <ul class="rank-list">
            <li v-for="(item, idx) in popularList" :key="`${rankRange}-${idx}-${item.title}`" class="rank-row">
              <span class="rank-badge" :class="{ top: idx < 3 }">{{ idx + 1 }}</span>
              <span class="rank-title">{{ item.title }}</span>
              <span class="rank-val">{{ item.label }}</span>
            </li>
            <li v-if="!popularList.length" class="empty-hint">暂无数据</li>
          </ul>
        </el-card>
      </div>

      <el-card class="table-card" shadow="hover">
      <div class="card-title-row">
        <span class="panel-title">待归还列表</span>
        <el-link type="primary" :underline="false" @click="goRental">查看全部</el-link>
      </div>
      <el-table :data="pendingReturns" stripe class="pending-table">
        <el-table-column prop="vcdTitle" label="VCD名称" min-width="140" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="expectedReturnDate" label="预计归还日期" width="130" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.overdue" type="danger" effect="plain" class="status-tag">已逾期</el-tag>
            <el-tag v-else type="warning" effect="plain" class="status-tag">待归还</el-tag>
          </template>
        </el-table-column>
      </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { salesApi, rentalApi, inventoryApi } from '../api/index.js'
/** 同步引入：Spring Boot 静态资源下懒加载 chunk 易 404，导致大屏空白 */
import DashboardBigScreen from '../components/dashboard/DashboardBigScreen.vue'

const router = useRouter()
/** 首页内切换数据可视化大屏（不跳转路由，保持左侧菜单与顶栏） */
const showBigScreen = ref(false)
const loading = ref(true)
const sales = ref([])
const rentals = ref([])
const inventories = ref([])

/** 热门榜：month=本月销量，all=全部时间累计销量 */
const rankRange = ref('month')

const displayName = computed(() => {
  try {
    const token = localStorage.getItem('jwt_token')
    if (!token) return '用户'
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.sub || '用户'
  } catch {
    return '用户'
  }
})

function localTodayStr() {
  const t = new Date()
  const y = t.getFullYear()
  const m = String(t.getMonth() + 1).padStart(2, '0')
  const d = String(t.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function parseDate(s) {
  if (!s) return null
  const str = String(s).slice(0, 10)
  const [y, m, d] = str.split('-').map(Number)
  return new Date(y, m - 1, d)
}

const todayStr = localTodayStr()

const todaySalesAmount = computed(() =>
  sales.value.filter((r) => String(r.saleDate).slice(0, 10) === todayStr).reduce((a, r) => a + (Number(r.price) || 0), 0)
)
const todaySalesCount = computed(() =>
  sales.value.filter((r) => String(r.saleDate).slice(0, 10) === todayStr).length
)

const todayRentalIncome = computed(() =>
  rentals.value
    .filter((r) => String(r.rentalDate).slice(0, 10) === todayStr)
    .reduce((a, r) => a + (Number(r.deposit) || 0), 0)
)
const todayRentalCount = computed(() =>
  rentals.value.filter((r) => String(r.rentalDate).slice(0, 10) === todayStr).length
)

const totalStock = computed(() => inventories.value.reduce((a, inv) => a + (inv.stock || 0), 0))
const inventoryKinds = computed(() => inventories.value.length)

const overdueCount = computed(() => {
  const today = parseDate(todayStr)
  return rentals.value.filter((r) => {
    if (r.isReturned === true || r.returned === true) return false
    const due = parseDate(r.expectedReturnDate)
    return due && due < today
  }).length
})

/** 各 VCD 零售销量（销售记录条数）；按 rankRange 过滤本月或全部时间 */
const popularList = computed(() => {
  const now = new Date()
  const y = now.getFullYear()
  const mo = now.getMonth()
  const pad = (n) => String(n).padStart(2, '0')
  const monthPrefix = `${y}-${pad(mo + 1)}-`

  const map = new Map()
  for (const s of sales.value) {
    const d = String(s.saleDate).slice(0, 10)
    if (rankRange.value === 'month' && !d.startsWith(monthPrefix)) continue
    const title = s.vcd?.title || '未知'
    map.set(title, (map.get(title) || 0) + 1)
  }
  return [...map.entries()]
    .map(([title, count]) => ({ title, label: `${count}笔`, count }))
    .sort((a, b) => b.count - a.count)
    .map(({ title, label }) => ({ title, label }))
    .slice(0, 7)
})

const pendingReturns = computed(() => {
  const today = parseDate(todayStr)
  return rentals.value
    .filter((r) => !(r.isReturned === true || r.returned === true))
    .map((r) => {
      const due = parseDate(r.expectedReturnDate)
      const overdue = due && due < today
      return {
        vcdTitle: r.vcd?.title || '-',
        customerName: r.customer?.name || '-',
        expectedReturnDate: r.expectedReturnDate || '-',
        overdue
      }
    })
    .slice(0, 8)
})

function goRental() {
  router.push('/rental')
}

onMounted(async () => {
  loading.value = true
  try {
    const [sRes, rRes, iRes] = await Promise.all([
      salesApi.getAll(),
      rentalApi.getAll(),
      inventoryApi.getAll()
    ])
    sales.value = sRes.data || []
    rentals.value = rRes.data || []
    inventories.value = iRes.data || []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home-view-root {
  min-height: 40px;
}

.dashboard {
  max-width: 1280px;
  margin: 0 auto;
}

/* 欢迎语与「切换可视化大屏」同一行，右侧与下方排行榜列对齐 */
.dashboard-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.greeting {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #5d4037;
}

.grid-top {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
  margin-bottom: 20px;
}

/* 立体科技风主按钮：蓝色渐变 + 高光面 + 柔和阴影（宽度随内容，不占满整行） */
.big-screen-btn {
  position: relative;
  width: auto;
  min-width: 200px;
  padding: 12px 22px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  overflow: hidden;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
  background: linear-gradient(165deg, #4dabff 0%, #1890ff 42%, #096dd9 100%);
  box-shadow:
    0 4px 14px rgba(24, 144, 255, 0.42),
    0 1px 0 rgba(255, 255, 255, 0.35) inset,
    0 -2px 6px rgba(0, 40, 120, 0.12) inset;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.big-screen-btn:hover {
  transform: translateY(-2px);
  box-shadow:
    0 8px 22px rgba(24, 144, 255, 0.48),
    0 1px 0 rgba(255, 255, 255, 0.45) inset,
    0 -2px 6px rgba(0, 40, 120, 0.1) inset;
}

.big-screen-btn:active {
  transform: translateY(0);
}

.big-screen-btn__shine {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    105deg,
    transparent 0%,
    transparent 40%,
    rgba(255, 255, 255, 0.22) 48%,
    rgba(255, 255, 255, 0.08) 52%,
    transparent 60%
  );
  pointer-events: none;
}

.big-screen-btn__label {
  position: relative;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.18);
}

@media (max-width: 1100px) {
  .grid-top {
    grid-template-columns: 1fr;
  }
}

.cards-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-card :deep(.el-card__body) {
  padding: 16px 18px;
}

.stat-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #5d4037;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-value.blue {
  color: #1890ff;
}
.stat-value.green {
  color: #52c41a;
}
.stat-value.red {
  color: #f5222d;
}

.stat-meta {
  margin-top: 6px;
  font-size: 13px;
  color: #8c8c8c;
}

.rank-card :deep(.el-card__body),
.table-card :deep(.el-card__body) {
  padding: 16px 18px;
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.rank-range-toggle {
  flex-shrink: 0;
}

.rank-range-toggle :deep(.el-radio-button__inner) {
  padding: 5px 12px;
  font-size: 12px;
}

.rank-range-toggle :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #1890ff;
  border-color: #1890ff;
  color: #fff;
  box-shadow: -1px 0 0 0 #1890ff;
}

.rank-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.rank-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.rank-row:last-child {
  border-bottom: none;
}

.rank-badge {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #d9d9d9;
  color: #fff;
  font-size: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.rank-badge.top {
  background: #fa8c16;
}

.rank-title {
  flex: 1;
  color: #262626;
}

.rank-val {
  color: #8c8c8c;
}

.empty-hint {
  padding: 24px;
  text-align: center;
  color: #bfbfbf;
}

.pending-table {
  border-radius: 8px;
}

.status-tag {
  border-radius: 4px;
}
</style>
