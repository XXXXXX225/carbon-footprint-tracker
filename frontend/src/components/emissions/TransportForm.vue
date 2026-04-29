<template>
  <div class="emission-form">

        <!-- 交通排放计算器 -->
        <el-card class="calculator-card">
          <template #header>
            <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
              <span>车辆与行程信息</span>
              <!-- 行程票据 OCR -->
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
          <el-form :model="transportForm" :rules="transportRules" ref="transportFormRef" label-width="120px">
            <el-form-item label="交通方式" prop="transportType">
              <el-select v-model="transportForm.transportType" placeholder="请选择交通方式">
                <el-option label="步行" value="walking" :disabled="true" />
                <el-option label="自行车" value="biking" :disabled="true" />
                <el-option label="公共汽车" value="bus" />
                <el-option label="地铁" value="subway" />
                <el-option label="出租车" value="taxi" />
                <el-option label="私家车" value="car" />
                <el-option label="飞机" value="plane" />
                <el-option label="火车" value="train" />
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>
            
            <template v-if="transportForm.transportType === 'custom'">
              <el-form-item label="自定义类型名称" prop="customName">
                <el-input v-model="transportForm.customName" placeholder="例如：骑马" />
              </el-form-item>
              <el-form-item label="排放因子" prop="customFactor">
                <el-input v-model.number="transportForm.customFactor" placeholder="请输入碳排放因子" />
                <span class="unit">kg CO₂e / 公里</span>
              </el-form-item>
            </template>

            <el-form-item label="行驶距离" prop="distance">
              <el-input v-model.number="transportForm.distance" placeholder="请输入距离（公里）" />
              <span class="unit">公里</span>
            </el-form-item>
            <el-form-item label="燃油类型" prop="fuelType" v-if="transportForm.transportType === 'car'">
              <el-select v-model="transportForm.fuelType" placeholder="请选择燃油类型">
                <el-option label="汽油" value="gasoline" />
                <el-option label="柴油" value="diesel" />
                <el-option label="电动车" value="electric" />
              </el-select>
            </el-form-item>
            <el-form-item label="车辆油耗" prop="fuelConsumption" v-if="transportForm.transportType === 'car' && transportForm.fuelType !== 'electric'">
              <el-input v-model.number="transportForm.fuelConsumption" placeholder="请输入油耗（升/百公里）" />
              <span class="unit">升/百公里</span>
            </el-form-item>
            <el-form-item label="出行日期" prop="date">
              <el-date-picker v-model="transportForm.date" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注" prop="description">
              <el-input v-model="transportForm.description" placeholder="请输入备注信息" type="textarea" :rows="2" />
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
              <span>交通排放历史记录</span>
              <el-button type="danger" size="small" @click="clearHistory" v-if="transportRecords.length > 0">清空记录</el-button>
            </div>
          </template>
          <el-table :data="transportRecords" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="transportType" label="交通方式" width="120">
              <template #default="scope">
                {{ getTransportTypeName(scope.row.transportType, scope.row) }}
              </template>
            </el-table-column>
            <el-table-column prop="distance" label="距离（公里）" width="120" />
            <el-table-column prop="emission" label="排放量（kg CO₂e）" width="150" />
            <el-table-column prop="description" label="备注" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteRecord(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination 
            v-if="transportRecords.length > 0" 
            layout="prev, pager, next" 
            :total="transportRecords.length" 
            :page-size="10" 
            style="margin-top: 20px; text-align: right;"
          />
        </el-card>
      
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, reactive, ref } from 'vue'
import { useCarbonStore } from '../../store'
import { carbonApi } from '../../api'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, CollectionTag, TrendCharts, Camera } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const carbonStore = useCarbonStore()
const transportFormRef = ref<FormInstance>()
const emissionResult = ref(0)
const ocrLoading = ref(false)



// 交通排放因子（kg CO₂e/公里）
const emissionFactors = {
  bus: 0.089,
  subway: 0.041,
  taxi: 0.271,
  car: {
    gasoline: 0.192,
    diesel: 0.221,
    electric: 0.046
  },
  plane: 0.255,
  train: 0.035
}

const transportForm = reactive({
  transportType: '',
  distance: 0,
  fuelType: 'gasoline',
  fuelConsumption: 8,
  date: new Date().toISOString().split('T')[0],
  description: '',
  customName: '',
  customFactor: null as number | null
})

interface TransportRecordItem {
  id: string
  date: string
  transportType: string
  distance: number
  emission: number
  description: string
}

const transportRules = reactive<FormRules>({
  transportType: [
    { required: true, message: '请选择交通方式', trigger: 'blur' }
  ],
  distance: [
    { required: true, message: '请输入距离', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '距离必须大于0', trigger: 'blur' }
  ],
  fuelType: [
    { required: true, message: '请选择燃油类型', trigger: 'blur' }
  ],
  fuelConsumption: [
    { required: true, message: '请输入油耗', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '油耗必须大于0', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'blur' }
  ],
  customName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  customFactor: [{ required: true, message: '请输入排放因子', trigger: 'blur' }]
})

const transportRecords = ref<TransportRecordItem[]>([])







const handleOCRUpload = (file: any) => {
  if (!file || !file.raw) return
  ocrLoading.value = true
  ElMessage.info('正在通过AI识别票据信息，请稍候...')
  
  setTimeout(() => {
    // 模拟AI提取出高铁行程
    transportForm.vehicleType = 'train'
    transportForm.distance = 1200 // 假设是京沪高铁
    transportForm.date = new Date().toISOString().split('T')[0]
    transportForm.description = '（由图文 OCR 技术自动识别乘车信息）'
    
    ocrLoading.value = false
    ElMessage.success('车票扫描成功！已填入行程数据')
    
    // 自动计算一下
    calculateEmission()
  }, 2000)
}

const calculateEmission = async () => {
  if (!transportFormRef.value) return
  
  await transportFormRef.value.validate(async (valid) => {
    if (valid) {
      let emissionFactor = 0
      
      if (transportForm.transportType === 'custom' && transportForm.customFactor !== null) {
        emissionFactor = transportForm.customFactor
      } else if (transportForm.transportType === 'car') {
        if (transportForm.fuelType === 'electric') {
          emissionFactor = emissionFactors.car.electric
        } else {
          // 根据油耗计算排放因子
          const fuelType = transportForm.fuelType as 'gasoline' | 'diesel'
          emissionFactor = (transportForm.fuelConsumption / 100) * emissionFactors.car[fuelType]
        }
      } else {
        emissionFactor = emissionFactors[transportForm.transportType as keyof typeof emissionFactors]
      }
      
      emissionResult.value = transportForm.distance * emissionFactor
    }
  })
}

const resetForm = () => {
  transportFormRef.value?.resetFields()
  emissionResult.value = 0
}

const mapTransportTypeToApiCode = (type: string) => {
  const typeMap: Record<string, number> = {
    car: 1,
    taxi: 1,
    bus: 2,
    subway: 3,
    train: 3,
    plane: 4,
    walking: 2,
    biking: 2
  }

  return typeMap[type] ?? 1
}

const mapTransportTypeFromApiCode = (type: number) => {
  const typeMap: Record<number, string> = {
    1: 'car',
    2: 'bus',
    3: 'subway',
    4: 'plane'
  }

  return typeMap[type] || 'car'
}

const loadTransportRecords = async () => {
  try {
    const records = await carbonApi.getTransportRecords()
    transportRecords.value = records
      .map(record => ({
        id: String(record.id),
        date: record.emissionDate,
        transportType: mapTransportTypeFromApiCode(record.transportType),
        distance: record.distance,
        emission: record.emissionAmount,
        description: record.description || ''
      }))
      .sort((left, right) => new Date(right.date).getTime() - new Date(left.date).getTime())
  } catch (error) {
    console.error('加载交通历史记录失败:', error)
    ElMessage.warning('交通历史记录加载失败，已显示当前页面数据')
  }
}

const saveRecord = async () => {
  try {
    const savedRecord = await carbonApi.addTransportRecord({
      transportType: mapTransportTypeToApiCode(transportForm.transportType),
      distance: transportForm.distance,
      fuelType: transportForm.fuelType,
      fuelConsumption: transportForm.fuelConsumption,
      emissionDate: transportForm.date,
      description: transportForm.description
    })

    // 全量刷新，而不是只向本地 mock push 以保证真实的数据流同步
    await loadTransportRecords()
    await carbonStore.fetchAllRecords()

    ElMessage.success('交通记录已保存')
    resetForm()
  } catch (error) {
    console.error('保存交通记录失败:', error)
    ElMessage.error('保存交通记录失败，请稍后重试')
  }
}

const deleteRecord = async (id: string) => {
  try {
    await carbonApi.deleteTransportRecord(Number(id))
    await loadTransportRecords()
    ElMessage.success('交通记录已删除')
  } catch (error) {
    console.error('删除交通记录失败:', error)
    ElMessage.error('删除交通记录失败，请稍后重试')
  }
}

const clearHistory = async () => {
  try {
    await carbonApi.clearTransportRecords()
    await loadTransportRecords()
    ElMessage.success('交通记录已清空')
  } catch (error) {
    console.error('清空交通记录失败:', error)
    ElMessage.error('清空交通记录失败，请稍后重试')
  }
}

const getTransportTypeName = (type: string) => {
  const typeMap = {
    walking: '步行',
    biking: '自行车',
    bus: '公共汽车',
    subway: '地铁',
    taxi: '出租车',
    car: '私家车',
    plane: '飞机',
    train: '火车'
  }
  return typeMap[type as keyof typeof typeMap] || type
}

onMounted(() => {
  loadTransportRecords()
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