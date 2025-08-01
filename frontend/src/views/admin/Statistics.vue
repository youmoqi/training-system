<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">统计分析</h3>
    </div>

    <!-- 系统整体统计 -->
    <el-card class="card">
      <template #header>
        <h3 class="page-title">系统整体统计</h3>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-number">{{ systemStats.totalQuestionBanks || 0 }}</div>
            <div class="stat-label">题库总数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-number">{{ systemStats.totalUsers || 0 }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-number">{{ systemStats.completedExams || 0 }}</div>
            <div class="stat-label">完成考试</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-number">{{ (systemStats.averageScore || 0).toFixed(1) }}</div>
            <div class="stat-label">平均分</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 题库选择 -->
    <el-card class="card" style="margin-top: 20px">
      <template #header>
        <h3 class="page-title" style="margin-bottom: 3px">题库详细统计</h3>
        <el-select v-model="selectedQuestionBankId" placeholder="选择题库" @change="loadQuestionBankStats" clearable>
          <el-option
              v-for="questionBank in questionBanks"
              :key="questionBank.id"
              :label="questionBank.title"
              :value="questionBank.id"
          />
        </el-select>
      </template>
      <div class="card-body">
        <!-- 加载状态 -->
        <div v-if="loadingStats" class="loading-stats">
          <el-icon class="is-loading">
            <Loading/>
          </el-icon>
          <p>正在加载题库统计...</p>
        </div>

        <!-- 题库统计内容 -->
        <div v-else-if="questionBankStats" class="question-bank-stats">
          <!-- 基础统计 -->
          <el-row :gutter="20" class="stats-row">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-number">{{ questionBankStats.totalQuestions || 0 }}</div>
                <div class="stat-label">题目数量</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-number">{{ questionBankStats.totalUsers || 0 }}</div>
                <div class="stat-label">购买用户</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-number">{{ questionBankStats.completedUsers || 0 }}</div>
                <div class="stat-label">完成用户</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-number">{{ (questionBankStats.averageScore || 0).toFixed(1) }}</div>
                <div class="stat-label">平均分</div>
              </div>
            </el-col>
          </el-row>

          <!-- 完成率 -->
          <el-row :gutter="20" class="stats-row">
            <el-col :span="12">
              <div class="completion-rate">
                <div class="rate-title">完成率</div>
                <el-progress
                    :percentage="questionBankStats.completionRate || 0"
                    :color="getProgressColor(questionBankStats.completionRate || 0)"
                    :stroke-width="20"
                />
                <div class="rate-text">{{ (questionBankStats.completionRate || 0).toFixed(1) }}%</div>
              </div>
            </el-col>
          </el-row>

          <!-- 分数分布 -->
          <el-row :gutter="20" class="stats-row">
            <el-col :span="24">
              <div class="score-distribution">
                <h4>分数分布</h4>
                <el-table :data="questionBankStats.scoreDistribution" style="width: 100%">
                  <el-table-column prop="range" label="分数区间" width="120"/>
                  <el-table-column prop="count" label="人数" width="100"/>
                  <el-table-column prop="percentage" label="占比" width="120">
                    <template #default="scope">
                      {{ (scope.row.percentage || 0).toFixed(1) }}%
                    </template>
                  </el-table-column>
                  <el-table-column label="分布图">
                    <template #default="scope">
                      <el-progress
                          :percentage="scope.row.percentage || 0"
                          :color="getScoreRangeColor(scope.row.range)"
                          :show-text="false"
                          :stroke-width="20"
                      />
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-col>
          </el-row>

          <!-- 题目统计 -->
          <el-row :gutter="20" class="stats-row">
            <el-col :span="24">
              <div class="question-stats">
                <h4>题目统计</h4>
                <el-table :data="questionBankStats.questionStatistics" style="width: 100%">
                  <el-table-column prop="questionId" label="题目ID" width="80"/>
                  <el-table-column prop="content" label="题目内容" show-overflow-tooltip/>
                  <el-table-column prop="type" label="类型" width="100">
                    <template #default="scope">
                      <el-tag :type="getQuestionTypeTag(scope.row.type)" size="small">
                        {{ getQuestionTypeText(scope.row.type) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="totalAttempts" label="答题次数" width="100"/>
                  <el-table-column prop="correctAttempts" label="正确次数" width="100"/>
                  <el-table-column prop="correctRate" label="正确率" width="120">
                    <template #default="scope">
                      {{ (scope.row.correctRate || 0).toFixed(1) }}%
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 空状态 -->
        <div v-else-if="selectedQuestionBankId" class="empty-stats">
          <el-empty description="暂无统计数据"/>
        </div>

        <!-- 默认状态 -->
        <div v-else class="default-stats">
          <el-empty description="请选择题库查看详细统计"/>
        </div>
      </div>

    </el-card>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import api from '../../api'
import {Loading} from '@element-plus/icons-vue'

export default {
  name: 'Statistics',
  components: {
    Loading
  },
  setup() {
    const systemStats = ref({
      totalQuestionBanks: 0,
      totalUsers: 0,
      completedExams: 0,
      averageScore: 0
    })

    const questionBanks = ref([])
    const selectedQuestionBankId = ref(null)
    const questionBankStats = ref(null)
    const router = useRouter()
    const loadingStats = ref(false)

    const loadSystemStats = async () => {
      try {
        const response = await api.get('/statistics/system')
        systemStats.value = response.data.data
      } catch (error) {
        ElMessage.error('加载系统统计失败：' + (error.response?.data?.message || '未知错误'))
        // 如果后端接口不存在，显示默认数据
        systemStats.value = {
          totalQuestionBanks: 0,
          totalUsers: 0,
          completedExams: 0,
          averageScore: 0
        }
        ElMessage.warning('当前显示默认数据，请配置后端接口')
      }
    }

    const loadQuestionBanks = async () => {
      try {
        const response = await api.get('/question-banks')
        questionBanks.value = response.data.data
      } catch (error) {
        ElMessage.error('加载题库列表失败：' + (error.response?.data?.message || '未知错误'))
        questionBanks.value = []
      }
    }

    const loadQuestionBankStats = async () => {
      if (!selectedQuestionBankId.value) {
        questionBankStats.value = null
        loadingStats.value = false
        return
      }

      loadingStats.value = true
      try {
        const response = await api.get(`/statistics/question-bank/${selectedQuestionBankId.value}`)
        questionBankStats.value = response.data.data
      } catch (error) {
        ElMessage.error('加载题库统计失败：' + (error.response?.data?.message || '未知错误'))
        // 如果后端接口不存在，显示模拟数据
        questionBankStats.value = {
          totalQuestions: 0,
          totalUsers: 0,
          completedUsers: 0,
          averageScore: 0.0,
          completionRate: 0.0,
          scoreDistribution: [
            {range: '0-60', count: 0, percentage: 0.0},
            {range: '60-70', count: 0, percentage: 0.0},
            {range: '70-80', count: 0, percentage: 0.0},
            {range: '80-90', count: 0, percentage: 0.0},
            {range: '90-100', count: 0, percentage: 0.0}
          ],
          questionStatistics: []
        }
        ElMessage.warning('当前显示模拟数据，请配置后端接口')
      } finally {
        loadingStats.value = false
      }
    }

    const getProgressColor = (percentage) => {
      const value = percentage || 0
      if (value >= 80) return '#67C23A'
      if (value >= 60) return '#E6A23C'
      return '#F56C6C'
    }

    const getScoreRangeColor = (range) => {
      if (!range) return '#909399'
      const colorMap = {
        '0-60': '#F56C6C',
        '60-70': '#E6A23C',
        '70-80': '#E6A23C',
        '80-90': '#67C23A',
        '90-100': '#67C23A'
      }
      return colorMap[range] || '#909399'
    }

    const getQuestionTypeText = (type) => {
      if (!type) return '未知'
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'SUBJECTIVE': '主观题'
      }
      return typeMap[type] || type
    }

    const getQuestionTypeTag = (type) => {
      if (!type) return 'info'
      const tagMap = {
        'SINGLE_CHOICE': 'primary',
        'MULTIPLE_CHOICE': 'success',
        'SUBJECTIVE': 'warning'
      }
      return tagMap[type] || 'info'
    }

    const goBack = () => {
      router.push('/admin/question-banks')
    }

    onMounted(() => {
      loadSystemStats()
      loadQuestionBanks()
      // 确保初始状态正确
      selectedQuestionBankId.value = null
      questionBankStats.value = null
      loadingStats.value = false
    })

    return {
      systemStats,
      questionBanks,
      selectedQuestionBankId,
      questionBankStats,
      loadQuestionBankStats,
      getProgressColor,
      getScoreRangeColor,
      getQuestionTypeText,
      getQuestionTypeTag,
      goBack,
      loadingStats
    }
  }
}
</script>

<style scoped>

.header-left h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.system-stats {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.question-bank-select {
  margin-bottom: 20px;
}

.question-bank-stats {
  padding: 20px 0;
}

.stats-row {
  margin-bottom: 30px;
}

.completion-rate {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.rate-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.rate-text {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.score-distribution h4,
.question-stats h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.loading-stats {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.loading-stats .el-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
}

.loading-stats p {
  color: #666;
  margin: 0;
}

.empty-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.default-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}
</style>
