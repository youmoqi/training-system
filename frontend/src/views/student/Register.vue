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
          status-icon
          :show-message="true"
          :hide-required-asterisk="false"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username" :required="isRequired('username')">
              <el-input v-model="formData.username" :disabled="!isEditable('username')" placeholder="请输入用户名"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" :required="isRequired('password')">
              <el-input
                  v-model="formData.password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  :disabled="!isEditable('password')"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName" :required="isRequired('realName')">
              <el-input v-model="formData.realName" :disabled="!isEditable('realName')" placeholder="请输入真实姓名"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender" :required="isRequired('gender')">
              <el-radio-group v-model="formData.gender" :disabled="!isEditable('gender')">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号码" prop="idCard" :required="isRequired('idCard')">
              <el-input v-model="formData.idCard" :disabled="!isEditable('idCard')" placeholder="请输入身份证号码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone" :required="isRequired('phone')">
              <el-input v-model="formData.phone" :disabled="!isEditable('phone')" placeholder="请输入手机号"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="工作单位" prop="workUnit" :required="isRequired('workUnit')">
          <el-input v-model="formData.workUnit" :disabled="!isEditable('workUnit')" placeholder="请输入工作单位"/>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="培训类型" prop="trainingType" :required="isRequired('trainingType')">
              <el-select v-model="formData.trainingType" :disabled="!isEditable('trainingType')" placeholder="请选择培训类型" style="width: 100%">
                <el-option
                    v-for="option in trainingOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作业类别" prop="jobCategory" :required="isRequired('jobCategory')">
              <el-select v-model="formData.jobCategory" :disabled="!isEditable('jobCategory')" placeholder="请选择作业类别" style="width: 100%">
                <el-option
                    v-for="option in jobOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="缴费额度" prop="paymentAmount" :required="isRequired('paymentAmount')">
          <el-input-number
              v-model="formData.paymentAmount"
              :min="0"
              :precision="2"
              style="width: 100%"
              placeholder="请输入缴费额度"
              :disabled="!isEditable('paymentAmount')"
          />
        </el-form-item>

        <el-form-item label="人脸照片" prop="facePhoto" :required="isRequired('facePhotoUrl')">
          <el-upload
              ref="upload"
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              accept="image/*"
              :disabled="!isEditable('facePhotoUrl')"
          >
            <el-button type="primary" :disabled="!isEditable('facePhotoUrl')">选择照片</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
            </template>
          </el-upload>
          <div v-if="formData.facePhoto" class="preview-image">
            <img :src="previewUrl" alt="预览"/>
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
import {ref, reactive, onMounted, nextTick} from 'vue'
import {useStore} from 'vuex'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import api from '@/api'

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

    const jobOptions = ref([])
    const trainingOptions = ref([])
    // Store selected training type to filter job categories
    const selectedTrainingType = ref('')

    const fieldConfig = reactive({})

    const baseRules = {
      username: [
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      idCard: [
        {
          pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
          message: '身份证号码格式不正确',
          trigger: 'blur'
        }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
      ]
    }

    const rules = reactive({})

    const buildRulesFromConfig = () => {
      const r = {}
      const apply = (key, label, extra = []) => {
        const arr = [...extra]
        if (isRequired(key)) {
          arr.unshift({ required: true, message: `请输入${label}`, trigger: key === 'gender' || key === 'trainingType' || key === 'jobCategory' ? 'change' : 'blur' })
        }
        r[key] = arr
      }
      apply('username', '用户名', baseRules.username)
      apply('password', '密码', baseRules.password)
      apply('realName', '真实姓名')
      apply('gender', '性别')
      apply('idCard', '身份证号码', baseRules.idCard)
      apply('phone', '手机号', baseRules.phone)
      apply('workUnit', '工作单位')
      apply('trainingType', '培训类型')
      apply('jobCategory', '作业类别')
      apply('paymentAmount', '缴费额度')
      r.facePhoto = isRequired('facePhotoUrl') ? [{ required: true, message: '请上传人脸照片', trigger: 'change' }] : []
      Object.assign(rules, r)
    }

    const isRequired = (key) => {
      return !!fieldConfig?.[key]?.required
    }

    const isEditable = (key) => {
      return fieldConfig?.[key]?.editable !== false
    }

    const loadConfig = async () => {
      try {
        const [cfgResp, jobsResp, trainingResp] = await Promise.all([
          api.get('/registration-config'),
          api.get('/categories/jobs'),
          api.get('/categories/roles')
        ])
        if (cfgResp.data.success) {
          const cfgJson = cfgResp.data.data.fieldsConfigJson
          const obj = JSON.parse(cfgJson || '{}')
          ;['username','password','realName','gender','idCard','phone','workUnit','trainingType','jobCategory','paymentAmount','facePhotoUrl']
            .forEach(k => {
              fieldConfig[k] = { required: !!obj?.[k]?.required, editable: obj?.[k]?.editable !== false }
            })
        }
        if (jobsResp.data.success) {
          jobOptions.value = (jobsResp.data.data || []).filter(it => it.isActive).map(it => ({ label: it.name, value: it.id }))
        }
        if (trainingResp.data.success) {
          trainingOptions.value = (trainingResp.data.data || []).filter(it => it.isActive).map(it => ({ label: it.name, value: it.code }))
        }
        buildRulesFromConfig()
        await nextTick()
        registerForm.value && registerForm.value.clearValidate()
      } catch (e) {
        buildRulesFromConfig()
        await nextTick()
        registerForm.value && registerForm.value.clearValidate()
      }
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

    onMounted(loadConfig)

    return {
      registerForm,
      formData,
      rules,
      loading,
      previewUrl,
      handleFileChange,
      handleRegister,
      isRequired,
      isEditable,
      jobOptions,
      trainingOptions
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: var(--spacing-lg);
}

.register-box {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: var(--spacing-xl);
  background: var(--bg-white);
  border-radius: var(--border-radius-extra-large);
  box-shadow: var(--shadow-dark);
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
