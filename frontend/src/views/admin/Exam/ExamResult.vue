<template>
  <div class="exam-paper-result">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考试结果详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-loading-component></el-loading-component>
      </div>

      <div v-else-if="examResult" class="result-container">
        <!-- 考试基本信息 -->
        <el-descriptions title="考试信息" :column="3" border>
          <el-descriptions-item label="试卷名称">{{ examResult.examTitle }}</el-descriptions-item>
          <el-descriptions-item label="考生姓名">{{ examResult.userName }}</el-descriptions-item>
          <el-descriptions-item label="考试时间">{{ formatDateTime(examResult.examTime) }}</el-descriptions-item>
          <el-descriptions-item label="得分">
            <span :class="getScoreClass(examResult.score, examResult.totalScore)">
              {{ examResult.score }}/{{ examResult.totalScore }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="正确题数">{{ examResult.correctAnswers }}/{{
              examResult.totalQuestions
            }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="examResult.isPassed ? 'success' : 'danger'">
              {{ examResult.isPassed ? '通过' : '未通过' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="考试次数">{{ examResult.attemptNumber }}</el-descriptions-item>
          <el-descriptions-item label="用时">{{ formatTime(examResult.timeTaken) }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ examResult.examDuration || '--' }}分钟</el-descriptions-item>
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
              <span v-if="!question.isCorrect" class="question-status incorrect">
                错误
              </span>
              <span v-else-if="question.isCorrect" class="question-status correct">
                正确
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
                  <span class="answer-label">考生答案:</span>
                  <span class="answer-text"
                        :class="{ 'correct': question.isCorrect, 'incorrect': !question.isCorrect }">
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
        <el-empty description="未找到考试结果">
          <el-button type="primary" @click="goBack">返回</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'ExamResult',
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
        const response = await api.get(`/exam-results/${resultId}`)
        if (response.data.success) {
          examResult.value = response.data.data
          questionResults.value = response.data.data.questionResults || []
          
          // 如果有试卷信息，获取试卷时长
          if (examResult.value.examId) {
            try {
              const examResponse = await api.get(`/exams/${examResult.value.examId}`)
              if (examResponse.data.success) {
                examResult.value.examDuration = examResponse.data.data.duration
              }
            } catch (error) {
              console.error('获取试卷信息失败:', error)
            }
          }
        } else {
          ElMessage.error('加载考试结果失败')
        }
      } catch (error) {
        ElMessage.error('加载考试结果失败: ' + (error.response?.data?.message || error.message))
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
      router.push('/admin/exam-history')
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
.exam-paper-result {
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
  font-size: 16px;
  font-weight: 600;
}

.question-item {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #fff;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.question-number {
  font-weight: 600;
  margin-right: 15px;
  color: #303133;
}

.question-type {
  padding: 2px 8px;
  background-color: #ecf5ff;
  color: #409eff;
  border-radius: 3px;
  font-size: 12px;
  margin-right: 15px;
}

.question-score {
  margin-right: 15px;
  color: #606266;
  font-size: 13px;
}

.question-status {
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 12px;
  font-weight: 600;
}

.question-status.correct {
  background-color: #f0f9eb;
  color: #67c23a;
}

.question-status.incorrect {
  background-color: #fef0f0;
  color: #f56c6c;
}

.question-content {
  padding-top: 10px;
}

.question-text {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #303133;
}

.options {
  margin-bottom: 15px;
}

.option {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
}

.option-label {
  font-weight: 600;
  margin-right: 8px;
  min-width: 20px;
  text-align: right;
  color: #606266;
}

.option-text {
  flex: 1;
  color: #303133;
}

.answer-comparison {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.answer-item {
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.answer-label {
  font-weight: 600;
  margin-right: 10px;
  width: 80px;
  color: #606266;
}

.answer-text {
  flex: 1;
}

.answer-text.correct {
  color: #67c23a;
  font-weight: 600;
}

.answer-text.incorrect {
  color: #f56c6c;
  font-weight: 600;
}

.explanation {
  background-color: #f0f7ff;
  padding: 10px;
  border-radius: 4px;
}

.explanation-label {
  font-weight: 600;
  margin-right: 10px;
  color: #409eff;
}

.explanation-text {
  color: #606266;
  line-height: 1.6;
}

.empty-state {
  padding: 40px;
  text-align: center;
}

/* 分数样式 */
.score-excellent {
  color: #67c23a;
  font-weight: 600;
  font-size: 16px;
}

.score-good {
  color: #e6a23c;
  font-weight: 600;
  font-size: 16px;
}

.score-poor {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}
</style>