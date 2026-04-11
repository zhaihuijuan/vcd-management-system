<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">库存管理</h2>
      <el-button type="success" :icon="Plus" @click="openAddDialog">新增库存</el-button>
    </div>

    <el-table :data="tableData" stripe border v-loading="loading" class="data-table">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column label="VCD片名" min-width="140">
        <template #default="{ row }">{{ row.vcd?.title || '-' }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="总库存" width="100" align="center" />
      <el-table-column prop="rentCount" label="已租出" width="100" align="center" />
      <el-table-column label="可用库存" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="(row.stock - row.rentCount) > 0 ? 'success' : 'danger'">
            {{ row.stock - row.rentCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑库存' : '新增库存'" width="440px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="VCD" prop="vcdId">
          <el-select v-model="form.vcdId" placeholder="请选择VCD" style="width:100%">
            <el-option v-for="v in vcds" :key="v.id" :label="v.title" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="总库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="已租出数量" prop="rentCount">
          <el-input-number v-model="form.rentCount" :min="0" style="width:100%" />
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
import { inventoryApi, vcdApi } from '../api/index.js'

const tableData = ref([])
const vcds = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = ref({ id: null, vcdId: null, stock: 0, rentCount: 0 })

const rules = {
  vcdId: [{ required: true, message: '请选择VCD', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await inventoryApi.getAll()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { id: null, vcdId: null, stock: 0, rentCount: 0 }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { id: row.id, vcdId: row.vcd?.id, stock: row.stock, rentCount: row.rentCount }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = { stock: form.value.stock, rentCount: form.value.rentCount, vcd: { id: form.value.vcdId } }
    if (isEdit.value) {
      await inventoryApi.update(form.value.id, payload)
      ElMessage.success('修改成功')
    } else {
      await inventoryApi.create(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该库存记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await inventoryApi.delete(id)
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
