<template>
  <div class="exam-page">
    <!-- 考试头部 -->
    <div class="exam-header">
      <div class="header-left">
        <el-button @click="confirmExit" icon="ArrowLeft">退出考试</el-button>
        <h3>{{ questionBank?.title || '考试' }}</h3>
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
      <p>正在加载考试题目...</p>
    </div>

    <!-- 考试内容 -->
    <div v-else-if="questions.length > 0" class="exam-content">
      <el-card class="question-card" shadow="hover">
        <div class="question-header">
          <div class="question-number">第 {{ currentQuestionIndex + 1 }} 题</div>
          <div class="question-type">
            <el-tag :type="getQuestionTypeTag(currentQuestion.type)" size="small">
              {{ getQuestionTypeText(currentQuestion.type) }}
            </el-tag>
          </div>
        </div>

        <div class="question-content">
          <h4>{{ currentQuestion.content }}</h4>
        </div>

        <!-- 单选题 -->
        <div v-if="currentQuestion.type === 'SINGLE_CHOICE'" class="question-options">
          <el-radio-group v-model="currentQuestion.userAnswers[0]">
            <el-radio 
              v-for="option in currentQuestion.options" 
              :key="option"
              :label="option"
              class="option-item"
            >
              {{ option }}
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 多选题 -->
        <div v-else-if="currentQuestion.type === 'MULTIPLE_CHOICE'" class="question-options">
          <el-checkbox-group v-model="currentQuestion.userAnswers">
            <el-checkbox 
              v-for="option in currentQuestion.options" 
              :key="option"
              :label="option"
              class="option-item"
            >
              {{ option }}
            </el-checkbox>
          </el-checkbox-group>
        </div>

        <!-- 主观题 -->
        <div v-else-if="currentQuestion.type === 'SUBJECTIVE'" class="question-answer">
          <el-input
            v-model="currentQuestion.userAnswers[0]"
            type="textarea"
            :rows="6"
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
            <el-button type="primary" @click="submitExam" :loading="submitting">
              提交考试
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
    <div v-else class="empty-state">
      <el-empty description="暂无考试题目">
        <el-button @click="goBack">返回</el-button>
      </el-empty>
    </div>

    <!-- 退出确认对话框 -->
    <el-dialog
      v-model="exitDialogVisible"
      title="确认退出"
      width="400px"
    >
      <p>确定要退出考试吗？已答题目将不会保存。</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="exitDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="exitExam">确定退出</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Loading } from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'Exam',
  components: {
    Clock,
    Loading
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()
    
    const questionBankId = route.params.questionBankId
    const questions = ref([])
    const questionBank = ref(null)
    const currentQuestionIndex = ref(0)
    const loading = ref(true)
    const submitting = ref(false)
    const exitDialogVisible = ref(false)
    
    // 考试时间设置（分钟）
    const timeLimit = ref(60)
    const remainingTime = ref(timeLimit.value * 60) // 转换为秒
    let timer = null

    const currentQuestion = computed(() => {
      if (questions.value.length === 0) return null
      return questions.value[currentQuestionIndex.value]
    })

    const loadExam = async () => {
      try {
        loading.value = true
        
        // 加载题库信息
        const questionBankResponse = await api.get(`/question-banks/${questionBankId}`)
        questionBank.value = questionBankResponse.data.data
        
        // 加载考试题目
        const questionsResponse = await api.get(`/questions/exam/${questionBankId}`)
        const examQuestions = questionsResponse.data.data
        
        // 初始化题目答案
        questions.value = examQuestions.map(q => ({
          ...q,
          userAnswers: q.type === 'MULTIPLE_CHOICE' ? [] : ['']
        }))
        
        // 设置时间限制
        timeLimit.value = questionBank.value.timeLimit || 60
        remainingTime.value = timeLimit.value * 60
        
        // 开始计时
        startTimer()
        
      } catch (error) {
        ElMessage.error('加载考试失败：' + (error.response?.data?.message || '未知错误'))
        router.push('/dashboard/exams')
      } finally {
        loading.value = false
      }
    }

    const startTimer = () => {
      timer = setInterval(() => {
        if (remainingTime.value > 0) {
          remainingTime.value--
        } else {
          // 时间到，自动提交
          clearInterval(timer)
          ElMessage.warning('考试时间已到，系统将自动提交')
          submitExam()
        }
      }, 1000)
    }

    const formatTime = (seconds) => {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      
      if (hours > 0) {
        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
      }
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }

    const nextQuestion = () => {
      if (currentQuestionIndex.value < questions.value.length - 1) {
        currentQuestionIndex.value++
      }
    }

    const previousQuestion = () => {
      if (currentQuestionIndex.value > 0) {
        currentQuestionIndex.value--
      }
    }

    const goToQuestion = (index) => {
      currentQuestionIndex.value = index
    }

    const getQuestionButtonType = (index) => {
      const question = questions.value[index]
      if (index === currentQuestionIndex.value) return 'primary'
      if (question.userAnswers && question.userAnswers.some(answer => answer && answer.trim() !== '')) {
        return 'success'
      }
      return 'info'
    }

    const getQuestionTypeText = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'SUBJECTIVE': '主观题'
      }
      return typeMap[type] || type
    }

    const getQuestionTypeTag = (type) => {
      const tagMap = {
        'SINGLE_CHOICE': 'primary',
        'MULTIPLE_CHOICE': 'success',
        'SUBJECTIVE': 'warning'
      }
      return tagMap[type] || 'info'
    }

    const submitExam = async () => {
      try {
        // 确认提交
        await ElMessageBox.confirm(
          '确定要提交考试吗？提交后将无法修改答案。',
          '提交考试',
          {
            confirmButtonText: '确定提交',
            cancelButtonText: '继续答题',
            type: 'warning'
          }
        )

        submitting.value = true
        
        // 准备答案数据
        const answers = questions.value.map(q => ({
          questionId: q.id,
          userAnswers: q.userAnswers || []
        }))

        // 提交考试
        const userId = store.getters.currentUser?.id
        const response = await api.post(`/questions/exam/${questionBankId}/submit?userId=${userId}`, answers)
        const examResult = response.data.data
        
        // 保存考试结果
        try {
          await api.post('/exam-results', examResult, {
            params: { userId }
          })
        } catch (error) {
          console.warn('保存考试结果失败：', error)
        }
        
        // 停止计时
        if (timer) {
          clearInterval(timer)
        }
        
        // 跳转到结果页面
        router.push(`/dashboard/exam-result/${questionBankId}`)
        
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('提交考试失败：' + (error.response?.data?.message || '未知错误'))
        }
      } finally {
        submitting.value = false
      }
    }

    const confirmExit = () => {
      exitDialogVisible.value = true
    }

    const exitExam = () => {
      if (timer) {
        clearInterval(timer)
      }
      exitDialogVisible.value = false
      router.push('/dashboard/exams')
    }

    const goBack = () => {
      router.push('/dashboard/exams')
    }

    onMounted(() => {
      loadExam()
    })

    onUnmounted(() => {
      if (timer) {
        clearInterval(timer)
      }
    })

    return {
      questions,
      questionBank,
      currentQuestionIndex,
      currentQuestion,
      loading,
      submitting,
      exitDialogVisible,
      remainingTime,
      formatTime,
      nextQuestion,
      previousQuestion,
      goToQuestion,
      getQuestionButtonType,
      getQuestionTypeText,
      getQuestionTypeTag,
      submitExam,
      confirmExit,
      exitExam,
      goBack
    }
  }
}
</script>

<style scoped>
.exam-page {
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h3 {
  margin: 0;
  color: #333;
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
  gap: 5px;
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.progress {
  font-size: 14px;
  color: #666;
}

.loading-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 100px 20px;
}

.loading-state .el-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
}

.loading-state p {
  color: #666;
  margin: 0;
}

.exam-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.question-card {
  margin-bottom: 20px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.question-number {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.question-content h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 16px;
  line-height: 1.6;
}

.question-options {
  margin-bottom: 20px;
}

.option-item {
  display: block;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409EFF;
  background: #f0f9ff;
}

.question-answer {
  margin-bottom: 20px;
}

.question-navigation {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.question-list-card {
  height: fit-content;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.question-button {
  min-width: 40px;
  height: 40px;
}

.question-button.current {
  background: #409EFF;
  border-color: #409EFF;
  color: white;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 100px 20px;
}

@media (max-width: 768px) {
  .exam-content {
    grid-template-columns: 1fr;
  }
  
  .exam-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
}
</style> 