<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">课程管理</h3>
      <div class="page-actions">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon>
            <Plus/>
          </el-icon>
          创建课程
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索课程..."
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
        </div>

        <!-- 课程列表 -->
        <div class="table-container">
          <el-table :data="courses" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="课程名称" min-width="200"/>
            <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip/>
            <el-table-column prop="price" label="价格" width="120">
              <template #default="scope">
                ¥{{ scope.row.price }}
              </template>
            </el-table-column>
            <el-table-column prop="videoUrl" label="视频链接" width="200" show-overflow-tooltip/>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isOnline ? 'success' : 'info'">
                  {{ scope.row.isOnline ? '已上线' : '未上线' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewCourse(scope.row)">查看</el-button>
                <el-button size="small" type="primary" @click="editCourse(scope.row)">
                  编辑
                </el-button>
                <el-button size="small" type="warning" @click="manageChapters(scope.row)">
                  管理章节
                </el-button>
                <el-button size="small" type="danger" @click="deleteCourse(scope.row)">
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
        <el-empty v-if="!loading && courses.length === 0" description="暂无课程数据"/>
      </div>
    </div>

    <!-- 创建/编辑课程对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑课程' : '创建课程'"
        width="600px"
        class="dialog-container"
    >
      <div class="dialog-body">
        <el-form :model="courseForm" :rules="rules" ref="courseFormRef" label-width="100px">
          <el-form-item label="课程名称" prop="title">
            <el-input v-model="courseForm.title" placeholder="请输入课程名称"/>
          </el-form-item>
          <el-form-item label="课程描述" prop="description">
            <el-input
                v-model="courseForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入课程描述"
            />
          </el-form-item>
          <el-form-item label="视频链接" prop="videoUrl">
            <el-input v-model="courseForm.videoUrl" placeholder="请输入视频链接"/>
          </el-form-item>
          <el-form-item label="封面图片" prop="coverImageUrl">
            <el-input v-model="courseForm.coverImageUrl" placeholder="请输入封面图片链接"/>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="课程价格" prop="price">
                <el-input-number v-model="courseForm.price" :min="0" :max="10000"/>
                <span style="margin-left: 10px">元</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否上线" prop="isOnline">
                <el-switch v-model="courseForm.isOnline"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="可见角色" prop="visibleRoleIds">
            <el-select
                v-model="courseForm.visibleRoleIds"
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
          <el-button type="primary" @click="submitCourse">确定</el-button>
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

export default {
  name: 'CourseManagement',
  components: {
    Plus,
    Search
  },
  setup() {
    const router = useRouter()

    const courses = ref([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const courseFormRef = ref(null)
    const searchKeyword = ref('')
    const roleCategories = ref([])
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)

    const courseForm = reactive({
      id: null,
      title: '',
      description: '',
      videoUrl: '',
      isOnline: true,
      visibleRoleIds: [],
      price: 0,
      coverImageUrl: ''
    })

    const rules = {
      title: [
        {required: true, message: '请输入课程名称', trigger: 'blur'}
      ],
      description: [
        {required: true, message: '请输入课程描述', trigger: 'blur'}
      ],
      videoUrl: [
        {required: true, message: '请输入视频链接', trigger: 'blur'}
      ],
      visibleRoleIds: [
        {type: 'array', required: true, message: '请选择可见用户角色', trigger: 'change'}
      ],
      price: [
        {required: true, message: '请输入课程价格', trigger: 'blur'}
      ]
    }

    const loadCourses = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value
        }
        const response = await api.get('/courses/admin/page', {params})
        if (response.data.success) {
          courses.value = response.data.data.content
          total.value = response.data.data.totalElements
        } else {
          ElMessage.error(response.data.message || '加载课程失败')
        }
      } catch (error) {
        console.error('Load courses error:', error)
        const errorMessage = error.response?.data?.message || error.message || '加载课程失败'
        ElMessage.error(errorMessage)
      } finally {
        loading.value = false
      }
    }

    const showCreateDialog = () => {
      isEdit.value = false
      Object.assign(courseForm, {
        id: null,
        title: '',
        description: '',
        videoUrl: '',
        coverImageUrl: '',
        isOnline: true,
        visibleRoleIds: [],
        price: 0,
        createTime: null,
        updateTime: null
      })
      dialogVisible.value = true
    }

    const editCourse = (course) => {
      isEdit.value = true
      Object.assign(courseForm, {
        id: course.id,
        title: course.title,
        description: course.description,
        videoUrl: course.videoUrl,
        coverImageUrl: course.coverImageUrl,
        isOnline: course.isOnline,
        visibleRoleIds: course.visibleRoles ? course.visibleRoles.map(role => role.id) : [],
        price: course.price || 0
      })
      dialogVisible.value = true
    }

    const submitCourse = async () => {
      try {
        await courseFormRef.value.validate()

        if (isEdit.value) {
          await api.put(`/courses/${courseForm.id}`, courseForm)
          ElMessage.success('更新成功')
        } else {
          await api.post('/courses', courseForm)
          ElMessage.success('创建成功')
        }

        dialogVisible.value = false
        loadCourses()
      } catch (error) {
        console.error('Submit course error:', error)
        const errorMessage = error.response?.data?.message || error.message || '操作失败'
        ElMessage.error(errorMessage)
      }
    }

    const deleteCourse = async (course) => {
      try {
        await ElMessageBox.confirm('确定要删除这个课程吗？此操作不可逆！', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/courses/${course.id}`)
        ElMessage.success('删除成功')
        loadCourses()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Delete course error:', error)
          const errorMessage = error.response?.data?.message || error.message || '删除失败'
          ElMessage.error(errorMessage)
        }
      }
    }

    const viewCourse = (course) => {
      router.push(`/admin/courses/${course.id}`)
    }

    const manageChapters = (course) => {
      router.push(`/admin/courses/${course.id}/chapters`)
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadCourses()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadCourses()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadCourses()
    }

    const formatDateTime = (dateTime) => {
      return new Date(dateTime).toLocaleString()
    }

    onMounted(() => {
      loadCourses()
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
      courses,
      loading,
      dialogVisible,
      isEdit,
      courseForm,
      courseFormRef,
      rules,
      showCreateDialog,
      editCourse,
      submitCourse,
      deleteCourse,
      viewCourse,
      manageChapters,
      searchKeyword,
      currentPage,
      pageSize,
      total,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      formatDateTime,
      roleCategories
    }
  }
}
</script>

<style scoped>
</style>
