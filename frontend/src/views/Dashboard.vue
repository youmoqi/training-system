<template>
  <div class="dashboard">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h2>易制爆与爆破作业人员培训系统</h2>
          <div class="user-info">
            <span>欢迎，{{ currentUser?.realName }}</span>
            <el-dropdown @command="handleCommand">
              <el-avatar :src="currentUser?.facePhotoUrl" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
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
            <el-menu-item index="/dashboard/courses">
              <el-icon><VideoPlay /></el-icon>
              <span>我的课程</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/join" v-if="userRole === 'BLAST_USER'">
              <el-icon><Link /></el-icon>
              <span>加入课程</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/question-banks">
              <el-icon><Document /></el-icon>
              <span>题库练习</span>
            </el-menu-item>
            <!-- <el-menu-item index="/dashboard/exams">
              <el-icon><EditPen /></el-icon>
              <span>我的考试</span>
            </el-menu-item> -->
            <el-menu-item index="/dashboard/profile">
              <el-icon><User /></el-icon>
              <span>个人资料</span>
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
import { VideoPlay, Document, User, Link, EditPen } from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: {
    VideoPlay,
    Document,
    User,
    Link,
    EditPen
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const currentUser = computed(() => store.getters.currentUser)
    const userRole = computed(() => store.getters.userRole)
    
    const handleCommand = (command) => {
      if (command === 'logout') {
        store.dispatch('logout')
        ElMessage.success('已退出登录')
        router.push('/login')
      } else if (command === 'profile') {
        router.push('/dashboard/profile')
      }
    }
    
    return {
      currentUser,
      handleCommand,
      userRole
    }
  }
}
</script>

<style scoped>
.dashboard {
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