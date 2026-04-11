import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: '/',
  timeout: 10000
})

// 判断 token 是否过期
function isTokenExpired(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.exp * 1000 < Date.now()
  } catch (e) {
    return true
  }
}

// 请求拦截器：从 localStorage 取 token，放入请求头
instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('jwt_token')
    if (token) {
      if (isTokenExpired(token)) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('jwt_token')
        window.location.href = '/login.html'
        return Promise.reject('Token expired')
      }
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理错误
instance.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('未授权，请重新登录')
        localStorage.removeItem('jwt_token')
        window.location.href = '/login.html'
      } else if (error.response.status === 403) {
        ElMessage.error('无权限访问')
      } else if (error.response.status === 500) {
        ElMessage.error('服务器错误：' + (error.response.data?.message || '未知错误'))
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    }
    return Promise.reject(error)
  }
)

export default instance
