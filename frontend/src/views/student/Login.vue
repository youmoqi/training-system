<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>易制爆与爆破作业人员培训系统</h2>
        <p>请登录您的账户</p>
      </div>

      <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
              size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              size="large"
              show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账户？</span>
          <el-link type="primary" @click="$router.push('/register')">
            立即注册
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import {ref, reactive} from 'vue'
import {useStore} from 'vuex'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const store = useStore()
    const router = useRouter()
    const formRef = ref(null)
    const loading = ref(false)

    const loginForm = reactive({
      username: '',
      password: ''
    })

    const rules = {
      username: [
        {required: true, message: '请输入用户名', trigger: 'blur'}
      ],
      password: [
        {required: true, message: '请输入密码', trigger: 'blur'}
      ]
    }

    const handleLogin = async () => {
      try {
        await formRef.value.validate()
        loading.value = true

        await store.dispatch('login', loginForm)
        ElMessage.success('登录成功')

        // 根据用户角色跳转到不同页面
        const userRole = store.getters.userRole?.code
        if (['SUPER_ADMIN'].includes(userRole)) {
          router.push('/admin')
        } else {
          router.push('/dashboard')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }

    return {
      formRef,
      loginForm,
      rules,
      loading,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: var(--spacing-lg);
}

.login-box {
  width: 400px;
  padding: var(--spacing-xl);
  background: var(--bg-white);
  border-radius: var(--border-radius-extra-large);
  box-shadow: var(--shadow-dark);
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.login-header h2 {
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  font-size: var(--font-size-xxl);
  font-weight: 600;
}

.login-header p {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  margin: 0;
}

.login-form {
  margin-top: var(--spacing-lg);
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: var(--font-size-base);
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.login-button:hover {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
}

.login-footer {
  text-align: center;
  margin-top: var(--spacing-lg);
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.login-footer .el-link {
  margin-left: var(--spacing-xs);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    width: 100%;
    max-width: 400px;
    margin: 0 var(--spacing-md);
  }

  .login-header h2 {
    font-size: var(--font-size-xl);
  }
}
</style>
