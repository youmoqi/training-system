<template>
  <div class="admin">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2>系统管理后台</h2>
          <div class="user-info">
            <span>管理员：{{ currentUser?.realName }}</span>
            <el-dropdown @command="handleCommand">
              <el-avatar :src="currentUser?.facePhotoUrl">
                {{ currentUser?.realName?.charAt(0) || currentUser?.username?.charAt(0) }}
              </el-avatar>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <el-container class="main-container">
        <el-aside width="200px" class="sidebar">
          <el-menu
            :default-active="$route.path"
            router
            class="sidebar-menu"
          >
            <el-menu-item index="/admin/courses">
              <el-icon><VideoPlay /></el-icon>
              <span>课程管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/question-banks">
              <el-icon><Document /></el-icon>
              <span>题库管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/exam-papers">
              <el-icon><Edit /></el-icon>
              <span>试卷管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/statistics">
              <el-icon><PieChart /></el-icon>
              <span>统计分析</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/registration-control">
              <el-icon><Document /></el-icon>
              <span>注册信息控制</span>
            </el-menu-item>
            <el-menu-item index="/admin/invitations">
              <el-icon><Link /></el-icon>
              <span>邀请链接</span>
            </el-menu-item>
            <el-menu-item index="/admin/certificates">
              <el-icon><Document /></el-icon>
              <span>培训证明</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay, Document, User, Link, PieChart, Edit } from '@element-plus/icons-vue'

export default {
  name: 'Admin',
  components: {
    VideoPlay,
    Document,
    User,
    Link,
    PieChart,
    Edit
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const currentUser = computed(() => store.getters.currentUser)
    
    const handleCommand = (command) => {
      if (command === 'logout') {
        store.dispatch('logout')
        ElMessage.success('已退出登录')
        router.push('/login')
      }
    }
    
    return {
      currentUser,
      handleCommand
    }
  }
}
</script>

<style scoped>
.admin {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
  padding: 0 var(--spacing-lg);
  box-shadow: var(--shadow-light);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h2 {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-xl);
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--text-regular);
  font-size: var(--font-size-sm);
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: var(--bg-white);
  border-right: 1px solid var(--border-light);
  box-shadow: var(--shadow-light);
  height: calc(100vh - 60px); /* 减去头部高度 */
  overflow-y: auto; /* 如果内容过多，允许滚动 */
}

.sidebar-menu {
  border-right: none;
  height: 100%;
  overflow-y: auto; /* 菜单可以滚动 */
}

.sidebar-menu .el-menu-item {
  color: var(--text-regular);
  font-size: var(--font-size-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  margin: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-base);
  height: 45px; /* 稍微减小高度 */
  line-height: 45px;
}

.sidebar-menu .el-menu-item:hover {
  background: var(--bg-extra-light);
  color: var(--primary-color);
}

.sidebar-menu .el-menu-item.is-active {
  background: var(--primary-color);
  color: white;
}

.sidebar-menu .el-menu-item .el-icon {
  margin-right: var(--spacing-sm);
  font-size: var(--font-size-base);
}

.main-content {
  background: var(--bg-page);
  padding: var(--spacing-lg);
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content h2 {
    font-size: var(--font-size-lg);
  }
  
  .sidebar {
    width: 60px !important;
  }
  
  .sidebar-menu .el-menu-item span {
    display: none;
  }
  
  .sidebar-menu .el-menu-item {
    text-align: center;
    padding: var(--spacing-sm);
  }
  
  .sidebar-menu .el-menu-item .el-icon {
    margin-right: 0;
  }
}

/* 自定义样式覆盖 */
:deep(.el-header) {
  height: 60px;
  line-height: 60px;
}

:deep(.el-aside) {
  background: var(--bg-white);
}

:deep(.el-main) {
  padding: var(--spacing-lg);
}

:deep(.el-menu) {
  background: transparent;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-dropdown) {
  cursor: pointer;
}

:deep(.el-avatar) {
  background: var(--primary-color);
  color: white;
  font-weight: 600;
}
</style> 