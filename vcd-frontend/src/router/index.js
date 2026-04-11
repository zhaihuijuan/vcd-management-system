import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/vcd',
    name: 'Vcd',
    component: () => import('../views/VcdView.vue')
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('../views/CategoriesView.vue')
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('../views/InventoryView.vue')
  },
  {
    path: '/customers',
    name: 'Customers',
    component: () => import('../views/CustomersView.vue')
  },
  {
    path: '/purchase',
    name: 'Purchase',
    component: () => import('../views/PurchaseView.vue')
  },
  {
    path: '/rental',
    name: 'Rental',
    component: () => import('../views/RentalView.vue')
  },
  {
    path: '/return',
    name: 'Return',
    component: () => import('../views/ReturnView.vue')
  },
  {
    path: '/sales',
    name: 'Sales',
    component: () => import('../views/SalesView.vue')
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('../views/UsersView.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫：检查 token
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('jwt_token')
  if (!token) {
    window.location.href = '/login.html'
    return
  }
  next()
})

export default router
