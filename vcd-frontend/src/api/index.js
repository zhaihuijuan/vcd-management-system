import request from './request'

// ===== 用户 =====
export const userApi = {
  login: (data) => request.post('/api/users/login', data),
  register: (data) => request.post('/api/users/register', data),
  getAll: () => request.get('/api/users'),
  getById: (id) => request.get(`/api/users/${id}`),
  update: (id, data) => request.put(`/api/users/${id}`, data),
  delete: (id) => request.delete(`/api/users/${id}`)
}

// ===== VCD分类 =====
export const categoryApi = {
  getAll: () => request.get('/api/vcdCategories'),
  getById: (id) => request.get(`/api/vcdCategories/${id}`),
  create: (data) => request.post('/api/vcdCategories', data),
  update: (id, data) => request.put(`/api/vcdCategories/${id}`, data),
  delete: (id) => request.delete(`/api/vcdCategories/${id}`)
}

// ===== VCD =====
export const vcdApi = {
  getAll: () => request.get('/api/vcd'),
  getById: (id) => request.get(`/api/vcd/${id}`),
  search: (keyword) => request.get('/api/vcd/search', { params: { keyword } }),
  create: (data) => request.post('/api/vcd', data),
  update: (id, data) => request.put(`/api/vcd/${id}`, data),
  delete: (id) => request.delete(`/api/vcd/${id}`)
}

// ===== 库存 =====
export const inventoryApi = {
  getAll: () => request.get('/api/vcdInventory'),
  getById: (id) => request.get(`/api/vcdInventory/${id}`),
  getByVcdId: (vcdId) => request.get(`/api/vcdInventory/vcd/${vcdId}`),
  create: (data) => request.post('/api/vcdInventory', data),
  update: (id, data) => request.put(`/api/vcdInventory/${id}`, data),
  delete: (id) => request.delete(`/api/vcdInventory/${id}`)
}

// ===== 客户 =====
export const customerApi = {
  getAll: () => request.get('/api/customers'),
  getById: (id) => request.get(`/api/customers/${id}`),
  search: (keyword) => request.get('/api/customers/search', { params: { keyword } }),
  create: (data) => request.post('/api/customers', data),
  update: (id, data) => request.put(`/api/customers/${id}`, data),
  delete: (id) => request.delete(`/api/customers/${id}`)
}

// ===== 采购记录 =====
export const purchaseApi = {
  getAll: () => request.get('/api/purchaseRecords'),
  getById: (id) => request.get(`/api/purchaseRecords/${id}`),
  create: (data) => request.post('/api/purchaseRecords', data),
  delete: (id) => request.delete(`/api/purchaseRecords/${id}`)
}

// ===== 租赁记录 =====
export const rentalApi = {
  getAll: () => request.get('/api/rentalRecords'),
  getById: (id) => request.get(`/api/rentalRecords/${id}`),
  create: (data) => request.post('/api/rentalRecords', data),
  delete: (id) => request.delete(`/api/rentalRecords/${id}`)
}

// ===== 归还记录 =====
export const returnApi = {
  getAll: () => request.get('/api/returnRecords'),
  getById: (id) => request.get(`/api/returnRecords/${id}`),
  create: (data) => request.post('/api/returnRecords', data),
  delete: (id) => request.delete(`/api/returnRecords/${id}`)
}

// ===== 销售记录 =====
export const salesApi = {
  getAll: () => request.get('/api/salesRecords'),
  getById: (id) => request.get(`/api/salesRecords/${id}`),
  create: (data) => request.post('/api/salesRecords', data),
  update: (id, data) => request.put(`/api/salesRecords/${id}`, data),
  delete: (id) => request.delete(`/api/salesRecords/${id}`)
}
