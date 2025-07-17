<template>
  <div class="exam-result-page">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft">返回考试列表</el-button>
        <h3>考试结果</h3>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载考试结果...</p>
    </div>

    <!-- 考试结果 -->
    <div v-else-if="examResult" class="exam-result">
      <!-- 成绩概览 -->
      <el-card class="score-overview" shadow="hover">
        <div class="score-header">
          <h2>{{ examResult.questionBankTitle }}</h2>
          <div class="score-info">
            <div class="score-circle">
              <div class="score-number">{{ examResult.score }}</div>
              <div class="score-label">分</div>
            </div>
            <div class="score-details">
              <div class="detail-item">
                <span class="label">总题数：</span>
                <span class="value">{{ examResult.totalQuestions }}</span>
              </div>
              <div class="detail-item">
                <span class="label">正确题数：</span>
                <span class="value correct">{{ examResult.correctAnswers }}</span>
              </div>
              <div class="detail-item">
                <span class="label">正确率：</span>
                <span class="value">{{ ((examResult.correctAnswers / examResult.totalQuestions) * 100).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 题目详情 -->
      <el-card class="question-details" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>题目详情</span>
            <div class="filter-buttons">
              <el-button 
                :type="filterType === 'all' ? 'primary' : 'default'"
                size="small"
                @click="filterType = 'all'"
              >
                全部
              </el-button>
              <el-button 
                :type="filterType === 'correct' ? 'primary' : 'default'"
                size="small"
                @click="filterType = 'correct'"
              >
                正确
              </el-button>
              <el-button 
                :type="filterType === 'incorrect' ? 'primary' : 'default'"
                size="small"
                @click="filterType = 'incorrect'"
              >
                错误
              </el-button>
            </div>
          </div>
        </template>

        <div class="questions-list">
          <div 
            v-for="(question, index) in filteredQuestions" 
            :key="question.questionId"
            class="question-item"
            :class="{ 'correct': question.isCorrect, 'incorrect': !question.isCorrect }"
          >
            <div class="question-header">
              <div class="question-number">第 {{ index + 1 }} 题</div>
              <div class="question-type">
                <el-tag :type="getQuestionTypeTag(question.type)" size="small">
                  {{ getQuestionTypeText(question.type) }}
                </el-tag>
              </div>
              <div class="question-status">
                <el-tag :type="question.isCorrect ? 'success' : 'danger'" size="small">
                  {{ question.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </div>
            </div>

            <div class="question-content">
              <h4>{{ question.content }}</h4>
            </div>

            <!-- 选项（选择题） -->
            <div v-if="question.type !== 'SUBJECTIVE'" class="question-options">
              <div 
                v-for="option in getQuestionOptions(question)" 
                :key="option"
                class="option-item"
                :class="getOptionClass(question, option)"
              >
                {{ option }}
                <el-icon v-if="isCorrectAnswer(question, option)" class="correct-icon">
                  <Check />
                </el-icon>
                <el-icon v-if="isUserAnswer(question, option) && !isCorrectAnswer(question, option)" class="incorrect-icon">
                  <Close />
                </el-icon>
              </div>
            </div>

            <!-- 主观题答案 -->
            <div v-else class="subjective-answers">
              <div class="answer-section">
                <div class="answer-label">您的答案：</div>
                <div class="answer-content user-answer">
                  {{ question.userAnswers?.[0] || '未作答' }}
                </div>
              </div>
              <div class="answer-section">
                <div class="answer-label">参考答案：</div>
                <div class="answer-content correct-answer">
                  {{ question.correctAnswers?.[0] || '暂无参考答案' }}
                </div>
              </div>
            </div>

            <!-- 解析 -->
            <div v-if="question.explanation" class="question-explanation">
              <div class="explanation-label">解析：</div>
              <div class="explanation-content">{{ question.explanation }}</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button @click="retakeExam" type="primary">重新考试</el-button>
        <el-button @click="goToExams">返回考试列表</el-button>
        <el-button @click="printResult" type="success">打印结果</el-button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="暂无考试结果">
        <el-button @click="goBack">返回</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Loading, Check, Close } from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'ExamResult',
  components: {
    Loading,
    Check,
    Close
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()
    
    const questionBankId = route.params.questionBankId
    const examResult = ref(null)
    const loading = ref(true)
    const filterType = ref('all')

    const filteredQuestions = computed(() => {
      if (!examResult.value?.questionResults) return []
      
      switch (filterType.value) {
        case 'correct':
          return examResult.value.questionResults.filter(q => q.isCorrect)
        case 'incorrect':
          return examResult.value.questionResults.filter(q => !q.isCorrect)
        default:
          return examResult.value.questionResults
      }
    })

    const loadExamResult = async () => {
      try {
        loading.value = true
        const userId = store.getters.currentUser?.id
        if (!userId) {
          ElMessage.error('用户信息获取失败')
          return
        }
        
        const response = await api.get(`/exam-results/${questionBankId}?userId=${userId}`)
        examResult.value = response.data.data
      } catch (error) {
        // 如果接口不存在，显示模拟数据
        examResult.value = {
          questionBankId: questionBankId,
          questionBankTitle: '模拟题库',
          totalQuestions: 10,
          correctAnswers: 7,
          score: 70,
          questionResults: [
            {
              questionId: 1,
              content: '易制爆化学品是指哪些化学品？',
              type: 'SINGLE_CHOICE',
              userAnswers: ['A'],
              correctAnswers: ['A'],
              isCorrect: true,
              explanation: '易制爆化学品是指可以用于制造爆炸物品的化学品。'
            },
            {
              questionId: 2,
              content: '爆破作业人员需要具备哪些资质？',
              type: 'MULTIPLE_CHOICE',
              userAnswers: ['A', 'B'],
              correctAnswers: ['A', 'B', 'C'],
              isCorrect: false,
              explanation: '爆破作业人员需要具备相应的资质证书、安全培训证书等。'
            }
          ]
        }
        ElMessage.warning('当前显示模拟数据，请配置后端接口')
      } finally {
        loading.value = false
      }
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

    const getQuestionOptions = (question) => {
      // 这里应该从题目中获取选项，暂时返回模拟数据
      return ['A. 选项A', 'B. 选项B', 'C. 选项C', 'D. 选项D']
    }

    const isCorrectAnswer = (question, option) => {
      const optionLetter = option.charAt(0)
      return question.correctAnswers?.includes(optionLetter)
    }

    const isUserAnswer = (question, option) => {
      const optionLetter = option.charAt(0)
      return question.userAnswers?.includes(optionLetter)
    }

    const getOptionClass = (question, option) => {
      const optionLetter = option.charAt(0)
      const isCorrect = question.correctAnswers?.includes(optionLetter)
      const isUserAnswer = question.userAnswers?.includes(optionLetter)
      
      if (isCorrect) return 'correct-option'
      if (isUserAnswer && !isCorrect) return 'incorrect-option'
      return ''
    }

    const retakeExam = () => {
      router.push(`/dashboard/exam/${questionBankId}`)
    }

    const goToExams = () => {
      router.push('/dashboard/exams')
    }

    const goBack = () => {
      router.push('/dashboard/exams')
    }

    const printResult = () => {
      window.print()
    }

    onMounted(() => {
      loadExamResult()
    })

    return {
      examResult,
      loading,
      filterType,
      filteredQuestions,
      getQuestionTypeText,
      getQuestionTypeTag,
      getQuestionOptions,
      isCorrectAnswer,
      isUserAnswer,
      getOptionClass,
      retakeExam,
      goToExams,
      goBack,
      printResult
    }
  }
}
</script>

<style scoped>
.exam-result-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
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

.exam-result {
  max-width: 1200px;
  margin: 0 auto;
}

.score-overview {
  margin-bottom: 20px;
}

.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.score-info {
  display: flex;
  align-items: center;
  gap: 30px;
}

.score-circle {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  color: white;
}

.score-number {
  font-size: 36px;
  font-weight: bold;
}

.score-label {
  font-size: 14px;
}

.score-details {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-item {
  display: flex;
  gap: 10px;
}

.detail-item .label {
  color: #666;
  min-width: 80px;
}

.detail-item .value {
  font-weight: bold;
  color: #333;
}

.detail-item .value.correct {
  color: #67C23A;
}

.question-details {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-item {
  padding: 20px;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  background: #fff;
}

.question-item.correct {
  border-left: 4px solid #67C23A;
}

.question-item.incorrect {
  border-left: 4px solid #F56C6C;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.question-number {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.question-content h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
  line-height: 1.6;
}

.question-options {
  margin-bottom: 15px;
}

.option-item {
  position: relative;
  padding: 10px;
  margin-bottom: 8px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  background: #f9f9f9;
}

.option-item.correct-option {
  background: #f0f9ff;
  border-color: #67C23A;
  color: #67C23A;
}

.option-item.incorrect-option {
  background: #fef0f0;
  border-color: #F56C6C;
  color: #F56C6C;
}

.correct-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #67C23A;
}

.incorrect-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #F56C6C;
}

.subjective-answers {
  margin-bottom: 15px;
}

.answer-section {
  margin-bottom: 10px;
}

.answer-label {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.answer-content {
  padding: 10px;
  border-radius: 4px;
  background: #f9f9f9;
  line-height: 1.5;
}

.user-answer {
  border-left: 3px solid #409EFF;
}

.correct-answer {
  border-left: 3px solid #67C23A;
}

.question-explanation {
  margin-top: 15px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 4px;
  border-left: 3px solid #409EFF;
}

.explanation-label {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.explanation-content {
  color: #666;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 100px 20px;
}

@media (max-width: 768px) {
  .score-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .score-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .question-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
}

@media print {
  .page-header,
  .action-buttons {
    display: none;
  }
}
</style> 