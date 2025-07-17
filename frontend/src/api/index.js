import axios from 'axios'
import store from '../store'
import router from '../router'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = store.state.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      const message = error.response.data.message || '您的登录已过期，请重新登录'
      ElMessage.error(message)
      store.dispatch('logout')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default api 