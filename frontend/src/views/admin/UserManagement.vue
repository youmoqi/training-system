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
        <div class="table-container">
          <el-table :data="users" style="width: 100%" v-loading="loading">
            <el-table-column prop="username" label="用户名" min-width="120"/>
            <el-table-column prop="realName" label="真实姓名" min-width="120"/>
            <el-table-column prop="phone" label="手机号" min-width="120"/>
            <el-table-column prop="role" label="角色" width="120">
              <template #default="scope">
                <el-tag :type="getRoleTagType(scope.row.role)">
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="viewUser(scope.row)">查看</el-button>
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
    <el-dialog v-model="showUserDialog" title="用户详情" width="600px" class="dialog-container">
      <div class="dialog-body" v-if="selectedUser">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ selectedUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="getRoleTagType(selectedUser.role)">
              {{ getRoleText(selectedUser.role) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDate(selectedUser.createTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showUserDialog = false">关闭</el-button>
        </div>
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
      // 实现添加用户功能
      ElMessage.info('添加用户功能待实现')
    }

    const viewUser = (user) => {
      selectedUser.value = user
      showUserDialog.value = true
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

    const getRoleText = (role) => {
      const roleMap = {
        'SUPER_ADMIN': '超级管理员',
        'ADMIN': '管理员',
        'EXPLOSIVE_USER': '易制爆人员',
        'BLAST_USER': '爆破三大员'
      }
      return roleMap[role] || '未知角色'
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
      loadUsers()
    })

    return {
      users,
      showUserDialog,
      selectedUser,
      loading,
      currentPage,
      pageSize,
      total,
      searchKeyword,
      viewUser,
      deleteUser,
      getRoleText,
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
