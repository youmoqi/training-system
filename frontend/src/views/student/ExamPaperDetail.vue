<template>
  <div class="exam-paper-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="$router.go(-1)" icon="ArrowLeft">返回</el-button>
          <span>试卷详情</span>
          <el-button type="primary" @click="purchaseExamPaper" v-if="!hasPurchased">
            购买试卷
          </el-button>
          <el-button type="success" @click="takeExam" v-if="hasPurchased">
            开始考试
          </el-button>
        </div>
      </template>

      <div v-if="examPaper" class="exam-paper-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="试卷名称">{{ examPaper.title }}</el-descriptions-item>
          <el-descriptions-item label="试卷描述">{{ examPaper.description }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ examPaper.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="及格分">{{ examPaper.passScore }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ examPaper.duration }}分钟</el-descriptions-item>
          <el-descriptions-item label="题目数量">{{ examPaper.totalQuestions }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="examPaper.isOnline ? 'success' : 'info'">
              {{ examPaper.isOnline ? '已上线' : '未上线' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="随机出题">
            <el-tag :type="examPaper.isRandom ? 'warning' : 'info'">
              {{ examPaper.isRandom ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="可见角色" :span="2">
            <el-tag v-for="role in examPaper.visibleRoles" :key="role" style="margin-right: 5px">
              {{ getRoleName(role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDateTime(examPaper.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 购买状态 -->
      <div v-if="examPaper" class="purchase-status">
        <el-alert
          v-if="hasPurchased"
          title="您已购买此试卷"
          type="success"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>您可以随时开始考试，考试结果将自动保存。</p>
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
          <el-table-column label="操作" width="120">
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ExamPaperDetail',
  components: {
    ArrowLeft
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const examPaper = ref(null)
    const examResults = ref([])
    const hasPurchased = ref(false)
    const loading = ref(false)

    const loadExamPaper = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exam-papers/${route.params.id}`)
        if (response.data.success) {
          examPaper.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载试卷详情失败')
      } finally {
        loading.value = false
      }
    }

    const checkPurchaseStatus = async () => {
      try {
        const response = await api.get(`/exam-papers/${route.params.id}/purchase-status`)
        if (response.data.success) {
          hasPurchased.value = response.data.data.hasPurchased
          examResults.value = response.data.data.examResults || []
        }
      } catch (error) {
        console.error('检查购买状态失败:', error)
      }
    }

    const purchaseExamPaper = async () => {
      try {
        await ElMessageBox.confirm('确定要购买此试卷吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        })
        
        await api.post(`/exam-papers/${route.params.id}/purchase`)
        ElMessage.success('购买成功')
        hasPurchased.value = true
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('购买失败')
        }
      }
    }

    const takeExam = () => {
      router.push(`/dashboard/exam-papers/${route.params.id}/exam`)
    }

    const viewResult = (result) => {
      router.push(`/dashboard/exam-papers/${route.params.id}/results/${result.id}`)
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
      loadExamPaper()
      checkPurchaseStatus()
    })

    return {
      examPaper,
      examResults,
      hasPurchased,
      loading,
      purchaseExamPaper,
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