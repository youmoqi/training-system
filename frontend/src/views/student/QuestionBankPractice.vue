<template>
  <div class="question-bank-practice">
    <!-- 练习头部 -->
    <div class="exam-header">
      <div class="header-left">
        <el-button @click="confirmExit" icon="ArrowLeft">退出练习</el-button>
        <h3>{{ questionBankTitle || '题库练习' }}</h3>
      </div>
      <div class="header-right">
        <div class="timer">
          <el-icon><Clock /></el-icon>
          <span>{{ formatTime(remainingTime) }}</span>
        </div>
        <div class="progress">
          <span>{{ currentQuestionIndex + 1 }} / {{ questions.length }}</span>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载题目...</p>
    </div>

    <!-- 练习内容 -->
    <div v-else-if="questions.length > 0" class="exam-content">
      <el-card class="question-card" shadow="hover">
        <div class="question-header">
          <div class="question-number">第 {{ currentQuestionIndex + 1 }} 题</div>
          <div class="question-type">
            <el-tag :type="getQuestionTypeTag(currentQuestion.type)" size="small">
              {{ getQuestionTypeText(currentQuestion.type) }}
            </el-tag>
          </div>
          <div class="question-score">
            <el-tag type="warning" size="small">{{ currentQuestion.score || 5 }}分</el-tag>
          </div>
        </div>

        <div class="question-content">
          <h4>{{ currentQuestion.content }}</h4>
        </div>

        <!-- 单选题 -->
        <div v-if="currentQuestion.type === 'SINGLE_CHOICE'" class="question-options">
          <el-radio-group v-model="currentQuestion.userAnswer">
            <el-radio
              v-for="(option, index) in currentQuestion.options"
              :key="option"
              :label="String.fromCharCode(65 + index)"
              class="option-item"
            >
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-content">{{ option }}</span>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 多选题 -->
        <div v-else-if="currentQuestion.type === 'MULTIPLE_CHOICE'" class="question-options">
          <el-checkbox-group v-model="currentQuestion.userAnswers">
            <el-checkbox
              v-for="(option, index) in currentQuestion.options"
              :key="option"
              :label="String.fromCharCode(65 + index)"
              class="option-item"
            >
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-content">{{ option }}</span>
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <!-- 判断题 -->
        <div v-else-if="currentQuestion.type === 'TRUE_FALSE'" class="question-options">
          <el-radio-group v-model="currentQuestion.userAnswer">
            <el-radio label="true" class="option-item">
              <span class="option-label">A.</span>
              <span class="option-content">正确</span>
            </el-radio>
            <el-radio label="false" class="option-item">
              <span class="option-label">B.</span>
              <span class="option-content">错误</span>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 填空题 -->
        <div v-else-if="currentQuestion.type === 'FILL_BLANK'" class="question-answer">
          <el-input
            v-model="currentQuestion.userAnswer"
            type="text"
            placeholder="请输入答案..."
          />
        </div>

        <!-- 简答题 -->
        <div v-else-if="currentQuestion.type === 'SHORT_ANSWER'" class="question-answer">
          <el-input
            v-model="currentQuestion.userAnswer"
            type="textarea"
            :rows="4"
            placeholder="请输入您的答案..."
          />
        </div>
      </el-card>

      <!-- 题目导航 -->
      <div class="question-navigation">
        <el-button @click="previousQuestion" :disabled="currentQuestionIndex === 0">
          上一题
        </el-button>
        <el-button @click="nextQuestion" :disabled="currentQuestionIndex === questions.length - 1">
          下一题
        </el-button>
      </div>

      <!-- 题目列表 -->
      <el-card class="question-list-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>题目导航</span>
            <el-button type="primary" @click="submitExam" :disabled="!canSubmit">
              提交练习
            </el-button>
          </div>
        </template>

        <div class="question-list">
          <el-button
            v-for="(question, index) in questions"
            :key="index"
            :type="getQuestionButtonType(index)"
            :class="['question-button', { 'current': index === currentQuestionIndex }]"
            @click="goToQuestion(index)"
            size="small"
          >
            {{ index + 1 }}
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-container">
      <el-empty description="暂无题目数据" />
      <el-button type="primary" @click="goBackToList">返回题库列表</el-button>
    </div>

    <!-- 提交确认对话框 -->
    <el-dialog v-model="showSubmitDialog" title="确认提交" width="400px">
      <div class="submit-confirm">
        <p>确定要提交考试吗？</p>
        <p>已答题数：{{ answeredCount }} / {{ questions.length }}</p>
        <p>提交后将无法修改答案</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSubmitDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit">确认提交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 考试结果对话框 -->
    <el-dialog v-model="showResultDialog" title="考试结果" width="600px" :close-on-click-modal="false">
      <div v-if="examResult" class="exam-result">
        <div class="result-summary">
          <h4>{{ examResult.questionBankTitle }}</h4>
          <div class="result-stats">
            <div class="stat-item">
              <span class="label">总题数：</span>
              <span class="value">{{ examResult.totalQuestions }}</span>
            </div>
            <div class="stat-item">
              <span class="label">正确题数：</span>
              <span class="value">{{ examResult.correctAnswers }}</span>
            </div>
            <div class="stat-item">
              <span class="label">得分：</span>
              <span class="value score">{{ examResult.score }}分</span>
            </div>
          </div>
        </div>

        <div class="result-details">
          <h5>答题详情</h5>
          <div
            v-for="(result, index) in examResult.questionResults"
            :key="index"
            class="question-result"
          >
            <div class="question-header">
              <span class="question-number">第 {{ index + 1 }} 题</span>
              <el-tag :type="result.isCorrect ? 'success' : 'danger'" size="small">
                {{ result.isCorrect ? '正确' : '错误' }}
              </el-tag>
            </div>
            <div class="question-content">{{ result.content }}</div>
            <div class="answer-info">
              <div class="user-answer">
                <span class="label">您的答案：</span>
                <span class="value">{{ result.userAnswers.join(', ') || '未作答' }}</span>
              </div>
              <div class="correct-answer">
                <span class="label">正确答案：</span>
                <span class="value">{{ result.correctAnswers.join(', ') }}</span>
              </div>
              <div v-if="result.explanation" class="explanation">
                <span class="label">解析：</span>
                <span class="value">{{ result.explanation }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="goBackToList">返回题库列表</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import api from '../../api'
import { Loading, Clock } from '@element-plus/icons-vue'

export default {
  name: 'QuestionBankPractice',
  components: {
    Loading,
    Clock
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()

    const questionBankId = ref(parseInt(route.params.questionBankId))
    const questionBankTitle = ref('')
    const questions = ref([])
    const currentQuestionIndex = ref(0)
    const remainingTime = ref(7200) // 2小时
    const showSubmitDialog = ref(false)
    const showResultDialog = ref(false)
    const examResult = ref(null)
    const loading = ref(false)
    let timer = null

    const currentQuestion = computed(() => {
      return questions.value[currentQuestionIndex.value] || {}
    })

    const answeredCount = computed(() => {
      return questions.value.filter(q => {
        if (q.type === 'MULTIPLE_CHOICE') {
          return q.userAnswers && q.userAnswers.length > 0
        } else {
          return q.userAnswer && q.userAnswer.toString().trim() !== ''
        }
      }).length
    })

    const canSubmit = computed(() => {
      return answeredCount.value > 0
    })

    const loadQuestions = async () => {
      loading.value = true
      try {
        const response = await api.get(`/questions/exam/${questionBankId.value}`)
        questions.value = response.data.data
        if (questions.value.length > 0) {
          questionBankTitle.value = questions.value[0].questionBankTitle || '题库练习'
          
          // 初始化题目答案
          questions.value = questions.value.map(q => ({
            ...q,
            userAnswer: '',
            userAnswers: q.type === 'MULTIPLE_CHOICE' ? [] : null
          }))
        } else {
          ElMessage.warning('该题库暂无题目')
          router.push('/dashboard/question-banks')
        }
      } catch (error) {
        ElMessage.error('加载题目失败：' + (error.response?.data?.message || '未知错误'))
        router.push('/dashboard/question-banks')
      } finally {
        loading.value = false
      }
    }

    const startTimer = () => {
      timer = setInterval(() => {
        if (remainingTime.value > 0) {
          remainingTime.value--
        } else {
          stopTimer()
          ElMessage.warning('考试时间已到，系统将自动提交')
          submitExam()
        }
      }, 1000)
    }

    const stopTimer = () => {
      if (timer) {
        clearInterval(timer)
        timer = null
      }
    }

    const formatTime = (seconds) => {
      if (seconds <= 0) return '00:00:00'
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }

    const getQuestionTypeText = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'TRUE_FALSE': '判断题',
        'FILL_BLANK': '填空题',
        'SHORT_ANSWER': '简答题',
        'SUBJECTIVE': '主观题'
      }
      return typeMap[type] || type
    }

    const getQuestionTypeTag = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': 'success',
        'MULTIPLE_CHOICE': 'info',
        'TRUE_FALSE': 'warning',
        'FILL_BLANK': 'primary',
        'SHORT_ANSWER': 'danger'
      }
      return typeMap[type] || 'info'
    }

    const getQuestionButtonType = (index) => {
      const question = questions.value[index]
      if (!question) return 'default'

      if (index === currentQuestionIndex.value) {
        return 'primary'
      }

      if (question.type === 'MULTIPLE_CHOICE') {
        return question.userAnswers && question.userAnswers.length > 0 ? 'success' : 'default'
      } else {
        return question.userAnswer && question.userAnswer.toString().trim() !== '' ? 'success' : 'default'
      }
    }

    const goToQuestion = (index) => {
      currentQuestionIndex.value = index
    }

    const previousQuestion = () => {
      if (currentQuestionIndex.value > 0) {
        currentQuestionIndex.value--
      }
    }

    const nextQuestion = () => {
      if (currentQuestionIndex.value < questions.value.length - 1) {
        currentQuestionIndex.value++
      }
    }

    const submitExam = () => {
      showSubmitDialog.value = true
    }

    const confirmSubmit = async () => {
      try {
        stopTimer()

        const answers = questions.value.map(question => {
          let userAnswer
          if (question.type === 'MULTIPLE_CHOICE') {
            // 多选题：将选项标签数组转换为逗号分隔的字符串
            userAnswer = (question.userAnswers || []).join(',')
          } else {
            // 单选题、判断题、填空题、简答题：直接使用答案
            userAnswer = question.userAnswer || ''
          }
          
          return {
            questionId: question.id,
            userAnswer: userAnswer
          }
        })

        const response = await api.post(`/questions/exam/${questionBankId.value}/submit`, answers, {
          params: { 
            userId: store.getters.userId,
            timeTaken: (7200 - remainingTime.value) // 计算实际用时（秒）
          }
        })

        if (response.data.success) {
          ElMessage.success('练习提交成功')
          // 跳转到练习结果详情页面
          router.push(`/dashboard/question-bank-results/${response.data.data.id}`)
        } else {
          ElMessage.error('练习提交失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '提交失败')
        showSubmitDialog.value = false
      }
    }

    const goBackToList = () => {
      stopTimer()
      router.push('/dashboard/question-banks')
    }

    const confirmExit = () => {
      if (confirm('确定要退出练习吗？已作答的题目将不会被保存。')) {
        stopTimer()
        router.push('/dashboard/question-banks')
      }
    }

    // 页面离开时停止计时器
    const handleBeforeUnload = () => {
      stopTimer()
    }

    onMounted(() => {
      loadQuestions()
      startTimer()
      window.addEventListener('beforeunload', handleBeforeUnload)
    })

    onUnmounted(() => {
      stopTimer()
      window.removeEventListener('beforeunload', handleBeforeUnload)
    })

    return {
      questionBankId,
      questionBankTitle,
      questions,
      currentQuestionIndex,
      remainingTime,
      showSubmitDialog,
      showResultDialog,
      examResult,
      currentQuestion,
      answeredCount,
      canSubmit,
      formatTime,
      getQuestionTypeText,
      getQuestionTypeTag,
      getQuestionButtonType,
      goToQuestion,
      previousQuestion,
      nextQuestion,
      submitExam,
      confirmSubmit,
      goBackToList,
      loading,
      confirmExit
    }
  }
}
</script>

<style scoped>
.question-bank-practice {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.timer .el-icon {
  font-size: 18px;
}

.progress {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.loading-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.loading-state .el-icon {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.loading-state p {
  color: #909399;
  margin: 0;
}

.exam-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.question-card {
  margin-bottom: 20px;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.question-number {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.question-score {
  margin-left: auto;
}

.question-content h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
}

.question-options {
  margin-top: 20px;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.option-label {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-right: 10px;
  min-width: 20px;
}

.option-content {
  font-size: 14px;
  color: #303133;
  flex: 1;
}

.question-answer {
  margin-top: 20px;
}

.question-navigation {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.question-list-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.question-button {
  min-width: 40px;
  height: 40px;
  border-radius: 50%;
}

.question-button.current {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.submit-confirm {
  text-align: center;
}

.submit-confirm p {
  margin: 10px 0;
  color: #606266;
}

.exam-result {
  max-height: 500px;
  overflow-y: auto;
}

.result-summary {
  text-align: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.result-summary h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.result-stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-item .label {
  display: block;
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-item .value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.stat-item .value.score {
  color: #409eff;
  font-size: 24px;
}

.result-details h5 {
  margin: 0 0 15px 0;
  color: #303133;
}

.question-result {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.question-result .question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.question-result .question-content {
  margin-bottom: 15px;
  font-weight: bold;
  color: #303133;
}

.answer-info {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.answer-info > div {
  margin-bottom: 8px;
}

.answer-info .label {
  font-weight: bold;
  color: #606266;
  margin-right: 10px;
}

.answer-info .value {
  color: #303133;
}

.explanation {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #dcdfe6;
}

.explanation .value {
  color: #409eff;
  font-style: italic;
}

.empty-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
