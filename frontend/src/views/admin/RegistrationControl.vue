<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">注册信息控制</h3>
      <div class="page-actions">
        <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-body">
        <el-table :data="rows" style="width: 100%">
          <el-table-column prop="label" label="字段" min-width="140" />
          <el-table-column label="是否必填" width="140">
            <template #default="scope">
              <el-switch v-model="scope.row.required" />
            </template>
          </el-table-column>
          <el-table-column label="是否可修改" width="160">
            <template #default="scope">
              <el-switch v-model="scope.row.editable" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

const FIELD_DEFS = [
  { key: 'username', label: '账号' },
  { key: 'password', label: '密码' },
  { key: 'realName', label: '真实姓名' },
  { key: 'gender', label: '性别' },
  { key: 'idCard', label: '身份证' },
  { key: 'phone', label: '手机号' },
  { key: 'workUnit', label: '工作单位' },
  { key: 'roleCategory', label: '角色类别' },
  { key: 'jobCategory', label: '岗位类别' },
  { key: 'facePhotoUrl', label: '人脸照片' },
  { key: 'paymentAmount', label: '缴费金额' }
]

export default {
  name: 'RegistrationControl',
  setup() {
    const configMap = reactive({})
    const rows = ref([])
    const saving = ref(false)

    const load = async () => {
      const resp = await api.get('/registration-config')
      if (resp.data.success) {
        const json = resp.data.data.fieldsConfigJson
        try {
          const obj = JSON.parse(json)
          FIELD_DEFS.forEach(f => {
            configMap[f.key] = { required: !!obj?.[f.key]?.required, editable: !!obj?.[f.key]?.editable }
          })
        } catch {
          FIELD_DEFS.forEach(f => { configMap[f.key] = { required: false, editable: true } })
        }
        rows.value = FIELD_DEFS.map(f => ({ key: f.key, label: f.label, ...configMap[f.key] }))
      }
    }

    const saveConfig = async () => {
      saving.value = true
      try {
        // 将 rows 的当前选择序列化为 key->config 的对象
        const out = {}
        rows.value.forEach(r => {
          out[r.key] = { required: !!r.required, editable: !!r.editable }
        })
        const payload = { fieldsConfigJson: JSON.stringify(out) }
        const resp = await api.put('/registration-config', payload)
        if (resp.data.success) {
          ElMessage.success('保存成功')
        } else {
          ElMessage.error(resp.data.message || '保存失败')
        }
      } finally {
        saving.value = false
      }
    }

    onMounted(load)

    return { rows, saving, saveConfig }
  }
}
</script>

<style scoped>
</style>
