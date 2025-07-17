<template>
  <div class="exams-page">
    <div class="page-header">
      <h3>我的考试</h3>
      <p>查看您已购买的题库，可以参加正式考试</p>
    </div>

    <!-- 考试列表 -->
    <el-card v-if="!loading" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>可参加的考试</span>
        </div>
      </template>

      <div v-if="questionBanks.length === 0" class="empty-state">
        <el-empty description="暂无可参加的考试">
          <el-button type="primary" @click="goToQuestionBanks">去购买题库</el-button>
        </el-empty>
      </div>

      <el-row v-else :gutter="20">
        <el-col 
          v-for="questionBank in questionBanks" 
          :key="questionBank.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
        >
          <el-card class="exam-card" shadow="hover">
            <div class="exam-info">
              <h4>{{ questionBank.title }}</h4>
              <p class="description">{{ questionBank.description }}</p>
              <div class="exam-stats">
                <span class="stat">
                  <el-icon><Document /></el-icon>
                  {{ questionBank.questionCount || 0 }} 题
                </span>
                <span class="stat">
                  <el-icon><Clock /></el-icon>
                  {{ questionBank.timeLimit || 60 }} 分钟
                </span>
              </div>
              <div class="exam-actions">
                <el-button 
                  type="primary" 
                  @click="startExam(questionBank.id)"
                  :loading="startingExam === questionBank.id"
                >
                  开始考试
                </el-button>
                <el-button 
                  @click="viewHistory(questionBank.id)"
                  size="small"
                >
                  查看历史
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 加载状态 -->
    <el-card v-else shadow="hover">
      <div class="loading-state">
        <el-icon class="is-loading"><Loading /></el-icon>
        <p>正在加载考试列表...</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Clock, Loading } from '@element-plus/icons-vue'
import api from '../api'

export default {
  name: 'Exams',
  components: {
    Document,
    Clock,
    Loading
  },
  setup() {
    const router = useRouter()
    const store = useStore()
    const questionBanks = ref([])
    const loading = ref(true)
    const startingExam = ref(null)

    const loadQuestionBanks = async () => {
      try {
        loading.value = true
        const userId = store.getters.currentUser?.id
        if (!userId) {
          ElMessage.error('用户信息获取失败')
          return
        }
        const response = await api.get(`/question-banks/purchased?userId=${userId}`)
        questionBanks.value = response.data.data || []
      } catch (error) {
        ElMessage.error('加载考试列表失败：' + (error.response?.data?.message || '未知错误'))
        questionBanks.value = []
      } finally {
        loading.value = false
      }
    }

    const startExam = async (questionBankId) => {
      try {
        startingExam.value = questionBankId
        
        // 确认开始考试
        await ElMessageBox.confirm(
          '确定要开始考试吗？开始后计时器将启动，请确保有足够的时间完成考试。',
          '开始考试',
          {
            confirmButtonText: '确定开始',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        // 跳转到考试页面
        router.push(`/dashboard/exam/${questionBankId}`)
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('开始考试失败：' + (error.response?.data?.message || '未知错误'))
        }
      } finally {
        startingExam.value = null
      }
    }

    const viewHistory = (questionBankId) => {
      // 这里可以跳转到考试历史页面
      ElMessage.info('考试历史功能开发中...')
    }

    const goToQuestionBanks = () => {
      router.push('/dashboard/question-banks')
    }

    onMounted(() => {
      loadQuestionBanks()
    })

    return {
      questionBanks,
      loading,
      startingExam,
      startExam,
      viewHistory,
      goToQuestionBanks
    }
  }
}
</script>

<style scoped>
.exams-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
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

.exam-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.exam-card:hover {
  transform: translateY(-5px);
}

.exam-info h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.description {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.exam-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 12px;
}

.exam-actions {
  display: flex;
  gap: 10px;
}
</style> 