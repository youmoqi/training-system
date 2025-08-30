<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="$router.go(-1)" icon="ArrowLeft">返回</el-button>
          <span>{{ exam?.title }} - 自动组卷</span>
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

      <!-- 自动组卷规则 -->
      <div class="auto-rules-section">
        <h3>自动组卷规则</h3>
        <el-button type="primary" @click="addRule" style="margin-bottom: 20px">
          <el-icon>
            <Plus/>
          </el-icon>
          添加规则
        </el-button>

        <el-table :data="autoRules" style="width: 100%" v-loading="loading">
          <el-table-column prop="questionType" label="题目类型" width="150">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.questionType)">
                {{ getQuestionTypeName(scope.row.questionType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="questionCount" label="题目数量" width="100"/>
          <el-table-column label="题库选择" min-width="200">
            <template #default="scope">
              <el-tag v-for="bankId in scope.row.questionBankIds" :key="bankId" style="margin-right: 5px">
                {{ getQuestionBankName(bankId) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="editRule(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="removeRule(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="autoRules.length === 0" class="no-rules">
          <el-empty description="暂无组卷规则">
            <el-button type="primary" @click="addRule">添加规则</el-button>
          </el-empty>
        </div>
      </div>

      <!-- 自动组卷操作 -->
      <div class="auto-generate-section">
        <h3>自动组卷</h3>
        <el-alert
            title="自动组卷将根据规则从题库中随机抽取题目，会覆盖现有题目"
            type="warning"
            :closable="false"
            style="margin-bottom: 20px"
        />
        <el-button type="success" @click="autoGenerate" :loading="generating">
          <el-icon>
            <MagicStick/>
          </el-icon>
          开始自动组卷
        </el-button>
      </div>
    </el-card>

    <!-- 添加/编辑规则对话框 -->
    <el-dialog v-model="ruleDialogVisible" :title="isEditRule ? '编辑规则' : '添加规则'" width="600px">
      <el-form :model="ruleForm" :rules="ruleRules" ref="ruleFormRef" label-width="120px">
        <el-form-item label="题目类型" prop="questionType">
          <el-select v-model="ruleForm.questionType" placeholder="请选择题目类型">
            <el-option label="单选题" value="SINGLE_CHOICE"/>
            <el-option label="多选题" value="MULTIPLE_CHOICE"/>
            <el-option label="判断题" value="TRUE_FALSE"/>
            <el-option label="填空题" value="FILL_BLANK"/>
            <el-option label="简答题" value="SHORT_ANSWER"/>
          </el-select>
        </el-form-item>
        <el-form-item label="题目数量" prop="questionCount">
          <el-input-number v-model="ruleForm.questionCount" :min="1" :max="100"/>
        </el-form-item>
        <el-form-item label="题库选择" prop="questionBankIds">
          <el-select v-model="ruleForm.questionBankIds" multiple placeholder="请选择题库">
            <el-option
                v-for="bank in questionBanks"
                :key="bank.id"
                :label="bank.title"
                :value="bank.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="ruleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRule">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, reactive, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowLeft, Plus, MagicStick} from '@element-plus/icons-vue'
import api from '@/api'
import {getCategoryName} from '@/utils/examCategory'

export default {
  name: 'ExamAutoGenerate',
  components: {
    ArrowLeft,
    Plus,
    MagicStick
  },
  setup() {
    const route = useRoute()
    const exam = ref(null)
    const loading = ref(false)
    const generating = ref(false)
    const ruleDialogVisible = ref(false)
    const isEditRule = ref(false)
    const ruleFormRef = ref(null)
    const autoRules = ref([])
    const questionBanks = ref([])

    const ruleForm = reactive({
      id: null,
      questionType: '',
      questionCount: 10,
      questionBankIds: []
    })

    const ruleRules = {
      questionType: [
        {required: true, message: '请选择题目类型', trigger: 'change'}
      ],
      questionCount: [
        {required: true, message: '请输入题目数量', trigger: 'blur'}
      ]
    }

    const loadExam = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exams/${route.params.id}`)
        if (response.data.success) {
          exam.value = response.data.data
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

    const loadAutoRules = async () => {
      try {
        const response = await api.get(`/exams/${route.params.id}/auto-rules`)
        if (response.data.success) {
          autoRules.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载组卷规则失败')
      }
    }

    const addRule = () => {
      isEditRule.value = false
      Object.assign(ruleForm, {
        id: null,
        questionType: '',
        questionCount: 10,
        questionBankIds: []
      })
      ruleDialogVisible.value = true
    }

    const editRule = (rule) => {
      isEditRule.value = true
      Object.assign(ruleForm, {
        id: rule.id,
        questionType: rule.questionType,
        questionCount: rule.questionCount,
        questionBankIds: rule.questionBankIds || []
      })
      ruleDialogVisible.value = true
    }

    const submitRule = async () => {
      try {
        await ruleFormRef.value.validate()

        if (isEditRule.value) {
          await api.put(`/exams/${route.params.id}/auto-rules/${ruleForm.id}`, ruleForm)
          ElMessage.success('更新规则成功')
        } else {
          await api.post(`/exams/${route.params.id}/auto-rules`, ruleForm)
          ElMessage.success('添加规则成功')
        }

        ruleDialogVisible.value = false
        loadAutoRules()
      } catch (error) {
        ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const removeRule = async (rule) => {
      try {
        await ElMessageBox.confirm('确定要删除这个规则吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/exams/${route.params.id}/auto-rules/${rule.id}`)
        ElMessage.success('删除规则成功')
        loadAutoRules()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
        }
      }
    }

    const autoGenerate = async () => {
      try {
        await ElMessageBox.confirm('确定要开始自动组卷吗？这将覆盖现有题目。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        generating.value = true

        const response = await api.post(`/exams/${route.params.id}/auto-generate`)

        if (response.data.success) {
          ElMessage.success('自动组卷成功')
          loadExam()
        } else {
          ElMessage.error(response.data.message || '自动组卷失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('自动组卷失败: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        generating.value = false
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
      return bank ? bank.title : bankId
    }

    onMounted(() => {
      loadExam()
      loadQuestionBanks()
      loadAutoRules()
    })

    return {
      exam,
      loading,
      generating,
      ruleDialogVisible,
      isEditRule,
      ruleForm,
      ruleFormRef,
      ruleRules,
      autoRules,
      questionBanks,
      addRule,
      editRule,
      submitRule,
      removeRule,
      autoGenerate,
      getQuestionTypeName,
      getQuestionTypeTag,
      getQuestionBankName,
      getCategoryName
    }
  }
}
</script>

<style scoped>
.exam-paper-info {
  margin-bottom: 30px;
}

.auto-rules-section {
  margin-top: 30px;
}

.auto-rules-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.auto-generate-section {
  margin-top: 30px;
}

.auto-generate-section h3 {
  margin-bottom: 20px;
  color: #303133;
}

.no-rules {
  text-align: center;
  padding: 40px 0;
}
</style>
