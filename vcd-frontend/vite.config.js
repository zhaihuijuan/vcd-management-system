import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { cpSync, existsSync } from 'node:fs'

const distDir = fileURLToPath(new URL('../vcd-backend/src/main/resources/dist', import.meta.url))
const publicDir = fileURLToPath(new URL('./public', import.meta.url))

export default defineConfig({
  plugins: [
    vue(),
    {
      name: 'copy-public-to-dist',
      closeBundle() {
        if (existsSync(publicDir)) {
          cpSync(publicDir, distDir, { recursive: true })
        }
      }
    }
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 构建时生成两个 HTML 入口，打包后放入 Spring Boot 的 dist/ 目录
  build: {
    emptyOutDir: true,
    rollupOptions: {
      input: {
        login: fileURLToPath(new URL('./login.html', import.meta.url)),
        manage: fileURLToPath(new URL('./manage.html', import.meta.url))
      }
    },
    outDir: '../vcd-backend/src/main/resources/dist'
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
