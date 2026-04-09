<template>
  <el-container class="transport-container">
    <el-header height="60px" class="dashboard-header">
      <div class="header-left">
        <router-link to="/home" class="logo-link">
          <h1>碳足迹追踪平台</h1>
        </router-link>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            {{ user.name }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateToProfile">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="dashboard-aside">
        <RoleSidebar />
      </el-aside>
      <el-main class="transport-main">
        <h2>交通排放计算</h2>
        
        <!-- 交通排放计算器 -->
        <el-card class="calculator-card">
          <template #header>
            <div class="card-header">
              <span>交通排放计算器</span>
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
              </el-select>
            </el-form-item>
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
                {{ getTransportTypeName(scope.row.transportType) }}
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
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted, computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import { carbonApi } from '../api'
import RoleSidebar from '../components/RoleSidebar.vue'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, CollectionTag, TrendCharts } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/transport')
const transportFormRef = ref<FormInstance>()
const emissionResult = ref(0)

const user = computed(() => carbonStore.user)

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
  description: ''
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
  ]
})

const transportRecords = ref<TransportRecordItem[]>([])

const handleMenuSelect = (key: string) => {
  router.push(key)
  activeMenu.value = key
}

const handleLogout = () => {
  router.push('/login')
}

const navigateToProfile = () => {
  router.push('/profile')
}

const calculateEmission = async () => {
  if (!transportFormRef.value) return
  
  await transportFormRef.value.validate(async (valid) => {
    if (valid) {
      let emissionFactor = 0
      
      if (transportForm.transportType === 'car') {
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

    carbonStore.addRecord({
      type: 'transport',
      value: emissionResult.value,
      date: transportForm.date,
      description: transportForm.description
    })

    transportRecords.value.unshift({
      id: String(savedRecord.id),
      date: savedRecord.emissionDate,
      transportType: mapTransportTypeFromApiCode(savedRecord.transportType),
      distance: savedRecord.distance,
      emission: savedRecord.emissionAmount,
      description: savedRecord.description || ''
    })

    loadTransportRecords().catch(error => {
      console.error('刷新交通历史记录失败:', error)
      ElMessage.warning('记录已保存，但历史列表刷新失败，请稍后手动刷新页面')
    })

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
.transport-container {
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left .logo-link {
  text-decoration: none;
  color: inherit;
  display: inline-block;
  transition: color 0.3s ease, transform 0.3s ease;
}

.header-left .logo-link:hover {
  color: #ffffff;
  transform: translateY(-2px);
}

.header-left h1 {
  font-size: 20px;
  margin: 0;
}

.user-info {
  color: white;
  cursor: pointer;
}

.dashboard-aside {
  background-color: #fff;
}

.dashboard-menu {
  height: 100%;
  border-right: none;
}

.transport-main {
  padding: 20px;
}

.calculator-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.unit {
  margin-left: 10px;
  color: #999;
}

.emission-result {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
}

.result-content {
  margin-top: 10px;
}

.result-item {
  margin-bottom: 10px;
}

.result-value {
  font-size: 24px;
  font-weight: bold;
  color: #4CAF50;
}

.history-card {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .dashboard-aside {
    display: none;
  }
  
  .el-main {
    padding: 10px;
  }
  
  .el-form-item {
    margin-bottom: 15px;
  }
}
</style>