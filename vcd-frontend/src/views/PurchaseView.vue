<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">采购记录</h2>
      <el-button type="success" :icon="Plus" @click="openAddDialog">新增采购</el-button>
    </div>

    <el-table :data="tableData" stripe border v-loading="loading" class="data-table">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column label="VCD片名" min-width="140">
        <template #default="{ row }">{{ row.vcd?.title || '-' }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="采购数量" width="100" align="center" />
      <el-table-column prop="price" label="单价(元)" width="100" align="center" />
      <el-table-column prop="purchaseDate" label="采购日期" width="130" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增采购记录" width="460px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="VCD" prop="vcdId">
          <el-select v-model="form.vcdId" placeholder="请选择VCD" style="width:100%">
            <el-option v-for="v in vcds" :key="v.id" :label="v.title" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD"
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
import { purchaseApi, vcdApi } from '../api/index.js'

const tableData = ref([])
const vcds = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({ vcdId: null, quantity: 1, price: 0, purchaseDate: '' })

const rules = {
  vcdId: [{ required: true, message: '请选择VCD', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await purchaseApi.getAll()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  form.value = { vcdId: null, quantity: 1, price: 0, purchaseDate: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = {
      vcd: { id: form.value.vcdId },
      quantity: form.value.quantity,
      price: form.value.price,
      purchaseDate: form.value.purchaseDate || null
    }
    await purchaseApi.create(payload)
    ElMessage.success('采购成功，库存已更新')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('采购失败：' + (e.response?.data?.message || e.message))
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该采购记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await purchaseApi.delete(id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

onMounted(async () => {
  loadData()
  const res = await vcdApi.getAll()
  vcds.value = res.data
})
</script>

<style scoped>
.page-container { padding: 4px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 20px; font-weight: 600; color: #1e293b; }
.data-table { border-radius: 8px; overflow: hidden; }
</style>
