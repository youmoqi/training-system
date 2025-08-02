<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">试卷考试历史</h3>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索试卷..."
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
          <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 150px" @change="handleSearch">
            <el-option label="全部" value=""/>
            <el-option label="通过" value="true"/>
            <el-option label="未通过" value="false"/>
          </el-select>
        </div>

        <!-- 考试历史列表 -->
        <el-table :data="examHistories" style="width: 100%" v-loading="loading">
          <el-table-column prop="examPaperTitle" label="试卷名称" min-width="200"/>
          <el-table-column prop="attemptNumber" label="考试次数" width="100"/>
          <el-table-column prop="score" label="得分" width="120">
            <template #default="scope">
            <span :class="getScoreClass(scope.row.score, scope.row.totalScore)">
              {{ scope.row.score }}/{{ scope.row.totalScore }}
            </span>
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
          <el-table-column prop="examTime" label="考试时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.examTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="viewExamResult(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="retakeExam(scope.row)"
                         v-if="canRetake(scope.row)">
                重新考试
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
        <div v-if="!loading && examHistories.length === 0" class="empty-state">
          <el-empty description="暂无考试历史记录">
            <el-button type="primary" @click="$router.push('/dashboard/exam-papers')">
              去购买试卷
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {Search} from '@element-plus/icons-vue'
import api from '@/api'
import store from "@/store"

export default {
  name: 'ExamPaperHistory',
  components: {
    Search
  },
  setup() {
    const router = useRouter()

    const examHistories = ref([])
    const loading = ref(false)
    const searchKeyword = ref('')
    const statusFilter = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)

    const loadExamHistories = async () => {
      loading.value = true
      try {
        const userId = store.getters.userId
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value,
          isPassed: statusFilter.value === '' ? null : statusFilter.value === 'true'
        }

        const response = await api.get(`/exam-results/history/${userId}`, {params})
        if (response.data.success) {
          examHistories.value = response.data.data.content
          total.value = response.data.data.totalElements

          // 为每个考试历史获取试卷信息
          for (let history of examHistories.value) {
            try {
              const examPaperResponse = await api.get(`/exam-papers/${history.examPaperId}`)
              if (examPaperResponse.data.success) {
                history.examPaper = examPaperResponse.data.data
              }
            } catch (error) {
              console.error('获取试卷信息失败:', error)
            }
          }
        }
      } catch (error) {
        ElMessage.error('加载考试历史失败')
        console.error('加载考试历史失败:', error)
      } finally {
        loading.value = false
      }
    }

    const viewExamResult = (history) => {
      router.push(`/dashboard/exam-papers/${history.examPaperId}/results/${history.examPaperResultId}`)
    }

    const retakeExam = async (history) => {
      try {
        // 检查是否可以重新考试
        const canRetakeResponse = await api.get(`/exam-papers/${history.examPaperId}/can-retake/${store.getters.userId}`)
        if (canRetakeResponse.data.success && canRetakeResponse.data.data) {
          router.push(`/dashboard/exam-papers/${history.examPaperId}/exam`)
        } else {
          ElMessage.warning('已达到最大考试次数或试卷不允许重复考试')
        }
      } catch (error) {
        ElMessage.error('检查重复考试权限失败')
      }
    }

    const canRetake = (history) => {
      // 使用已加载的试卷信息判断是否可以重新考试
      if (history.examPaper) {
        return history.examPaper.allowRetake && history.attemptNumber < history.examPaper.maxAttempts
      }
      return false
    }

    const getScoreClass = (score, totalScore) => {
      const percentage = (score / totalScore) * 100
      if (percentage >= 80) return 'score-excellent'
      if (percentage >= 60) return 'score-good'
      return 'score-poor'
    }

    const formatTime = (seconds) => {
      if (!seconds) return '--'
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }

    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadExamHistories()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadExamHistories()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadExamHistories()
    }

    onMounted(() => {
      loadExamHistories()
    })

    return {
      examHistories,
      loading,
      searchKeyword,
      statusFilter,
      currentPage,
      pageSize,
      total,
      loadExamHistories,
      viewExamResult,
      retakeExam,
      canRetake,
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
.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  align-items: center;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
