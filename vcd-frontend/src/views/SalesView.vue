<template>
  <div class="sales-page">
    <h2 class="page-title">销售管理</h2>

    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增销售记录</el-button>
      <el-input
        v-model="keyword"
        class="search"
        clearable
        placeholder="搜索VCD名称或用户名"
        :prefix-icon="Search"
        @input="onSearchInput"
      />
    </div>

    <el-table :data="pagedData" v-loading="loading" stripe class="data-table" :header-cell-style="headerStyle">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column label="VCD名称" min-width="160">
        <template #default="{ row }">{{ row.vcd?.title || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户" width="110">
        <template #default="{ row }">{{ row.customer?.name || '-' }}</template>
      </el-table-column>
      <el-table-column label="销售日期" width="120" align="center">
        <template #default="{ row }">{{ formatSaleDate(row.saleDate) }}</template>
      </el-table-column>
      <el-table-column label="销售价格" width="110" align="center">
        <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openView(row)">查看</el-button>
          <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager-wrap">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="filteredData.length"
        :page-size="pageSize"
        v-model:current-page="currentPage"
      />
    </div>

    <!-- 新增 / 编辑 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" destroy-on-close @closed="onDialogClosed">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="VCD" prop="vcdId">
          <el-select v-model="form.vcdId" placeholder="请选择VCD" style="width: 100%" filterable>
            <el-option v-for="v in vcds" :key="v.id" :label="v.title" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%" filterable>
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="销售日期" prop="saleDate">
          <el-date-picker
            v-model="form.saleDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="销售价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 查看 -->
    <el-dialog v-model="viewVisible" title="销售记录详情" width="420px" destroy-on-close>
      <el-descriptions v-if="viewRow" :column="1" border>
        <el-descriptions-item label="ID">{{ viewRow.id }}</el-descriptions-item>
        <el-descriptions-item label="VCD名称">{{ viewRow.vcd?.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ viewRow.customer?.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="销售日期">{{ formatSaleDate(viewRow.saleDate) }}</el-descriptions-item>
        <el-descriptions-item label="销售价格">¥{{ Number(viewRow.price).toFixed(2) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="viewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { salesApi, vcdApi, customerApi } from '../api/index.js'

const tableData = ref([])
const vcds = ref([])
const customers = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = 10

const dialogVisible = ref(false)
const dialogMode = ref('add')
const dialogTitle = computed(() => (dialogMode.value === 'add' ? '新增销售记录' : '编辑销售记录'))
const submitting = ref(false)
const formRef = ref(null)
const editingId = ref(null)
const form = ref({
  vcdId: null,
  customerId: null,
  price: 0,
  saleDate: ''
})

const viewVisible = ref(false)
const viewRow = ref(null)

const rules = {
  vcdId: [{ required: true, message: '请选择VCD', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  saleDate: [{ required: true, message: '请选择销售日期', trigger: 'change' }],
  price: [{ required: true, message: '请输入销售价格', trigger: 'blur' }]
}

const headerStyle = () => ({ background: '#fafafa', color: '#262626' })

const filteredData = computed(() => {
  const k = keyword.value.trim().toLowerCase()
  if (!k) return tableData.value
  return tableData.value.filter((row) => {
    const title = (row.vcd?.title || '').toLowerCase()
    const name = (row.customer?.name || '').toLowerCase()
    return title.includes(k) || name.includes(k)
  })
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredData.value.slice(start, start + pageSize)
})

function onSearchInput() {
  currentPage.value = 1
}

function formatSaleDate(d) {
  if (!d) return '-'
  const s = String(d).slice(0, 10)
  const [y, m, day] = s.split('-').map(Number)
  if (!y) return s
  return `${m}/${day}/${y}`
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await salesApi.getAll()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openAdd() {
  dialogMode.value = 'add'
  editingId.value = null
  const t = new Date()
  const y = t.getFullYear()
  const m = String(t.getMonth() + 1).padStart(2, '0')
  const d = String(t.getDate()).padStart(2, '0')
  form.value = { vcdId: null, customerId: null, price: 0, saleDate: `${y}-${m}-${d}` }
  dialogVisible.value = true
}

function openEdit(row) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  form.value = {
    vcdId: row.vcd?.id ?? null,
    customerId: row.customer?.id ?? null,
    price: row.price,
    saleDate: String(row.saleDate).slice(0, 10)
  }
  dialogVisible.value = true
}

function openView(row) {
  viewRow.value = row
  viewVisible.value = true
}

function onDialogClosed() {
  formRef.value?.resetFields?.()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = {
      vcd: { id: form.value.vcdId },
      customer: { id: form.value.customerId },
      price: form.value.price,
      saleDate: form.value.saleDate || null
    }
    if (dialogMode.value === 'add') {
      await salesApi.create(payload)
      ElMessage.success('新增成功')
    } else {
      await salesApi.update(editingId.value, payload)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该销售记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await salesApi.delete(id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(async () => {
  loadData()
  const [vRes, cRes] = await Promise.all([vcdApi.getAll(), customerApi.getAll()])
  vcds.value = vRes.data || []
  customers.value = cRes.data || []
})
</script>

<style scoped>
.sales-page {
  background: #fff;
  border-radius: 8px;
  padding: 20px 24px 24px;
  min-height: calc(100vh - 120px);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.page-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: #262626;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.search {
  width: 280px;
}

.data-table {
  border-radius: 8px;
}

.pager-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
