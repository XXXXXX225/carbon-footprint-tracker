<template>
  <div class="emission-form">
    <!-- 水资源消耗计算器 -->
    <el-card class="calculator-card">
      <template #header>
        <div class="card-header">
          <span>水资源消耗记录</span>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="用水类型" prop="waterType">
          <el-select v-model="form.waterType" placeholder="请选择用水类型">
            <el-option label="生活用水 (自来水)" value="tap_water" />
            <el-option label="饮用纯净水 (桶装/瓶装)" value="bottled_water" />
            <el-option label="热水 (洗浴等)" value="hot_water" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>

        <template v-if="form.waterType === 'custom'">
          <el-form-item label="自定义用水类型" prop="customName">
            <el-input v-model="form.customName" placeholder="例如：工业用水" />
          </el-form-item>
          <el-form-item label="排放因子" prop="customFactor">
            <el-input v-model.number="form.customFactor" placeholder="请输入碳排放因子" />
            <span class="unit">kg CO₂e / 计量单位</span>
          </el-form-item>
        </template>

        <el-form-item label="用水量" prop="amount">
          <el-input v-model.number="form.amount" placeholder="单次用水量/月用水量" />
        </el-form-item>
        <el-form-item label="计量单位" prop="unit">
          <el-radio-group v-model="form.unit">
            <el-radio label="L">升 (L)</el-radio>
            <el-radio label="t">吨 (t/m³)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input v-model="form.description" placeholder="请输入备注信息" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="calculateEmission">计算排放</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 计算结果 -->
      <div v-if="emissionResult !== null" class="emission-result" style="margin-top: 20px; padding: 20px; background-color: #f7f9fa; border-radius: 8px;">
        <h3>计算结果</h3>
        <div class="result-content" style="display: flex; align-items: center; justify-content: space-between;">
          <div class="result-item">
            <span class="result-label">碳排放量：</span>
            <span class="result-value" style="font-size: 24px; color: #67c23a; font-weight: bold;">{{ emissionResult.toFixed(2) }} kg CO₂e</span>
          </div>
          <el-button type="success" @click="saveRecord">保存记录</el-button>
        </div>
      </div>
    </el-card>

    <!-- 历史记录 -->
    <el-card class="history-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header" style="display: flex; justify-content: space-between;">
          <span>水资源排放历史记录 (本地存储)</span>
          <el-button type="danger" size="small" @click="clearHistory" v-if="records.length > 0">清空记录</el-button>
        </div>
      </template>
      <el-table :data="records" style="width: 100%">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="waterType" label="用水类型" width="160">
          <template #default="scope">
            {{ getWaterTypeName(scope.row.waterType) }}
          </template>
        </el-table-column>
        <el-table-column label="用水量" width="120">
          <template #default="scope">
            {{ scope.row.amount }} {{ scope.row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="emission" label="排放量(kg CO₂e)" width="150" />
        <el-table-column prop="description" label="备注" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="danger" @click="deleteRecord(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const formRef = ref()
const emissionResult = ref<number | null>(null)
const records = ref<any[]>([])

const form = reactive({
  waterType: 'tap_water',
  customName: '',
  customFactor: null as number | null,
  amount: null as number | null,
  unit: 't',
  date: new Date().toISOString().split('T')[0],
  description: ''
})

const rules = {
  waterType: [{ required: true, message: '请选择用水类型', trigger: 'change' }],
  customName: [{ required: true, message: '请输入自定义类型名称', trigger: 'blur' }],
  customFactor: [{ required: true, message: '请输入排放因子', trigger: 'blur' }],
  amount: [
    { required: true, message: '请输入用水量', trigger: 'blur' },
    { type: 'number', message: '必须为数字' }
  ],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const waterTypeMap: Record<string, string> = {
  tap_water: '生活用水 (自来水)',
  bottled_water: '饮用纯净水',
  hot_water: '热水 (洗浴等)',
  custom: '自定义'
}

const getWaterTypeName = (type: string) => {
  if (type === 'custom') return '自定义'
  return waterTypeMap[type] || type
}

// 简单的碳排放因子 (mock)
const calculateEmission = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid && form.amount !== null) {
      let factor = 0
      let amountInLiters = form.unit === 'L' ? form.amount : form.amount * 1000
      let amountInTonnes = form.unit === 't' ? form.amount : form.amount / 1000

      if (form.waterType === 'tap_water') {
        factor = amountInTonnes * 0.3
      } else if (form.waterType === 'bottled_water') {
        factor = amountInLiters * 0.4
      } else if (form.waterType === 'hot_water') {
        factor = amountInTonnes * 5.0
      } else if (form.waterType === 'custom' && form.customFactor) {
        factor = form.amount * form.customFactor
      }
      
      emissionResult.value = factor
    }
  })
}

const resetForm = () => {
  form.waterType = 'tap_water'
  form.customName = ''
  form.customFactor = null
  form.amount = null
  form.unit = 't'
  form.description = ''
  emissionResult.value = null
  formRef.value?.clearValidate()
}

const STORAGE_KEY = 'mock_water_emissions'

const loadRecords = () => {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored) {
    try {
      records.value = JSON.parse(stored)
    } catch(e) {
      records.value = []
    }
  }
}

const saveRecord = () => {
  if (emissionResult.value === null) return
  
  const typeName = form.waterType === 'custom' ? `自定义 (${form.customName})` : form.waterType
  
  const newRecord = {
    id: Date.now(),
    waterType: typeName,
    amount: form.amount,
    unit: form.unit,
    date: form.date,
    description: form.description,
    emission: Number(emissionResult.value.toFixed(2))
  }
  
  records.value.unshift(newRecord)
  localStorage.setItem(STORAGE_KEY, JSON.stringify(records.value))
  
  ElMessage.success('保存成功 (已存至本地)')
  resetForm()
}

const deleteRecord = (id: number) => {
  records.value = records.value.filter(r => r.id !== id)
  localStorage.setItem(STORAGE_KEY, JSON.stringify(records.value))
  ElMessage.success('删除成功')
}

const clearHistory = () => {
  records.value = []
  localStorage.removeItem(STORAGE_KEY)
  ElMessage.success('历史记录已清空')
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.unit {
  color: #666;
  font-size: 14px;
}
</style>
