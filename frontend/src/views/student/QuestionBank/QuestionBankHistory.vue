<template>
  <div class="question-bank-history">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库练习历史</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索题库..."
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

      <!-- 练习历史列表 -->
      <el-table :data="examHistories" style="width: 100%" v-loading="loading">
        <el-table-column prop="questionBankTitle" label="题库名称" min-width="200"/>
        <el-table-column prop="attemptNumber" label="练习次数" width="100"/>
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
        <el-table-column prop="submitTime" label="提交时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.submitTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewExamResult(scope.row)">查看详情</el-button>
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
        <el-empty description="暂无练习历史记录">
          <el-button type="primary" @click="$router.push('/dashboard/question-banks')">
            去练习题库
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {Search} from '@element-plus/icons-vue'
import api from '@/api'
import store from "@/store"

export default {
  name: 'QuestionBankHistory',
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

        const response = await api.get(`/question-bank-results/history/${userId}`, {params})
        if (response.data.success) {
          examHistories.value = response.data.data.content
          total.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载练习历史失败')
        console.error('加载练习历史失败:', error)
      } finally {
        loading.value = false
      }
    }

    const viewExamResult = (history) => {
      router.push(`/dashboard/question-bank-results/${history.id}`)
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
.question-bank-history {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

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

.score-excellent {
  color: #67c23a;
  font-weight: bold;
}

.score-good {
  color: #e6a23c;
  font-weight: bold;
}

.score-poor {
  color: #f56c6c;
  font-weight: bold;
}
</style>
