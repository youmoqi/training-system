<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">试卷管理</h3>
      <div class="page-actions">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon>
            <Plus/>
          </el-icon>
          创建试卷
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索试卷..."
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
          <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 150px" @Change="handleSearch">
            <el-option label="全部" value=""/>
            <el-option label="已上线" value="true"/>
            <el-option label="未上线" value="false"/>
          </el-select>
        </div>

        <!-- 试卷列表 -->
        <div class="table-container">
          <el-table :data="examPapers" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="试卷名称" min-width="200"/>
            <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip/>
            <el-table-column prop="totalScore" label="总分" width="80"/>
            <el-table-column prop="passScore" label="及格分" width="80"/>
            <el-table-column prop="duration" label="时长(分钟)" width="100"/>
            <el-table-column prop="totalQuestions" label="题目数" width="80"/>
            <el-table-column prop="examCategory" label="考试分类" width="150">
              <template #default="scope">
                <el-tag :type="getCategoryTagType(scope.row.examCategory)">
                  {{ getCategoryName(scope.row.examCategory) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isOnline ? 'success' : 'info'">
                  {{ scope.row.isOnline ? '已上线' : '未上线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="重复考试" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.allowRetake ? 'success' : 'warning'">
                  {{ scope.row.allowRetake ? '允许' : '禁止' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="360" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewExamPaper(scope.row)">查看</el-button>
                <el-button size="small" type="primary" @click="manageQuestions(scope.row)">
                  管理题目
                </el-button>
                <el-button size="small" type="success" @click="autoGenerate(scope.row)">
                  自动组卷
                </el-button>
                <el-button size="small" type="warning" @click="editExamPaper(scope.row)">
                  编辑
                </el-button>
                <el-button size="small" type="danger" @click="deleteExamPaper(scope.row)">
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

        <!-- 空状态 -->
        <el-empty v-if="!loading && examPapers.length === 0" description="暂无试卷数据"/>
      </div>
    </div>

    <!-- 创建/编辑试卷对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑试卷' : '创建试卷'"
        width="800px"
        class="dialog-container"
    >
      <div class="dialog-body">
        <el-form :model="examPaperForm" :rules="rules" ref="examPaperFormRef" label-width="100px">
          <el-form-item label="试卷名称" prop="title">
            <el-input v-model="examPaperForm.title" placeholder="请输入试卷名称"/>
          </el-form-item>
          <el-form-item label="试卷描述" prop="description">
            <el-input
                v-model="examPaperForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入试卷描述"
            />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="总分" prop="totalScore">
                <el-input-number v-model="examPaperForm.totalScore" :min="1" :max="200"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="及格分" prop="passScore">
                <el-input-number v-model="examPaperForm.passScore" :min="1" :max="200"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="考试时长" prop="duration">
                <el-input-number v-model="examPaperForm.duration" :min="10" :max="300"/>
                <span style="margin-left: 10px">分钟</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否上线" prop="isOnline">
                <el-switch v-model="examPaperForm.isOnline"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="考试分类" prop="examCategory">
                <el-select v-model="examPaperForm.examCategory" placeholder="请选择考试分类">
                  <el-option label="易制爆用户-首次培训" value="EXPLOSIVE_FIRST"/>
                  <el-option label="易制爆用户-继续教育" value="EXPLOSIVE_CONTINUE"/>
                  <el-option label="爆破三大员-首次培训" value="BLAST_THREE_FIRST"/>
                  <el-option label="爆破三大员-继续教育" value="BLAST_THREE_CONTINUE"/>
                  <el-option label="爆破工程技术人员-首次培训" value="BLAST_TECH_FIRST"/>
                  <el-option label="爆破工程技术人员-继续教育" value="BLAST_TECH_CONTINUE"/>
                  <el-option label="通用" value="GENERAL"/>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="允许重复考试" prop="allowRetake">
                <el-switch v-model="examPaperForm.allowRetake"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最大考试次数" prop="maxAttempts">
                <el-input-number v-model="examPaperForm.maxAttempts" :min="1" :max="10"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="题型分值设置">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="单选题分值">
                  <el-input-number v-model="examPaperForm.singleChoiceScore" :min="1" :max="10"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="多选题分值">
                  <el-input-number v-model="examPaperForm.multipleChoiceScore" :min="1" :max="10"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="判断题分值">
                  <el-input-number v-model="examPaperForm.trueFalseScore" :min="1" :max="10"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top: 5px">
              <el-col :span="10">
                <el-form-item label="填空题分值">
                  <el-input-number v-model="examPaperForm.fillBlankScore" :min="1" :max="10"/>
                </el-form-item>
              </el-col>
              <el-col :span="10">
                <el-form-item label="简答题分值">
                  <el-input-number v-model="examPaperForm.shortAnswerScore" :min="1" :max="10"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="可见角色" prop="visibleRoleIds">
            <el-select
                v-model="examPaperForm.visibleRoleIds"
                multiple
                placeholder="请选择可见角色分类"
                style="width: 100%"
            >
              <el-option
                  v-for="role in roleCategories"
                  :key="role.id"
                  :label="role.name"
                  :value="role.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitExamPaper">确定</el-button>
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
import {getCategoryName, getCategoryTagType} from '@/utils/examCategory'

export default {
  name: 'ExamPaperManagement',
  components: {
    Plus,
    Search
  },
  setup() {
    const router = useRouter()

    const examPapers = ref([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const examPaperFormRef = ref(null)
    const searchKeyword = ref('')
    const statusFilter = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)
    const roleCategories = ref([])

    const examPaperForm = reactive({
      id: null,
      title: '',
      description: '',
      totalScore: 100,
      passScore: 60,
      duration: 120,
      totalQuestions: 0,
      isOnline: true,
      examCategory: 'GENERAL',
      allowRetake: true,
      maxAttempts: 3,
      singleChoiceScore: 2,
      multipleChoiceScore: 4,
      trueFalseScore: 2,
      fillBlankScore: 3,
      shortAnswerScore: 5,
      visibleRoleIds: []
    })

    const rules = {
      title: [
        {required: true, message: '请输入试卷名称', trigger: 'blur'}
      ],
      description: [
        {required: true, message: '请输入试卷描述', trigger: 'blur'}
      ],
      totalScore: [
        {required: true, message: '请输入总分', trigger: 'blur'}
      ],
      passScore: [
        {required: true, message: '请输入及格分', trigger: 'blur'}
      ],
      duration: [
        {required: true, message: '请输入考试时长', trigger: 'blur'}
      ],
      visibleRoleIds: [
        {type: 'array', required: true, message: '请选择可见用户角色', trigger: 'change'}
      ]
    }

    const loadExamPapers = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value,
          isOnline: statusFilter.value === '' ? null : statusFilter.value === 'true'
        }
        const response = await api.get('/exam-papers/page', {params})
        if (response.data.success) {
          examPapers.value = response.data.data.content
          total.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载试卷失败')
      } finally {
        loading.value = false
      }
    }

    const showCreateDialog = () => {
      isEdit.value = false
      Object.assign(examPaperForm, {
        id: null,
        title: '',
        description: '',
        totalScore: 100,
        passScore: 60,
        duration: 120,
        totalQuestions: 0,
        isOnline: true,
        visibleRoleIds: []
      })
      dialogVisible.value = true
    }

    const editExamPaper = (paper) => {
      isEdit.value = true
      Object.assign(examPaperForm, {
        id: paper.id,
        title: paper.title,
        description: paper.description,
        totalScore: paper.totalScore,
        passScore: paper.passScore,
        duration: paper.duration,
        totalQuestions: paper.totalQuestions || 0,
        isOnline: paper.isOnline,
        examCategory: paper.examCategory || 'GENERAL',
        allowRetake: paper.allowRetake !== undefined ? paper.allowRetake : true,
        maxAttempts: paper.maxAttempts || 3,
        singleChoiceScore: paper.singleChoiceScore || 2,
        multipleChoiceScore: paper.multipleChoiceScore || 4,
        trueFalseScore: paper.trueFalseScore || 2,
        fillBlankScore: paper.fillBlankScore || 3,
        shortAnswerScore: paper.shortAnswerScore || 5,
        visibleRoleIds: paper.visibleRoles ? paper.visibleRoles.map(role => role.id) : []
      })
      dialogVisible.value = true
    }

    const submitExamPaper = async () => {
      try {
        await examPaperFormRef.value.validate()

        if (isEdit.value) {
          await api.put(`/exam-papers/${examPaperForm.id}`, examPaperForm)
          ElMessage.success('更新成功')
        } else {
          await api.post('/exam-papers', examPaperForm)
          ElMessage.success('创建成功')
        }

        dialogVisible.value = false
        loadExamPapers()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const deleteExamPaper = async (paper) => {
      try {
        await ElMessageBox.confirm('确定要删除这个试卷吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/exam-papers/${paper.id}`)
        ElMessage.success('删除成功')
        loadExamPapers()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const viewExamPaper = (paper) => {
      router.push(`/admin/exam-papers/${paper.id}`)
    }

    const manageQuestions = (paper) => {
      router.push(`/admin/exam-papers/${paper.id}/questions`)
    }

    const autoGenerate = (paper) => {
      router.push(`/admin/exam-papers/${paper.id}/auto-generate`)
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadExamPapers()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadExamPapers()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadExamPapers()
    }

    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    onMounted(() => {
      loadExamPapers()
      loadRoleCategories()
    })

    const loadRoleCategories = async () => {
      try {
        const response = await api.get('/categories/roles')
        roleCategories.value = response.data.data
      } catch (error) {
        console.error('Failed to load role categories:', error)
        ElMessage.error('获取角色分类失败')
      }
    }

    return {
      examPapers,
      loading,
      dialogVisible,
      isEdit,
      examPaperForm,
      examPaperFormRef,
      rules,
      loadExamPapers,
      showCreateDialog,
      editExamPaper,
      submitExamPaper,
      deleteExamPaper,
      viewExamPaper,
      manageQuestions,
      autoGenerate,
      searchKeyword,
      statusFilter,
      currentPage,
      pageSize,
      total,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      formatDateTime,
      getCategoryName,
      getCategoryTagType
    }
  }
}
</script>

<style scoped>
</style>
