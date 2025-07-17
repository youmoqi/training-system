<template>
  <div class="question-banks-page">
    <div class="page-header">
      <h3>题库练习</h3>
      <el-button type="primary" @click="showPurchaseDialog = true">
        选择题库
      </el-button>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载题库...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="userQuestionBanks.length === 0" class="empty-container">
      <el-empty description="您还没有购买任何题库" />
      <el-button type="primary" @click="showPurchaseDialog = true">去购买题库</el-button>
    </div>

    <!-- 题库列表 -->
    <el-row v-else :gutter="20">
      <el-col
        v-for="userQuestionBank in userQuestionBanks"
        :key="userQuestionBank.id"
        :span="8"
      >
        <el-card class="question-bank-card" shadow="hover">
          <div class="question-bank-info">
            <h4>{{ userQuestionBank.questionBankTitle }}</h4>
            <p>{{ userQuestionBank.questionBankDescription }}</p>
            
            <div class="question-bank-meta">
              <span class="purchase-time">
                购买时间：{{ formatDate(userQuestionBank.purchaseTime) }}
              </span>
              <el-tag
                :type="userQuestionBank.isCompleted ? 'success' : 'warning'"
                size="small"
              >
                {{ userQuestionBank.isCompleted ? '已完成' : '未完成' }}
              </el-tag>
            </div>
            
            <div v-if="userQuestionBank.score !== null" class="score-info">
              <span>得分：{{ userQuestionBank.score }}分</span>
            </div>
            
            <div class="question-bank-actions">
              <el-button
                type="primary"
                size="small"
                @click="startPractice(userQuestionBank)"
              >
                {{ userQuestionBank.isCompleted ? '重新练习' : '开始练习' }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 购买题库对话框 -->
    <el-dialog
      v-model="showPurchaseDialog"
      title="购买题库"
      width="600px"
    >
      <el-table
        :data="availableQuestionBanks"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="题库名称" />
        <el-table-column prop="description" label="题库描述" />
        <el-table-column prop="price" label="价格">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPurchaseDialog = false">取消</el-button>
          <el-button
            type="primary"
            :disabled="selectedQuestionBanks.length === 0"
            @click="purchaseQuestionBanks"
          >
            确认购买
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'
import { Loading } from '@element-plus/icons-vue'

export default {
  name: 'QuestionBanks',
  components: {
    Loading
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const userQuestionBanks = ref([])
    const availableQuestionBanks = ref([])
    const selectedQuestionBanks = ref([])
    const showPurchaseDialog = ref(false)
    const loading = ref(false)
    
    const loadUserQuestionBanks = async () => {
      loading.value = true
      try {
        const response = await api.get(`/question-banks/user/${store.getters.currentUser.id}`)
        userQuestionBanks.value = response.data.data
      } catch (error) {
        ElMessage.error('加载题库失败：' + (error.response?.data?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    const loadAvailableQuestionBanks = async () => {
      try {
        const response = await api.get('/question-banks/online', {
          params: { userRole: store.getters.currentUser.role }
        })
        availableQuestionBanks.value = response.data.data
      } catch (error) {
        ElMessage.error('加载可用题库失败')
      }
    }
    
    const handleSelectionChange = (selection) => {
      selectedQuestionBanks.value = selection
    }
    
    const purchaseQuestionBanks = async () => {
      try {
        for (const questionBank of selectedQuestionBanks.value) {
          await api.post(`/question-banks/${questionBank.id}/purchase`, null, {
            params: { userId: store.getters.currentUser.id }
          })
        }
        ElMessage.success('购买成功')
        showPurchaseDialog.value = false
        loadUserQuestionBanks()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '购买失败')
      }
    }
    
    const startPractice = (userQuestionBank) => {
      router.push(`/dashboard/question-bank/${userQuestionBank.questionBankId}`)
    }
    
    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString('zh-CN')
    }
    
    onMounted(() => {
      loadUserQuestionBanks()
      loadAvailableQuestionBanks()
    })
    
    return {
      userQuestionBanks,
      availableQuestionBanks,
      selectedQuestionBanks,
      showPurchaseDialog,
      handleSelectionChange,
      purchaseQuestionBanks,
      startPractice,
      formatDate,
      loading
    }
  }
}
</script>

<style scoped>
.question-banks-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  color: #333;
}

.question-bank-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.question-bank-card:hover {
  transform: translateY(-5px);
}

.question-bank-info {
  padding: 15px;
}

.question-bank-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.question-bank-info p {
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.question-bank-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.purchase-time {
  font-size: 12px;
  color: #999;
}

.score-info {
  margin-bottom: 15px;
  font-weight: bold;
  color: #409EFF;
}

.question-bank-actions {
  display: flex;
  gap: 10px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 200px;
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
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 200px;
}
</style> 