<template>
  <div class="question-bank-result">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>练习结果详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-loading-component></el-loading-component>
      </div>

      <div v-else-if="examResult" class="result-container">
        <!-- 练习基本信息 -->
        <el-descriptions title="练习信息" :column="3" border>
          <el-descriptions-item label="题库名称">{{ examResult.questionBankTitle }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateTime(examResult.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="用时">{{ formatTime(examResult.timeTaken) }}</el-descriptions-item>
          <el-descriptions-item label="得分">
            <span :class="getScoreClass(examResult.score, examResult.totalScore)">
              {{ examResult.score }}/{{ examResult.totalScore }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="正确题数">{{ examResult.correctAnswers }}/{{ examResult.totalQuestions }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="examResult.isPassed ? 'success' : 'danger'">
              {{ examResult.isPassed ? '通过' : '未通过' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 题目详情 -->
        <div class="questions-section">
          <h3>题目详情</h3>
          <div v-for="(question, index) in questionResults" :key="question.id" class="question-item">
            <div class="question-header">
              <span class="question-number">第{{ index + 1 }}题</span>
              <span class="question-type">{{ getQuestionTypeText(question.questionType) }}</span>
              <span class="question-score">
                得分: {{ question.score }}/{{ question.maxScore }}
              </span>
            </div>
            
            <div class="question-content">
              <div class="question-text">{{ question.questionContent }}</div>
              
              <!-- 选项 -->
              <div v-if="question.options && question.options.length > 0" class="options">
                <div v-for="(option, optIndex) in question.options" :key="optIndex" class="option">
                  <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                  <span class="option-text">{{ option }}</span>
                </div>
              </div>
              
              <!-- 答案对比 -->
              <div class="answer-comparison">
                <div class="answer-item">
                  <span class="answer-label">您的答案:</span>
                  <span class="answer-text" :class="{ 'correct': question.isCorrect, 'incorrect': !question.isCorrect }">
                    {{ question.userAnswer || '未作答' }}
                  </span>
                </div>
                <div class="answer-item">
                  <span class="answer-label">正确答案:</span>
                  <span class="answer-text correct">{{ question.correctAnswer }}</span>
                </div>
              </div>
              
              <!-- 解析 -->
              <div v-if="question.explanation" class="explanation">
                <span class="explanation-label">解析:</span>
                <span class="explanation-text">{{ question.explanation }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-empty description="未找到练习结果">
          <el-button type="primary" @click="goBack">返回</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {ElMessage} from 'element-plus'
import api from '@/api'

export default {
  name: 'QuestionBankResult',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const loading = ref(false)
    const examResult = ref(null)
    const questionResults = ref([])

    const loadExamResult = async () => {
      loading.value = true
      try {
        const resultId = route.params.resultId
        const response = await api.get(`/question-bank-results/${resultId}`)
        if (response.data.success) {
          examResult.value = response.data.data
          questionResults.value = response.data.data.questionResults || []
        } else {
          ElMessage.error('加载练习结果失败')
        }
      } catch (error) {
        ElMessage.error('加载练习结果失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loading.value = false
      }
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

    const getQuestionTypeText = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'TRUE_FALSE': '判断题',
        'FILL_BLANK': '填空题',
        'SHORT_ANSWER': '简答题'
      }
      return typeMap[type] || '未知类型'
    }

    const goBack = () => {
      router.push('/dashboard/question-bank-history')
    }

    onMounted(() => {
      loadExamResult()
    })

    return {
      loading,
      examResult,
      questionResults,
      getScoreClass,
      formatTime,
      formatDateTime,
      getQuestionTypeText,
      goBack
    }
  }
}
</script>

<style scoped>
.question-bank-result {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container {
  text-align: center;
  padding: 40px;
}

.result-container {
  margin-top: 20px;
}

.questions-section {
  margin-top: 30px;
}

.questions-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.question-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fafafa;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.question-number {
  font-weight: bold;
  color: #303133;
}

.question-type {
  background-color: #409eff;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.question-score {
  font-weight: bold;
  color: #606266;
}

.question-content {
  margin-top: 15px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 15px;
  color: #303133;
}

.options {
  margin-bottom: 15px;
}

.option {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  padding: 8px 12px;
  background-color: white;
  border-radius: 4px;
}

.option-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 20px;
}

.option-text {
  flex: 1;
}

.answer-comparison {
  margin-bottom: 15px;
}

.answer-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.answer-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.answer-text {
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: bold;
}

.answer-text.correct {
  background-color: #f0f9ff;
  color: #67c23a;
}

.answer-text.incorrect {
  background-color: #fef0f0;
  color: #f56c6c;
}

.explanation {
  margin-top: 15px;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.explanation-label {
  font-weight: bold;
  color: #409eff;
  margin-right: 10px;
}

.explanation-text {
  color: #606266;
  line-height: 1.6;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
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