<template>
  <div class="question-bank-practice">
    <div class="page-header">
      <h3>{{ questionBankTitle }}</h3>
      <div class="exam-info">
        <span>题目数量：{{ questions.length }}</span>
        <span>当前进度：{{ currentQuestionIndex + 1 }} / {{ questions.length }}</span>
        <span>剩余时间：{{ formatTime(remainingTime) }}</span>
      </div>
    </div>

    <div v-if="questions.length > 0" class="exam-container">
      <!-- 题目导航 -->
      <div class="question-nav">
        <div class="nav-title">题目导航</div>
        <div class="nav-buttons">
          <el-button
            v-for="(question, index) in questions"
            :key="index"
            :type="getQuestionButtonType(index)"
            size="small"
            @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </el-button>
        </div>
      </div>

      <!-- 题目内容 -->
      <div class="question-content">
        <div class="question-header">
          <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
          <span class="question-type">{{ getQuestionTypeText(currentQuestion.type) }}</span>
        </div>

        <div class="question-text">
          {{ currentQuestion.content }}
        </div>

        <!-- 选择题选项 -->
        <div v-if="isChoiceQuestion" class="question-options">
          <el-radio-group
            v-if="currentQuestion.type === 'SINGLE_CHOICE'"
            v-model="userAnswers[currentQuestion.id]"
            @change="saveAnswer"
          >
            <el-radio
              v-for="(option, index) in currentQuestion.options"
              :key="index"
              :label="option"
              class="option-item"
            >
              {{ option }}
            </el-radio>
          </el-radio-group>

          <el-checkbox-group
            v-else-if="currentQuestion.type === 'MULTIPLE_CHOICE'"
            v-model="userAnswers[currentQuestion.id]"
            @change="saveAnswer"
          >
            <el-checkbox
              v-for="(option, index) in currentQuestion.options"
              :key="index"
              :label="option"
              class="option-item"
            >
              {{ option }}
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <!-- 主观题答案 -->
        <div v-else-if="currentQuestion.type === 'SUBJECTIVE'" class="question-answer">
          <el-input
            v-model="userAnswers[currentQuestion.id]"
            type="textarea"
            :rows="4"
            placeholder="请输入您的答案"
            @input="saveAnswer"
          />
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="question-actions">
        <el-button @click="previousQuestion" :disabled="currentQuestionIndex === 0">
          上一题
        </el-button>
        <el-button @click="nextQuestion" :disabled="currentQuestionIndex === questions.length - 1">
          下一题
        </el-button>
        <el-button type="primary" @click="submitExam" :disabled="!canSubmit">
          提交考试
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载题目...</p>
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
import api from '../api'
import { Loading } from '@element-plus/icons-vue'

export default {
  name: 'QuestionBankPractice',
  components: {
    Loading
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()

    const questionBankId = ref(parseInt(route.params.questionBankId))
    const questionBankTitle = ref('')
    const questions = ref([])
    const currentQuestionIndex = ref(0)
    const userAnswers = ref({})
    const remainingTime = ref(7200) // 2小时
    const showSubmitDialog = ref(false)
    const showResultDialog = ref(false)
    const examResult = ref(null)
    const loading = ref(false)
    let timer = null

    const currentQuestion = computed(() => {
      return questions.value[currentQuestionIndex.value] || {}
    })

    const isChoiceQuestion = computed(() => {
      return ['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(currentQuestion.value.type)
    })

    const answeredCount = computed(() => {
      return Object.keys(userAnswers.value).filter(key => {
        const answer = userAnswers.value[key]
        if (Array.isArray(answer)) {
          return answer.length > 0
        }
        return answer && answer.toString().trim() !== ''
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
        'SUBJECTIVE': '主观题'
      }
      return typeMap[type] || type
    }

    const getQuestionButtonType = (index) => {
      const question = questions.value[index]
      if (!question) return 'default'
      
      if (index === currentQuestionIndex.value) {
        return 'primary'
      }
      
      const answer = userAnswers.value[question.id]
      if (Array.isArray(answer)) {
        return answer.length > 0 ? 'success' : 'default'
      }
      return answer && answer.toString().trim() !== '' ? 'success' : 'default'
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

    const saveAnswer = () => {
      // 答案已自动保存到userAnswers中
      // 对于主观题，确保答案是字符串类型
      if (currentQuestion.value.type === 'SUBJECTIVE' && userAnswers.value[currentQuestion.value.id]) {
        userAnswers.value[currentQuestion.value.id] = userAnswers.value[currentQuestion.value.id].toString()
      }
    }

    const submitExam = () => {
      showSubmitDialog.value = true
    }

    const confirmSubmit = async () => {
      try {
        stopTimer()
        
        const answers = Object.keys(userAnswers.value).map(questionId => {
          const answer = userAnswers.value[questionId]
          return {
            questionId: parseInt(questionId),
            userAnswers: Array.isArray(answer) 
              ? answer 
              : [answer.toString()]
          }
        }).filter(item => {
          // 过滤掉空答案
          if (Array.isArray(item.userAnswers)) {
            return item.userAnswers.length > 0 && item.userAnswers.some(a => a && a.toString().trim() !== '')
          }
          return false
        })

        const response = await api.post(`/questions/exam/${questionBankId.value}/submit`, answers, {
          params: { userId: store.getters.currentUser.id }
        })

        examResult.value = response.data.data
        showSubmitDialog.value = false
        showResultDialog.value = true
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '提交失败')
        showSubmitDialog.value = false
      }
    }

    const goBackToList = () => {
      stopTimer()
      router.push('/dashboard/question-banks')
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
      userAnswers,
      remainingTime,
      showSubmitDialog,
      showResultDialog,
      examResult,
      currentQuestion,
      isChoiceQuestion,
      answeredCount,
      canSubmit,
      formatTime,
      getQuestionTypeText,
      getQuestionButtonType,
      goToQuestion,
      previousQuestion,
      nextQuestion,
      saveAnswer,
      submitExam,
      confirmSubmit,
      goBackToList,
      loading
    }
  }
}
</script>

<style scoped>
.question-bank-practice {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.page-header h3 {
  margin: 0;
  color: #333;
}

.exam-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.exam-container {
  flex: 1;
  display: flex;
  gap: 20px;
  overflow: hidden;
}

.question-nav {
  width: 200px;
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  height: fit-content;
}

.nav-title {
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.nav-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.question-content {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.question-number {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.question-type {
  background: #409EFF;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 20px;
  color: #333;
}

.question-options {
  margin-bottom: 20px;
}

.option-item {
  display: block;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

.question-answer {
  margin-bottom: 20px;
}

.question-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.submit-confirm {
  text-align: center;
}

.submit-confirm p {
  margin: 10px 0;
  color: #666;
}

.exam-result {
  max-height: 500px;
  overflow-y: auto;
}

.result-summary {
  text-align: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.result-summary h4 {
  margin: 0 0 15px 0;
  color: #333;
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
  color: #666;
  margin-bottom: 5px;
}

.stat-item .value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-item .value.score {
  color: #409EFF;
  font-size: 24px;
}

.result-details h5 {
  margin: 0 0 15px 0;
  color: #333;
}

.question-result {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.question-result .question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.question-result .question-content {
  margin-bottom: 15px;
  font-weight: bold;
  color: #333;
}

.answer-info {
  background: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
}

.answer-info > div {
  margin-bottom: 8px;
}

.answer-info .label {
  font-weight: bold;
  color: #666;
  margin-right: 10px;
}

.answer-info .value {
  color: #333;
}

.explanation {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ddd;
}

.explanation .value {
  color: #409EFF;
  font-style: italic;
}

.loading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.loading-container .el-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
}

.loading-container p {
  color: #666;
  margin: 0;
}

.empty-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style> 