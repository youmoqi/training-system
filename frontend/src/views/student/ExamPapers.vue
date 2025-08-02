<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">我的试卷</h3>
      <div class="page-actions">
        <el-button type="primary" @click="showPurchaseDialog">
          <el-icon>
            <Plus/>
          </el-icon>
          购买试卷
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索试卷..."
              style="width: 300px"
              clearable
              @input="handleSearch"
          >
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 试卷列表 -->
        <el-table :data="examPapers" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="试卷名称" min-width="200"/>
          <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip/>
          <el-table-column prop="totalScore" label="总分" width="80"/>
          <el-table-column prop="passScore" label="及格分" width="80"/>
          <el-table-column prop="duration" label="时长(分钟)" width="100"/>
          <el-table-column prop="totalQuestions" label="题目数" width="80"/>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="viewExamPaper(scope.row)">查看</el-button>
              <el-button size="small" type="primary" @click="takeExam(scope.row)">
                考试
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && examPapers.length === 0" class="empty-state">
          <el-empty description="暂无试卷">
            <el-button type="primary" @click="showPurchaseDialog">
              购买试卷
            </el-button>
          </el-empty>
        </div>
      </div>

    </div>


    <!-- 购买试卷对话框 -->
    <el-dialog v-model="purchaseDialogVisible" title="购买试卷" width="1000px">
      <div class="purchase-container">
        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="purchaseSearchKeyword"
              placeholder="搜索试卷..."
              style="width: 300px"
              clearable
              @input="handlePurchaseSearch"
          >
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 可购买试卷列表 -->
        <el-table
            :data="availableExamPapers"
            style="width: 100%; margin-top: 20px"
            v-loading="purchaseLoading"
        >
          <el-table-column prop="title" label="试卷名称" min-width="200"/>
          <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip/>
          <el-table-column prop="totalScore" label="总分" width="80"/>
          <el-table-column prop="passScore" label="及格分" width="80"/>
          <el-table-column prop="duration" label="时长(分钟)" width="100"/>
          <el-table-column prop="totalQuestions" label="题目数" width="80"/>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button size="small" type="primary" @click="purchaseExamPaper(scope.row)">
                购买
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="purchaseCurrentPage"
              v-model:page-size="purchasePageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="purchaseTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handlePurchaseSizeChange"
              @current-change="handlePurchaseCurrentChange"
          />
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!purchaseLoading && availableExamPapers.length === 0" description="暂无可购买试卷"/>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="purchaseDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Search} from '@element-plus/icons-vue'
import api from '@/api'
import store from "@/store";

export default {
  name: 'ExamPapers',
  components: {
    Plus,
    Search
  },
  setup() {
    const router = useRouter()

    const loading = ref(false)
    const examPapers = ref([])
    const searchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)

    // 购买相关状态
    const purchaseDialogVisible = ref(false)
    const purchaseLoading = ref(false)
    const availableExamPapers = ref([])
    const purchaseSearchKeyword = ref('')
    const purchaseCurrentPage = ref(1)
    const purchasePageSize = ref(20)
    const purchaseTotal = ref(0)

    const loadExamPapers = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value,
          userId: store.getters.userId,
        }
        const response = await api.get('/exam-papers/available/page', {params})
        if (response.data.success) {
          examPapers.value = response.data.data.content
          total.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载试卷失败')
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadExamPapers()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadExamPapers()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadExamPapers()
    }

    const loadAvailableExamPapers = async () => {
      purchaseLoading.value = true
      try {
        const params = {
          page: purchaseCurrentPage.value - 1,
          size: purchasePageSize.value,
          keyword: purchaseSearchKeyword.value,
          userId: store.getters.userId
        }

        const response = await api.get('/exam-papers/purchasable/page', {params})
        if (response.data.success) {
          availableExamPapers.value = response.data.data.content
          purchaseTotal.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载可购买试卷失败')
      } finally {
        purchaseLoading.value = false
      }
    }

    const showPurchaseDialog = () => {
      purchaseDialogVisible.value = true
      loadAvailableExamPapers()
    }

    const handlePurchaseSearch = () => {
      purchaseCurrentPage.value = 1
      loadAvailableExamPapers()
    }

    const handlePurchaseSizeChange = (size) => {
      purchasePageSize.value = size
      purchaseCurrentPage.value = 1
      loadAvailableExamPapers()
    }

    const handlePurchaseCurrentChange = (page) => {
      purchaseCurrentPage.value = page
      loadAvailableExamPapers()
    }

    const purchaseExamPaper = async (paper) => {
      try {
        await ElMessageBox.confirm(`确定要购买试卷"${paper.title}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        })

        await api.post(`/exam-papers/${paper.id}/purchase`)
        ElMessage.success('购买成功')
        purchaseDialogVisible.value = false
        loadExamPapers() // 重新加载已购买的试卷
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('购买失败: ' + (error.response?.data?.message || error.message))
        }
      }
    }

    const viewExamPaper = (paper) => {
      router.push(`/dashboard/exam-papers/${paper.id}`)
    }

    const takeExam = (paper) => {
      router.push(`/dashboard/exam-papers/${paper.id}/exam`)
    }

    onMounted(() => {
      loadExamPapers()
    })

    return {
      loading,
      examPapers,
      searchKeyword,
      currentPage,
      pageSize,
      total,
      loadExamPapers,
      showPurchaseDialog,
      viewExamPaper,
      takeExam,
      purchaseDialogVisible,
      purchaseLoading,
      availableExamPapers,
      purchaseSearchKeyword,
      purchaseCurrentPage,
      purchasePageSize,
      purchaseTotal,
      loadAvailableExamPapers,
      handlePurchaseSearch,
      handlePurchaseSizeChange,
      handlePurchaseCurrentChange,
      purchaseExamPaper,
      handleSearch,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}

.purchase-container {
  max-height: 600px;
  overflow-y: auto;
}
</style>
