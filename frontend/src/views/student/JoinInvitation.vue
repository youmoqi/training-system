<template>
  <div class="join-invitation-container">
    <div class="join-box">
      <div class="join-header">
        <h2>使用邀请链接加入课程</h2>
        <p>请输入有效的邀请码和密码以加入新课程</p>
      </div>

      <el-form :model="formData" :rules="formRules" ref="joinForm" label-position="top" @submit.prevent="handleJoin">
        <el-form-item label="邀请码" prop="linkCode">
          <el-input v-model="formData.linkCode" placeholder="请输入邀请码" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleJoin" :loading="loading" class="join-button">立即加入</el-button>
        </el-form-item>
      </el-form>

      <div class="back-link">
        <router-link to="/dashboard/courses">返回我的课程</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '../../api';

const router = useRouter();
const joinForm = ref(null);
const loading = ref(false);

const formData = reactive({
  linkCode: '',
  password: '',
});

const formRules = {
  linkCode: [{ required: true, message: '邀请码不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
};

const handleJoin = async () => {
  if (!joinForm.value) return;
  await joinForm.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await api.post('/invitations/join', formData);
        ElMessage.success('成功加入课程！');
        router.push('/dashboard/courses');
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '加入失败，请检查您的输入');
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.join-invitation-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  min-height: 100vh;
  background-color: #f0f2f5;
}
.join-box {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.join-header {
  text-align: center;
  margin-bottom: 30px;
}
.join-header h2 {
  margin: 0;
  font-size: 24px;
}
.join-header p {
  color: #666;
}
.join-button {
  width: 100%;
  font-size: 16px;
  padding: 20px;
}
.back-link {
  text-align: center;
  margin-top: 20px;
}
.back-link a {
  color: #409eff;
  text-decoration: none;
}
</style>
