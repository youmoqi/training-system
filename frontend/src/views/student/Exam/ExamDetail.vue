<template>
  <div class="exam-paper-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="$router.go(-1)" icon="ArrowLeft">返回</el-button>
          <span>试卷详情</span>
          <el-button type="primary" @click="purchaseExam" v-if="!hasPurchased">
            购买试卷
          </el-button>
          <el-button type="success" @click="takeExam" v-if="hasPurchased" :disabled="!canTakeExam">
            开始考试
          </el-button>
        </div>
      </template>

      <div v-if="exam" class="exam-paper-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="试卷名称">{{ exam.title }}</el-descriptions-item>
          <el-descriptions-item label="试卷描述">{{ exam.description }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ exam.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="及格分">{{ exam.passScore }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ exam.duration }}分钟</el-descriptions-item>
          <el-descriptions-item label="题目数量">{{ exam.totalQuestions }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="exam.isOnline ? 'success' : 'info'">
              {{ exam.isOnline ? '已上线' : '未上线' }}
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="可见角色" :span="2">
            <el-tag v-for="role in exam.visibleRoleIds" :key="role" style="margin-right: 5px">
              {{ getRoleName(role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDateTime(exam.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 购买状态 -->
      <div v-if="exam" class="purchase-status">
        <el-alert
          v-if="hasPurchased"
          title="您已购买此试卷"
          type="success"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>您可以随时开始考试，考试结果将自动保存。</p>
            <p v-if="exam.allowRetake">
              最大考试次数：{{ exam.maxAttempts }}次
            </p>
            <p v-else>
              此试卷不允许重复考试
            </p>
          </template>
        </el-alert>
        <el-alert
          v-else
          title="您尚未购买此试卷"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>购买后即可参加考试，考试结果将自动保存。</p>
            <p v-if="exam.allowRetake">
              最大考试次数：{{ exam.maxAttempts }}次
            </p>
            <p v-else>
              此试卷不允许重复考试
            </p>
          </template>
        </el-alert>
      </div>

      <!-- 考试记录 -->
      <div v-if="hasPurchased && examResults.length > 0" class="exam-results">
        <h3>考试记录</h3>
        <el-table :data="examResults" style="width: 100%">
          <el-table-column prop="examTime" label="考试时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.examTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="attemptNumber" label="考试次数" width="100" />
          <el-table-column prop="score" label="得分" width="100" />
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column prop="correctAnswers" label="正确题数" width="120" />
          <el-table-column prop="totalQuestions" label="总题数" width="100" />
          <el-table-column prop="timeTaken" label="用时" width="120">
            <template #default="scope">
              {{ formatTime(scope.row.timeTaken) }}
            </template>
          </el-table-column>
          <el-table-column label="是否通过" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.isPassed ? 'success' : 'danger'">
                {{ scope.row.isPassed ? '通过' : '未通过' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="viewResult(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import api from '@/api'
import store from "@/store"

export default {
  name: 'ExamDetail',
  components: {
    ArrowLeft
  },
  setup() {
    const route = useRoute()
    const router = useRouter()

    const exam = ref(null)
    const examResults = ref([])
    const hasPurchased = ref(false)
    const loading = ref(false)
    const canTakeExam = ref(true)

    const loadExam = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exams/${route.params.id}`)
        if (response.data.success) {
          exam.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载试卷详情失败')
      } finally {
        loading.value = false
      }
    }

    const checkPurchaseStatus = async () => {
      try {
        const response = await api.get(`/exams/${route.params.id}/purchase-status`, {
          params: {
            userId: store.getters.userId
          }
        })
        if (response.data.success) {
          hasPurchased.value = response.data.data.hasPurchased
          examResults.value = response.data.data.examResults || []

          // 如果已购买，检查是否可以参加考试
          if (hasPurchased.value) {
            try {
              const canRetakeResponse = await api.get(`/exams/${route.params.id}/can-retake/${store.getters.userId}`)
              canTakeExam.value = canRetakeResponse.data.success && canRetakeResponse.data.data
            } catch (error) {
              console.error('检查考试权限失败:', error)
              canTakeExam.value = false
            }
          }
        }
      } catch (error) {
        console.error('检查购买状态失败:', error)
      }
    }

    const purchaseExam = async () => {
      try {
        await ElMessageBox.confirm('确定要购买此试卷吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        })

        await api.post(`/exams/${route.params.id}/purchase`)
        ElMessage.success('购买成功')
        // 重新检查购买状态
        await checkPurchaseStatus()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('购买失败')
        }
      }
    }

    const takeExam = async () => {
      try {
        // 检查是否可以参加考试
        const canRetakeResponse = await api.get(`/exams/${route.params.id}/can-retake/${store.getters.userId}`)
        if (canRetakeResponse.data.success && canRetakeResponse.data.data) {
          router.push(`/dashboard/exams/${route.params.id}/exam`)
        } else {
          ElMessage.warning('您已达到该试卷的最大考试次数或试卷不允许重复考试')
        }
      } catch (error) {
        ElMessage.error('检查考试权限失败')
      }
    }

    const viewResult = (result) => {
      router.push(`/dashboard/exams/${route.params.id}/results/${result.id}`)
    }

    const getRoleName = (role) => {
      const roleMap = {
        'EXPLOSIVE_USER': '易制爆用户',
        'BLAST_USER': '爆破用户'
      }
      return roleMap[role] || role
    }

    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    const formatTime = (seconds) => {
      if (!seconds) return '0分钟'
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = seconds % 60
      return `${minutes}分${remainingSeconds}秒`
    }

    onMounted(() => {
      loadExam()
      checkPurchaseStatus()
    })

    return {
      exam,
      examResults,
      hasPurchased,
      loading,
      canTakeExam,
      purchaseExam,
      takeExam,
      viewResult,
      getRoleName,
      formatDateTime,
      formatTime
    }
  }
}
</script>

<style scoped>
.exam-paper-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-paper-info {
  margin-bottom: 30px;
}

.purchase-status {
  margin-bottom: 30px;
}

.exam-results {
  margin-top: 30px;
}

.exam-results h3 {
  margin-bottom: 20px;
  color: #303133;
}
</style>
