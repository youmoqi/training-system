<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>请填写您的个人信息</p>
      </div>
      
      <el-form
        ref="registerForm"
        :model="formData"
        :rules="rules"
        class="register-form"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="formData.password"
                type="password"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号码" prop="idCard">
              <el-input v-model="formData.idCard" placeholder="请输入身份证号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="工作单位" prop="workUnit">
          <el-input v-model="formData.workUnit" placeholder="请输入工作单位" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="培训类型" prop="trainingType">
              <el-select v-model="formData.trainingType" placeholder="请选择培训类型" style="width: 100%">
                <el-option label="易制爆" value="易制爆" />
                <el-option label="爆破作业" value="爆破作业" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作业类别" prop="jobCategory">
              <el-select v-model="formData.jobCategory" placeholder="请选择作业类别" style="width: 100%">
                <el-option label="爆破" value="爆破" />
                <el-option label="拆除" value="拆除" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="缴费额度" prop="paymentAmount">
          <el-input-number
            v-model="formData.paymentAmount"
            :min="0"
            :precision="2"
            style="width: 100%"
            placeholder="请输入缴费额度"
          />
        </el-form-item>
        
        <el-form-item label="人脸照片" prop="facePhoto">
          <el-upload
            ref="upload"
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="false"
            accept="image/*"
          >
            <el-button type="primary">选择照片</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
            </template>
          </el-upload>
          <div v-if="formData.facePhoto" class="preview-image">
            <img :src="previewUrl" alt="预览" />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
          <el-button size="large" @click="$router.push('/login')">
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Register',
  setup() {
    const store = useStore()
    const router = useRouter()
    const registerForm = ref(null)
    const loading = ref(false)
    const previewUrl = ref('')
    
    const formData = reactive({
      username: '',
      password: '',
      realName: '',
      gender: '',
      idCard: '',
      phone: '',
      workUnit: '',
      trainingType: '',
      jobCategory: '',
      paymentAmount: 0,
      facePhoto: null
    })
    
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ],
      idCard: [
        { required: true, message: '请输入身份证号码', trigger: 'blur' },
        { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '身份证号码格式不正确', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
      ],
      workUnit: [
        { required: true, message: '请输入工作单位', trigger: 'blur' }
      ],
      trainingType: [
        { required: true, message: '请选择培训类型', trigger: 'change' }
      ],
      jobCategory: [
        { required: true, message: '请选择作业类别', trigger: 'change' }
      ],
      paymentAmount: [
        { required: true, message: '请输入缴费额度', trigger: 'blur' }
      ],
      facePhoto: [
        { required: true, message: '请上传人脸照片', trigger: 'change' }
      ]
    }
    
    const handleFileChange = (file) => {
      if (file.size > 2 * 1024 * 1024) {
        ElMessage.error('文件大小不能超过2MB')
        return
      }
      
      formData.facePhoto = file.raw
      previewUrl.value = URL.createObjectURL(file.raw)
    }
    
    const handleRegister = async () => {
      try {
        await registerForm.value.validate()
        loading.value = true
        
        await store.dispatch('register', formData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
    
    return {
      registerForm,
      formData,
      rules,
      loading,
      previewUrl,
      handleFileChange,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.register-button {
  margin-right: 10px;
}

.preview-image {
  margin-top: 10px;
}

.preview-image img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 5px;
  border: 1px solid #ddd;
}
</style> 