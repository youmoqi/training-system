<template>
  <div class="exam-paper-questions">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="$router.go(-1)" icon="ArrowLeft">返回</el-button>
          <span>{{ examPaper?.title }} - 题目管理</span>
          <el-button type="primary" @click="showAddQuestionDialog">
            <el-icon>
              <Plus/>
            </el-icon>
            添加题目
          </el-button>
        </div>
      </template>

      <!-- 试卷基本信息 -->
      <div v-if="examPaper" class="exam-paper-info">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="总分">{{ examPaper.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="及格分">{{ examPaper.passScore }}</el-descriptions-item>
          <el-descriptions-item label="当前题目数">{{ examPaper.questions?.length || 0 }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 题目列表 -->
      <div class="questions-section">
        <h3>题目列表</h3>
        <el-table
            :data="examPaper?.questions || []"
            style="width: 100%"
            v-loading="loading"
            row-key="id"
        >
          <el-table-column prop="questionOrder" label="序号" width="80"/>
          <el-table-column prop="questionContent" label="题目内容" min-width="300" show-overflow-tooltip/>
          <el-table-column prop="questionType" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.questionType)">
                {{ getQuestionTypeName(scope.row.questionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="分值" width="80"/>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="viewQuestion(scope.row)">查看</el-button>
              <el-button size="small" type="warning" @click="editQuestionScore(scope.row)">
                修改分值
              </el-button>
              <el-button size="small" type="danger" @click="removeQuestion(scope.row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="!examPaper?.questions || examPaper.questions.length === 0" class="no-questions">
          <el-empty description="暂无题目">
            <el-button type="primary" @click="showAddQuestionDialog">添加题目</el-button>
          </el-empty>
        </div>
      </div>
    </el-card>

    <!-- 添加题目对话框 -->
    <el-dialog v-model="addQuestionDialogVisible" title="添加题目" width="1000px">
      <div class="add-question-container">
        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索题目..."
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
          <el-select v-model="questionTypeFilter" placeholder="题目类型" style="width: 150px; margin-left: 10px">
            <el-option label="全部" value=""/>
            <el-option label="单选题" value="SINGLE_CHOICE"/>
            <el-option label="多选题" value="MULTIPLE_CHOICE"/>
            <el-option label="判断题" value="TRUE_FALSE"/>
            <el-option label="填空题" value="FILL_BLANK"/>
            <el-option label="简答题" value="SHORT_ANSWER"/>
          </el-select>
          <el-select v-model="questionBankFilter" placeholder="题库" style="width: 200px; margin-left: 10px">
            <el-option label="全部题库" value=""/>
            <el-option
                v-for="bank in questionBanks"
                :key="bank.id"
                :label="bank.title"
                :value="bank.id"
            />
          </el-select>
        </div>

        <!-- 题目列表 -->
        <el-table
            :data="filteredQuestions"
            style="width: 100%; margin-top: 20px"
            v-loading="questionsLoading"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip/>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.type)">
                {{ getQuestionTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="questionBankTitle" label="所属题库" width="150"/>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" @click="previewQuestion(scope.row)">预览</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="totalQuestions"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addQuestionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addSelectedQuestions" :disabled="selectedQuestions.length === 0">
            添加选中题目 ({{ selectedQuestions.length }})
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 题目预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="题目预览" width="800px">
      <div v-if="previewQuestion">
        <h4>题目内容</h4>
        <p>{{ previewQuestion.content }}</p>

        <h4 v-if="previewQuestion.options && previewQuestion.options.length > 0">选项</h4>
        <ul v-if="previewQuestion.options && previewQuestion.options.length > 0">
          <li v-for="(option, index) in previewQuestion.options" :key="index">
            {{ String.fromCharCode(65 + index) }}. {{ option }}
          </li>
        </ul>

        <h4>正确答案</h4>
        <p v-if="previewQuestion.answers && previewQuestion.answers.length > 0">
          {{ previewQuestion.answers.join(', ') }}
        </p>

        <h4 v-if="previewQuestion.explanation">解析</h4>
        <p v-if="previewQuestion.explanation">{{ previewQuestion.explanation }}</p>
      </div>
    </el-dialog>

    <!-- 修改分值对话框 -->
    <el-dialog v-model="scoreDialogVisible" title="修改分值" width="400px">
      <el-form :model="scoreForm" label-width="100px">
        <el-form-item label="题目内容">
          <p>{{ scoreForm.questionContent }}</p>
        </el-form-item>
        <el-form-item label="当前分值">
          <el-input-number v-model="scoreForm.score" :min="1" :max="100"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scoreDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateQuestionScore">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowLeft, Plus, Search} from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ExamPaperQuestions',
  components: {
    ArrowLeft,
    Plus,
    Search
  },
  setup() {
    const route = useRoute()
    const router = useRouter()

    const examPaper = ref(null)
    const loading = ref(false)
    const addQuestionDialogVisible = ref(false)
    const previewDialogVisible = ref(false)
    const scoreDialogVisible = ref(false)
    const questionsLoading = ref(false)

    const searchKeyword = ref('')
    const questionTypeFilter = ref('')
    const questionBankFilter = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalQuestions = ref(0)

    const questions = ref([])
    const questionBanks = ref([])
    const selectedQuestions = ref([])
    const previewQuestion = ref(null)

    const scoreForm = reactive({
      questionId: null,
      questionContent: '',
      score: 0
    })

    const loadExamPaper = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exam-papers/${route.params.id}`)
        if (response.data.success) {
          examPaper.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载试卷失败')
      } finally {
        loading.value = false
      }
    }

    const loadQuestionBanks = async () => {
      try {
        const response = await api.get('/question-banks')
        if (response.data.success) {
          questionBanks.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载题库失败')
      }
    }

    const loadQuestions = async () => {
      questionsLoading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value,
          type: questionTypeFilter.value,
          questionBankId: questionBankFilter.value
        }

        const response = await api.get('/questions', {params})
        if (response.data.success) {
          questions.value = response.data.data.content
          totalQuestions.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载题目失败')
      } finally {
        questionsLoading.value = false
      }
    }

    const filteredQuestions = computed(() => {
      return questions.value
    })

    const showAddQuestionDialog = () => {
      addQuestionDialogVisible.value = true
      loadQuestions()
      loadQuestionBanks()
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadQuestions()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadQuestions()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadQuestions()
    }

    const handleSelectionChange = (selection) => {
      selectedQuestions.value = selection
    }

    const addSelectedQuestions = async () => {
      try {
        if (selectedQuestions.value.length === 0) {
          ElMessage.warning('请选择要添加的题目')
          return
        }

        const promises = selectedQuestions.value.map((question, index) => {
          const order = (examPaper.value.questions?.length || 0) + index + 1
          return api.post(`/exam-papers/${route.params.id}/questions/${question.id}`, null, {
            params: {
              score: 5, // 默认分值
              order: order
            }
          })
        })

        await Promise.all(promises)
        ElMessage.success('添加成功')
        addQuestionDialogVisible.value = false
        selectedQuestions.value = []
        loadExamPaper()
      } catch (error) {
        console.error('添加题目失败:', error)
        ElMessage.error('添加失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const previewQuestion = (question) => {
      previewQuestion.value = question
      previewDialogVisible.value = true
    }

    const viewQuestion = (question) => {
      previewQuestion.value = question
      previewDialogVisible.value = true
    }

    const editQuestionScore = (question) => {
      scoreForm.questionId = question.questionId
      scoreForm.questionContent = question.questionContent
      scoreForm.score = question.score
      scoreDialogVisible.value = true
    }

    const updateQuestionScore = async () => {
      try {
        await api.put(`/exam-papers/${route.params.id}/questions/${scoreForm.questionId}/score`, scoreForm.score)
        ElMessage.success('更新成功')
        scoreDialogVisible.value = false
        loadExamPaper()
      } catch (error) {
        console.error('更新分值失败:', error)
        ElMessage.error('更新失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const removeQuestion = async (question) => {
      try {
        await ElMessageBox.confirm('确定要移除这道题目吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/exam-papers/${route.params.id}/questions/${question.questionId}`)
        ElMessage.success('移除成功')
        loadExamPaper()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('移除题目失败:', error)
          ElMessage.error('移除失败: ' + (error.response?.data?.message || error.message))
        }
      }
    }

    const getQuestionTypeName = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'TRUE_FALSE': '判断题',
        'FILL_BLANK': '填空题',
        'SHORT_ANSWER': '简答题'
      }
      return typeMap[type] || type
    }

    const getQuestionTypeTag = (type) => {
      const tagMap = {
        'SINGLE_CHOICE': 'primary',
        'MULTIPLE_CHOICE': 'success',
        'TRUE_FALSE': 'warning',
        'FILL_BLANK': 'info',
        'SHORT_ANSWER': 'danger'
      }
      return tagMap[type] || 'info'
    }

    onMounted(() => {
      loadExamPaper()
    })

    return {
      examPaper,
      loading,
      addQuestionDialogVisible,
      previewDialogVisible,
      scoreDialogVisible,
      questionsLoading,
      searchKeyword,
      questionTypeFilter,
      questionBankFilter,
      currentPage,
      pageSize,
      totalQuestions,
      questions,
      questionBanks,
      selectedQuestions,
      previewQuestion,
      scoreForm,
      filteredQuestions,
      showAddQuestionDialog,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      addSelectedQuestions,
      previewQuestion,
      viewQuestion,
      editQuestionScore,
      updateQuestionScore,
      removeQuestion,
      getQuestionTypeName,
      getQuestionTypeTag
    }
  }
}
</script>

<style scoped>
.exam-paper-questions {
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

.questions-section {
  margin-top: 30px;
}

.questions-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.no-questions {
  text-align: center;
  padding: 40px 0;
}

.add-question-container {
  max-height: 600px;
  overflow-y: auto;
}

.search-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>
