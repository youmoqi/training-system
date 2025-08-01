<template>
  <div class="exam-paper-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="$router.go(-1)" icon="ArrowLeft">返回</el-button>
          <span>{{ isEdit ? '编辑试卷' : '创建试卷' }}</span>
        </div>
      </template>

      <el-form 
        :model="examPaperForm" 
        :rules="rules" 
        ref="examPaperFormRef" 
        label-width="120px"
        v-loading="loading"
      >
        <el-form-item label="试卷名称" prop="title">
          <el-input v-model="examPaperForm.title" placeholder="请输入试卷名称" />
        </el-form-item>
        
        <el-form-item label="试卷描述" prop="description">
          <el-input
            v-model="examPaperForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入试卷描述"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number 
                v-model="examPaperForm.totalScore" 
                :min="1" 
                :max="200" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="及格分" prop="passScore">
              <el-input-number 
                v-model="examPaperForm.passScore" 
                :min="1" 
                :max="200" 
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长" prop="duration">
              <el-input-number 
                v-model="examPaperForm.duration" 
                :min="10" 
                :max="300" 
                style="width: 100%"
              />
              <span style="margin-left: 10px">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否上线" prop="isOnline">
              <el-switch v-model="examPaperForm.isOnline" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="随机出题" prop="isRandom">
          <el-switch v-model="examPaperForm.isRandom" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">
            开启后，每次考试题目顺序将随机排列
          </span>
        </el-form-item>
        
        <el-form-item label="可见角色" prop="visibleRoles">
          <el-checkbox-group v-model="examPaperForm.visibleRoles">
            <el-checkbox label="EXPLOSIVE_USER">易制爆用户</el-checkbox>
            <el-checkbox label="BLAST_USER">爆破用户</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
          <el-button @click="$router.go(-1)">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ExamPaperEdit',
  components: {
    ArrowLeft
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const loading = ref(false)
    const submitting = ref(false)
    const examPaperFormRef = ref()
    const isEdit = ref(false)

    const examPaperForm = reactive({
      id: null,
      title: '',
      description: '',
      totalScore: 100,
      passScore: 60,
      duration: 120,
      isOnline: true,
      isRandom: false,
      visibleRoles: []
    })

    const rules = {
      title: [
        { required: true, message: '请输入试卷名称', trigger: 'blur' },
        { min: 2, max: 200, message: '试卷名称长度在 2 到 200 个字符', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入试卷描述', trigger: 'blur' },
        { min: 10, max: 1000, message: '试卷描述长度在 10 到 1000 个字符', trigger: 'blur' }
      ],
      totalScore: [
        { required: true, message: '请输入总分', trigger: 'blur' },
        { type: 'number', min: 1, max: 200, message: '总分必须在 1 到 200 之间', trigger: 'blur' }
      ],
      passScore: [
        { required: true, message: '请输入及格分', trigger: 'blur' },
        { type: 'number', min: 1, max: 200, message: '及格分必须在 1 到 200 之间', trigger: 'blur' }
      ],
      duration: [
        { required: true, message: '请输入考试时长', trigger: 'blur' },
        { type: 'number', min: 10, max: 300, message: '考试时长必须在 10 到 300 分钟之间', trigger: 'blur' }
      ]
    }

    const loadExamPaper = async () => {
      if (route.params.id) {
        loading.value = true
        try {
          const response = await api.get(`/exam-papers/${route.params.id}`)
          if (response.data.success) {
            const examPaper = response.data.data
            Object.assign(examPaperForm, {
              id: examPaper.id,
              title: examPaper.title,
              description: examPaper.description,
              totalScore: examPaper.totalScore,
              passScore: examPaper.passScore,
              duration: examPaper.duration,
              isOnline: examPaper.isOnline,
              isRandom: examPaper.isRandom,
              visibleRoles: examPaper.visibleRoles || []
            })
            isEdit.value = true
          }
        } catch (error) {
          ElMessage.error('加载试卷失败')
        } finally {
          loading.value = false
        }
      }
    }

    const submitForm = async () => {
      try {
        await examPaperFormRef.value.validate()
        submitting.value = true
        
        if (isEdit.value) {
          await api.put(`/exam-papers/${examPaperForm.id}`, examPaperForm)
          ElMessage.success('更新成功')
        } else {
          await api.post('/exam-papers', examPaperForm)
          ElMessage.success('创建成功')
        }
        
        router.go(-1)
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message)
        } else {
          ElMessage.error('操作失败')
        }
      } finally {
        submitting.value = false
      }
    }

    onMounted(() => {
      loadExamPaper()
    })

    return {
      loading,
      submitting,
      examPaperForm,
      examPaperFormRef,
      rules,
      isEdit,
      submitForm
    }
  }
}
</script>

<style scoped>
.exam-paper-edit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-form {
  max-width: 800px;
  margin: 0 auto;
}
</style> 