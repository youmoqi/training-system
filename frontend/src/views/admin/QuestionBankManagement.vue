<template>
  <div class="question-bank-management">
    <div class="page-header">
      <div class="header-left">
        <h3>题库管理</h3>
        <el-tag type="info">共 {{ questionBanks.length }} 个题库</el-tag>
      </div>
      <el-button type="primary" @click="createQuestionBank">
        创建题库
      </el-button>
    </div>
    
    <el-table :data="questionBanks" style="width: 100%">
      <el-table-column prop="title" label="题库名称" />
      <el-table-column prop="description" label="题库描述" />
      <el-table-column prop="price" label="价格">
        <template #default="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="questionCount" label="题目数量" width="100">
        <template #default="scope">
          <el-tag type="info">{{ scope.row.questionCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isOnline" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.isOnline ? 'success' : 'danger'">
            {{ scope.row.isOnline ? '已上线' : '已下线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300">
        <template #default="scope">
          <el-button size="small" type="primary" @click="editQuestionBank(scope.row)">
            编辑
          </el-button>
          <el-button
            size="small"
            type="success"
            @click="manageQuestions(scope.row)"
          >
            题目管理
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="deleteQuestionBank(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 空状态 -->
    <el-empty v-if="questionBanks.length === 0" description="暂无题库数据" />
    
    <!-- 创建/编辑题库对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingQuestionBank ? '编辑题库' : '创建题库'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="questionBankFormRef"
        :model="formData"
        :rules="questionBankRules"
        label-width="100px"
      >
        <el-form-item label="题库名称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入题库名称" />
        </el-form-item>
        
        <el-form-item label="题库描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入题库描述"
          />
        </el-form-item>
        
        <el-form-item label="题库价格" prop="price">
          <el-input-number
            v-model="formData.price"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="请输入题库价格"
          />
        </el-form-item>
        
        <el-form-item label="上线状态" prop="isOnline">
          <el-switch v-model="formData.isOnline" />
        </el-form-item>
        
        <el-form-item label="可见角色" prop="visibleRoles">
          <el-checkbox-group v-model="formData.visibleRoles">
            <el-checkbox label="EXPLOSIVE_USER">易制爆人员</el-checkbox>
            <el-checkbox label="BLAST_USER">爆破三大员</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <!-- Word导入题目入口 -->
        <el-form-item label="导入题库(Word)">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept=".doc,.docx"
            :on-change="handleWordFileChange"
          >
            <el-button type="primary">选择Word文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持.doc/.docx格式，自动解析题库和题目</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="wordImportPreview" label="预览题目">
          <div class="import-preview">
            <div class="preview-header">
              <p>将导入 {{ wordImportPreview.questions.length }} 道题目：</p>
              <el-button size="small" type="danger" @click="clearWordImport">清除</el-button>
            </div>
            <el-table :data="wordImportPreview.questions.slice(0, 5)" size="small">
              <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" width="100" />
            </el-table>
            <p v-if="wordImportPreview.questions.length > 5" class="preview-tip">仅显示前5道题目，共 {{ wordImportPreview.questions.length }} 道</p>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialog">取消</el-button>
          <el-button type="primary" @click="saveQuestionBank">
            {{ editingQuestionBank ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

export default {
  name: 'QuestionBankManagement',
  setup() {
    const questionBanks = ref([])
    const showCreateDialog = ref(false)
    const editingQuestionBank = ref(null)
    const questionBankFormRef = ref(null)
    const router = useRouter()
    const loading = ref(false)
    
    const formData = reactive({
      title: '',
      description: '',
      price: 0,
      isOnline: true,
      visibleRoles: []
    })
    
    const questionBankRules = {
      title: [
        { required: true, message: '请输入题库名称', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入题库描述', trigger: 'blur' }
      ],
      price: [
        { required: true, message: '请输入题库价格', trigger: 'blur' }
      ]
    }
    
    const loadQuestionBanks = async () => {
      loading.value = true
      try {
        const response = await api.get('/question-banks')
        questionBanks.value = response.data.data
      } catch (error) {
        ElMessage.error('加载题库失败')
      } finally {
        loading.value = false
      }
    }
    
    const editQuestionBank = (questionBank) => {
      editingQuestionBank.value = questionBank
      wordImportPreview.value = null
      Object.assign(formData, questionBank)
      showCreateDialog.value = true
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
    
    const saveQuestionBank = async () => {
      try {
        await questionBankFormRef.value.validate()
        
        const questionBankData = {
          ...formData,
          questions: wordImportPreview.value ? wordImportPreview.value.questions : []
        }
        
        if (editingQuestionBank.value) {
          await api.put(`/question-banks/${editingQuestionBank.value.id}`, questionBankData)
          ElMessage.success('更新成功')
        } else {
          await api.post('/question-banks', questionBankData)
          ElMessage.success('创建成功')
        }
        
        showCreateDialog.value = false
        resetForm()
        wordImportPreview.value = null
        loadQuestionBanks()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
    
    const resetForm = () => {
      editingQuestionBank.value = null
      wordImportPreview.value = null
      Object.assign(formData, {
        title: '',
        description: '',
        price: 0,
        isOnline: true,
        visibleRoles: []
      })
    }
    
    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleString('zh-CN')
    }
    
    const manageQuestions = (questionBank) => {
      router.push(`/admin/question-banks/${questionBank.id}/questions`)
    }
    
    const wordImportPreview = ref(null)
    
    const handleWordFileChange = async (file) => {
      if (!file.raw) {
        ElMessage.error('请选择有效的Word文件')
        return
      }
      
      // 检查文件类型
      const allowedTypes = ['application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
      if (!allowedTypes.includes(file.raw.type)) {
        ElMessage.error('请选择.doc或.docx格式的文件')
        return
      }
      
      try {
        // 创建FormData
        const uploadFormData = new FormData()
        uploadFormData.append('file', file.raw)
        uploadFormData.append('title', formData.title || '')
        uploadFormData.append('description', formData.description || '')
        
        // 调用后端接口解析Word文件
        const response = await api.post('/question-banks/import-word', uploadFormData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        // 显示解析预览
        wordImportPreview.value = response.data.data
        
        ElMessage.success('文件解析成功，请查看预览')
      } catch (error) {
        ElMessage.error('文件解析失败：' + (error.response?.data?.message || '未知错误'))
        // 如果后端接口不存在，显示模拟数据
        wordImportPreview.value = {
          questions: [
            { content: '示例题目1：易制爆物品包括哪些？', type: '单选题' },
            { content: '示例题目2：爆破作业的安全要求有哪些？', type: '多选题' },
            { content: '示例题目3：请简述爆破作业的基本流程。', type: '主观题' },
            { content: '示例题目4：易制爆物品的储存要求是什么？', type: '单选题' },
            { content: '示例题目5：爆破作业人员的资质要求有哪些？', type: '多选题' }
          ]
        }
        ElMessage.warning('当前显示模拟数据，请配置后端接口')
      }
    }
    
    const clearWordImport = () => {
      wordImportPreview.value = null
    }
    
    const cancelDialog = () => {
      showCreateDialog.value = false
      resetForm()
    }
    
    const createQuestionBank = () => {
      showCreateDialog.value = true
      resetForm()
    }
    
    onMounted(() => {
      loadQuestionBanks()
    })
    
    return {
      questionBanks,
      showCreateDialog,
      editingQuestionBank,
      questionBankFormRef,
      formData,
      questionBankRules,
      editQuestionBank,
      deleteQuestionBank,
      saveQuestionBank,
      formatDate,
      manageQuestions,
      wordImportPreview,
      handleWordFileChange,
      clearWordImport,
      cancelDialog,
      createQuestionBank
    }
  }
}
</script>

<style scoped>
.question-bank-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

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

.import-preview {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 4px;
  background: #f8f9fa;
}

.preview-tip {
  text-align: center;
  color: #666;
  font-size: 12px;
  margin-top: 10px;
}

.el-upload__tip {
  color: #666;
  font-size: 12px;
  margin-top: 5px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
</style> 