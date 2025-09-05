<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" icon="ArrowLeft">返回</el-button>
          <span>{{ exam?.title }} - 手动抽题</span>
        </div>
      </template>

      <!-- 试卷基本信息 -->
      <div v-if="exam" class="exam-paper-info">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="总分">{{ exam.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="及格分">{{ exam.passScore }}</el-descriptions-item>
          <el-descriptions-item label="当前题目数">{{ exam.questions?.length || 0 }}</el-descriptions-item>
          <el-descriptions-item label="考试分类">{{ getCategoryName(exam.examCategory) }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ exam.duration }}分钟</el-descriptions-item>
          <el-descriptions-item label="允许重复考试">{{ exam.allowRetake ? '是' : '否' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-form :model="filterForm" inline>
          <el-form-item label="题目类型">
            <el-select v-model="filterForm.questionType" placeholder="请选择题目类型" style="width: 200px">
              <el-option label="全部" value=""/>
              <el-option label="单选题" value="SINGLE_CHOICE"/>
              <el-option label="多选题" value="MULTIPLE_CHOICE"/>
              <el-option label="判断题" value="TRUE_FALSE"/>
              <el-option label="填空题" value="FILL_BLANK"/>
              <el-option label="简答题" value="SHORT_ANSWER"/>
            </el-select>
          </el-form-item>
          
          <el-form-item label="题库选择">
            <el-select v-model="filterForm.questionBankId" placeholder="请选择题库" style="width: 200px">
              <el-option label="全部" value=""/>
              <el-option v-for="bank in questionBanks" :key="bank.id" :label="bank.title" :value="bank.id"/>
            </el-select>
          </el-form-item>
          
          <el-form-item label="关键词搜索">
            <el-input v-model="filterForm.keyword" placeholder="搜索题目内容" clearable style="width: 200px"/>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="searchQuestions">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 题目列表 -->
      <div class="questions-section">
        <div class="section-header">
          <h3>可选题目列表</h3>
          <div class="selection-info">
            <span>已选择 {{ selectedQuestions.length }} 道题目</span>
            <el-button type="success" @click="addSelectedToExam" :disabled="selectedQuestions.length === 0 || adding">
              添加选中题目到试卷
            </el-button>
          </div>
        </div>

        <el-table 
          v-loading="loading" 
          :data="questions" 
          style="width: 100%" 
          @selection-change="handleSelectionChange"
          stripe
        >
          <el-table-column type="selection" width="55" :selectable="isQuestionSelectable"/>
          <el-table-column prop="id" label="题目ID" width="80"/>
          <el-table-column prop="questionContent" label="题目内容" min-width="150">
            <template #default="scope">
              <div class="question-content-cell" @click="openQuestionPreview(scope.row)" :class="{ 'already-added': isQuestionAlreadyAdded(scope.row.id) }">
                {{ scope.row.questionContent || scope.row.content || '暂无题目内容' }}
                <el-tag v-if="isQuestionAlreadyAdded(scope.row.id)" size="small" type="success" class="added-tag">
                  已添加
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="questionType" label="题目类型" width="100">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.questionType || scope.row.type)">
                {{ getQuestionTypeName(scope.row.questionType || scope.row.type) || '未知类型' }}
              </el-tag>
            </template>
          </el-table-column>
<!--          <el-table-column prop="difficulty" label="难度" width="80">-->
<!--            <template #default="scope">-->
<!--              <el-tag :type="getDifficultyTag(scope.row.difficulty)">-->
<!--                {{ getDifficultyName(scope.row.difficulty) }}-->
<!--              </el-tag>-->
<!--            </template>-->
<!--          </el-table-column>-->
          <el-table-column label="题库" width="200">
            <template #default="scope">
              {{ getQuestionBankName(scope.row.questionBankId) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="openQuestionPreview(scope.row)">预览</el-button>
              <el-button 
                v-if="!isQuestionAlreadyAdded(scope.row.id)" 
                size="small" 
                type="primary" 
                @click="addSingleQuestion(scope.row)"
              >
                添加
              </el-button>
              <el-button 
                v-if="isQuestionAlreadyAdded(scope.row.id)" 
                size="small" 
                type="danger" 
                @click="removeQuestion(scope.row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>

        <div v-if="questions.length === 0 && !loading" class="no-questions">
          <el-empty description="暂无符合条件的题目"/>
        </div>
      </div>
    </el-card>

    <!-- 题目预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="题目预览" width="800px" class="question-preview-dialog">
      <div v-if="previewQuestion" class="question-preview">
        <div class="question-header">
          <el-tag :type="getQuestionTypeTag(previewQuestion.questionType)" size="large">
            {{ getQuestionTypeName(previewQuestion.type) }}
          </el-tag>
        </div>

        <div class="question-content-section">
          <h4 class="section-title">
            <el-icon><Document/></el-icon>
            题目内容
          </h4>
          <div class="content-box">{{ previewQuestion.content }}</div>
        </div>

        <div v-if="previewQuestion.options && previewQuestion.options.length > 0" class="question-options-section">
          <h4 class="section-title">
            <el-icon><List/></el-icon>
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
            <el-icon><Check/></el-icon>
            正确答案
          </h4>
          <div class="answer-box">
            {{ previewQuestion.answers && previewQuestion.answers.length > 0 ? previewQuestion.answers.join(', ') : '无答案' }}
          </div>
        </div>

        <div v-if="previewQuestion.explanation" class="question-explanation-section">
          <h4 class="section-title">
            <el-icon><InfoFilled/></el-icon>
            解析
          </h4>
          <div class="explanation-box">{{ previewQuestion.explanation }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowLeft, Document, List, Check, InfoFilled} from '@element-plus/icons-vue'
import api from '@/api'
import {getCategoryName} from '@/utils/examCategory'

export default {
  name: 'ManualQuestionSelection',
  components: {
    ArrowLeft,
    Document,
    List,
    Check,
    InfoFilled
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const exam = ref(null)
    const loading = ref(false)
    const adding = ref(false)
    const previewDialogVisible = ref(false)
    const previewQuestion = ref(null)
    const questions = ref([])
    const questionBanks = ref([])
    const selectedQuestions = ref([])
    
    // 筛选条件
    const filterForm = reactive({
      questionType: '',
      questionBankId: '',
      difficulty: '',
      keyword: ''
    })
    
    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 20,
      total: 0
    })
    
    // 返回上一页
    const goBack = () => {
      router.push('/admin/exams')
    }
    
    // 加载试卷信息
    const loadExam = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exams/${route.params.id}`)
        if (response.data.success) {
          exam.value = response.data.data
        } else {
          ElMessage.error(response.data.message || '加载试卷失败')
        }
      } catch (error) {
        ElMessage.error('加载试卷失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loading.value = false
      }
    }
    
    // 加载题库列表
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
    
    // 搜索题目
    const searchQuestions = () => {
      pagination.currentPage = 1
      loadQuestions()
    }
    
    // 重置筛选条件
    const resetFilters = () => {
      filterForm.questionType = ''
      filterForm.questionBankId = ''
      filterForm.difficulty = ''
      filterForm.keyword = ''
      pagination.currentPage = 1
      loadQuestions()
    }
    
    // 加载题目列表
    const loadQuestions = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.currentPage - 1,
          size: pagination.pageSize,
          keyword: filterForm.keyword,
          type: filterForm.questionType,
          questionBankId: filterForm.questionBankId,
          difficulty: filterForm.difficulty
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
          pagination.total = response.data.data.totalElements
        } else {
          ElMessage.error(response.data.message || '加载题目失败')
        }
      } catch (error) {
        console.error('加载题目失败:', error)
        ElMessage.error('加载题目失败: ' + (error.response?.data?.message || error.message))
        questions.value = []
        pagination.total = 0
      } finally {
        loading.value = false
      }
    }
    
    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedQuestions.value = selection
    }
    
    // 处理分页大小变化
    const handleSizeChange = (size) => {
      pagination.pageSize = size
      pagination.currentPage = 1
      loadQuestions()
    }
    
    // 处理页码变化
    const handleCurrentChange = (page) => {
      pagination.currentPage = page
      loadQuestions()
    }
    
    // 预览题目
    const openQuestionPreview = (question) => {
      previewQuestion.value = question
      previewDialogVisible.value = true
    }
    
    // 检查题目是否已添加到试卷
    const isQuestionAlreadyAdded = (questionId) => {
      if (!exam.value || !exam.value.questions) {
        return false
      }
      return exam.value.questions.some(item => item.questionId === questionId)
    }
    
    // 控制是否可以选择题目
    const isQuestionSelectable = (question) => {
      return !isQuestionAlreadyAdded(question.id)
    }
    
    // 添加单个题目到试卷
    const addSingleQuestion = async (question) => {
      if (isQuestionAlreadyAdded(question.id)) {
        ElMessage.warning('该题目已添加到试卷')
        return
      }
      
      try {
        await ElMessageBox.confirm(
          `确定要添加该题目到试卷吗？`, 
          '确认添加', 
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        adding.value = true
        
        // 获取试卷当前题目数量，用于设置新题目的顺序
        const currentQuestionCount = exam.value.questions?.length || 0
        const order = currentQuestionCount + 1
        const score = getScoreByQuestionType(question.questionType)
        
        const response = await api.post(`/exams/${route.params.id}/questions/${question.id}`, null, {
          params: {
            score: score,
            order: order
          }
        })
        
        if (response.data.success) {
          ElMessage.success('题目添加成功')
          loadExam() // 重新加载试卷信息
        } else {
          ElMessage.error(response.data.message || '添加题目失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('添加题目失败:', error)
          ElMessage.error('添加题目失败: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        adding.value = false
      }
    }
    
    // 添加选中题目到试卷
    const addSelectedToExam = async () => {
      if (selectedQuestions.value.length === 0) {
        ElMessage.warning('请先选择题目')
        return
      }
      
      try {
        await ElMessageBox.confirm(
          `确定要添加选中的 ${selectedQuestions.value.length} 道题目到试卷吗？`, 
          '确认添加', 
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        adding.value = true
        const currentQuestionCount = exam.value.questions?.length || 0
        
        // 循环添加题目
        const addPromises = selectedQuestions.value.map((question, index) => {
          const order = currentQuestionCount + index + 1
          const score = getScoreByQuestionType(question.questionType)
          
          return api.post(`/exams/${route.params.id}/questions/${question.id}`, null, {
            params: {
              score: score,
              order: order
            }
          })
        })
        
        // 等待所有请求完成
        const results = await Promise.all(addPromises)
        
        // 检查是否所有请求都成功
        const allSuccess = results.every(result => result.data.success)
        
        if (allSuccess) {
          ElMessage.success(`成功添加 ${selectedQuestions.value.length} 道题目到试卷`)
          loadExam() // 重新加载试卷信息
          selectedQuestions.value = [] // 清空选择
        } else {
          ElMessage.error('部分题目添加失败，请重试')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量添加题目失败:', error)
          ElMessage.error('批量添加题目失败: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        adding.value = false
      }
    }
    
    // 根据题目类型获取默认分值
    const getScoreByQuestionType = (questionType) => {
      if (!exam.value) return 5 // 默认分值
      
      const typeScoreMap = {
        'SINGLE_CHOICE': exam.value.singleChoiceScore || 2,
        'MULTIPLE_CHOICE': exam.value.multipleChoiceScore || 4,
        'TRUE_FALSE': exam.value.trueFalseScore || 2,
        'FILL_BLANK': exam.value.fillBlankScore || 3,
        'SHORT_ANSWER': exam.value.shortAnswerScore || 5
      }
      
      return typeScoreMap[questionType] || 5
    }
    
    // 获取题目类型名称
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
    
    // 获取题目类型标签样式
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
    
    // 获取难度等级名称
    const getDifficultyName = (difficulty) => {
      const difficultyMap = {
        'EASY': '简单',
        'MEDIUM': '中等',
        'HARD': '困难'
      }
      return difficultyMap[difficulty] || difficulty
    }
    
    // 获取难度等级标签样式
    const getDifficultyTag = (difficulty) => {
      const tagMap = {
        'EASY': 'success',
        'MEDIUM': 'warning',
        'HARD': 'danger'
      }
      return tagMap[difficulty] || 'info'
    }
    
    // 获取题库名称
    const getQuestionBankName = (bankId) => {
      const bank = questionBanks.value.find(bank => bank.id === bankId)
      return bank ? bank.title : '未分类'
    }
    
    // 删除题目
    const removeQuestion = async (questionId) => {
      try {
        await ElMessageBox.confirm(
          '确定要从试卷中删除该题目吗？', 
          '确认删除', 
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'danger'
          }
        )
        
        loading.value = true
        
        const response = await api.delete(`/exams/${route.params.id}/questions/${questionId}`)
        
        if (response.data.success) {
          ElMessage.success('题目删除成功')
          loadExam() // 重新加载试卷信息
        } else {
          ElMessage.error(response.data.message || '删除题目失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除题目失败:', error)
          ElMessage.error('删除题目失败: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        loading.value = false
      }
    }
    
    // 页面加载时执行
    onMounted(() => {
      loadExam()
      loadQuestionBanks()
      loadQuestions()
    })
     return {
       exam,
       loading,
       adding,
       previewDialogVisible,
       previewQuestion,
       questions,
       questionBanks,
       selectedQuestions,
       filterForm,
       pagination,
       goBack,
       searchQuestions,
       resetFilters,
       handleSelectionChange,
       handleSizeChange,
       handleCurrentChange,
       openQuestionPreview,
       addSelectedToExam,
       isQuestionAlreadyAdded,
       isQuestionSelectable,
       addSingleQuestion,
       getQuestionTypeName,
       getQuestionTypeTag,
       getDifficultyName,
       getDifficultyTag,
       getQuestionBankName,
       getCategoryName,
       removeQuestion
     }
  }
}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
}

.card-header span {
  margin-left: 10px;
  font-size: 16px;
  font-weight: bold;
}

.exam-paper-info {
  margin-bottom: 20px;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.questions-section {
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  color: #303133;
}

.selection-info {
  display: flex;
  align-items: center;
}

.selection-info span {
  margin-right: 10px;
  color: #606266;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.no-questions {
  text-align: center;
  padding: 40px 0;
}

.question-content-cell {
  cursor: pointer;
  color: #409eff;
  line-height: 1.5;
}

     .question-content-cell:hover {
       text-decoration: underline;
     }
     
     /* 已添加题目的样式 */
     .question-content-cell.already-added {
       color: #67c23a;
       font-style: italic;
     }
     
     .question-content-cell.already-added:hover {
       color: #67c23a;
     }
     
     .added-tag {
       margin-left: 8px;
     }

.question-preview {
  padding: 10px 0;
}

.question-header {
  margin-bottom: 20px;
}

.question-header .el-tag {
  margin-right: 10px;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: #303133;
}

.section-title .el-icon {
  margin-right: 5px;
}

.content-box,
.answer-box,
.explanation-box {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.options-list {
  padding: 10px 0;
}

.option-item {
  display: flex;
  margin-bottom: 5px;
  line-height: 1.6;
}

.option-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 20px;
}
</style>