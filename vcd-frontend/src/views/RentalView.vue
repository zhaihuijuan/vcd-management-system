<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">租赁记录</h2>
      <el-button type="success" :icon="Plus" @click="openAddDialog">新增租赁</el-button>
    </div>

    <el-table :data="tableData" stripe border v-loading="loading" class="data-table">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column label="VCD片名" min-width="130">
        <template #default="{ row }">{{ row.vcd?.title || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户姓名" width="110">
        <template #default="{ row }">{{ row.customer?.name || '-' }}</template>
      </el-table-column>
      <el-table-column prop="deposit" label="押金(元)" width="100" align="center" />
      <el-table-column prop="rentalDate" label="租赁日期" width="120" align="center" />
      <el-table-column prop="expectedReturnDate" label="预计归还" width="120" align="center" />
      <el-table-column label="是否归还" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.returned ? 'success' : 'warning'">
            {{ row.returned ? '已归还' : '未归还' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增租赁记录" width="480px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
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
        <el-form-item label="押金" prop="deposit">
          <el-input-number v-model="form.deposit" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="租赁日期" prop="rentalDate">
          <el-date-picker v-model="form.rentalDate" type="date" value-format="YYYY-MM-DD"
            placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="预计归还日期">
          <el-date-picker v-model="form.expectedReturnDate" type="date" value-format="YYYY-MM-DD"
            placeholder="选择日期" style="width:100%" />
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
import { rentalApi, vcdApi, customerApi } from '../api/index.js'

const tableData = ref([])
const vcds = ref([])
const customers = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({ vcdId: null, customerId: null, deposit: 0, rentalDate: '', expectedReturnDate: '' })

const rules = {
  vcdId: [{ required: true, message: '请选择VCD', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  deposit: [{ required: true, message: '请输入押金', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await rentalApi.getAll()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { vcdId: null, customerId: null, deposit: 0, rentalDate: '', expectedReturnDate: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = {
      vcd: { id: form.value.vcdId },
      customer: { id: form.value.customerId },
      deposit: form.value.deposit,
      rentalDate: form.value.rentalDate || null,
      expectedReturnDate: form.value.expectedReturnDate || null
    }
    await rentalApi.create(payload)
    ElMessage.success('租赁成功，库存已更新')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('租赁失败：' + (e.response?.data?.message || e.message))
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该租赁记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await rentalApi.delete(id)
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
</style>
