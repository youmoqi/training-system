<template>
  <div class="page-container">
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
        <div class="section-header">
          <h3>题目列表</h3>
          <div class="drag-tip">
            <el-icon>
              <Rank/>
            </el-icon>
            <span>点击上移/下移按钮调整题目顺序</span>
          </div>
        </div>
        <el-table
            :data="examPaper?.questions || []"
            style="width: 100%"
            v-loading="loading"
            row-key="id"
        >
          <el-table-column prop="questionOrder" label="序号" width="80" align="center">
            <template #default="scope">
              <el-tag type="info" size="small">{{ scope.row.questionOrder }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="questionContent" label="题目内容" min-width="300" show-overflow-tooltip/>
          <el-table-column prop="questionType" label="类型" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.questionType)" size="small">
                {{ getQuestionTypeName(scope.row.questionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="分值" width="100" align="center">
            <template #default="scope">
              <el-tag type="success" size="small">{{ scope.row.score }}分</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="340" fixed="right" align="center">
            <template #default="scope">
              <el-button size="small" type="primary" @click="viewQuestion(scope.row)" :icon="View">
                查看
              </el-button>
              <el-button size="small" type="warning" @click="editQuestionScore(scope.row)" :icon="Edit">
                修改分值
              </el-button>
              <el-button size="small" type="info" @click="moveQuestionUp(scope.row)" :icon="ArrowUp"
                         :disabled="scope.$index === 0">
                上移
              </el-button>
              <el-button size="small" type="info" @click="moveQuestionDown(scope.row)" :icon="ArrowDown"
                         :disabled="scope.$index === (examPaper?.questions?.length - 1)">
                下移
              </el-button>
              <el-button size="small" type="danger" @click="removeQuestion(scope.row)" :icon="Delete">
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
        <!-- 分值设置提示 -->
        <div v-if="examPaper" class="score-settings-info">
          <el-alert
              title="试卷分值设置"
              type="info"
              :closable="false"
              show-icon
          >
            <template #default>
              <div class="score-settings">
                <span>单选题：{{ examPaper.singleChoiceScore || 2 }}分</span>
                <span>多选题：{{ examPaper.multipleChoiceScore || 4 }}分</span>
                <span>判断题：{{ examPaper.trueFalseScore || 2 }}分</span>
                <span>填空题：{{ examPaper.fillBlankScore || 3 }}分</span>
                <span>主观题：{{ examPaper.shortAnswerScore || 5 }}分</span>
              </div>
            </template>
          </el-alert>
        </div>

        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索题目..."
              style="width: 300px"
              clearable
              @input="handleSearch"
              @clear="handleSearch"
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
            <el-option label="主观题" value="SHORT_ANSWER"/>
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
          <el-button type="primary" @click="handleSearch" style="margin-left: 10px">
            <el-icon>
              <Search/>
            </el-icon>
            搜索
          </el-button>
          <el-button @click="clearFilters" style="margin-left: 10px">
            清除筛选
          </el-button>
        </div>

        <!-- 搜索结果统计 -->
        <div v-if="questions.length > 0 || searchKeyword || questionTypeFilter || questionBankFilter"
             class="search-stats">
          <el-text type="info">
            共找到 {{ totalQuestions }} 道题目
            <span v-if="searchKeyword">，关键词："{{ searchKeyword }}"</span>
            <span v-if="questionTypeFilter">，类型：{{ getQuestionTypeName(questionTypeFilter) }}</span>
            <span v-if="questionBankFilter">，题库：{{ getQuestionBankName(questionBankFilter) }}</span>
          </el-text>
        </div>

        <!-- 题目列表 -->
        <el-table
            :data="filteredQuestions"
            style="width: 100%; margin-top: 20px"
            v-loading="questionsLoading"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="content" label="题目内容" show-overflow-tooltip/>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.type)">
                {{ getQuestionTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="questionBankTitle" label="所属题库" width="200"/>
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button size="small" @click="preview(scope.row)">预览</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态显示 -->
        <div v-if="!questionsLoading && questions.length === 0" class="empty-state">
          <el-empty
              :description="getEmptyStateDescription()"
              :image-size="120"
          >
            <el-button type="primary" @click="clearFilters" v-if="hasActiveFilters()">
              清除筛选条件
            </el-button>
          </el-empty>
        </div>

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
    <el-dialog v-model="previewDialogVisible" title="题目预览" width="800px" class="question-preview-dialog">
      <div v-if="previewQuestion" class="question-preview">
        <div class="question-header">
          <el-tag :type="getQuestionTypeTag(previewQuestion.questionType)" size="large">
            {{ getQuestionTypeName(previewQuestion.questionType) }}
          </el-tag>
          <el-tag type="success" size="large">{{ previewQuestion.score }}分</el-tag>
        </div>

        <div class="question-content-section">
          <h4 class="section-title">
            <el-icon>
              <Document/>
            </el-icon>
            题目内容
          </h4>
          <div class="content-box">
            {{ previewQuestion.questionContent }}
          </div>
        </div>

        <div v-if="previewQuestion.options && previewQuestion.options.length > 0" class="question-options-section">
          <h4 class="section-title">
            <el-icon>
              <List/>
            </el-icon>
            选项
          </h4>
          <div class="options-list">
            <div v-for="(option, index) in previewQuestion.options" :key="index" class="option-item">
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-content">{{ option }}</span>
            </div>
          </div>
        </div>

        <div class="question-answer-section">
          <h4 class="section-title">
            <el-icon>
              <Check/>
            </el-icon>
            正确答案
          </h4>
          <div class="answer-box">
            {{
              previewQuestion.answers && previewQuestion.answers.length > 0 ? previewQuestion.answers.join(', ') : '无答案'
            }}
          </div>
        </div>

        <div v-if="previewQuestion.explanation" class="question-explanation-section">
          <h4 class="section-title">
            <el-icon>
              <InfoFilled/>
            </el-icon>
            解析
          </h4>
          <div class="explanation-box">
            {{ previewQuestion.explanation }}
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 修改分值对话框 -->
    <el-dialog v-model="scoreDialogVisible" title="修改分值" width="500px" class="score-edit-dialog">
      <div class="score-edit-content">
        <div class="question-info">
          <div class="question-type">
            <el-tag :type="getQuestionTypeTag(scoreForm.questionType)" size="small">
              {{ getQuestionTypeName(scoreForm.questionType) }}
            </el-tag>
          </div>
          <div class="question-content">
            <h4>题目内容</h4>
            <div class="content-text">{{ scoreForm.questionContent }}</div>
          </div>
        </div>

        <div class="score-edit-section">
          <h4>分值设置</h4>
          <div class="score-input-group">
            <el-input-number
                v-model="scoreForm.score"
                :min="1"
                :max="100"
                size="large"
                style="width: 200px"
            />
            <span class="score-unit">分</span>
          </div>
          <div class="score-tips">
            <el-text type="info" size="small">
              <el-icon>
                <InfoFilled/>
              </el-icon>
              建议分值范围：1-100分
            </el-text>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="scoreDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateQuestionScore" :icon="Check">
            确定修改
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, computed, onMounted, watch} from 'vue'
import {useRoute} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  ArrowLeft,
  Plus,
  Search,
  View,
  Edit,
  Delete,
  InfoFilled,
  Document,
  List,
  Check,
  Rank,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ExamPaperQuestions',
  components: {
    ArrowLeft,
    Plus,
    Search,
    View,
    Edit,
    Delete,
    InfoFilled,
    Document,
    List,
    Check,
    Rank,
    ArrowUp,
    ArrowDown
  },
  setup() {
    const route = useRoute()

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

    // 防抖搜索
    let searchTimeout = null
    const debouncedSearch = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      searchTimeout = setTimeout(() => {
        currentPage.value = 1
        loadQuestions()
      }, 300)
    }

    const loadExamPaper = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exam-papers/${route.params.id}`)
        if (response.data.success) {
          examPaper.value = response.data.data
        } else {
          ElMessage.error(response.data.message || '加载试卷失败')
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

        // 过滤空值参数
        const filteredParams = Object.fromEntries(
            Object.entries(params).filter(([key, value]) =>
                value !== null && value !== undefined && value !== ''
            )
        )

        const response = await api.get('/questions', {params: filteredParams})

        if (response.data.success) {
          questions.value = response.data.data.content
          totalQuestions.value = response.data.data.totalElements
        } else {
          ElMessage.error(response.data.message || '加载题目失败')
        }
      } catch (error) {
        console.error('加载题目失败:', error)
        ElMessage.error('加载题目失败: ' + (error.response?.data?.message || error.message))
        questions.value = []
        totalQuestions.value = 0
      } finally {
        questionsLoading.value = false
      }
    }

    const filteredQuestions = computed(() => {
      return questions.value
    })

    const showAddQuestionDialog = () => {
      addQuestionDialogVisible.value = true
      // 重置搜索条件
      searchKeyword.value = ''
      questionTypeFilter.value = ''
      questionBankFilter.value = ''
      currentPage.value = 1
      loadQuestions()
      loadQuestionBanks()
    }

    const handleSearch = () => {
      debouncedSearch()
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

    // 监听筛选器变化
    watch([questionTypeFilter, questionBankFilter], () => {
      currentPage.value = 1
      loadQuestions()
    })

    // 根据题目类型获取试卷设置的分值
    const getScoreByQuestionType = (questionType) => {
      if (!examPaper.value) return 5 // 默认分值

      const typeScoreMap = {
        'SINGLE_CHOICE': examPaper.value.singleChoiceScore || 2,
        'MULTIPLE_CHOICE': examPaper.value.multipleChoiceScore || 4,
        'TRUE_FALSE': examPaper.value.trueFalseScore || 2,
        'FILL_BLANK': examPaper.value.fillBlankScore || 3,
        'SHORT_ANSWER': examPaper.value.shortAnswerScore || 5
      }

      return typeScoreMap[questionType] || 5
    }

    const addSelectedQuestions = async () => {
      try {
        if (selectedQuestions.value.length === 0) {
          ElMessage.warning('请选择要添加的题目')
          return
        }

        const promises = selectedQuestions.value.map((question, index) => {
          const order = (examPaper.value.questions?.length || 0) + index + 1
          const score = getScoreByQuestionType(question.type)
          return api.post(`/exam-papers/${route.params.id}/questions/${question.id}`, null, {
            params: {
              score: score,
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

    const preview = (question) => {
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
      scoreForm.questionType = question.questionType
      scoreForm.score = question.score
      scoreDialogVisible.value = true
    }

    const updateQuestionScore = async () => {
      try {
        await api.put(`/exam-papers/${route.params.id}/questions/${scoreForm.questionId}/score`, null, {
          params: {score: scoreForm.score}
        })
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

    const getQuestionBankName = (bankId) => {
      const bank = questionBanks.value.find(bank => bank.id === bankId)
      return bank ? bank.title : '全部题库'
    }

    const clearFilters = () => {
      searchKeyword.value = ''
      questionTypeFilter.value = ''
      questionBankFilter.value = ''
      currentPage.value = 1
      loadQuestions()
    }

    const getEmptyStateDescription = () => {
      if (searchKeyword) {
        return `未找到题目`
      }
      if (questionTypeFilter) {
        return `未找到类型为"${getQuestionTypeName(questionTypeFilter)}"的题目`
      }
      if (questionBankFilter) {
        return `未找到题库为"${getQuestionBankName(questionBankFilter)}"的题目`
      }
      return '暂无题目'
    }

    const hasActiveFilters = () => {
      return searchKeyword || questionTypeFilter || questionBankFilter
    }

    // 题目顺序调整方法
    const moveQuestionUp = async (question) => {
      if (!examPaper.value?.questions) return

      const questions = [...examPaper.value.questions]
      const currentIndex = questions.findIndex(q => q.id === question.id)

      if (currentIndex <= 0) return

      // 交换位置
      [questions[currentIndex], questions[currentIndex - 1]] = [questions[currentIndex - 1], questions[currentIndex]]

      // 更新序号
      questions.forEach((q, index) => {
        q.questionOrder = index + 1
      })

      // 发送到后端更新
      try {
        const questionIds = questions.map(q => q.questionId)
        await api.put(`/exam-papers/${route.params.id}/questions/order`, questionIds)
        ElMessage.success('题目顺序更新成功')
        loadExamPaper() // 重新加载数据
      } catch (error) {
        console.error('更新题目顺序失败:', error)
        ElMessage.error('更新题目顺序失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const moveQuestionDown = async (question) => {
      if (!examPaper.value?.questions) return

      const questions = [...examPaper.value.questions]
      const currentIndex = questions.findIndex(q => q.id === question.id)

      if (currentIndex >= questions.length - 1) return

      // 交换位置
      [questions[currentIndex], questions[currentIndex + 1]] = [questions[currentIndex + 1], questions[currentIndex]]

      // 更新序号
      questions.forEach((q, index) => {
        q.questionOrder = index + 1
      })

      // 发送到后端更新
      try {
        const questionIds = questions.map(q => q.questionId)
        await api.put(`/exam-papers/${route.params.id}/questions/order`, questionIds)
        ElMessage.success('题目顺序更新成功')
        loadExamPaper() // 重新加载数据
      } catch (error) {
        console.error('更新题目顺序失败:', error)
        ElMessage.error('更新题目顺序失败: ' + (error.response?.data?.message || error.message))
      }
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
      preview,
      viewQuestion,
      editQuestionScore,
      updateQuestionScore,
      removeQuestion,
      getQuestionTypeName,
      getQuestionTypeTag,
      getQuestionBankName,
      getScoreByQuestionType,
      clearFilters,
      getEmptyStateDescription,
      hasActiveFilters,
      moveQuestionUp,
      moveQuestionDown
    }
  }
}
</script>

<style scoped>
.exam-paper-info {
  margin-bottom: 30px;
}

.questions-section {
  margin-top: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
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
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.search-section .el-input,
.search-section .el-select {
  margin-right: 10px;
}

.search-section .el-button {
  margin-left: 5px;
}

.search-stats {
  margin-top: 10px;
  margin-bottom: 20px;
  padding: 12px 16px;
  background-color: #f0f9ff;
  border-radius: 6px;
  border: 1px solid #bae6fd;
  color: #0369a1;
}

.search-stats .el-text {
  font-size: 14px;
}

.empty-state {
  margin-top: 20px;
  padding: 40px 0;
}

.score-settings-info {
  margin-bottom: 20px;
}

.score-settings {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 14px;
}

.score-settings span {
  background-color: #f0f9ff;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #bae6fd;
  color: #0369a1;
}

/* 题目列表样式优化 */
.question-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.question-text {
  line-height: 1.5;
  color: #303133;
}

.question-explanation {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.question-explanation .el-icon {
  font-size: 12px;
}

/* 题目预览对话框样式 */
.question-preview-dialog .el-dialog__body {
  padding: 20px;
}

.question-preview {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.question-header {
  display: flex;
  gap: 12px;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-title .el-icon {
  color: #409eff;
}

.content-box, .answer-box, .explanation-box {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  line-height: 1.6;
  color: #303133;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.option-label {
  font-weight: 600;
  color: #409eff;
  min-width: 20px;
}

.option-content {
  flex: 1;
  line-height: 1.5;
  color: #303133;
}

/* 修改分值对话框样式 */
.score-edit-dialog .el-dialog__body {
  padding: 24px;
}

.score-edit-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.question-info {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.question-type {
  margin-bottom: 12px;
}

.question-content h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.content-text {
  line-height: 1.6;
  color: #303133;
  padding: 12px;
  background-color: #ffffff;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.score-edit-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.score-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.score-unit {
  font-size: 16px;
  color: #606266;
  font-weight: 500;
}

.score-tips {
  margin-top: 8px;
}

.score-tips .el-text {
  display: flex;
  align-items: center;
  gap: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 题目顺序调整提示样式 */
.drag-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #909399;
  background-color: #f0f9ff;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #bae6fd;
}

.drag-tip .el-icon {
  font-size: 14px;
  color: #409eff;
}
</style>
