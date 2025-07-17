<template>
  <div class="courses-page">
    <div class="page-header">
      <h3>我的课程</h3>
      <el-button v-if="userRole !== 'BLAST_USER'" type="primary" @click="showCourseSelection = true">
        选择课程
      </el-button>
    </div>
    
    <el-row :gutter="20">
      <el-col
        v-for="userCourse in userCourses"
        :key="userCourse.id"
        :span="8"
      >
        <el-card class="course-card" shadow="hover">
          <div class="course-image">
            <img 
              :src="userCourse.course.coverImageUrl || '/course-placeholder.svg'" 
              :alt="userCourse.course.title"
              @error="handleImageError"
            />
            <div class="course-progress">
              <el-progress
                :percentage="userCourse.watchProgress || 0"
                :stroke-width="8"
                color="#409EFF"
              />
            </div>
          </div>
          
          <div class="course-info">
            <h4>{{ userCourse.course.title }}</h4>
            <p>{{ userCourse.course.description }}</p>
            
            <div class="course-meta">
              <span class="enroll-time">
                选课时间：{{ formatDate(userCourse.enrollTime) }}
              </span>
              <el-tag
                :type="userCourse.isCompleted ? 'success' : 'warning'"
                size="small"
              >
                {{ userCourse.isCompleted ? '已完成' : '学习中' }}
              </el-tag>
            </div>
            
            <div class="course-actions">
              <el-button
                type="primary"
                size="small"
                @click="watchCourse(userCourse.course)"
              >
                观看课程
              </el-button>
              <el-button
                v-if="!userCourse.isCompleted"
                type="success"
                size="small"
                @click="markAsCompleted(userCourse)"
              >
                标记完成
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="unenroll(userCourse.course.id)"
              >
                退课
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 课程选择对话框 -->
    <el-dialog
      v-model="showCourseSelection"
      title="选择课程"
      width="800px"
    >
      <el-table
        :data="availableCourses"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="课程名称" />
        <el-table-column prop="description" label="课程描述" />
        <el-table-column prop="price" label="价格">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCourseSelection = false">取消</el-button>
          <el-button
            type="primary"
            :disabled="selectedCourses.length === 0"
            @click="enrollCourses"
          >
            确认选课
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api'

export default {
  name: 'Courses',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const userCourses = ref([])
    const availableCourses = ref([])
    const selectedCourses = ref([])
    const showCourseSelection = ref(false)
    
    const userRole = computed(() => store.getters.userRole)

    const loadUserCourses = async () => {
      try {
        const response = await api.get(`/courses/user/${store.getters.currentUser.id}`)
        userCourses.value = response.data.data
      } catch (error) {
        ElMessage.error('加载课程失败')
      }
    }
    
    const loadAvailableCourses = async () => {
      try {
        const response = await api.get('/courses/online')
        availableCourses.value = response.data.data
      } catch (error) {
        ElMessage.error('加载可用课程失败')
      }
    }
    
    const handleSelectionChange = (selection) => {
      selectedCourses.value = selection
    }
    
    const enrollCourses = async () => {
      try {
        for (const course of selectedCourses.value) {
          await api.post(`/courses/${course.id}/enroll`, null, {
            params: { userId: store.getters.currentUser.id }
          })
        }
        ElMessage.success('选课成功')
        showCourseSelection.value = false
        loadUserCourses()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '选课失败')
      }
    }
    
    const watchCourse = (course) => {
      router.push(`/dashboard/course/${course.id}`)
    }
    
    const markAsCompleted = async (userCourse) => {
      try {
        await api.put(`/courses/${userCourse.course.id}/progress`, null, {
          params: {
            userId: store.getters.currentUser.id,
            progress: 100
          }
        })
        ElMessage.success('课程已完成')
        loadUserCourses()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
    
    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString('zh-CN')
    }
    
    const handleImageError = () => {
      // Handle image loading error
    }
    
    const unenroll = async (courseId) => {
      try {
        await ElMessageBox.confirm('您确定要退出这门课程吗？此操作不可逆！', '确认退课', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        });
        
        await api.delete(`/courses/unenroll/${courseId}`);
        ElMessage.success('成功退出课程');
        loadUserCourses(); // Refresh the list
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '退课失败');
        }
      }
    };
    
    onMounted(() => {
      loadUserCourses()
      loadAvailableCourses()
    })
    
    return {
      userCourses,
      availableCourses,
      selectedCourses,
      showCourseSelection,
      handleSelectionChange,
      enrollCourses,
      watchCourse,
      markAsCompleted,
      formatDate,
      handleImageError,
      userRole,
      unenroll
    }
  }
}
</script>

<style scoped>
.courses-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  color: #333;
}

.course-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  padding: 10px;
}

.course-info {
  padding: 15px;
}

.course-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.course-info p {
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.enroll-time {
  font-size: 12px;
  color: #999;
}

.course-actions {
  display: flex;
  gap: 10px;
}
</style> 