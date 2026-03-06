<template>
  <el-container class="electricity-container">
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
        <el-menu :default-active="activeMenu" class="dashboard-menu" @select="handleMenuSelect">
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/transport">
            <el-icon><Van /></el-icon>
            <span>交通排放</span>
          </el-menu-item>
          <el-menu-item index="/diet">
            <el-icon><KnifeFork /></el-icon>
            <span>饮食排放</span>
          </el-menu-item>
          <el-menu-item index="/electricity">
            <el-icon><Lightning /></el-icon>
            <span>用电排放</span>
          </el-menu-item>
          <el-menu-item index="/report">
            <el-icon><DataLine /></el-icon>
            <span>报表展示</span>
          </el-menu-item>
          <el-menu-item index="/recommendations">
            <el-icon><Star /></el-icon>
            <span>减排建议</span>
          </el-menu-item>
          <el-menu-item index="/points">
            <el-icon><CollectionTag /></el-icon>
            <span>减碳积分</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="electricity-main">
        <h2>用电排放计算</h2>
        
        <!-- 用电排放计算器 -->
        <el-card class="calculator-card">
          <template #header>
            <div class="card-header">
              <span>用电排放计算器</span>
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
              </el-select>
            </el-form-item>
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
              <el-date-picker v-model="electricityForm.startDate" type="date" placeholder="选择开始日期" />
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
            <el-table-column prop="deviceType" label="用电设备" width="120">
              <template #default="scope">
                {{ getDeviceTypeName(scope.row.deviceType) }}
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
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, CollectionTag } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/electricity')
const electricityFormRef = ref<FormInstance>()
const emissionResult = ref(0)
const totalElectricity = ref(0)

const user = computed(() => carbonStore.user)

// 电力排放因子（kg CO₂e/千瓦时）
const electricityEmissionFactor = 0.583

const electricityForm = reactive({
  deviceType: '',
  power: 0,
  usageTime: 0,
  usageDays: 1,
  startDate: new Date().toISOString().split('T')[0],
  description: ''
})

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
  ]
})

const electricityRecords = ref([
  {
    id: '1',
    date: '2026-01-30',
    deviceType: 'air_conditioner',
    power: 1500,
    usageTime: 8,
    usageDays: 1,
    electricity: 12.0,
    emission: 6.996,
    description: '客厅空调'
  },
  {
    id: '2',
    date: '2026-01-30',
    deviceType: 'refrigerator',
    power: 200,
    usageTime: 24,
    usageDays: 1,
    electricity: 4.8,
    emission: 2.798,
    description: '冰箱'
  }
])

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
  if (!electricityFormRef.value) return
  
  await electricityFormRef.value.validate(async (valid) => {
    if (valid) {
      // 计算总用电量（千瓦时）
      totalElectricity.value = (electricityForm.power * electricityForm.usageTime * electricityForm.usageDays) / 1000
      
      // 计算碳排放
      emissionResult.value = totalElectricity.value * electricityEmissionFactor
    }
  })
}

const resetForm = () => {
  electricityFormRef.value?.resetFields()
  emissionResult.value = 0
  totalElectricity.value = 0
}

const saveRecord = () => {
  const record = {
    type: 'electricity' as const,
    value: emissionResult.value,
    date: electricityForm.startDate,
    description: electricityForm.description
  }
  
  carbonStore.addRecord(record)
  
  // 添加到本地记录
  electricityRecords.value.unshift({
    id: Date.now().toString(),
    date: electricityForm.startDate,
    deviceType: electricityForm.deviceType,
    power: electricityForm.power,
    usageTime: electricityForm.usageTime,
    usageDays: electricityForm.usageDays,
    electricity: totalElectricity.value,
    emission: emissionResult.value,
    description: electricityForm.description
  })
  
  // 重置表单
  resetForm()
}

const deleteRecord = (id: string) => {
  electricityRecords.value = electricityRecords.value.filter(record => record.id !== id)
}

const clearHistory = () => {
  electricityRecords.value = []
}

const getDeviceTypeName = (type: string) => {
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
</script>

<style scoped>
.electricity-container {
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

.electricity-main {
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
  font-size: 20px;
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