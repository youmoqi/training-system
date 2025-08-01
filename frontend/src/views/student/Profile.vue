<template>
  <div class="profile-page">
    <div class="page-header">
      <h3>个人资料</h3>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar
              :src="currentUser?.facePhotoUrl"
              :size="120"
              class="profile-avatar"
            />
            <h4>{{ currentUser?.realName }}</h4>
            <p>{{ getRoleText(currentUser?.role) }}</p>
          </div>

          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-label">缴费额度</span>
              <span class="stat-value">¥{{ currentUser?.paymentAmount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已选课程</span>
              <span class="stat-value">{{ userCourses.length }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已完成课程</span>
              <span class="stat-value">{{ completedCourses.length }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="info-card">
          <template #header>
            <span>基本信息</span>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">
              {{ currentUser?.username }}
            </el-descriptions-item>
            <el-descriptions-item label="真实姓名">
              {{ currentUser?.realName }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ currentUser?.gender }}
            </el-descriptions-item>
            <el-descriptions-item label="身份证号码">
              {{ currentUser?.idCard }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ currentUser?.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="工作单位">
              {{ currentUser?.workUnit }}
            </el-descriptions-item>
            <el-descriptions-item label="培训类型">
              {{ currentUser?.trainingType }}
            </el-descriptions-item>
            <el-descriptions-item label="作业类别">
              {{ currentUser?.jobCategory }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ formatDate(currentUser?.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="最后更新">
              {{ formatDate(currentUser?.updateTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="courses-card" style="margin-top: 20px;">
          <template #header>
            <span>我的课程</span>
          </template>

          <el-table :data="userCourses" style="width: 100%">
            <el-table-column prop="course.title" label="课程名称" />
            <el-table-column prop="enrollTime" label="选课时间">
              <template #default="scope">
                {{ formatDate(scope.row.enrollTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="watchProgress" label="观看进度">
              <template #default="scope">
                <el-progress
                  :percentage="scope.row.watchProgress || 0"
                  :stroke-width="8"
                />
              </template>
            </el-table-column>
            <el-table-column prop="isCompleted" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.isCompleted ? 'success' : 'warning'">
                  {{ scope.row.isCompleted ? '已完成' : '学习中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import api from '../../api'

export default {
  name: 'Profile',
  setup() {
    const store = useStore()
    const userCourses = ref([])

    const currentUser = computed(() => store.getters.currentUser)

    const completedCourses = computed(() => {
      return userCourses.value.filter(course => course.isCompleted)
    })

    const loadUserCourses = async () => {
      try {
        const response = await api.get(`/courses/user/${currentUser.value.id}`)
        userCourses.value = response.data.data
      } catch (error) {
        ElMessage.error('加载课程信息失败')
      }
    }

    const getRoleText = (role) => {
      const roleMap = {
        'SUPER_ADMIN': '超级管理员',
        'ADMIN': '管理员',
        'EXPLOSIVE_USER': '易制爆人员',
        'BLAST_USER': '爆破三大员'
      }
      return roleMap[role] || '未知角色'
    }

    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadUserCourses()
    })

    return {
      currentUser,
      userCourses,
      completedCourses,
      getRoleText,
      formatDate
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  color: #333;
}

.profile-card {
  text-align: center;
}

.avatar-section {
  margin-bottom: 30px;
}

.profile-avatar {
  margin-bottom: 15px;
}

.avatar-section h4 {
  margin: 10px 0 5px 0;
  color: #333;
}

.avatar-section p {
  margin: 0;
  color: #666;
}

.user-stats {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-value {
  color: #333;
  font-weight: bold;
  font-size: 16px;
}

.info-card {
  margin-bottom: 20px;
}

.courses-card {
  margin-top: 20px;
}
</style>
