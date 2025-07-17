<template>
  <div class="invitation-management">
    <div class="page-header">
      <h3>邀请链接管理</h3>
      <el-button type="primary" @click="showCreateDialog = true">创建新链接</el-button>
    </div>

    <!-- Invitation Links Table -->
    <el-table :data="invitations" style="width: 100%" v-loading="loading">
      <el-table-column prop="title" label="标题" width="180" />
      <el-table-column prop="linkCode" label="邀请码" width="150" />
      <el-table-column prop="password" label="密码" width="120" />
      <el-table-column label="关联课程" width="250">
        <template #default="scope">
          <el-tag v-for="course in scope.row.courses" :key="course.id" style="margin-right: 5px;">
            {{ course.title }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" width="180">
        <template #default="scope">{{ formatDate(scope.row.expireTime) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
            {{ scope.row.isActive ? '有效' : '失效' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="danger" @click="deleteInvitation(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create Invitation Link Dialog -->
    <el-dialog v-model="showCreateDialog" title="创建邀请链接" width="600px" @close="resetForm">
      <el-form :model="formData" :rules="formRules" ref="invitationForm" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入链接标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入链接描述" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" placeholder="请输入链接密码" />
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker v-model="formData.expireTime" type="datetime" placeholder="选择过期时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="包含课程" prop="courseIds">
          <el-select v-model="formData.courseIds" multiple placeholder="请选择课程" style="width: 100%;">
            <el-option v-for="course in availableCourses" :key="course.id" :label="course.title" :value="course.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createInvitation">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../../api';

const loading = ref(true);
const invitations = ref([]);
const availableCourses = ref([]);
const showCreateDialog = ref(false);
const invitationForm = ref(null);

const initialFormData = {
  title: '',
  description: '',
  password: '',
  expireTime: '',
  courseIds: [],
};

const formData = reactive({ ...initialFormData });

const formRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  expireTime: [{ required: true, message: '请选择过期时间', trigger: 'change' }],
  courseIds: [{ required: true, message: '请至少选择一门课程', trigger: 'change' }],
};

const formatDate = (dateString) => new Date(dateString).toLocaleString('zh-CN');

const loadInvitations = async () => {
  loading.value = true;
  try {
    const response = await api.get('/invitations');
    invitations.value = response.data.data;
  } catch (error) {
    ElMessage.error('加载邀请链接列表失败');
  } finally {
    loading.value = false;
  }
};

const loadAvailableCourses = async () => {
  try {
    const response = await api.get('/courses/online');
    availableCourses.value = response.data.data;
  } catch (error) {
    ElMessage.error('加载课程列表失败');
  }
};

const resetForm = () => {
  Object.assign(formData, initialFormData);
  if (invitationForm.value) {
    invitationForm.value.clearValidate();
  }
};

const createInvitation = async () => {
  if (!invitationForm.value) return;
  await invitationForm.value.validate(async (valid) => {
    if (valid) {
      try {
        await api.post('/invitations', formData);
        ElMessage.success('创建成功');
        showCreateDialog.value = false;
        loadInvitations();
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '创建失败');
      }
    }
  });
};

const deleteInvitation = async (id) => {
  try {
    await ElMessageBox.confirm('确定要永久删除此邀请链接吗？', '警告', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
    });
    await api.delete(`/invitations/${id}`);
    ElMessage.success('删除成功');
    loadInvitations();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败');
    }
  }
};

onMounted(() => {
  loadInvitations();
  loadAvailableCourses();
});
</script>

<style scoped>
.invitation-management {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 