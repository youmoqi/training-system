<template>
  <div class="exam-paper-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="$router.go(-1)" icon="ArrowLeft">返回</el-button>
          <span>试卷详情</span>
          <div>
            <el-button type="success" @click="manageQuestions" v-if="examPaper">
              管理题目
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="examPaper" class="exam-paper-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="试卷名称">{{ examPaper.title }}</el-descriptions-item>
          <el-descriptions-item label="试卷描述">{{ examPaper.description }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ examPaper.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="及格分">{{ examPaper.passScore }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ examPaper.duration }}分钟</el-descriptions-item>
          <el-descriptions-item label="题目数量">{{ examPaper.totalQuestions }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="examPaper.isOnline ? 'success' : 'info'">
              {{ examPaper.isOnline ? '已上线' : '未上线' }}
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="可见角色" :span="2">
            <el-tag v-for="role in examPaper.visibleRoles" :key="role" style="margin-right: 5px">
              {{ role?.visibilityCategory.name }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDateTime(examPaper.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 题目列表 -->
      <div v-if="examPaper && examPaper.questions && examPaper.questions.length > 0" class="questions-section">
        <h3>题目列表</h3>
        <el-table :data="examPaper.questions" style="width: 100%">
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
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="viewQuestion(scope.row)">查看</el-button>
              <el-button size="small" type="danger" @click="removeQuestion(scope.row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else-if="examPaper" class="no-questions">
        <el-empty description="暂无题目">
          <el-button type="primary" @click="manageQuestions">添加题目</el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 题目详情对话框 -->
    <el-dialog v-model="questionDialogVisible" title="题目详情" width="800px">
      <div v-if="selectedQuestion">
        <h4>题目内容</h4>
        <p>{{ selectedQuestion.questionContent }}</p>

        <h4 v-if="selectedQuestion.options && selectedQuestion.options.length > 0">选项</h4>
        <ul v-if="selectedQuestion.options && selectedQuestion.options.length > 0">
          <li v-for="(option, index) in selectedQuestion.options" :key="index">
            {{ String.fromCharCode(65 + index) }}. {{ option }}
          </li>
        </ul>

        <h4>正确答案</h4>
        <p v-if="selectedQuestion.answers && selectedQuestion.answers.length > 0">
          {{ selectedQuestion.answers.join(', ') }}
        </p>

        <h4 v-if="selectedQuestion.explanation">解析</h4>
        <p v-if="selectedQuestion.explanation">{{ selectedQuestion.explanation }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowLeft} from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'ExamPaperDetail',
  components: {
    ArrowLeft
  },
  setup() {
    const route = useRoute()
    const router = useRouter()

    const examPaper = ref(null)
    const loading = ref(false)
    const questionDialogVisible = ref(false)
    const selectedQuestion = ref(null)

    const loadExamPaper = async () => {
      loading.value = true
      try {
        const response = await api.get(`/exam-papers/${route.params.id}`)
        if (response.data.success) {
          examPaper.value = response.data.data
        }
      } catch (error) {
        ElMessage.error('加载试卷详情失败')
      } finally {
        loading.value = false
      }
    }

    const manageQuestions = () => {
      router.push(`/admin/exam-papers/${route.params.id}/questions`)
    }

    const viewQuestion = (question) => {
      selectedQuestion.value = question
      questionDialogVisible.value = true
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
          ElMessage.error('移除失败')
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

    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    onMounted(() => {
      loadExamPaper()
    })

    return {
      examPaper,
      loading,
      questionDialogVisible,
      selectedQuestion,
      manageQuestions,
      viewQuestion,
      removeQuestion,
      getQuestionTypeName,
      getQuestionTypeTag,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.exam-paper-detail {
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
</style>
