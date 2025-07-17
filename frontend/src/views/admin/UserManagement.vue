<template>
  <div class="user-management">
    <div class="page-header">
      <h3>用户管理</h3>
    </div>
    
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="workUnit" label="工作单位" />
      <el-table-column prop="trainingType" label="培训类型" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">
            {{ getRoleText(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentAmount" label="缴费额度">
        <template #default="scope">
          ¥{{ scope.row.paymentAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="viewUser(scope.row)">
            查看
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="deleteUser(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="showUserDialog"
      title="用户详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">
          {{ selectedUser?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名">
          {{ selectedUser?.realName }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ selectedUser?.gender }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证号码">
          {{ selectedUser?.idCard }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ selectedUser?.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="工作单位">
          {{ selectedUser?.workUnit }}
        </el-descriptions-item>
        <el-descriptions-item label="培训类型">
          {{ selectedUser?.trainingType }}
        </el-descriptions-item>
        <el-descriptions-item label="作业类别">
          {{ selectedUser?.jobCategory }}
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          {{ getRoleText(selectedUser?.role) }}
        </el-descriptions-item>
        <el-descriptions-item label="缴费额度">
          ¥{{ selectedUser?.paymentAmount }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatDate(selectedUser?.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div class="user-avatar" style="text-align: center; margin-top: 20px;">
        <el-avatar
          :src="selectedUser?.facePhotoUrl"
          :size="100"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([])
    const showUserDialog = ref(false)
    const selectedUser = ref(null)
    
    const loadUsers = async () => {
      try {
        const response = await api.get('/users')
        users.value = response.data.data
      } catch (error) {
        ElMessage.error('加载用户失败')
      }
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
      viewUser,
      deleteUser,
      getRoleText,
      getRoleTagType,
      formatDate
    }
  }
}
</script>

<style scoped>
.user-management {
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