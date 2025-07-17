<template>
  <div class="course-management">
    <div class="page-header">
      <h3>课程管理</h3>
      <el-button type="primary" @click="showCreateDialog = true">
        创建课程
      </el-button>
    </div>
    
    <el-table :data="courses" style="width: 100%">
      <el-table-column prop="title" label="课程名称" />
      <el-table-column prop="description" label="课程描述" />
      <el-table-column prop="price" label="价格">
        <template #default="scope">
          ¥{{ scope.row.price }}
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
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="editCourse(scope.row)">
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="deleteCourse(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 创建/编辑课程对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingCourse ? '编辑课程' : '创建课程'"
      width="600px"
    >
      <el-form
        ref="courseForm"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        
        <el-form-item label="封面图片" prop="coverImageUrl">
          <el-input v-model="courseForm.coverImageUrl" placeholder="请输入封面图片URL" />
          <div class="image-preview" v-if="courseForm.coverImageUrl">
            <img :src="courseForm.coverImageUrl" alt="封面预览" style="max-width: 200px; max-height: 120px; margin-top: 10px;" />
          </div>
        </el-form-item>
        
        <el-form-item label="视频链接" prop="videoUrl">
          <el-input v-model="courseForm.videoUrl" placeholder="请输入视频链接" />
        </el-form-item>
        
        <el-form-item label="课程价格" prop="price">
          <el-input-number
            v-model="courseForm.price"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="请输入课程价格"
          />
        </el-form-item>
        
        <el-form-item label="上线状态" prop="isOnline">
          <el-switch v-model="courseForm.isOnline" />
        </el-form-item>
        
        <el-form-item label="可见角色" prop="visibleRoles">
          <el-checkbox-group v-model="courseForm.visibleRoles">
            <el-checkbox label="EXPLOSIVE_USER">易制爆人员</el-checkbox>
            <el-checkbox label="BLAST_USER">爆破三大员</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="saveCourse">
            {{ editingCourse ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

export default {
  name: 'CourseManagement',
  setup() {
    const courses = ref([])
    const showCreateDialog = ref(false)
    const editingCourse = ref(null)
    const courseForm = ref(null)
    
    const formData = reactive({
      title: '',
      description: '',
      coverImageUrl: '',
      videoUrl: '',
      price: 0,
      isOnline: true,
      visibleRoles: []
    })
    
    const courseRules = {
      title: [
        { required: true, message: '请输入课程名称', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入课程描述', trigger: 'blur' }
      ],
      coverImageUrl: [
        { required: true, message: '请输入封面图片URL', trigger: 'blur' }
      ],
      videoUrl: [
        { required: true, message: '请输入视频链接', trigger: 'blur' }
      ],
      price: [
        { required: true, message: '请输入课程价格', trigger: 'blur' }
      ]
    }
    
    const loadCourses = async () => {
      try {
        const response = await api.get('/courses')
        courses.value = response.data.data
      } catch (error) {
        ElMessage.error('加载课程失败')
      }
    }
    
    const editCourse = (course) => {
      editingCourse.value = course
      Object.assign(formData, course)
      showCreateDialog.value = true
    }
    
    const deleteCourse = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个课程吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await api.delete(`/courses/${id}`)
        ElMessage.success('删除成功')
        loadCourses()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    const saveCourse = async () => {
      try {
        await courseForm.value.validate()
        
        if (editingCourse.value) {
          await api.put(`/courses/${editingCourse.value.id}`, formData)
          ElMessage.success('更新成功')
        } else {
          await api.post('/courses', formData)
          ElMessage.success('创建成功')
        }
        
        showCreateDialog.value = false
        resetForm()
        loadCourses()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
    
    const resetForm = () => {
      editingCourse.value = null
      Object.assign(formData, {
        title: '',
        description: '',
        coverImageUrl: '',
        videoUrl: '',
        price: 0,
        isOnline: true,
        visibleRoles: []
      })
    }
    
    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleString('zh-CN')
    }
    
    onMounted(() => {
      loadCourses()
    })
    
    return {
      courses,
      showCreateDialog,
      editingCourse,
      courseForm,
      courseForm: formData,
      courseRules,
      editCourse,
      deleteCourse,
      saveCourse,
      formatDate
    }
  }
}
</script>

<style scoped>
.course-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  color: #333;
}
</style> 