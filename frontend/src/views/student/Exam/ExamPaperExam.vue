<template>
  <div class="exam-page">
    <!-- 考试头部 -->
    <div class="exam-header">
      <div class="header-left">
        <el-button @click="confirmExit" icon="ArrowLeft">退出考试</el-button>
        <h3>{{ examPaper?.title || '试卷考试' }}</h3>
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
      <p>正在加载试卷题目...</p>
    </div>

    <!-- 考试内容 -->
    <div v-else-if="questions.length > 0" class="exam-content">
      <el-card class="question-card" shadow="hover">
        <div class="question-header">
          <div class="question-number">第 {{ currentQuestionIndex + 1 }} 题</div>
          <div class="question-type">
            <el-tag :type="getQuestionTypeTag(currentQuestion.questionType)" size="small">
              {{ getQuestionTypeText(currentQuestion.questionType) }}
            </el-tag>
          </div>
          <div class="question-score">
            <el-tag type="warning" size="small">{{ currentQuestion.score }}分</el-tag>
          </div>
        </div>

        <div class="question-content">
          <h4>{{ currentQuestion.questionContent }}</h4>
        </div>

        <!-- 单选题 -->
        <div v-if="currentQuestion.questionType === 'SINGLE_CHOICE'" class="question-options">
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
        <div v-else-if="currentQuestion.questionType === 'MULTIPLE_CHOICE'" class="question-options">
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
        <div v-else-if="currentQuestion.questionType === 'TRUE_FALSE'" class="question-options">
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
        <div v-else-if="currentQuestion.questionType === 'FILL_BLANK'" class="question-answer">
          <el-input
            v-model="currentQuestion.userAnswer"
            type="text"
            placeholder="请输入答案..."
          />
        </div>

        <!-- 简答题 -->
        <div v-else-if="currentQuestion.questionType === 'SHORT_ANSWER'" class="question-answer">
          <el-input
            v-model="currentQuestion.userAnswer"
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

    <!-- 提交确认对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="确认提交"
      width="400px"
    >
      <p>确定要提交考试吗？提交后将无法修改答案。</p>
      <div class="submit-summary">
        <p>已答题数：{{ answeredCount }} / {{ questions.length }}</p>
        <p>剩余时间：{{ formatTime(remainingTime) }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="submitDialogVisible = false">继续答题</el-button>
          <el-button type="primary" @click="confirmSubmit" :loading="submitting">
            确定提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Loading } from '@element-plus/icons-vue'
import api from '@/api'
import store from "@/store"

export default {
  name: 'ExamPaperExam',
  components: {
    Clock,
    Loading
  },
  setup() {
    const route = useRoute()
    const router = useRouter()

    const examPaperId = route.params.id
    const questions = ref([])
    const examPaper = ref(null)
    const currentQuestionIndex = ref(0)
    const loading = ref(true)
    const submitting = ref(false)
    const exitDialogVisible = ref(false)
    const submitDialogVisible = ref(false)

    // 考试时间设置（分钟）
    const timeLimit = ref(120)
    const remainingTime = ref(timeLimit.value * 60) // 转换为秒
    let timer = null

    const currentQuestion = computed(() => {
      if (questions.value.length === 0) return null
      return questions.value[currentQuestionIndex.value]
    })

    const answeredCount = computed(() => {
      return questions.value.filter(q => {
        if (q.questionType === 'MULTIPLE_CHOICE') {
          return q.userAnswers && q.userAnswers.length > 0
        } else {
          return q.userAnswer && q.userAnswer.trim() !== ''
        }
      }).length
    })

    const loadExam = async () => {
      try {
        loading.value = true

        // 检查是否可以参加考试
        const canRetakeResponse = await api.get(`/exam-papers/${examPaperId}/can-retake/${store.getters.userId}`)
        if (!canRetakeResponse.data.success || !canRetakeResponse.data.data) {
          ElMessage.error('您已达到该试卷的最大考试次数或试卷不允许重复考试')
          router.push(`/dashboard/exam-papers/${examPaperId}`)
          return
        }

        // 加载试卷信息
        const examPaperResponse = await api.get(`/exam-papers/${examPaperId}`)
        examPaper.value = examPaperResponse.data.data

        // 加载试卷题目
        const questionsResponse = await api.get(`/exam-papers/${examPaperId}/questions`)
        const examQuestions = questionsResponse.data.data

        console.log('试卷题目数据:', examQuestions) // 调试信息

        // 初始化题目答案
        questions.value = examQuestions.map(q => ({
          ...q,
          userAnswer: '',
          userAnswers: q.questionType === 'MULTIPLE_CHOICE' ? [] : null
        }))

        console.log('处理后的题目数据:', questions.value) // 调试信息

        // 设置时间限制
        timeLimit.value = examPaper.value.duration || 120
        remainingTime.value = timeLimit.value * 60

        // 开始计时
        startTimer()
      } catch (error) {
        ElMessage.error('加载试卷失败')
        console.error('加载试卷失败:', error)
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
        return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
      } else {
        return `${minutes}:${secs.toString().padStart(2, '0')}`
      }
    }

    const getQuestionTypeTag = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': 'primary',
        'MULTIPLE_CHOICE': 'success',
        'TRUE_FALSE': 'warning',
        'FILL_BLANK': 'info',
        'SHORT_ANSWER': 'danger'
      }
      return typeMap[type] || 'default'
    }

    const getQuestionTypeText = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'TRUE_FALSE': '判断题',
        'FILL_BLANK': '填空题',
        'SHORT_ANSWER': '简答题'
      }
      return typeMap[type] || '未知'
    }

    const getQuestionButtonType = (index) => {
      const question = questions.value[index]
      if (index === currentQuestionIndex.value) return 'primary'
      
      if (question.questionType === 'MULTIPLE_CHOICE') {
        return question.userAnswers && question.userAnswers.length > 0 ? 'success' : 'default'
      } else {
        return question.userAnswer && question.userAnswer.trim() !== '' ? 'success' : 'default'
      }
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

    const goToQuestion = (index) => {
      currentQuestionIndex.value = index
    }

    const confirmExit = () => {
      exitDialogVisible.value = true
    }

    const exitExam = () => {
      clearInterval(timer)
      router.go(-1)
    }

    const submitExam = () => {
      submitDialogVisible.value = true
    }

    const confirmSubmit = async () => {
      try {
        submitting.value = true
        
        // 准备提交数据
        const submitData = {
          examPaperId: parseInt(examPaperId),
          answers: questions.value.map(q => ({
            questionId: q.questionId,
            userAnswer: q.questionType === 'MULTIPLE_CHOICE' ? q.userAnswers.join(',') : q.userAnswer
          })).filter(a => a.userAnswer && a.userAnswer.trim() !== ''),
          timeTaken: (timeLimit.value * 60) - remainingTime.value
        }

        // 提交考试
        const response = await api.post('/exam-papers/submit', submitData)
        
        if (response.data.success) {
          ElMessage.success('考试提交成功')
          clearInterval(timer)
          
          // 跳转到试卷详情页面
          router.push(`/dashboard/exam-papers/${examPaperId}`)
        } else {
          ElMessage.error('考试提交失败')
        }
      } catch (error) {
        ElMessage.error('考试提交失败: ' + (error.response?.data?.message || error.message))
      } finally {
        submitting.value = false
        submitDialogVisible.value = false
      }
    }

    const goBack = () => {
      router.go(-1)
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
      examPaper,
      questions,
      currentQuestion,
      currentQuestionIndex,
      loading,
      submitting,
      exitDialogVisible,
      submitDialogVisible,
      remainingTime,
      answeredCount,
      formatTime,
      getQuestionTypeTag,
      getQuestionTypeText,
      getQuestionButtonType,
      previousQuestion,
      nextQuestion,
      goToQuestion,
      confirmExit,
      exitExam,
      submitExam,
      confirmSubmit,
      goBack
    }
  }
}
</script>

<style scoped>
.exam-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.exam-header {
  background-color: #fff;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h3 {
  margin: 0;
  color: #303133;
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
  color: #e6a23c;
}

.progress {
  font-size: 14px;
  color: #606266;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.submit-summary {
  margin-top: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.submit-summary p {
  margin: 5px 0;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
