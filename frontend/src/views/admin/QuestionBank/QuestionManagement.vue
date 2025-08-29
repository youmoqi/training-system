<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft">返回</el-button>
        <h3>题目管理 - {{ questionBankTitle }}</h3>
        <el-tag type="info">共 {{ questions.length }} 道题目</el-tag>
      </div>
      <div class="header-actions">
        <el-button @click="showImportDialog = true">导入题目</el-button>
        <el-button @click="exportQuestionsToWord">导出Word</el-button>
        <el-button type="warning" @click="batchDelete" :disabled="selectedQuestions.length === 0">
          批量删除 ({{ selectedQuestions.length }})
        </el-button>
        <el-button type="primary" @click="addQuestion">添加题目</el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <el-table
            :data="questions"
            style="width: 100%"
            v-loading="loading"
            @selection-change="handleSelectionChange"
            stripe
        >
          <el-table-column type="selection" width="55"/>
          <el-table-column prop="id" label="ID" width="80"/>
          <el-table-column prop="content" label="题目内容" show-overflow-tooltip min-width="400"/>
          <el-table-column prop="type" label="题目类型" width="140">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.type)">
                {{ getQuestionTypeText(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
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
            <el-option label="判断题" value="TRUE_FALSE"/>
            <el-option label="填空题" value="FILL_BLANK"/>
            <el-option label="简答题" value="SHORT_ANSWER"/>
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
            v-if="isChoiceQuestion || isTrueFalseQuestion"
            label="选项"
            prop="options"
        >
          <div class="options-container">
            <div class="options-grid">
              <div v-for="(option, index) in formData.options" :key="index" class="option-item">
                <div class="option-label">
                  {{ String.fromCharCode(65 + index) }}.
                </div>
                <el-input
                    v-model="formData.options[index]"
                    :placeholder="`请输入选项${String.fromCharCode(65 + index)}的内容`"
                    style="flex: 1"
                    :disabled="isTrueFalseQuestion"
                    @focus="handleOptionFocus"
                />
                <el-button
                    v-if="!isTrueFalseQuestion"
                    type="danger"
                    size="small"
                    @click="removeOption(index)"
                    :disabled="formData.options.length <= 2"
                    class="remove-btn"
                >
                  删除
                </el-button>
              </div>
            </div>
            <el-button
                v-if="!isTrueFalseQuestion"
                type="primary"
                size="small"
                @click="addOption"
                class="add-option-btn"
                icon="Plus"
            >
              添加选项
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="正确答案" prop="answers">
          <!-- 多选题 -->
          <el-checkbox-group
              v-if="formData.type === 'MULTIPLE_CHOICE'"
              v-model="formData.answers"
              class="answer-group"
          >
            <div class="answers-grid">
              <div v-for="(option, index) in formData.options" :key="index" class="answer-item">
                <el-checkbox :label="option">
                  <span class="answer-label">{{ String.fromCharCode(65 + index) }}.</span>
                  <span class="answer-content">{{ option }}</span>
                </el-checkbox>
              </div>
            </div>
          </el-checkbox-group>

          <!-- 单选题 -->
          <el-radio-group
              v-else-if="formData.type === 'SINGLE_CHOICE'"
              v-model="formData.answers"
              class="answer-group"
          >
            <div class="answers-grid">
              <div v-for="(option, index) in formData.options" :key="index" class="answer-item">
                <el-radio :label="option">
                  <span class="answer-label">{{ String.fromCharCode(65 + index) }}.</span>
                  <span class="answer-content">{{ option }}</span>
                </el-radio>
              </div>
            </div>
          </el-radio-group>

          <!-- 判断题 -->
          <el-radio-group
              v-else-if="formData.type === 'TRUE_FALSE'"
              v-model="formData.answers"
              class="answer-group"
          >
            <div class="answers-grid">
              <div class="answer-item">
                <el-radio label="正确">
                  <span class="answer-label">A.</span>
                  <span class="answer-content">正确</span>
                </el-radio>
              </div>
              <div class="answer-item">
                <el-radio label="错误">
                  <span class="answer-label">B.</span>
                  <span class="answer-content">错误</span>
                </el-radio>
              </div>
            </div>
          </el-radio-group>

          <!-- 填空题和简答题 -->
          <el-input
              v-else
              v-model="formData.answers"
              type="textarea"
              :rows="3"
              :placeholder="formData.type === 'FILL_BLANK' ? '请输入填空题答案' : '请输入简答题答案'"
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
    <el-dialog v-model="showImportDialog" title="导入题目" width="900px">
      <el-tabs v-model="importTab" type="card">
        <!-- Word文档导入 -->
        <el-tab-pane label="Word文档导入" name="word">
          <el-form label-width="100px">
            <el-form-item label="Word文档">
              <el-upload
                  ref="wordUploadRef"
                  :auto-upload="false"
                  :on-change="handleWordFileChange"
                  :show-file-list="false"
                  accept=".doc,.docx"
              >
                <el-button type="primary">选择Word文档</el-button>
                <template #tip>
                                       <div class="el-upload__tip">
                       支持 .doc、.docx 格式文件
                       <br>
                       <el-button type="text" @click="downloadTemplate" class="template-link">
                         下载Word模板
                       </el-button>
                     </div>
                </template>
              </el-upload>
            </el-form-item>

            <el-form-item v-if="wordImportData" label="预览数据">
              <div class="import-preview">
                <div class="preview-header">
                  <el-tag type="success" size="large">
                    将导入 {{ wordImportData.length }} 道题目
                  </el-tag>
                </div>
                <el-table :data="wordImportData.slice(0, 5)" size="small" stripe>
                  <el-table-column prop="content" label="题目内容" show-overflow-tooltip min-width="350"/>
                  <el-table-column prop="type" label="类型" width="120" align="center">
                    <template #default="scope">
                      <el-tag :type="getQuestionTypeTag(scope.row.type)" size="small">
                        {{ getQuestionTypeText(scope.row.type) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
                <p v-if="wordImportData.length > 5" class="preview-tip">
                  仅显示前5道题目，共 {{ wordImportData.length }} 道
                </p>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showImportDialog = false">取消</el-button>
          <el-button
              v-if="importTab === 'json'"
              type="primary"
              :disabled="!importData"
              @click="confirmImport"
          >
            确认导入
          </el-button>
          <el-button
              v-if="importTab === 'word'"
              type="primary"
              :disabled="!wordImportData"
              @click="confirmWordImport"
          >
            确认导入
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, computed, onMounted, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import api from '../../../api'

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
    const wordImportData = ref(null)
    const importTab = ref('word')
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
        {
          validator: (rule, value, callback) => {
            if (isChoiceQuestion.value || isTrueFalseQuestion.value) {
              if (!value || value.length < 2) {
                callback(new Error('请至少添加两个选项'))
              } else if (value.some(option => !option.trim())) {
                callback(new Error('请填写所有选项内容'))
              } else if (value.some(option => option.trim().length < 1)) {
                callback(new Error('选项内容不能为空'))
              } else {
                callback()
              }
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ],
      answers: [
        {
          validator: (rule, value, callback) => {
            if (isChoiceQuestion.value || isTrueFalseQuestion.value) {
              if (!value || (Array.isArray(value) && value.length === 0)) {
                callback(new Error('请选择正确答案'))
              } else if (Array.isArray(value) && value.some(answer => !answer.trim())) {
                callback(new Error('请选择有效的答案'))
              } else {
                callback()
              }
            } else {
              if (!value || !value.trim()) {
                callback(new Error('请输入正确答案'))
              } else {
                callback()
              }
            }
          },
          trigger: 'blur'
        }
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

    const isTrueFalseQuestion = computed(() => {
      return formData.value.type === 'TRUE_FALSE'
    })

    const isFillBlankQuestion = computed(() => {
      return formData.value.type === 'FILL_BLANK'
    })

    const isShortAnswerQuestion = computed(() => {
      return formData.value.type === 'SHORT_ANSWER'
    })

    // 监听题目类型变化，重置答案
    const handleTypeChange = () => {
      const type = formData.value.type

      // 根据题目类型设置默认值
      switch (type) {
        case 'SINGLE_CHOICE':
        case 'MULTIPLE_CHOICE':
          formData.value.options = ['选项A', '选项B']
          formData.value.answers = type === 'SINGLE_CHOICE' ? '' : []
          break
        case 'TRUE_FALSE':
          formData.value.options = ['正确', '错误']
          formData.value.answers = ''
          break
        case 'FILL_BLANK':
        case 'SHORT_ANSWER':
          formData.value.options = []
          formData.value.answers = ''
          break
      }
    }

    const getQuestionTypeText = (type) => {
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

    const addQuestion = () => {
      editingQuestion.value = null
      resetForm()
      showCreateDialog.value = true
    }

    const editQuestion = (question) => {
      editingQuestion.value = question

      // 处理选项，确保有足够的选项
      let options = question.options || []
      if (question.type === 'TRUE_FALSE') {
        options = ['正确', '错误']
      } else if (question.type === 'SINGLE_CHOICE' || question.type === 'MULTIPLE_CHOICE') {
        while (options.length < 2) options.push('')
      }

      // 处理答案 - 将答案标签转换为选项内容
      let answers = question.answers
      if (question.type === 'SINGLE_CHOICE' || question.type === 'MULTIPLE_CHOICE' || question.type === 'TRUE_FALSE') {
        if (Array.isArray(answers)) {
          // 多选题：将答案标签转换为选项内容
          answers = answers.map(answerLabel => {
            const index = answerLabel.charCodeAt(0) - 65 // A=0, B=1, C=2...
            return options[index] || answerLabel
          })
        } else if (answers) {
          // 单选题和判断题：将答案标签转换为选项内容
          const index = answers.charCodeAt(0) - 65 // A=0, B=1, C=2...
          answers = options[index] || answers
        }

        // 确保答案格式正确
        if (question.type === 'MULTIPLE_CHOICE') {
          answers = Array.isArray(answers) ? answers : [answers]
        } else {
          answers = Array.isArray(answers) ? (answers.length > 0 ? answers[0] : '') : (answers || '')
        }
      } else {
        // 填空题和简答题：直接使用答案内容
        answers = Array.isArray(answers) ? (answers.length > 0 ? answers[0] : '') : (answers || '')
      }

      Object.assign(formData.value, {
        type: question.type,
        content: question.content,
        options,
        answers,
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
      const newIndex = formData.value.options.length
      const defaultContent = `选项${String.fromCharCode(65 + newIndex)}`
      formData.value.options.push(defaultContent)
      ElMessage.success(`已添加选项${String.fromCharCode(65 + newIndex)}`)
    }

    const handleOptionFocus = (event) => {
      // 自动选中输入框内容，方便编辑
      event.target.select()
    }

    const removeOption = async (index) => {
      try {
        await ElMessageBox.confirm(
            `确定要删除选项${String.fromCharCode(65 + index)}吗？`,
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
        )

        formData.value.options.splice(index, 1)

        // 更新答案
        if (Array.isArray(formData.value.answers)) {
          formData.value.answers = formData.value.answers.filter(answer =>
              formData.value.options.includes(answer)
          )
        } else if (formData.value.answers === formData.value.options[index]) {
          formData.value.answers = ''
        }

        // 重新设置选项内容，确保字母标识正确
        if (formData.value.type === 'SINGLE_CHOICE' || formData.value.type === 'MULTIPLE_CHOICE') {
          formData.value.options = formData.value.options.map((option, idx) => {
            if (option.startsWith('选项')) {
              return `选项${String.fromCharCode(65 + idx)}`
            }
            return option
          })
        }

        ElMessage.success('选项删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除选项失败')
        }
      }
    }

    const saveQuestion = async () => {
      try {
        await questionFormRef.value.validate()

        // 根据题目类型处理答案格式
        let answers
        if (formData.value.type === 'MULTIPLE_CHOICE') {
          // 多选题：答案数组
          answers = Array.isArray(formData.value.answers) ? formData.value.answers : [formData.value.answers]
        } else if (formData.value.type === 'SINGLE_CHOICE' || formData.value.type === 'TRUE_FALSE') {
          // 单选题和判断题：单个答案
          answers = [formData.value.answers]
        } else {
          // 填空题和简答题：单个答案
          answers = [formData.value.answers]
        }

        // 将选项内容转换为答案标签
        if (formData.value.type === 'SINGLE_CHOICE' || formData.value.type === 'MULTIPLE_CHOICE' || formData.value.type === 'TRUE_FALSE') {
          answers = answers.map(answerContent => {
            const index = formData.value.options.indexOf(answerContent)
            return index >= 0 ? String.fromCharCode(65 + index) : answerContent
          })
        }

        const questionData = {
          questionBankId: questionBankId.value,
          type: formData.value.type,
          content: formData.value.content.trim(),
          options: (isChoiceQuestion.value || isTrueFalseQuestion.value) ? formData.value.options : null,
          answers: answers,
          explanation: formData.value.explanation.trim()
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
        options: ['选项A', '选项B'],
        answers: '',
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

    const exportQuestionsToWord = async () => {
      try {
        const response = await api.get(`/questions/export/word/${questionBankId.value}`, {
          responseType: 'blob'
        })

        // 创建下载链接
        const blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `${questionBankTitle.value}_题目.docx`
        link.click()
        window.URL.revokeObjectURL(url)

        ElMessage.success('导出Word文档成功')
      } catch (error) {
        ElMessage.error('导出Word文档失败')
      }
    }

    const downloadTemplate = async () => {
      try {
        const response = await api.get('/questions/template/word', {
          responseType: 'blob'
        })

        // 创建下载链接
        const blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = 'question_template.docx'
        link.click()
        window.URL.revokeObjectURL(url)

        ElMessage.success('模板下载成功')
      } catch (error) {
        ElMessage.error('模板下载失败')
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

    const handleWordFileChange = async (file) => {
      try {
        const formData = new FormData()
        formData.append('file', file.raw)
        formData.append('questionBankId', questionBankId.value)

        const response = await api.post('/questions/import/word', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (response.data.success) {
          wordImportData.value = response.data.data
          ElMessage.success(`成功解析 ${wordImportData.value.length} 道题目`)
        } else {
          ElMessage.error(response.data.message || '解析失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '文件解析失败')
      }
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

    const confirmWordImport = async () => {
      try {
        const importDto = {
          questionBankId: questionBankId.value,
          questions: wordImportData.value
        }

        await api.post('/questions/import/word/confirm', importDto)
        ElMessage.success('导入成功')
        showImportDialog.value = false
        wordImportData.value = null
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

    // 监听选项变化，自动更新答案
    watch(() => formData.value.options, (newOptions, oldOptions) => {
      if (!oldOptions) return // 初始化时不处理

      const type = formData.value.type
      if (type === 'SINGLE_CHOICE' || type === 'TRUE_FALSE') {
        // 单选题和判断题：如果当前答案不在新选项中，清空答案
        if (formData.value.answers && !newOptions.includes(formData.value.answers)) {
          formData.value.answers = ''
        }
      } else if (type === 'MULTIPLE_CHOICE') {
        // 多选题：过滤掉不在新选项中的答案
        if (Array.isArray(formData.value.answers)) {
          formData.value.answers = formData.value.answers.filter(answer =>
              newOptions.includes(answer)
          )
        }
      }
    }, {deep: true})

    return {
      questionBankId,
      questionBankTitle,
      questions,
      showCreateDialog,
      showImportDialog,
      editingQuestion,
      questionFormRef,
      importData,
      wordImportData,
      importTab,
      formData,
      questionRules,
      isChoiceQuestion,
      isTrueFalseQuestion,
      isFillBlankQuestion,
      isShortAnswerQuestion,
      getQuestionTypeText,
      getQuestionTypeTag,
      addQuestion,
      editQuestion,
      deleteQuestion,
      addOption,
      removeOption,
      saveQuestion,
      exportQuestions,
      exportQuestionsToWord,
      downloadTemplate,
      handleFileChange,
      handleWordFileChange,
      confirmImport,
      confirmWordImport,
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
      handleCurrentChange,
      handleOptionFocus
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

.options-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 15px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 15px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.option-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.15);
}

.option-label {
  width: 35px;
  text-align: center;
  padding: 6px 10px;
  background: linear-gradient(135deg, #409eff 0%, #36a3f7 100%);
  color: white;
  border-radius: 6px;
  font-weight: bold;
  font-size: 13px;
  min-width: 35px;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
  flex-shrink: 0;
}

.remove-btn {
  margin-left: 12px;
  flex-shrink: 0;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.remove-btn:hover {
  transform: scale(1.05);
}

.add-option-btn {
  align-self: flex-start;
  background: linear-gradient(135deg, #409eff 0%, #36a3f7 100%);
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
  transition: all 0.3s ease;
  margin-top: 5px;
}

.add-option-btn:hover {
  background: linear-gradient(135deg, #36a3f7 0%, #2d8cf0 100%);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
  transform: translateY(-1px);
}

.add-option-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.2);
}

.import-preview {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 4px;
}

.import-preview .el-table {
  margin-top: 10px;
}

.import-preview .el-table .el-table__cell {
  padding: 8px 12px;
}

.import-preview .el-table .cell {
  line-height: 1.4;
  word-break: break-word;
}

.preview-header {
  margin-bottom: 15px;
  text-align: center;
}

.preview-header .el-tag {
  font-size: 14px;
  padding: 8px 16px;
}

.preview-tip {
  text-align: center;
  color: #666;
  font-size: 12px;
  margin-top: 10px;
}

.template-link {
  color: #409eff;
  text-decoration: none;
  font-size: 12px;
}

.template-link:hover {
  text-decoration: underline;
}

.answer-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.answers-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.answer-item {
  display: flex;
  align-items: center;
  padding: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.answer-item:hover {
  background-color: #f0f9ff;
  border-color: #409eff;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.15);
}

.answer-label {
  font-weight: bold;
  color: #409eff;
  min-width: 30px;
  margin-right: 10px;
  font-size: 14px;
  background: linear-gradient(135deg, #409eff 0%, #36a3f7 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  text-align: center;
  flex-shrink: 0;
}

.answer-content {
  flex: 1;
  word-break: break-word;
  line-height: 1.6;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .options-grid,
  .answers-grid {
    grid-template-columns: 1fr;
  }

  .option-item,
  .answer-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .option-label,
  .answer-label {
    align-self: flex-start;
  }

  .remove-btn {
    align-self: flex-end;
    margin-left: 0;
  }
}

/* 题目列表中的选项显示 */
.options-display {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.options-count {
  font-weight: 500;
  color: #409eff;
  font-size: 12px;
}

.options-preview {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.option-preview {
  font-size: 11px;
  color: #666;
  background-color: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.more-options {
  font-size: 10px;
  color: #999;
  font-style: italic;
}

.question-content {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

/* 表格内容优化 */
.card .el-table .el-table__cell {
  padding: 12px 16px;
}

.card .el-table .cell {
  line-height: 1.5;
  word-break: break-word;
}

.card .el-table .el-table__row:hover {
  background-color: #f5f7fa;
}

.question-text {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  word-break: break-word;
  max-height: 60px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.question-explanation {
  margin-top: 3px;
}

.question-explanation .el-tag {
  font-size: 11px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
