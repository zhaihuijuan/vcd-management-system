<template>
  <div class="manage-shell">
    <header class="top-bar">
      <h1 class="app-title">VCD管理系统</h1>
      <el-link :underline="false" class="logout-link" @click="handleLogout">退出登录</el-link>
    </header>

    <div class="body-row">
      <aside class="side-nav">
        <el-menu
          :default-active="activeMenu"
          :default-openeds="defaultOpeneds"
          class="cream-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="sub-vcd">
            <template #title>
              <el-icon><VideoCamera /></el-icon>
              <span>VCD管理</span>
            </template>
            <el-menu-item index="/vcd">VCD列表</el-menu-item>
            <el-menu-item index="/categories">分类管理</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/purchase">
            <el-icon><Box /></el-icon>
            <span>入库管理</span>
          </el-menu-item>

          <el-menu-item index="/inventory">
            <el-icon><OfficeBuilding /></el-icon>
            <span>库存管理</span>
          </el-menu-item>

          <el-sub-menu index="sub-sales">
            <template #title>
              <el-icon><ShoppingBag /></el-icon>
              <span>销售管理</span>
            </template>
            <el-menu-item index="/sales">VCD销售</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sub-rental">
            <template #title>
              <el-icon><Key /></el-icon>
              <span>租赁管理</span>
            </template>
            <el-menu-item index="/rental">租赁记录</el-menu-item>
            <el-menu-item index="/return">归还记录</el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/customers">
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </el-menu-item>

          <el-sub-menu index="sub-account">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>账户管理</span>
            </template>
            <el-menu-item index="/users">用户管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  VideoCamera,
  Box,
  OfficeBuilding,
  ShoppingBag,
  Key,
  User,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const defaultOpeneds = ['sub-vcd', 'sub-sales', 'sub-rental', 'sub-account']

const activeMenu = computed(() => {
  const p = route.path
  if (p === '/' || p === '/home') return '/home'
  return p
})

const handleMenuSelect = (key) => {
  router.push(key)
}

const handleLogout = () => {
  localStorage.removeItem('jwt_token')
  ElMessage.success('已退出登录')
  window.location.href = '/login.html'
}
</script>

<style>
html,
body {
  margin: 0;
  height: 100%;
}
#app {
  height: 100%;
}
body {
  font-family: 'Microsoft YaHei', 'PingFang SC', -apple-system, sans-serif;
}
</style>

<style scoped>
.manage-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  background: #fdf8f3;
}

.top-bar {
  height: 52px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fdf8f3;
  border-bottom: 1px solid rgba(93, 64, 55, 0.1);
  flex-shrink: 0;
}

.app-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #5d4037;
}

.logout-link {
  font-size: 14px;
  color: #5d4037 !important;
}

.logout-link:hover {
  color: #1890ff !important;
}

.body-row {
  flex: 1;
  display: flex;
  min-height: 0;
  background: #fdf8f3;
}

.side-nav {
  width: 220px;
  flex-shrink: 0;
  background: #fdf8f3;
  border-right: 1px solid rgba(93, 64, 55, 0.08);
  overflow-y: auto;
}

.cream-menu {
  border-right: none !important;
  background: #fdf8f3 !important;
}

/* 子菜单容器与主菜单统一米色，去掉 Element Plus 默认白底 */
.cream-menu :deep(.el-sub-menu .el-menu) {
  background-color: #fdf8f3 !important;
}

.cream-menu :deep(.el-sub-menu .el-menu-item) {
  background-color: #fdf8f3 !important;
}

.cream-menu :deep(.el-sub-menu .el-menu-item:hover) {
  background-color: rgba(93, 64, 55, 0.06) !important;
}

.cream-menu :deep(.el-menu-item),
.cream-menu :deep(.el-sub-menu__title) {
  color: #5d4037 !important;
  background-color: #fdf8f3 !important;
}

.cream-menu :deep(.el-menu-item:hover),
.cream-menu :deep(.el-sub-menu__title:hover) {
  background-color: rgba(93, 64, 55, 0.06) !important;
}

/* 顶层与子项选中态一致 */
.cream-menu :deep(.el-menu-item.is-active) {
  background-color: #efe6dc !important;
  color: #5d4037 !important;
  border-left: 3px solid #5d4037;
  padding-left: 17px !important;
}

.cream-menu :deep(.el-menu-item .el-icon),
.cream-menu :deep(.el-sub-menu__title .el-icon) {
  color: #6d4c41;
}

.content {
  flex: 1;
  min-width: 0;
  overflow: auto;
  padding: 20px 24px;
  background: #f5f5f5;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
