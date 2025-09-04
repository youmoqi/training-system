<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">历史试卷管理</h3>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索试卷名称..."
              style="width: 300px"
              clearable
              @input="handleSearch"
          >
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
          <el-input
              v-model="userSearchKeyword"
              placeholder="搜索用户名..."
              style="width: 200px; margin-left: 10px"
              clearable
              @input="handleSearch"
          >
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
          <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 150px; margin-left: 10px" @change="handleSearch">
            <el-option label="全部" value=""/>
            <el-option label="通过" value="true"/>
            <el-option label="未通过" value="false"/>
          </el-select>
        </div>

        <!-- 考试历史列表 -->
        <el-table :data="examHistories" style="width: 100%" v-loading="loading">
          <el-table-column prop="examTitle" label="试卷名称" min-width="200"/>
          <el-table-column prop="userName" label="考生姓名" width="120"/>
          <el-table-column prop="attemptNumber" label="考试次数" width="100"/>
          <el-table-column prop="score" label="得分" width="120">
            <template #default="scope">
            <span :class="getScoreClass(scope.row.score, scope.row.totalScore)">
              {{ scope.row.score }}/{{ scope.row.totalScore }}
            </span>
            </template>
          </el-table-column>
          <el-table-column prop="correctAnswers" label="正确题数" width="100">
            <template #default="scope">
              {{ scope.row.correctAnswers }}/{{ scope.row.totalQuestions }}
            </template>
          </el-table-column>
          <el-table-column prop="examTime" label="考试时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.examTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="timeTaken" label="用时" width="120">
            <template #default="scope">
              {{ formatTime(scope.row.timeTaken) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isPassed ? 'success' : 'danger'">
                {{ scope.row.isPassed ? '通过' : '未通过' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewExamResult(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && examHistories.length === 0" description="暂无考试历史数据"/>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ExamHistoryManagement',
  components: {
    Search,
    User
  },
  setup() {
    const router = useRouter()
    
    // 响应式数据
    const loading = ref(false)
    const examHistories = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(20)
    const searchKeyword = ref('')
    const userSearchKeyword = ref('')
    const statusFilter = ref('')

    // 加载考试历史记录
    const loadExamHistories = async () => {
      loading.value = true
      try {
        // 使用新的API端点获取所有用户的考试历史
        const params = {
          page: currentPage.value - 1, // 后端使用0-based分页
          size: pageSize.value,
          examKeyword: searchKeyword.value || undefined,
          userKeyword: userSearchKeyword.value || undefined,
          isPassed: statusFilter.value === '' ? null : statusFilter.value === 'true'
        }
        
        const response = await api.get('/exam-results/all-users-history', {params})
        if (response.data.success && response.data.data) {
          examHistories.value = response.data.data.content
          total.value = response.data.data.totalElements
        } else {
          ElMessage.error('加载考试历史失败')
        }

      } catch (error) {
        ElMessage.error('加载考试历史失败')
        console.error('加载考试历史失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 查看考试结果详情
    const viewExamResult = (history) => {
      router.push(`/admin/exam-results/${history.id}`)
    }

    // 获取分数样式类
    const getScoreClass = (score, totalScore) => {
      const percentage = (score / totalScore) * 100
      if (percentage >= 80) return 'score-excellent'
      if (percentage >= 60) return 'score-good'
      return 'score-poor'
    }

    // 格式化时间（秒转时分秒）
    const formatTime = (seconds) => {
      if (!seconds) return '--'
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }

    // 格式化日期时间
    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1
      loadExamHistories()
    }

    // 分页大小改变
    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadExamHistories()
    }

    // 当前页改变
    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadExamHistories()
    }

    // 初始加载
    onMounted(() => {
      loadExamHistories()
    })

    return {
      loading,
      examHistories,
      total,
      currentPage,
      pageSize,
      searchKeyword,
      userSearchKeyword,
      statusFilter,
      loadExamHistories,
      viewExamResult,
      getScoreClass,
      formatTime,
      formatDateTime,
      handleSearch,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 分数样式 */
.score-excellent {
  color: #67c23a;
  font-weight: 600;
}

.score-good {
  color: #e6a23c;
  font-weight: 600;
}

.score-poor {
  color: #f56c6c;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-bar .el-input,
  .search-bar .el-select {
    margin: 5px 0 !important;
  }
}
</style>