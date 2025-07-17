<template>
  <div class="admin">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2>系统管理后台</h2>
          <div class="user-info">
            <span>管理员：{{ currentUser?.realName }}</span>
            <el-dropdown @command="handleCommand">
              <el-avatar :src="currentUser?.facePhotoUrl" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <el-container>
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
            <el-menu-item index="/admin/statistics">
              <el-icon><PieChart /></el-icon>
              <span>统计分析</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/invitations">
              <el-icon><Link /></el-icon>
              <span>邀请链接</span>
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
}

.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h2 {
  margin: 0;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sidebar {
  background: #fff;
  border-right: 1px solid #e6e6e6;
}

.sidebar-menu {
  border-right: none;
}

.main-content {
  background: #f5f5f5;
  padding: 20px;
}
</style> 