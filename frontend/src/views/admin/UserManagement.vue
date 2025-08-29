<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">用户管理</h3>
      <div class="page-actions">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon>
            <Plus/>
          </el-icon>
          添加用户
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索用户..."
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

        <!-- 用户列表 -->
        <div class="table-container" style="overflow-x: auto;">
          <el-table :data="users" style="min-width: 900px" v-loading="loading">
            <el-table-column prop="username" label="用户名" width="150"/>
            <el-table-column prop="realName" label="真实姓名" width="150"/>
            <el-table-column prop="phone" label="手机号" width="150"/>
            <el-table-column prop="role" label="角色" width="200">
              <template #default="scope">
                <el-tag :type="getRoleTagType(scope.row.role.code)">
                  {{ scope.row.role.name}}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewUser(scope.row)">查看</el-button>
                <el-button size="small" type="primary" @click="openPermissions(scope.row)">编辑权限</el-button>
                <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">
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

    <!-- 用户详情对话框 -->
    <el-dialog v-model="showUserDialog" title="用户详情" width="700px" class="dialog-container">
      <div class="dialog-body" v-if="selectedUser">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ selectedUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedUser.gender }}</el-descriptions-item>
          <el-descriptions-item label="身份证">{{ selectedUser.idCard }}</el-descriptions-item>
          <el-descriptions-item label="工作单位">{{ selectedUser.workUnit }}</el-descriptions-item>
          <el-descriptions-item label="培训类型">{{ selectedUser.trainingType }}</el-descriptions-item>
          <el-descriptions-item label="岗位类别">{{ selectedUser.jobCategory }}</el-descriptions-item>
          <el-descriptions-item label="人脸照片" :span="2">
            <a :href="selectedUser.facePhotoUrl" target="_blank">查看</a>
          </el-descriptions-item>
          <el-descriptions-item label="缴费金额">{{ selectedUser.paymentAmount }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleTagType(selectedUser?.role.code)">
              {{ selectedUser?.role.name }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学习权限">{{ selectedUser.canLearn ? '允许' : '禁止' }}</el-descriptions-item>
          <el-descriptions-item label="考试权限">{{ selectedUser.canExam ? '允许' : '禁止' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDate(selectedUser.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="2">
            {{ formatDate(selectedUser.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showUserDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑权限对话框 -->
    <el-dialog v-model="showPermissionDialog" title="编辑权限" width="520px">
      <div v-if="permissionForm">
        <el-form :model="permissionForm" label-width="110px">
          <el-form-item label="角色">
            <el-select v-model="permissionForm.role" placeholder="选择角色" style="width: 260px">
              <el-option v-for="role in roleCategories" :key="role.value" :label="role.label" :value="role.value"/>
            </el-select>
          </el-form-item>
          <el-form-item label="作业类别">
            <el-select v-model="permissionForm.jobCategory" placeholder="选择作业类别" style="width: 260px">
              <el-option v-for="option in jobOptions" :key="option.value" :label="option.label" :value="option.value"/>
            </el-select>
          </el-form-item>
          <el-form-item label="学习权限">
            <el-switch v-model="permissionForm.canLearn"/>
          </el-form-item>
          <el-form-item label="考试权限">
            <el-switch v-model="permissionForm.canExam"/>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showPermissionDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingPermission" @click="savePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Search} from '@element-plus/icons-vue'
import api from '@/api'

export default {
  name: 'UserManagement',
  components: {
    Plus,
    Search
  },
  setup() {
    const users = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(20)
    const total = ref(0)
    const searchKeyword = ref('')
    const showUserDialog = ref(false)
    const selectedUser = ref(null)

    const showPermissionDialog = ref(false)
    const savingPermission = ref(false)
    const permissionForm = ref(null)
    const jobOptions = ref([])
    const roleCategories = ref([])

    const loadJobCategories = async () => {
      try {
        const resp = await api.get('/categories/jobs')
        if (resp.data.success) {
          jobOptions.value = (resp.data.data || []).filter(it => it.isActive).map(it => ({
            label: it.name,
            value: it.code
          }))
        }
      } catch {
      }
    }

    const loadRoleCategories = async () => {
      try {
        const [rolesResp, jobsResp] = await Promise.all([
          api.get('/categories/roles'),
          api.get('/categories/jobs')
        ])
        
        if (rolesResp.data.success) {
          roleCategories.value = (rolesResp.data.data || []).filter(it => it.isActive).map(it => ({
            label: it.name,
            value: it.id
          }))
        }
        
        if (jobsResp.data.success) {
          jobOptions.value = jobsResp.data.data.filter(it => it.isActive).map(it => ({
            label: it.name,
            value: it.id
          }))
        }
      } catch (error) {
        console.error('加载角色分类失败', error)
      }
    }

    const loadUsers = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value - 1,
          size: pageSize.value,
          keyword: searchKeyword.value
        }
        const response = await api.get('/users/page', {params})
        if (response.data.success) {
          users.value = response.data.data.content
          total.value = response.data.data.totalElements
        }
      } catch (error) {
        ElMessage.error('加载用户失败')
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadUsers()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      loadUsers()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      loadUsers()
    }

    const showCreateDialog = () => {
      ElMessage.info('添加用户功能待实现')
    }

    const viewUser = (user) => {
      selectedUser.value = user
      showUserDialog.value = true
    }

    const openPermissions = (user) => {
      permissionForm.value = {
        id: user.id,
        role: user.visibilityCategory?.id,
        jobCategory: user.jobCategory?.id,
        canLearn: !!user.canLearn,
        canExam: !!user.canExam
      }
      showPermissionDialog.value = true
    }

    const savePermissions = async () => {
      if (!permissionForm.value) return
      savingPermission.value = true
      try {
        const {id, role, canLearn, canExam, jobCategory} = permissionForm.value
        const body = {visibilityCategoryId: role, canLearn, canExam, jobCategoryId: jobCategory}
        const resp = await api.put(`/users/${id}/permissions`, body)
        if (resp.data.success) {
          ElMessage.success('保存成功')
          showPermissionDialog.value = false
          loadUsers()
        } else {
          ElMessage.error(resp.data.message || '保存失败')
        }
      } catch (e) {
        ElMessage.error('保存失败')
      } finally {
        savingPermission.value = false
      }
    }

    const deleteUser = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await api.delete(`/users/${id}`)
        ElMessage.success('删除成功')
        loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    const getRoleTagType = (role) => {
      const typeMap = {
        'SUPER_ADMIN': 'danger',
        'ADMIN': 'warning',
        'EXPLOSIVE_USER': 'primary',
        'BLAST_USER': 'success'
      }
      return typeMap[role] || 'info'
    }

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadJobCategories()
      loadRoleCategories()
      loadUsers()
    })

    return {
      users,
      showUserDialog,
      selectedUser,
      showPermissionDialog,
      savingPermission,
      permissionForm,
      jobOptions,
      roleCategories,
      loading,
      currentPage,
      pageSize,
      total,
      searchKeyword,
      openPermissions,
      savePermissions,
      viewUser,
      deleteUser,
      getRoleTagType,
      formatDate,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      showCreateDialog
    }
  }
}
</script>

<style scoped>
</style>
