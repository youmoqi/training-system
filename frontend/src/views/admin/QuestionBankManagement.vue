<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">题库管理</h3>
      <div class="page-actions">
        <el-button type="primary" @click="createQuestionBank">
          <el-icon>
            <Plus/>
          </el-icon>
          创建题库
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索题库..."
              class="search-input"
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

        <!-- 题库列表 -->
        <div class="table-container">
          <el-table :data="questionBanks" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="题库名称" min-width="200"/>
            <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip/>
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">
                ¥{{ scope.row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="isOnline" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isOnline ? 'success' : 'info'">
                  {{ scope.row.isOnline ? '已上线' : '未上线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="editQuestionBank(scope.row)">编辑</el-button>
                <el-button size="small" type="primary" @click="manageQuestions(scope.row)">
                  管理题目
                </el-button>
                <el-button size="small" type="danger" @click="deleteQuestionBank(scope.row.id)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

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
      </div>
    </div>

    <!-- 创建/编辑题库对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        :title="editingQuestionBank ? '编辑题库' : '创建题库'"
        width="600px"
        class="dialog-container"
    >
      <div class="dialog-body">
        <el-form :model="formData" label-width="100px">
          <el-form-item label="题库名称" required>
            <el-input v-model="formData.title" placeholder="请输入题库名称"/>
          </el-form-item>
          <el-form-item label="题库描述" required>
            <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入题库描述"
            />
          </el-form-item>
          <el-form-item label="价格">
            <el-input-number v-model="formData.price" :min="0" :precision="2"/>
          </el-form-item>
          <el-form-item label="是否上线">
            <el-switch v-model="formData.isOnline"/>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Search} from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'QuestionBankManagement',
  components: {
    Plus,
    Search
  },
  setup() {
    const router = useRouter()

    const questionBanks = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)
    const searchKeyword = ref('')
    const showCreateDialog = ref(false)
    const editingQuestionBank = ref(null)

    const formData = reactive({
      title: '',
      description: '',
      price: 0,
      isOnline: true
    })

    const loadQuestionBanks = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value
        }
        const response = await api.get('/question-banks/page', {params})
        if (response.data.success) {
          questionBanks.value = response.data.data.content
          total.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载题库失败')
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadQuestionBanks()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadQuestionBanks()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadQuestionBanks()
    }

    const createQuestionBank = () => {
      editingQuestionBank.value = null
      resetForm()
      showCreateDialog.value = true
    }

    const editQuestionBank = (questionBank) => {
      editingQuestionBank.value = questionBank
      formData.title = questionBank.title
      formData.description = questionBank.description
      formData.price = questionBank.price
      formData.isOnline = questionBank.isOnline
      showCreateDialog.value = true
    }

    const manageQuestions = (questionBank) => {
      router.push(`/admin/question-banks/${questionBank.id}/questions`)
    }

    const deleteQuestionBank = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个题库吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/question-banks/${id}`)
        ElMessage.success('删除成功')
        loadQuestionBanks()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const submitForm = async () => {
      try {
        if (editingQuestionBank.value) {
          await api.put(`/question-banks/${editingQuestionBank.value.id}`, formData)
          ElMessage.success('更新成功')
        } else {
          await api.post('/question-banks', formData)
          ElMessage.success('创建成功')
        }
        showCreateDialog.value = false
        loadQuestionBanks()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const resetForm = () => {
      formData.title = ''
      formData.description = ''
      formData.price = 0
      formData.isOnline = true
    }

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadQuestionBanks()
    })

    return {
      questionBanks,
      showCreateDialog,
      editingQuestionBank,
      loading,
      currentPage,
      pageSize,
      total,
      searchKeyword,
      formData,
      createQuestionBank,
      editQuestionBank,
      manageQuestions,
      deleteQuestionBank,
      submitForm,
      formatDate,
      handleSearch,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
</style>
