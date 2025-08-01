<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft">返回题库管理</el-button>
        <h3>题目管理 - {{ questionBankTitle }}</h3>
        <el-tag type="info">共 {{ questions.length }} 道题目</el-tag>
      </div>
      <div class="header-actions">
        <el-button @click="showImportDialog = true">导入题目</el-button>
        <el-button @click="exportQuestions">导出题目</el-button>
        <el-button type="warning" @click="batchDelete" :disabled="selectedQuestions.length === 0">
          批量删除 ({{ selectedQuestions.length }})
        </el-button>
        <el-button type="primary" @click="showCreateDialog = true">添加题目</el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <el-table
            :data="questions"
            style="width: 100%"
            v-loading="loading"
            @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="id" label="ID" width="80"/>
          <el-table-column prop="content" label="题目内容" show-overflow-tooltip/>
          <el-table-column prop="type" label="题目类型" width="120">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.type)">
                {{ getQuestionTypeText(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="options" label="选项数量" width="100">
            <template #default="scope">
              {{ scope.row.options ? scope.row.options.length : 0 }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="editQuestion(scope.row)">编辑</el-button>
              <el-button
                  size="small"
                  type="danger"
                  @click="deleteQuestion(scope.row.id)"
              >
                删除
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
        <el-empty v-if="!loading && questions.length === 0" description="暂无题目数据"/>
      </div>
    </div>

    <!-- 创建/编辑题目对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        :title="editingQuestion ? '编辑题目' : '添加题目'"
        width="800px"
    >
      <el-form
          ref="questionFormRef"
          :model="formData"
          :rules="questionRules"
          label-width="100px"
      >
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择题型" @change="handleTypeChange">
            <el-option label="单选题" value="SINGLE_CHOICE"/>
            <el-option label="多选题" value="MULTIPLE_CHOICE"/>
            <el-option label="主观题" value="SUBJECTIVE"/>
          </el-select>
        </el-form-item>

        <el-form-item label="题目内容" prop="content">
          <el-input
              v-model="formData.content"
              type="textarea"
              :rows="4"
              placeholder="请输入题目内容"
          />
        </el-form-item>

        <el-form-item
            v-if="isChoiceQuestion"
            label="选项"
            prop="options"
        >
          <div v-for="(option, index) in formData.options" :key="index" class="option-item">
            <el-input
                v-model="formData.options[index]"
                :placeholder="`选项 ${String.fromCharCode(65 + index)}`"
                style="width: 80%"
            />
            <el-button
                type="danger"
                size="small"
                @click="removeOption(index)"
                :disabled="formData.options.length <= 2"
            >
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addOption">添加选项</el-button>
        </el-form-item>

        <el-form-item label="正确答案" prop="answers">
          <el-checkbox-group
              v-if="formData.type === 'MULTIPLE_CHOICE'"
              v-model="formData.answers"
          >
            <el-checkbox
                v-for="(option, index) in formData.options"
                :key="index"
                :label="option"
            >
              {{ option }}
            </el-checkbox>
          </el-checkbox-group>
          <el-radio-group
              v-else-if="formData.type === 'SINGLE_CHOICE'"
              v-model="formData.answers"
          >
            <el-radio
                v-for="(option, index) in formData.options"
                :key="index"
                :label="option"
            >
              {{ option }}
            </el-radio>
          </el-radio-group>
          <el-input
              v-else
              v-model="formData.answers"
              type="textarea"
              :rows="3"
              placeholder="请输入正确答案"
          />
        </el-form-item>

        <el-form-item label="题目解析" prop="explanation">
          <el-input
              v-model="formData.explanation"
              type="textarea"
              :rows="3"
              placeholder="请输入题目解析"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="saveQuestion">
            {{ editingQuestion ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 导入题目对话框 -->
    <el-dialog v-model="showImportDialog" title="导入题目" width="600px">
      <el-form label-width="100px">
        <el-form-item label="导入文件">
          <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              accept=".json,.xlsx,.xls"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 JSON、Excel 格式文件，请确保文件格式正确
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item v-if="importData" label="预览数据">
          <div class="import-preview">
            <p>将导入 {{ importData.questions.length }} 道题目</p>
            <el-table :data="importData.questions.slice(0, 5)" size="small">
              <el-table-column prop="content" label="题目内容" show-overflow-tooltip/>
              <el-table-column prop="type" label="类型" width="100"/>
            </el-table>
            <p v-if="importData.questions.length > 5" class="preview-tip">
              仅显示前5道题目，共 {{ importData.questions.length }} 道
            </p>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showImportDialog = false">取消</el-button>
          <el-button
              type="primary"
              :disabled="!importData"
              @click="confirmImport"
          >
            确认导入
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import api from '../../api'

export default {
  name: 'QuestionManagement',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const questionBankId = ref(route.path.split('/')[3])
    const questionBankTitle = ref('')
    const questions = ref([])
    const showCreateDialog = ref(false)
    const showImportDialog = ref(false)
    const editingQuestion = ref(null)
    const questionFormRef = ref(null)
    const importData = ref(null)
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)
    const selectedQuestions = ref([])

    const formData = ref({
      type: 'SINGLE_CHOICE',
      content: '',
      options: ['', ''],
      answers: [],
      explanation: ''
    })

    const questionRules = {
      type: [
        {required: true, message: '请选择题型', trigger: 'change'}
      ],
      content: [
        {required: true, message: '请输入题目内容', trigger: 'blur'}
      ],
      options: [
        {required: true, message: '请至少添加两个选项', trigger: 'blur'}
      ]
    }

    const loadQuestionBankInfo = async () => {
      try {
        const response = await api.get(`/question-banks/${questionBankId.value}`)
        if (response.data.success) {
          questionBankTitle.value = response.data.data.title
        }
      } catch (error) {
        ElMessage.error('加载题库信息失败')
      }
    }

    const loadQuestions = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          questionBankId: questionBankId.value
        }
        const response = await api.get('/questions', {params})
        if (response.data.success) {
          questions.value = response.data.data.content
          total.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载题目失败')
      } finally {
        loading.value = false
      }
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

    const isChoiceQuestion = computed(() => {
      return ['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(formData.value.type)
    })

    // 监听题目类型变化，重置答案
    const handleTypeChange = () => {
      if (formData.value.type === 'SUBJECTIVE') {
        formData.value.answers = ''
        formData.value.options = ['', '']
      } else {
        formData.value.answers = []
      }
    }

    const getQuestionTypeText = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'SUBJECTIVE': '主观题'
      }
      return typeMap[type] || type
    }

    const getQuestionTypeTag = (type) => {
      const tagMap = {
        'SINGLE_CHOICE': 'primary',
        'MULTIPLE_CHOICE': 'success',
        'SUBJECTIVE': 'warning'
      }
      return tagMap[type] || 'info'
    }

    const editQuestion = (question) => {
      editingQuestion.value = question
      Object.assign(formData.value, {
        type: question.type,
        content: question.content,
        options: question.options || ['', ''],
        answers: question.type === 'SUBJECTIVE' ? (question.answers && question.answers.length > 0 ? question.answers[0] : '') : (question.answers || []),
        explanation: question.explanation || ''
      })
      showCreateDialog.value = true
    }

    const deleteQuestion = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/questions/${id}`)
        ElMessage.success('删除成功')
        loadQuestions()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const addOption = () => {
      formData.value.options.push('')
    }

    const removeOption = (index) => {
      formData.value.options.splice(index, 1)
      // 更新答案
      if (Array.isArray(formData.value.answers)) {
        formData.value.answers = formData.value.answers.filter(answer =>
            formData.value.options.includes(answer)
        )
      }
    }

    const saveQuestion = async () => {
      try {
        await questionFormRef.value.validate()

        const questionData = {
          questionBankId: questionBankId.value,
          type: formData.value.type,
          content: formData.value.content,
          options: isChoiceQuestion.value ? formData.value.options : null,
          answers: formData.value.type === 'SUBJECTIVE' ? [formData.value.answers] : (Array.isArray(formData.value.answers) ? formData.value.answers : [formData.value.answers]),
          explanation: formData.value.explanation
        }

        if (editingQuestion.value) {
          await api.put(`/questions/${editingQuestion.value.id}`, questionData)
          ElMessage.success('更新成功')
        } else {
          await api.post('/questions', questionData)
          ElMessage.success('创建成功')
        }

        showCreateDialog.value = false
        resetForm()
        loadQuestions()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }

    const resetForm = () => {
      editingQuestion.value = null
      Object.assign(formData.value, {
        type: 'SINGLE_CHOICE',
        content: '',
        options: ['', ''],
        answers: [],
        explanation: ''
      })
    }

    const exportQuestions = async () => {
      try {
        const response = await api.get(`/questions/export/${questionBankId.value}`)
        const data = response.data.data

        // 创建下载链接
        const blob = new Blob([JSON.stringify(data, null, 2)], {type: 'application/json'})
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `${questionBankTitle.value}_题目.json`
        link.click()
        window.URL.revokeObjectURL(url)

        ElMessage.success('导出成功')
      } catch (error) {
        ElMessage.error('导出失败')
      }
    }

    const handleFileChange = (file) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const data = JSON.parse(e.target.result)
          if (data.questions && Array.isArray(data.questions)) {
            importData.value = data
          } else {
            ElMessage.error('文件格式不正确')
          }
        } catch (error) {
          ElMessage.error('文件解析失败')
        }
      }
      reader.readAsText(file.raw)
    }

    const confirmImport = async () => {
      try {
        const importDto = {
          questionBankId: questionBankId.value,
          questions: importData.value.questions
        }

        await api.post('/questions/import', importDto)
        ElMessage.success('导入成功')
        showImportDialog.value = false
        importData.value = null
        loadQuestions()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '导入失败')
      }
    }

    const goBack = () => {
      router.push('/admin/question-banks')
    }

    const batchDelete = async () => {
      if (selectedQuestions.value.length === 0) {
        ElMessage.warning('请选择要删除的题目')
        return
      }

      try {
        await ElMessageBox.confirm(
            `确定要删除选中的 ${selectedQuestions.value.length} 道题目吗？`,
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
        )

        // 批量删除选中的题目
        const deletePromises = selectedQuestions.value.map(id =>
            api.delete(`/questions/${id}`)
        )

        await Promise.all(deletePromises)
        ElMessage.success('批量删除成功')
        selectedQuestions.value = []
        loadQuestions()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('批量删除失败')
        }
      }
    }

    const handleSelectionChange = (selected) => {
      selectedQuestions.value = selected.map(q => q.id)
    }

    onMounted(() => {
      loadQuestionBankInfo()
      loadQuestions()
    })

    return {
      questionBankId,
      questionBankTitle,
      questions,
      showCreateDialog,
      showImportDialog,
      editingQuestion,
      questionFormRef,
      importData,
      formData,
      questionRules,
      isChoiceQuestion,
      getQuestionTypeText,
      getQuestionTypeTag,
      editQuestion,
      deleteQuestion,
      addOption,
      removeOption,
      saveQuestion,
      exportQuestions,
      handleFileChange,
      confirmImport,
      goBack,
      handleTypeChange,
      selectedQuestions,
      batchDelete,
      handleSelectionChange,
      loading,
      currentPage,
      pageSize,
      total,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.header-actions .el-button {
  margin-left: 0;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.import-preview {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 4px;
}

.preview-tip {
  text-align: center;
  color: #666;
  font-size: 12px;
  margin-top: 10px;
}
</style>
