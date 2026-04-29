<template>
  <div class="emission-form">

        <!-- 用电排放计算器 -->
        <el-card class="calculator-card">
          <template #header>
            <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
              <span>设备与用电详情</span>
              <el-upload
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleOCRUpload"
                accept="image/*"
              >
                <el-button type="success" size="small" :loading="ocrLoading">
                  <el-icon style="margin-right: 4px"><Camera /></el-icon>
                  ⚡智能票据 OCR 采集
                </el-button>
              </el-upload>
            </div>
          </template>
          <el-form :model="electricityForm" :rules="electricityRules" ref="electricityFormRef" label-width="120px">
            <el-form-item label="用电设备" prop="deviceType">
              <el-select v-model="electricityForm.deviceType" placeholder="请选择用电设备">
                <el-option label="空调" value="air_conditioner" />
                <el-option label="冰箱" value="refrigerator" />
                <el-option label="洗衣机" value="washing_machine" />
                <el-option label="电视" value="tv" />
                <el-option label="电脑" value="computer" />
                <el-option label="照明" value="lighting" />
                <el-option label="热水器" value="water_heater" />
                <el-option label="厨房电器" value="kitchen_appliances" />
                <el-option label="其他" value="other" />
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>

            <template v-if="electricityForm.deviceType === 'custom'">
              <el-form-item label="自定义设备名称" prop="customName">
                <el-input v-model="electricityForm.customName" placeholder="例如：实验仪器" />
              </el-form-item>
              <el-form-item label="排放因子" prop="customFactor">
                <el-input v-model.number="electricityForm.customFactor" placeholder="请输入碳排放因子" />
                <span class="unit">kg CO₂e / 千瓦时</span>
              </el-form-item>
            </template>

            <el-form-item label="功率" prop="power" v-if="electricityForm.deviceType">
              <el-input v-model.number="electricityForm.power" placeholder="请输入功率（瓦特）" />
              <span class="unit">瓦特</span>
            </el-form-item>
            <el-form-item label="使用时间" prop="usageTime" v-if="electricityForm.deviceType">
              <el-input v-model.number="electricityForm.usageTime" placeholder="请输入使用时间（小时/天）" />
              <span class="unit">小时/天</span>
            </el-form-item>
            <el-form-item label="使用天数" prop="usageDays" v-if="electricityForm.deviceType">
              <el-input v-model.number="electricityForm.usageDays" placeholder="请输入使用天数" />
              <span class="unit">天</span>
            </el-form-item>
            <el-form-item label="开始日期" prop="startDate" v-if="electricityForm.deviceType">
              <el-date-picker v-model="electricityForm.startDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD" placeholder="选择开始日期" />
            </el-form-item>
            <el-form-item label="备注" prop="description">
              <el-input v-model="electricityForm.description" placeholder="请输入备注信息" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="calculateEmission">计算排放</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
          
          <!-- 计算结果 -->
          <div v-if="emissionResult > 0" class="emission-result">
            <h3>计算结果</h3>
            <div class="result-content">
              <div class="result-item">
                <span class="result-label">总用电量：</span>
                <span class="result-value">{{ totalElectricity.toFixed(2) }} 千瓦时</span>
              </div>
              <div class="result-item">
                <span class="result-label">碳排放量：</span>
                <span class="result-value">{{ emissionResult.toFixed(2) }} kg CO₂e</span>
              </div>
              <el-button type="success" @click="saveRecord" style="margin-top: 20px;">保存记录</el-button>
            </div>
          </div>
        </el-card>
        
        <!-- 历史记录 -->
        <el-card class="history-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>用电排放历史记录</span>
              <el-button type="danger" size="small" @click="clearHistory" v-if="electricityRecords.length > 0">清空记录</el-button>
            </div>
          </template>
          <el-table :data="electricityRecords" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="deviceType" label="用电设备" width="140">
              <template #default="scope">
                {{ getDeviceTypeName(scope.row.deviceType, scope.row) }}
              </template>
            </el-table-column>
            <el-table-column prop="power" label="功率（瓦特）" width="100" />
            <el-table-column prop="usageTime" label="使用时间（小时/天）" width="150" />
            <el-table-column prop="usageDays" label="使用天数" width="100" />
            <el-table-column prop="electricity" label="用电量（千瓦时）" width="120" />
            <el-table-column prop="emission" label="排放量（kg CO₂e）" width="150" />
            <el-table-column prop="description" label="备注" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteRecord(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination 
            v-if="electricityRecords.length > 0" 
            layout="prev, pager, next" 
            :total="electricityRecords.length" 
            :page-size="10" 
            style="margin-top: 20px; text-align: right;"
          />
        </el-card>
      
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, reactive } from 'vue'
import { useCarbonStore } from '../../store'
import { carbonApi } from '../../api'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, CollectionTag, TrendCharts, Camera } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const carbonStore = useCarbonStore()
const electricityFormRef = ref<FormInstance>()
const emissionResult = ref(0)
const totalElectricity = ref(0)
const ocrLoading = ref(false)



// 电力排放因子（kg CO₂e/千瓦时）
const electricityEmissionFactor = 0.583

const electricityForm = reactive({
  deviceType: '',
  power: 0,
  usageTime: 0,
  usageDays: 1,
  startDate: new Date().toISOString().split('T')[0],
  description: '',
  customName: '',
  customFactor: null as number | null
})

interface ElectricityRecordItem {
  id: string
  date: string
  deviceType: string
  power: number
  usageTime: number
  usageDays: number
  electricity: number
  emission: number
  description: string
}

const electricityRules = reactive<FormRules>({
  deviceType: [
    { required: true, message: '请选择用电设备', trigger: 'blur' }
  ],
  power: [
    { required: true, message: '请输入功率', trigger: 'blur' },
    { type: 'number', min: 1, message: '功率必须大于0', trigger: 'blur' }
  ],
  usageTime: [
    { required: true, message: '请输入使用时间', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '使用时间必须大于0', trigger: 'blur' }
  ],
  usageDays: [
    { required: true, message: '请输入使用天数', trigger: 'blur' },
    { type: 'number', min: 1, message: '使用天数必须大于0', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'blur' }
  ],
  customName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  customFactor: [{ required: true, message: '请输入排放因子', trigger: 'blur' }]
})

const electricityRecords = ref<ElectricityRecordItem[]>([])







const handleOCRUpload = (file: any) => {
  if (!file || !file.raw) return
  ocrLoading.value = true
  ElMessage.info('正在由AI分析电费账单，请稍候...')
  
  // 模拟一个 OCR 分析过程
  setTimeout(() => {
    // 假设AI分析出了空调的用电情况
    electricityForm.deviceType = 'air_conditioner'
    electricityForm.power = 1200 // 假设是 1匹多一点
    electricityForm.usageTime = 8  // 每天8小时
    electricityForm.usageDays = 30 // 月度账单，30天
    electricityForm.startDate = new Date().toISOString().split('T')[0]
    electricityForm.description = '（由 OCR 自动提取的电费账单数据）'
    
    ocrLoading.value = false
    ElMessage.success('票据扫描成功，已自动填充相关信息！')
    
    // 自动计算一下
    calculateEmission()
  }, 2000)
}

const calculateEmission = async () => {
  if (!electricityFormRef.value) return
  
  await electricityFormRef.value.validate(async (valid) => {
    if (valid) {
      // 计算总用电量（千瓦时）
      totalElectricity.value = (electricityForm.power * electricityForm.usageTime * electricityForm.usageDays) / 1000
      
      // 计算碳排放
      if (electricityForm.deviceType === 'custom' && electricityForm.customFactor !== null) {
        emissionResult.value = totalElectricity.value * electricityForm.customFactor
      } else {
        emissionResult.value = totalElectricity.value * electricityEmissionFactor
      }
    }
  })
}

const resetForm = () => {
  electricityFormRef.value?.resetFields()
  emissionResult.value = 0
  totalElectricity.value = 0
}

const deleteRecord = async (id: string) => {
  try {
    if (id.startsWith('local_')) {
      const localRecords = JSON.parse(localStorage.getItem('mock_electricity_emissions') || '[]')
      const newRecords = localRecords.filter((r: any) => r.id !== id)
      localStorage.setItem('mock_electricity_emissions', JSON.stringify(newRecords))
      await loadElectricityRecords()
      ElMessage.success('自定义记录已删除')
      return
    }

    await carbonApi.deleteElectricityRecord(Number(id))
    await loadElectricityRecords()
    ElMessage.success('用电记录已删除')
  } catch (error) {
    console.error('删除用电记录失败:', error)
    ElMessage.error('删除用电记录失败，请稍后重试')
  }
}

const clearHistory = async () => {
  try {
    await carbonApi.clearElectricityRecords()
    localStorage.removeItem('mock_electricity_emissions')
    await loadElectricityRecords()
    ElMessage.success('用电记录已清空')
  } catch (error) {
    console.error('清空用电记录失败:', error)
    ElMessage.error('清空用电记录失败，请稍后重试')
  }
}

const getDeviceTypeName = (type: string, row?: any) => {
  if (type === 'custom') return `自定义 (${row?.customName || '未知'})`
  const typeMap = {
    air_conditioner: '空调',
    refrigerator: '冰箱',
    washing_machine: '洗衣机',
    tv: '电视',
    computer: '电脑',
    lighting: '照明',
    water_heater: '热水器',
    kitchen_appliances: '厨房电器',
    other: '其他'
  }
  return typeMap[type as keyof typeof typeMap] || type
}

const loadElectricityRecords = async () => {
  try {
    const apiRecords = await carbonApi.getElectricityRecords()
    const mapped = apiRecords.map(record => ({
      id: String(record.id),
      date: record.emissionDate,
      deviceType: record.deviceType,
      power: record.power,
      usageTime: record.usageTime,
      usageDays: record.usageDays,
      electricity: record.electricityAmount,
      emission: record.emissionAmount,
      description: record.description || ''
    }))
    
    // 加载自定义数据
    const localRecords = JSON.parse(localStorage.getItem('mock_electricity_emissions') || '[]')
    
    electricityRecords.value = [...mapped, ...localRecords].sort((left, right) => new Date(right.date).getTime() - new Date(left.date).getTime())
  } catch (error) {
    console.error('加载用电历史记录失败:', error)
    ElMessage.warning('用电历史记录加载失败，已显示当前页面数据')
    
    const localRecords = JSON.parse(localStorage.getItem('mock_electricity_emissions') || '[]')
    electricityRecords.value = localRecords.sort((left: any, right: any) => new Date(right.date).getTime() - new Date(left.date).getTime())
  }
}

const saveRecord = async () => {
  try {
    if (electricityForm.deviceType === 'custom') {
      const localRecords = JSON.parse(localStorage.getItem('mock_electricity_emissions') || '[]')
      localRecords.push({
        id: 'local_' + Date.now(),
        date: electricityForm.startDate,
        deviceType: 'custom',
        customName: electricityForm.customName,
        power: electricityForm.power,
        usageTime: electricityForm.usageTime,
        usageDays: electricityForm.usageDays,
        electricity: Number(totalElectricity.value.toFixed(2)),
        emission: Number(emissionResult.value.toFixed(2)),
        description: electricityForm.description || ''
      })
      localStorage.setItem('mock_electricity_emissions', JSON.stringify(localRecords))
      await loadElectricityRecords()
      ElMessage.success('自定义用电记录已保存至本地')
      resetForm()
      return
    }

    await carbonApi.addElectricityRecord({
      deviceType: electricityForm.deviceType,
      power: electricityForm.power,
      usageTime: electricityForm.usageTime,
      usageDays: electricityForm.usageDays,
      emissionDate: electricityForm.startDate,
      description: electricityForm.description
    })

    await loadElectricityRecords()
    await carbonStore.fetchAllRecords()
    ElMessage.success('用电记录已保存')
    resetForm()
  } catch (error) {
    console.error('保存用电记录失败:', error)
    ElMessage.error('保存用电记录失败，请稍后重试')
  }
}

onMounted(() => {
  loadElectricityRecords()
})
</script>

<style scoped>
.emission-form { width: 100%; border: none; padding: 10px; }
.calculator-card { margin-bottom: 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.card-header { font-weight: bold; font-size: 16px; }
.unit { margin-left: 10px; color: #999; }
.emission-result { margin-top: 30px; padding: 20px; background-color: #f5f5f5; border-radius: 8px; border-left: 4px solid #4CAF50; }
.result-value { font-size: 24px; font-weight: bold; color: #4CAF50; }
.history-card { margin-top: 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
</style>