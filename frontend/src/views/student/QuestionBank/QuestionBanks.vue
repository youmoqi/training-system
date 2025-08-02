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
      <el-icon class="is-loading">
        <Loading/>
      </el-icon>
      <p>正在加载题库...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="userQuestionBanks.length === 0" class="empty-container">
      <el-empty description="您还没有购买任何题库"/>
      <el-button type="primary" @click="showPurchaseDialog = true">去购买题库</el-button>
    </div>

    <!-- 题库列表 -->
    <el-row v-else :gutter="20" v-loading="myQuestionBanksLoading">
      <el-col
          v-for="userQuestionBank in userQuestionBanks"
          :key="userQuestionBank.id"
          :span="8"
      >
        <el-card class="question-bank-card" shadow="hover">
          <div class="question-bank-info">
            <div class="question-bank-header">
              <h4>{{ userQuestionBank.questionBankTitle }}</h4>
              <el-tag
                  :type="userQuestionBank.isCompleted ? 'success' : 'warning'"
                  size="small"
              >
                {{ userQuestionBank.isCompleted ? '已完成' : '未完成' }}
              </el-tag>
            </div>

            <p class="question-bank-description">{{ userQuestionBank.questionBankDescription || '暂无描述' }}</p>

            <div class="question-bank-details">
              <div class="detail-item">
                <span class="detail-label">购买时间：</span>
                <span class="detail-value">{{ formatDate(userQuestionBank.purchaseTime) }}</span>
              </div>

              <div class="detail-item">
                <span class="detail-label">最高分：</span>
                <span class="detail-value score">{{ userQuestionBank.score !== null ? userQuestionBank.score + '分' : '暂无记录' }}</span>
              </div>

              <div class="detail-item">
                <span class="detail-label">完成时间：</span>
                <span class="detail-value">{{ userQuestionBank.isCompleted && userQuestionBank.completeTime ? formatDate(userQuestionBank.completeTime) : '暂无记录' }}</span>
              </div>
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

    <!-- 我的题库分页 -->
    <div class="pagination-container" style="margin-top: 20px; text-align: center;">
      <el-pagination
        v-model:current-page="myQuestionBanksCurrentPage"
        v-model:page-size="myQuestionBanksPageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="myQuestionBanksTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleMyQuestionBanksSizeChange"
        @current-change="handleMyQuestionBanksCurrentChange"
      />
    </div>

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
          v-loading="dialogLoading"
      >
        <el-table-column type="selection" width="55"/>
        <el-table-column prop="title" label="题库名称"/>
        <el-table-column prop="description" label="题库描述"/>
        <el-table-column prop="price" label="价格">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

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
import {ref, onMounted} from 'vue'
import {useStore} from 'vuex'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import api from '../../../api'
import {Loading} from '@element-plus/icons-vue'

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

    // 分页相关
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const dialogLoading = ref(false)

    // 我的题库分页相关
    const myQuestionBanksCurrentPage = ref(1)
    const myQuestionBanksPageSize = ref(10)
    const myQuestionBanksTotal = ref(0)
    const myQuestionBanksLoading = ref(false)

    const loadUserQuestionBanks = async () => {
      myQuestionBanksLoading.value = true
      try {
        const response = await api.get('/question-banks/student/my-question-banks', {
          params: {
            userId: store.getters.currentUser.id,
            page: myQuestionBanksCurrentPage.value - 1,
            size: myQuestionBanksPageSize.value
          }
        })
        userQuestionBanks.value = response.data.data.content
        myQuestionBanksTotal.value = response.data.data.totalElements
      } catch (error) {
        ElMessage.error('加载题库失败：' + (error.response?.data?.message || '未知错误'))
      } finally {
        myQuestionBanksLoading.value = false
      }
    }

    const loadAvailableQuestionBanks = async () => {
      dialogLoading.value = true
      try {
        const response = await api.get('/question-banks/student/available-question-banks', {
          params: {
            userId: store.getters.currentUser.id,
            page: currentPage.value - 1, // 后端从0开始，前端从1开始
            size: pageSize.value,
            userRole: store.getters.currentUser.role
          }
        })
        availableQuestionBanks.value = response.data.data.content
        total.value = response.data.data.totalElements
      } catch (error) {
        ElMessage.error('加载可用题库失败')
      } finally {
        dialogLoading.value = false
      }
    }

    const handleSelectionChange = (selection) => {
      // 更新当前页面的选择状态
      selectedQuestionBanks.value = selection
    }

    // 分页处理方法
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      // 清空选择状态
      selectedQuestionBanks.value = []
      loadAvailableQuestionBanks()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      // 清空选择状态
      selectedQuestionBanks.value = []
      loadAvailableQuestionBanks()
    }

    // 我的题库分页处理方法
    const handleMyQuestionBanksSizeChange = (val) => {
      myQuestionBanksPageSize.value = val
      myQuestionBanksCurrentPage.value = 1
      loadUserQuestionBanks()
    }

    const handleMyQuestionBanksCurrentChange = (val) => {
      myQuestionBanksCurrentPage.value = val
      loadUserQuestionBanks()
    }

    const purchaseQuestionBanks = async () => {
      try {
        for (const questionBank of selectedQuestionBanks.value) {
          await api.post(`/question-banks/${questionBank.id}/purchase`, null)
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
      if (!dateString) return '--'
      return new Date(dateString).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
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
      loading,
      currentPage,
      pageSize,
      total,
      dialogLoading,
      handleSizeChange,
      handleCurrentChange,
      myQuestionBanksCurrentPage,
      myQuestionBanksPageSize,
      myQuestionBanksTotal,
      myQuestionBanksLoading,
      handleMyQuestionBanksSizeChange,
      handleMyQuestionBanksCurrentChange
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
  height: 320px;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
}

.question-bank-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.question-bank-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.question-bank-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  min-height: 35px;
  flex-shrink: 0;
}

.question-bank-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  flex: 1;
  margin-right: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.question-bank-description {
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.6;
  font-size: 14px;
  min-height: 55px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

.question-bank-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
  min-height: 70px;
  flex-shrink: 0;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  min-height: 18px;
}

.detail-label {
  color: #909399;
  margin-right: 8px;
  min-width: 70px;
  font-weight: 500;
}

.detail-value {
  color: #303133;
  font-weight: 400;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.detail-value.score {
  color: #409eff;
  font-weight: 600;
  font-size: 14px;
}

.question-bank-actions {
  display: flex;
  gap: 10px;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  min-height: 50px;
  align-items: center;
  flex-shrink: 0;
  justify-content: center;
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

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.pagination-container .el-pagination {
  justify-content: center;
}
</style>
