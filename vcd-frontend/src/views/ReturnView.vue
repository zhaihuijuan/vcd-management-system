<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">归还记录</h2>
      <el-button type="success" :icon="Plus" @click="openAddDialog">新增归还</el-button>
    </div>

    <el-table :data="tableData" stripe border v-loading="loading" class="data-table">
      <el-table-column label="ID" width="90" align="center">
        <template #default="{ row }">
          <template v-if="row.id != null && row.id < 0">
            <el-tooltip content="由租赁记录补全，无独立归还表记录" placement="top">
              <span>{{ row.rental?.id ?? '—' }}</span>
            </el-tooltip>
          </template>
          <template v-else>{{ row.id }}</template>
        </template>
      </el-table-column>
      <el-table-column label="VCD片名" min-width="120">
        <template #default="{ row }">{{ row.vcd?.title || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户姓名" width="100">
        <template #default="{ row }">{{ row.customer?.name || '-' }}</template>
      </el-table-column>
      <el-table-column prop="returnDate" label="归还日期" width="120" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 'NORMAL' ? 'success' : row.status === 'OVERDUE' ? 'warning' : 'danger'">
            {{ row.status === 'NORMAL' ? '正常' : row.status === 'OVERDUE' ? '逾期' : '损坏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="overdueDays" label="逾期天数" width="100" align="center" />
      <el-table-column prop="overdueFee" label="逾期费(元)" width="100" align="center" />
      <el-table-column prop="depositRefund" label="退还押金(元)" width="110" align="center" />
      <el-table-column prop="price" label="实收(元)" width="90" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button
            v-if="row.id != null && row.id > 0"
            size="small"
            type="danger"
            @click="handleDelete(row.id)"
          >删除</el-button>
          <span v-else class="muted-op">—</span>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增归还记录" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="租赁记录ID" prop="rentalId">
          <el-select v-model="form.rentalId" placeholder="请选择租赁记录" style="width:100%">
            <el-option
              v-for="r in unreturned"
              :key="r.id"
              :label="`#${r.id} - ${r.vcd?.title} (${r.customer?.name})`"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="VCD" prop="vcdId">
          <el-select v-model="form.vcdId" placeholder="请选择VCD" style="width:100%">
            <el-option v-for="v in vcds" :key="v.id" :label="v.title" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" style="width:100%">
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="归还日期" prop="returnDate">
          <el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD"
            placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="归还状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="正常归还" value="NORMAL" />
            <el-option label="逾期归还" value="OVERDUE" />
            <el-option label="损坏归还" value="DAMAGED" />
          </el-select>
        </el-form-item>
        <el-form-item label="逾期费用">
          <el-input-number v-model="form.overdueFee" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="退还押金">
          <el-input-number v-model="form.depositRefund" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="实收金额">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { returnApi, rentalApi, vcdApi, customerApi } from '../api/index.js'

const tableData = ref([])
const unreturned = ref([])
const vcds = ref([])
const customers = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
  rentalId: null, vcdId: null, customerId: null,
  returnDate: '', status: 'NORMAL',
  overdueDays: 0, overdueFee: 0, depositRefund: 0, price: 0
})

const rules = {
  rentalId: [{ required: true, message: '请选择租赁记录', trigger: 'change' }],
  vcdId: [{ required: true, message: '请选择VCD', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  status: [{ required: true, message: '请选择归还状态', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await returnApi.getAll()
    const raw = res?.data
    tableData.value = Array.isArray(raw) ? raw : []
  } catch (e) {
    console.error(e)
    ElMessage.error('加载归还记录失败：' + (e.response?.data?.message || e.message || '未知错误'))
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const openAddDialog = async () => {
  form.value = {
    rentalId: null, vcdId: null, customerId: null,
    returnDate: '', status: 'NORMAL',
    overdueDays: 0, overdueFee: 0, depositRefund: 0, price: 0
  }
  // 加载未归还的租赁记录
  const res = await rentalApi.getAll()
  unreturned.value = (res.data || []).filter((r) => !(r.returned === true || r.isReturned === true))
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = {
      rental: { id: form.value.rentalId },
      vcd: { id: form.value.vcdId },
      customer: { id: form.value.customerId },
      returnDate: form.value.returnDate || null,
      status: form.value.status,
      overdueDays: form.value.overdueDays,
      overdueFee: form.value.overdueFee,
      depositRefund: form.value.depositRefund,
      price: form.value.price
    }
    await returnApi.create(payload)
    ElMessage.success('归还成功，库存已恢复')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('归还失败：' + (e.response?.data?.message || e.message))
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该归还记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await returnApi.delete(id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(async () => {
  loadData()
  const [vRes, cRes] = await Promise.all([vcdApi.getAll(), customerApi.getAll()])
  vcds.value = vRes.data
  customers.value = cRes.data
})
</script>

<style scoped>
.page-container { padding: 4px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 20px; font-weight: 600; color: #1e293b; }
.data-table { border-radius: 8px; overflow: hidden; }
.muted-op { color: #94a3b8; font-size: 13px; }
</style>
