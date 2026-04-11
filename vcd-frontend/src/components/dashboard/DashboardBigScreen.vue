<template>
  <div class="big-screen" v-loading="loading">
    <!-- 顶部：标题与返回，风格与后台一致 -->
    <div class="screen-header">
      <div class="screen-title-wrap">
        <h2 class="screen-title">数据可视化大屏</h2>
        <span class="screen-sub">VCD 租赁业务总览</span>
      </div>
      <button type="button" class="big-screen-btn" @click="$emit('close')">
        <span class="big-screen-btn__shine" aria-hidden="true" />
        <span class="big-screen-btn__label">
          <el-icon class="back-btn__icon"><ArrowLeft /></el-icon>
          返回工作台
        </span>
      </button>
    </div>

    <!-- 四张核心指标卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-label">总租赁数</div>
          <div class="kpi-value primary">{{ stats.totalRentals }}</div>
          <div class="kpi-desc">历史租赁单据总量</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-label">影片种类</div>
          <div class="kpi-value primary">{{ stats.totalVcds }}</div>
          <div class="kpi-desc">在库 VCD 影片数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-label">注册客户数</div>
          <div class="kpi-value primary">{{ stats.totalCustomers }}</div>
          <div class="kpi-desc">客户档案总数</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="kpi-card kpi-card--warn" shadow="hover">
          <div class="kpi-label">当前逾期数</div>
          <div class="kpi-value danger">{{ stats.overdueCount }}</div>
          <div class="kpi-desc">未归还且已超过应还日</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区：两列卡片 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span class="card-head-title">影片租赁次数 TOP10</span>
          </template>
          <div ref="chartBarRef" class="chart-box" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span class="card-head-title">月度租赁趋势</span>
          </template>
          <div ref="chartLineRef" class="chart-box" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span class="card-head-title">影片分类占比</span>
          </template>
          <div ref="chartPieRef" class="chart-box" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span class="card-head-title">客户租赁频次分布</span>
          </template>
          <div ref="chartFreqRef" class="chart-box" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近租赁表格 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <span class="card-head-title">最近租赁记录</span>
      </template>
      <el-table :data="recentRentals" stripe border class="rent-table" max-height="360">
        <el-table-column prop="vcdTitle" label="VCD 片名" min-width="140" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户" width="110" />
        <el-table-column prop="rentalDate" label="租赁日期" width="120" align="center" />
        <el-table-column prop="expectedReturnDate" label="预计归还" width="120" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.returned" type="success" effect="plain" size="small">已归还</el-tag>
            <el-tag v-else type="warning" effect="plain" size="small">在租</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-alert
      v-if="usingMockHint"
      class="mock-hint"
      type="info"
      :closable="false"
      show-icon
      title="当前为演示数据：接口无租赁记录或请求失败时，大屏自动填充示例数据以便预览图表效果。接入真实数据后将自动切换。"
    />
  </div>
</template>

<script setup>
/**
 * VCD 租赁管理 — 数据可视化大屏
 * 依赖：echarts（已在 package.json 中声明）、Element Plus、现有 api 封装
 * 数据：优先 rental / vcd / customer / category 接口；无数据时使用内置模拟数据
 */
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { LabelLayout, UniversalTransition } from 'echarts/features'
import { rentalApi, vcdApi, customerApi } from '../../api/index.js'

echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  BarChart,
  LineChart,
  PieChart,
  CanvasRenderer,
  LabelLayout,
  UniversalTransition
])

defineEmits(['close'])

/** Element Plus 主色蓝，与后台统一 */
const BRAND = '#1890ff'
const BRAND_LIGHT = '#69c0ff'
const BRAND_DEEP = '#096dd9'
const TEXT_MAIN = '#262626'
const TEXT_SUB = '#8c8c8c'
const BORDER = '#f0f0f0'

const loading = ref(true)
const usingMockHint = ref(false)

const rentals = ref([])
const vcds = ref([])
const customers = ref([])

const stats = reactive({
  totalRentals: 0,
  totalVcds: 0,
  totalCustomers: 0,
  overdueCount: 0
})

const recentRentals = ref([])

const chartBarRef = ref(null)
const chartLineRef = ref(null)
const chartPieRef = ref(null)
const chartFreqRef = ref(null)

/** @type {import('echarts/core').EChartsType[]} */
let chartInstances = []

function parseLocalDate(s) {
  if (!s) return null
  const str = String(s).slice(0, 10)
  const [y, m, d] = str.split('-').map(Number)
  if (!y || !m || !d) return null
  return new Date(y, m - 1, d)
}

function todayDate() {
  const t = new Date()
  const y = t.getFullYear()
  const m = String(t.getMonth() + 1).padStart(2, '0')
  const d = String(t.getDate()).padStart(2, '0')
  return new Date(y, t.getMonth(), t.getDate())
}

/** 兼容 Jackson 对 boolean isReturned 的序列化 */
function rentalReturned(r) {
  return r.isReturned === true || r.returned === true
}

/** 无业务数据时注入模拟数据，保证图表可展示 */
function applyMockData() {
  usingMockHint.value = true
  const mockTitles = ['功夫', '无间道', '英雄', '我不是药神', '让子弹飞', '泰坦尼克号', '阿凡达', '盗梦空间', '星际穿越', '肖申克的救赎']
  const mockCustomers = ['张伟', '王芳', '李娜', '赵丽', '刘强']
  const mockCats = ['动作', '剧情', '喜剧', '科幻', '爱情']
  const list = []
  const base = new Date()
  for (let i = 0; i < 48; i++) {
    const day = new Date(base.getFullYear(), base.getMonth() - (i % 6), Math.max(1, (i % 28) + 1))
    const y = day.getFullYear()
    const mo = String(day.getMonth() + 1).padStart(2, '0')
    const dd = String((i % 27) + 1).padStart(2, '0')
    list.push({
      id: i + 1,
      rentalDate: `${y}-${mo}-${dd}`,
      expectedReturnDate: `${y}-${mo}-${String(Math.min(28, Number(dd) + 4)).padStart(2, '0')}`,
      returned: i % 4 === 0,
      isReturned: i % 4 === 0,
      vcd: { title: mockTitles[i % mockTitles.length], category: { name: mockCats[i % mockCats.length] } },
      customer: { id: (i % mockCustomers.length) + 1, name: mockCustomers[i % mockCustomers.length] }
    })
  }
  rentals.value = list
  vcds.value = mockTitles.map((title, i) => ({
    id: i + 1,
    title,
    category: { name: mockCats[i % mockCats.length] }
  }))
  customers.value = mockCustomers.map((name, i) => ({ id: i + 1, name }))
}

function computeStats() {
  const today = todayDate()
  stats.totalRentals = rentals.value.length
  stats.totalVcds = vcds.value.length
  stats.totalCustomers = customers.value.length
  stats.overdueCount = rentals.value.filter((r) => {
    if (rentalReturned(r)) return false
    const due = parseLocalDate(r.expectedReturnDate)
    return due && due < today
  }).length
}

/** 各片名租赁次数 → TOP10 */
function buildTop10Rentals() {
  const map = new Map()
  for (const r of rentals.value) {
    const t = r.vcd?.title || '未知片名'
    map.set(t, (map.get(t) || 0) + 1)
  }
  return [...map.entries()]
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
}

/** 按租赁日期 yyyy-MM 聚合 */
function buildMonthlyTrend() {
  const map = new Map()
  for (const r of rentals.value) {
    const d = String(r.rentalDate || '').slice(0, 7)
    if (d.length < 7) continue
    map.set(d, (map.get(d) || 0) + 1)
  }
  const months = [...map.keys()].sort()
  return {
    months,
    counts: months.map((m) => map.get(m))
  }
}

/** 按 VCD 分类统计租赁次数（饼图用「租赁次数」更贴近业务） */
function buildCategoryPie() {
  const catMap = new Map()
  const titleToCat = new Map()
  for (const v of vcds.value) {
    const name = v.category?.name || '未分类'
    titleToCat.set(v.title, name)
  }
  for (const r of rentals.value) {
    const title = r.vcd?.title
    const cat = r.vcd?.category?.name || (title ? titleToCat.get(title) : null) || '未分类'
    catMap.set(cat, (catMap.get(cat) || 0) + 1)
  }
  return [...catMap.entries()].map(([name, value]) => ({ name, value }))
}

/** 每位客户租赁次数 → 区间人数分布 */
function buildCustomerFreqBuckets() {
  const byId = new Map()
  for (const r of rentals.value) {
    const id = r.customer?.id
    if (id == null) continue
    byId.set(id, (byId.get(id) || 0) + 1)
  }
  let b1 = 0
  let b2 = 0
  let b35 = 0
  let b6 = 0
  for (const cnt of byId.values()) {
    if (cnt === 1) b1++
    else if (cnt === 2) b2++
    else if (cnt >= 3 && cnt <= 5) b35++
    else if (cnt >= 6) b6++
  }
  return {
    labels: ['租赁 1 次', '租赁 2 次', '租赁 3～5 次', '租赁 6 次及以上'],
    values: [b1, b2, b35, b6]
  }
}

function buildRecentTable() {
  const rows = rentals.value
    .map((r) => ({
      vcdTitle: r.vcd?.title || '-',
      customerName: r.customer?.name || '-',
      rentalDate: String(r.rentalDate || '').slice(0, 10),
      expectedReturnDate: String(r.expectedReturnDate || '').slice(0, 10),
      returned: rentalReturned(r),
      _sort: parseLocalDate(r.rentalDate)?.getTime() || 0
    }))
    .sort((a, b) => b._sort - a._sort)
    .slice(0, 12)
  rows.forEach((x) => delete x._sort)
  recentRentals.value = rows
}

function baseChartOptions() {
  return {
    animationDuration: 750,
    animationEasing: 'cubicOut',
    textStyle: { fontFamily: 'Microsoft YaHei, PingFang SC, sans-serif' }
  }
}

function initBarChart() {
  const el = chartBarRef.value
  if (!el) return
  const top10 = buildTop10Rentals()
  const names = top10.map(([t]) => (t.length > 14 ? `${t.slice(0, 14)}…` : t)).reverse()
  const vals = top10.map(([, v]) => v).reverse()
  const chart = echarts.init(el)
  chart.setOption({
    ...baseChartOptions(),
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 12, right: 24, top: 16, bottom: 8, containLabel: true },
    xAxis: { type: 'value', axisLine: { lineStyle: { color: BORDER } }, splitLine: { lineStyle: { color: '#fafafa' } } },
    yAxis: {
      type: 'category',
      data: names,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: TEXT_SUB, fontSize: 12 }
    },
    series: [
      {
        type: 'bar',
        data: vals,
        barMaxWidth: 28,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: BRAND_LIGHT },
              { offset: 1, color: BRAND }
            ]
          },
          borderRadius: [0, 6, 6, 0]
        }
      }
    ]
  })
  chartInstances.push(chart)
}

function initLineChart() {
  const el = chartLineRef.value
  if (!el) return
  const { months, counts } = buildMonthlyTrend()
  const chart = echarts.init(el)
  chart.setOption({
    ...baseChartOptions(),
    tooltip: { trigger: 'axis' },
    grid: { left: 12, right: 12, top: 28, bottom: 0, containLabel: true },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: BORDER } },
      axisLabel: { color: TEXT_SUB, rotate: months.length > 8 ? 30 : 0 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: '#fafafa' } },
      axisLabel: { color: TEXT_SUB }
    },
    series: [
      {
        type: 'line',
        data: counts,
        smooth: true,
        symbol: 'circle',
        symbolSize: 7,
        lineStyle: { width: 2.5, color: BRAND },
        itemStyle: { color: BRAND, borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(24,144,255,0.22)' },
              { offset: 1, color: 'rgba(24,144,255,0.02)' }
            ]
          }
        }
      }
    ]
  })
  chartInstances.push(chart)
}

function initPieChart() {
  const el = chartPieRef.value
  if (!el) return
  const data = buildCategoryPie()
  const chart = echarts.init(el)
  const colors = [BRAND, BRAND_DEEP, '#597ef7', '#36cfc9', '#73d13d', '#ffc53d', '#ff7a45', '#9254de']
  chart.setOption({
    ...baseChartOptions(),
    tooltip: { trigger: 'item', formatter: '{b}<br/>租赁次数：{c}（{d}%）' },
    legend: { bottom: 0, type: 'scroll', textStyle: { color: TEXT_SUB, fontSize: 12 } },
    series: [
      {
        type: 'pie',
        radius: ['40%', '68%'],
        center: ['50%', '46%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { color: TEXT_MAIN, fontSize: 12 },
        data,
        color: colors
      }
    ]
  })
  chartInstances.push(chart)
}

function initFreqChart() {
  const el = chartFreqRef.value
  if (!el) return
  const { labels, values } = buildCustomerFreqBuckets()
  const chart = echarts.init(el)
  chart.setOption({
    ...baseChartOptions(),
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 12, right: 12, top: 16, bottom: 32, containLabel: true },
    xAxis: {
      type: 'category',
      data: labels,
      axisLine: { lineStyle: { color: BORDER } },
      axisLabel: { color: TEXT_SUB, fontSize: 11, interval: 0, rotate: 12 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: '#fafafa' } },
      axisLabel: { color: TEXT_SUB }
    },
    series: [
      {
        type: 'bar',
        data: values,
        barMaxWidth: 36,
        itemStyle: {
          color: (params) => {
            const blues = [BRAND, '#40a9ff', BRAND_DEEP, '#597ef7']
            return blues[params.dataIndex % blues.length]
          },
          borderRadius: [6, 6, 0, 0]
        }
      }
    ]
  })
  chartInstances.push(chart)
}

function disposeCharts() {
  chartInstances.forEach((c) => c.dispose())
  chartInstances = []
}

function renderAllCharts() {
  disposeCharts()
  initBarChart()
  initLineChart()
  initPieChart()
  initFreqChart()
  nextTick(() => {
    chartInstances.forEach((c) => c.resize())
  })
}

function onResize() {
  chartInstances.forEach((c) => c.resize())
}

async function loadData() {
  loading.value = true
  usingMockHint.value = false
  try {
    const [rRes, vRes, cRes] = await Promise.all([rentalApi.getAll(), vcdApi.getAll(), customerApi.getAll()])
    rentals.value = rRes.data || []
    vcds.value = vRes.data || []
    customers.value = cRes.data || []

    if (!rentals.value.length) {
      applyMockData()
    }

    computeStats()
    buildRecentTable()
    await nextTick()
    renderAllCharts()
  } catch (e) {
    console.error(e)
    ElMessage.error('大屏数据加载失败，已切换演示数据')
    applyMockData()
    computeStats()
    buildRecentTable()
    await nextTick()
    renderAllCharts()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  disposeCharts()
})
</script>

<style scoped>
.big-screen {
  max-width: 1320px;
  margin: 0 auto;
  padding-bottom: 24px;
}

.screen-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.screen-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.screen-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #5d4037;
}

.screen-sub {
  font-size: 13px;
  color: #8c8c8c;
}

/* 与首页「切换可视化大屏」同款的立体科技风主按钮 */
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
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.18);
}

.back-btn__icon {
  font-size: 18px;
}

.stat-row {
  margin-bottom: 16px;
}

.kpi-card :deep(.el-card__body) {
  padding: 18px 20px;
}

.kpi-label {
  font-size: 14px;
  color: #5d4037;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 30px;
  font-weight: 700;
  line-height: 1.2;
}

.kpi-value.primary {
  color: #1890ff;
}

.kpi-value.danger {
  color: #f5222d;
}

.kpi-desc {
  margin-top: 8px;
  font-size: 12px;
  color: #8c8c8c;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card :deep(.el-card__header) {
  padding: 12px 18px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-card :deep(.el-card__body) {
  padding: 12px 14px 16px;
}

.card-head-title {
  font-size: 15px;
  font-weight: 600;
  color: #262626;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.table-card :deep(.el-card__header) {
  padding: 12px 18px;
}

.table-card :deep(.el-card__body) {
  padding: 0 18px 18px;
}

.rent-table {
  border-radius: 0 0 8px 8px;
}

.mock-hint {
  margin-top: 16px;
  border-radius: 8px;
}
</style>
