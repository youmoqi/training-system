<template>
  <div class="courses-page">
    <div class="page-header">
      <h3>我的课程</h3>
      <el-button v-if="userRole !== 'BLAST_USER'" type="primary" @click="showCourseSelection = true">
        选择课程
      </el-button>
    </div>

    <!-- 我的课程列表 -->
    <el-row :gutter="20" v-loading="myCoursesLoading">
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
            <div class="course-header">
              <h4>{{ userCourse.course.title }}</h4>
              <el-tag
                :type="userCourse.isCompleted ? 'success' : 'warning'"
                size="small"
              >
                {{ userCourse.isCompleted ? '已完成' : '学习中' }}
              </el-tag>
            </div>

            <p class="course-description">{{ userCourse.course.description || '暂无描述' }}</p>

            <div class="course-details">
              <div class="detail-item">
                <span class="detail-label">选课时间：</span>
                <span class="detail-value">{{ formatDate(userCourse.enrollTime) }}</span>
              </div>

              <div class="detail-item">
                <span class="detail-label">观看进度：</span>
                <span class="detail-value progress">{{ userCourse.watchProgress || 0 }}%</span>
              </div>
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

    <!-- 我的课程分页 -->
    <div class="pagination-container" style="margin-top: 20px; text-align: center;">
      <el-pagination
        v-model:current-page="myCoursesCurrentPage"
        v-model:page-size="myCoursesPageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="myCoursesTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleMyCoursesSizeChange"
        @current-change="handleMyCoursesCurrentChange"
      />
    </div>

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
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="课程名称" width="200"/>
        <el-table-column prop="description" label="课程描述" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

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
import api from '../../api'

export default {
  name: 'Courses',
  setup() {
    const store = useStore()
    const router = useRouter()

    const userCourses = ref([])
    const availableCourses = ref([])
    const selectedCourses = ref([])
    const showCourseSelection = ref(false)

    // 分页相关
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const loading = ref(false)

    // 我的课程分页相关
    const myCoursesCurrentPage = ref(1)
    const myCoursesPageSize = ref(10)
    const myCoursesTotal = ref(0)
    const myCoursesLoading = ref(false)

    const userRole = computed(() => store.getters.userRole)

    const loadUserCourses = async () => {
      myCoursesLoading.value = true
      try {
        const response = await api.get('/courses/student/my-courses', {
          params: {
            userId: store.getters.currentUser.id,
            page: myCoursesCurrentPage.value - 1,
            size: myCoursesPageSize.value
          }
        })
        userCourses.value = response.data.data.content
        myCoursesTotal.value = response.data.data.totalElements
      } catch (error) {
        ElMessage.error('加载课程失败')
      } finally {
        myCoursesLoading.value = false
      }
    }

    const loadAvailableCourses = async () => {
      loading.value = true
      try {
        const response = await api.get('/courses/student/available-courses', {
          params: {
            userId: store.getters.currentUser.id,
            page: currentPage.value - 1, // 后端从0开始，前端从1开始
            size: pageSize.value,
            userRole: store.getters.currentUser.role
          }
        })
        availableCourses.value = response.data.data.content
        total.value = response.data.data.totalElements
      } catch (error) {
        ElMessage.error('加载可用课程失败')
      } finally {
        loading.value = false
      }
    }

    const handleSelectionChange = (selection) => {
      // 更新当前页面的选择状态
      selectedCourses.value = selection
    }

    // 分页处理方法
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      // 清空选择状态
      selectedCourses.value = []
      loadAvailableCourses()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      // 清空选择状态
      selectedCourses.value = []
      loadAvailableCourses()
    }

    // 我的课程分页处理方法
    const handleMyCoursesSizeChange = (val) => {
      myCoursesPageSize.value = val
      myCoursesCurrentPage.value = 1
      loadUserCourses()
    }

    const handleMyCoursesCurrentChange = (val) => {
      myCoursesCurrentPage.value = val
      loadUserCourses()
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
      unenroll,
      currentPage,
      pageSize,
      handleSizeChange,
      handleCurrentChange,
      total,
      loading,
      myCoursesCurrentPage,
      myCoursesPageSize,
      myCoursesTotal,
      myCoursesLoading,
      handleMyCoursesSizeChange,
      handleMyCoursesCurrentChange
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
  height: 500px;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  flex-shrink: 0;
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
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: 300px;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  min-height: 35px;
  flex-shrink: 0;
}

.course-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  flex: 1;
  margin-right: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-description {
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.6;
  font-size: 14px;
  min-height: 55px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

.course-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
  min-height: 50px;
  flex-shrink: 0;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  min-height: 18px;
}

.detail-label {
  color: #909399;
  margin-right: 8px;
  min-width: 70px;
  font-weight: 500;
}

.detail-value {
  color: #303133;
  font-weight: 400;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.detail-value.progress {
  color: #409eff;
  font-weight: 600;
  font-size: 14px;
}

.course-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  min-height: 80px;
  align-items: center;
  flex-shrink: 0;
  justify-content: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.pagination-container .el-pagination {
  justify-content: center;
}
</style>
