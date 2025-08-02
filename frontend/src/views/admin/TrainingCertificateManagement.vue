<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">培训证明管理</h3>
      <div class="page-actions">
        <el-button type="primary" @click="showGenerateDialogHandler">
          <el-icon>
            <Plus/>
          </el-icon>
          生成证书
        </el-button>
        <el-button type="success" @click="downloadBatchCertificates" :disabled="selectedCertificates.length === 0">
          <el-icon>
            <Download/>
          </el-icon>
          批量下载
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 筛选栏 -->
        <div class="filter-section">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-select v-model="filterType" placeholder="证书类型" clearable @change="handleFilter">
                <el-option label="易制爆人员" value="EXPLOSIVE_USER"/>
                <el-option label="爆破三大员" value="BLAST_USER"/>
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="filterPayment" placeholder="收费类型" clearable @change="handleFilter">
                <el-option label="收费" :value="true"/>
                <el-option label="免费" :value="false"/>
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-input
                  v-model="searchKeyword"
                  placeholder="搜索证书编号或用户名..."
                  clearable
                  @input="handleSearch"
              >
                <template #prefix>
                  <el-icon>
                    <Search/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="loadCertificates">查询</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 证书列表 -->
        <div class="table-container">
          <el-table
            :data="certificates"
            style="width: 100%"
            v-loading="loading"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column prop="certificateNumber" label="证书编号" min-width="180"/>
            <el-table-column prop="realName" label="姓名" min-width="100"/>
            <el-table-column prop="username" label="用户名" min-width="120"/>
            <el-table-column prop="courseTitle" label="课程名称" min-width="150"/>
            <el-table-column prop="certificateType" label="证书类型" width="120">
              <template #default="scope">
                <el-tag :type="getCertificateTypeTagType(scope.row.certificateType)">
                  {{ getCertificateTypeText(scope.row.certificateType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isPaid" label="收费类型" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isPaid ? 'danger' : 'success'">
                  {{ scope.row.isPaid ? '收费' : '免费' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="completeDate" label="完成时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.completeDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="issueDate" label="颁发时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.issueDate) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewCertificate(scope.row)">查看</el-button>
                <el-button size="small" type="success" @click="downloadCertificate(scope.row.id)">
                  下载
                </el-button>
                <el-button size="small" type="danger" @click="deleteCertificate(scope.row.id)">
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
      </div>
    </div>

    <!-- 生成证书对话框 -->
    <el-dialog v-model="showGenerateDialog" title="生成培训证明" width="500px" class="dialog-container">
      <el-form :model="generateForm" :rules="generateRules" ref="generateFormRef" label-width="100px">
        <el-form-item label="用户" prop="userId">
          <el-select v-model="generateForm.userId" placeholder="选择用户" filterable>
            <el-option
                v-for="user in users"
                :key="user.id"
                :label="`${user.realName} (${user.username})`"
                :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="generateForm.courseId" placeholder="选择课程" filterable>
            <el-option
                v-for="course in courses"
                :key="course.id"
                :label="course.title"
                :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收费类型" prop="isPaid">
          <el-radio-group v-model="generateForm.isPaid">
            <el-radio :label="true">收费</el-radio>
            <el-radio :label="false">免费</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showGenerateDialog = false">取消</el-button>
          <el-button type="primary" @click="generateCertificate" :loading="generating">
            生成证书
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 证书详情对话框 -->
    <el-dialog v-model="showCertificateDialog" title="证书详情" width="600px" class="dialog-container">
      <div class="dialog-body" v-if="selectedCertificate">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="证书编号">{{ selectedCertificate.certificateNumber }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ selectedCertificate.realName }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ selectedCertificate.username }}</el-descriptions-item>
          <el-descriptions-item label="课程名称">{{ selectedCertificate.courseTitle }}</el-descriptions-item>
          <el-descriptions-item label="证书类型">
            <el-tag :type="getCertificateTypeTagType(selectedCertificate.certificateType)">
              {{ getCertificateTypeText(selectedCertificate.certificateType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收费类型">
            <el-tag :type="selectedCertificate.isPaid ? 'danger' : 'success'">
              {{ selectedCertificate.isPaid ? '收费' : '免费' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ formatDate(selectedCertificate.completeDate) }}</el-descriptions-item>
          <el-descriptions-item label="颁发时间">{{ formatDate(selectedCertificate.issueDate) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCertificateDialog = false">关闭</el-button>
          <el-button type="success" @click="downloadCertificate(selectedCertificate.id)">
            下载证书
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Search } from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'TrainingCertificateManagement',
  components: {
    Plus,
    Download,
    Search
  },
  setup() {
    const loading = ref(false)
    const generating = ref(false)
    const certificates = ref([])
    const users = ref([])
    const courses = ref([])
    const selectedCertificates = ref([])
    const selectedCertificate = ref(null)

    // 分页
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    // 筛选
    const filterType = ref('')
    const filterPayment = ref('')
    const searchKeyword = ref('')

    // 对话框
    const showGenerateDialog = ref(false)
    const showCertificateDialog = ref(false)

    // 生成证书表单
    const generateFormRef = ref()
    const generateForm = reactive({
      userId: '',
      courseId: '',
      isPaid: true
    })

    const generateRules = {
      userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
      courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
      isPaid: [{ required: true, message: '请选择收费类型', trigger: 'change' }]
    }

    // 加载证书列表
    const loadCertificates = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value
        }

        // 添加筛选参数
        if (filterType.value) {
          params.certificateType = filterType.value
        }

        if (filterPayment.value !== '') {
          params.isPaid = filterPayment.value
        }

        if (searchKeyword.value) {
          params.searchKeyword = searchKeyword.value
        }

        const response = await api.get('/certificates', { params })
        const data = response.data.data

        certificates.value = data.certificates || []
        total.value = data.total || 0
      } catch (error) {
        ElMessage.error('加载证书列表失败')
        console.error(error)
      } finally {
        loading.value = false
      }
    }

    // 加载用户列表
    const loadUsers = async () => {
      try {
        const response = await api.get('/users')
        users.value = response.data.data || []
      } catch (error) {
        console.error('加载用户列表失败:', error)
      }
    }

    // 加载课程列表
    const loadCourses = async () => {
      try {
        const response = await api.get('/courses')
        courses.value = response.data.data || []
      } catch (error) {
        console.error('加载课程列表失败:', error)
      }
    }

    // 生成证书
    const generateCertificate = async () => {
      try {
        await generateFormRef.value.validate()
        generating.value = true

        const response = await api.post('/certificates/generate', null, {
          params: {
            userId: generateForm.userId,
            courseId: generateForm.courseId,
            isPaid: generateForm.isPaid
          }
        })

        ElMessage.success('证书生成成功')
        showGenerateDialog.value = false
        loadCertificates()

        // 重置表单
        generateForm.userId = ''
        generateForm.courseId = ''
        generateForm.isPaid = true
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '生成证书失败')
      } finally {
        generating.value = false
      }
    }

    // 下载单个证书
    const downloadCertificate = async (certificateId) => {
      try {
        const response = await api.get(`/certificates/download/${certificateId}`, {
          responseType: 'blob'
        })

        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `certificate_${certificateId}.pdf`)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        ElMessage.success('证书下载成功')
      } catch (error) {
        ElMessage.error('证书下载失败')
      }
    }

    // 批量下载证书
    const downloadBatchCertificates = async () => {
      if (selectedCertificates.value.length === 0) {
        ElMessage.warning('请选择要下载的证书')
        return
      }

      try {
        const certificateIds = selectedCertificates.value.map(cert => cert.id)
        const response = await api.post('/certificates/download/batch', certificateIds, {
          responseType: 'blob'
        })

        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', 'certificates.pdf')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        ElMessage.success('批量下载成功')
      } catch (error) {
        ElMessage.error('批量下载失败')
      }
    }

    // 删除证书
    const deleteCertificate = async (certificateId) => {
      try {
        await ElMessageBox.confirm('确定要删除这个证书吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/certificates/${certificateId}`)
        ElMessage.success('证书删除成功')
        loadCertificates()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('证书删除失败')
        }
      }
    }

    // 查看证书详情
    const viewCertificate = (certificate) => {
      selectedCertificate.value = certificate
      showCertificateDialog.value = true
    }

    // 显示生成证书对话框
    const showGenerateDialogHandler = () => {
      showGenerateDialog.value = true
    }

    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedCertificates.value = selection
    }

    // 处理筛选
    const handleFilter = () => {
      currentPage.value = 1
      loadCertificates()
    }

    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 1
      loadCertificates()
    }

    // 重置筛选
    const resetFilter = () => {
      filterType.value = ''
      filterPayment.value = ''
      searchKeyword.value = ''
      currentPage.value = 1
      loadCertificates()
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadCertificates()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadCertificates()
    }

    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    }

    // 获取证书类型标签类型
    const getCertificateTypeTagType = (type) => {
      switch (type) {
        case 'EXPLOSIVE_USER':
          return 'warning'
        case 'BLAST_USER':
          return 'info'
        default:
          return 'default'
      }
    }

    // 获取证书类型文本
    const getCertificateTypeText = (type) => {
      switch (type) {
        case 'EXPLOSIVE_USER':
          return '易制爆人员'
        case 'BLAST_USER':
          return '爆破三大员'
        default:
          return type
      }
    }

    onMounted(() => {
      loadCertificates()
      loadUsers()
      loadCourses()
    })

    return {
      loading,
      generating,
      certificates,
      users,
      courses,
      selectedCertificates,
      selectedCertificate,
      currentPage,
      pageSize,
      total,
      filterType,
      filterPayment,
      searchKeyword,
      showGenerateDialog,
      showCertificateDialog,
      generateFormRef,
      generateForm,
      generateRules,
      loadCertificates,
      generateCertificate,
      downloadCertificate,
      downloadBatchCertificates,
      deleteCertificate,
      viewCertificate,
      showGenerateDialogHandler,
      handleSelectionChange,
      handleFilter,
      handleSearch,
      resetFilter,
      handleSizeChange,
      handleCurrentChange,
      formatDate,
      getCertificateTypeTagType,
      getCertificateTypeText
    }
  }
}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 20px;
}

.filter-section {
  margin-bottom: 20px;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-container {
  border-radius: 8px;
}

.dialog-body {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>
