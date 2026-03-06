<template>
  <el-container class="dashboard-container">
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
      <el-main class="dashboard-main">
        <h2>个人碳足迹仪表盘</h2>
        
        <!-- 时间范围选择 -->
        <div class="time-range-selector">
          <el-button-group>
            <el-button 
              v-for="range in timeRanges" 
              :key="range.value"
              :type="selectedRange === range.value ? 'primary' : 'default'"
              @click="handleTimeRangeChange(range.value)"
            >
              {{ range.label }}
            </el-button>
          </el-button-group>
          <div class="export-all">
            <el-button type="primary" @click="handleExportAll">
              <el-icon><Download /></el-icon>
              导出完整报表
            </el-button>
          </div>
        </div>
        
        <!-- 碳足迹概览卡片 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">总碳足迹</div>
                <div class="overview-value">{{ totalFootprint.toFixed(2) }} kg CO₂e</div>
                <div class="overview-change" :class="{ positive: totalChange > 0 }">
                  <el-icon v-if="totalChange > 0"><ArrowDown /></el-icon>
                  <el-icon v-else><ArrowUp /></el-icon>
                  <span>{{ Math.abs(totalChange).toFixed(1) }}%</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">交通排放</div>
                <div class="overview-value">{{ footprint.transport.toFixed(2) }} kg CO₂e</div>
                <div class="overview-change" :class="{ positive: transportChange > 0 }">
                  <el-icon v-if="transportChange > 0"><ArrowDown /></el-icon>
                  <el-icon v-else><ArrowUp /></el-icon>
                  <span>{{ Math.abs(transportChange).toFixed(1) }}%</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">饮食排放</div>
                <div class="overview-value">{{ footprint.diet.toFixed(2) }} kg CO₂e</div>
                <div class="overview-change" :class="{ positive: dietChange > 0 }">
                  <el-icon v-if="dietChange > 0"><ArrowDown /></el-icon>
                  <el-icon v-else><ArrowUp /></el-icon>
                  <span>{{ Math.abs(dietChange).toFixed(1) }}%</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">用电排放</div>
                <div class="overview-value">{{ footprint.electricity.toFixed(2) }} kg CO₂e</div>
                <div class="overview-change" :class="{ positive: electricityChange > 0 }">
                  <el-icon v-if="electricityChange > 0"><ArrowDown /></el-icon>
                  <el-icon v-else><ArrowUp /></el-icon>
                  <span>{{ Math.abs(electricityChange).toFixed(1) }}%</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card points-card">
              <div class="overview-item">
                <div class="overview-label">减碳积分</div>
                <div class="overview-value points-value">{{ totalPoints }}</div>
                <div class="overview-change points-change">
                  <el-icon><Star /></el-icon>
                  <span>累计减碳 {{ totalEmissionReduced.toFixed(2) }} kg</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 数据可视化图表 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 目标完成度仪表盘 -->
          <el-col :xs="24" :md="8">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>月度碳排放目标</span>
                </div>
              </template>
              <CarbonChart 
                type="gauge"
                :data="gaugeData"
                :height="300"
                @drillDown="handleChartDrillDown"
              />
            </el-card>
          </el-col>
          
          <!-- 碳足迹分类占比 -->
          <el-col :xs="24" :md="8">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>碳足迹分类占比</span>
                </div>
              </template>
              <CarbonChart 
                type="pie"
                :data="pieData"
                :height="300"
                @drillDown="handleChartDrillDown"
              />
            </el-card>
          </el-col>
          
          <!-- 历史排放趋势 -->
          <el-col :xs="24" :md="8">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>历史排放趋势</span>
                </div>
              </template>
              <CarbonChart 
                type="line"
                :data="lineData"
                :height="300"
                @drillDown="handleChartDrillDown"
              />
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 对比分析图表 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :xs="24">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>不同时期排放量对比</span>
                </div>
              </template>
              <CarbonChart 
                type="bar"
                :data="barData"
                :height="400"
                @drillDown="handleChartDrillDown"
              />
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 减排目标 -->
        <el-card class="goal-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>减排目标</span>
              <el-button type="primary" size="small" @click="openGoalDialog">设置目标</el-button>
            </div>
          </template>
          <div class="goal-content">
            <div class="goal-progress">
              <el-progress 
                :percentage="progress" 
                :color="['#4CAF50', '#FFC107', '#F44336']" 
                :format="formatProgress"
              />
            </div>
            <div class="goal-info">
              <span>当前目标：减少 {{ reductionGoal }}% 的碳排放</span>
              <span>距离目标还有 {{ remainingDays }} 天</span>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 目标设置对话框 -->
    <el-dialog v-model="goalDialogVisible" title="设置减排目标">
      <el-form :model="goalForm" :rules="goalRules" ref="goalFormRef">
        <el-form-item label="减排百分比" prop="reductionGoal">
          <el-input-number v-model="goalForm.reductionGoal" :min="1" :max="50" :step="1" />
          <span class="unit">%</span>
        </el-form-item>
        <el-form-item label="目标期限" prop="deadline">
          <el-date-picker v-model="goalForm.deadline" type="date" placeholder="选择日期" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="goalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitGoal">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 钻取详情对话框 -->
    <el-dialog v-model="drillDownDialogVisible" :title="drillDownTitle" width="80%">
      <div v-if="drillDownData">
        <CarbonChart 
          :type="drillDownChartType"
          :data="drillDownData"
          :height="400"
        />
        <div class="drill-down-details" v-if="drillDownDetails.length > 0">
          <h4>详细数据</h4>
          <el-table :data="drillDownDetails">
            <el-table-column 
              v-for="column in drillDownColumns" 
              :key="column.prop"
              :prop="column.prop"
              :label="column.label"
            />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import CarbonChart from '../components/CarbonChart.vue'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, ArrowUp, Download, Document, CollectionTag } from '@element-plus/icons-vue'
import { ExportService, type ExportData } from '../utils/export'
import { ElMessage } from 'element-plus'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/dashboard')
const goalDialogVisible = ref(false)
const drillDownDialogVisible = ref(false)
const drillDownTitle = ref('')
const drillDownChartType = ref('')
const drillDownData = ref<any>(null)
const drillDownDetails = ref<any[]>([])
const drillDownColumns = ref<any[]>([])

// 加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  loadPointsData()
})

// 加载积分数据
const loadPointsData = async () => {
  try {
    // 这里应该调用API获取积分数据
    // 暂时使用模拟数据
    totalPoints.value = 1250
    totalEmissionReduced.value = 125.5
  } catch (error) {
    console.error('Failed to load points data:', error)
  }
}

const user = computed(() => carbonStore.user)
const footprint = computed(() => carbonStore.footprint)
const totalFootprint = computed(() => carbonStore.totalFootprint)
const reductionGoal = computed(() => carbonStore.reductionGoal)

const progress = ref(65)
const remainingDays = ref(45)
const totalChange = ref(-5.2)
const transportChange = ref(3.1)
const dietChange = ref(-1.8)
const electricityChange = ref(2.5)
const totalPoints = ref(0)
const totalEmissionReduced = ref(0)

const selectedRange = ref('month')
const timeRanges = [
  { label: '日', value: 'day' },
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '年', value: 'year' }
]

const goalForm = ref({
  reductionGoal: 20,
  deadline: ''
})

const goalRules = ref({
  reductionGoal: [
    { required: true, message: '请输入减排百分比', trigger: 'blur' }
  ],
  deadline: [
    { required: true, message: '请选择目标期限', trigger: 'blur' }
  ]
})

const goalFormRef = ref()

// 仪表盘数据
const gaugeData = computed(() => {
  return {
    value: progress.value,
    name: '目标完成度'
  }
})

// 饼图数据
const pieData = computed(() => {
  return {
    legend: ['交通', '饮食', '用电'],
    series: [
      {
        name: '交通',
        value: footprint.value.transport,
        itemStyle: { color: '#4CAF50' }
      },
      {
        name: '饮食',
        value: footprint.value.diet,
        itemStyle: { color: '#81C784' }
      },
      {
        name: '用电',
        value: footprint.value.electricity,
        itemStyle: { color: '#A5D6A7' }
      }
    ]
  }
})

// 折线图数据
const lineData = computed(() => {
  const labels = getTimeLabels(selectedRange.value)
  const data = generateTrendData(labels.length)
  
  return {
    legend: ['总排放', '交通', '饮食', '用电'],
    xAxis: labels,
    series: [
      {
        name: '总排放',
        type: 'line',
        data: data.total,
        smooth: true,
        areaStyle: {
          color: new (typeof window !== 'undefined' && window.echarts ? window.echarts.graphic.LinearGradient : function() { return {}})(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(76, 175, 80, 0.3)' },
            { offset: 1, color: 'rgba(76, 175, 80, 0.1)' }
          ])
        },
        lineStyle: { color: '#4CAF50' }
      },
      {
        name: '交通',
        type: 'line',
        data: data.transport,
        smooth: true,
        lineStyle: { color: '#2196F3' }
      },
      {
        name: '饮食',
        type: 'line',
        data: data.diet,
        smooth: true,
        lineStyle: { color: '#FF9800' }
      },
      {
        name: '用电',
        type: 'line',
        data: data.electricity,
        smooth: true,
        lineStyle: { color: '#9C27B0' }
      }
    ]
  }
})

// 柱状图数据
const barData = computed(() => {
  const labels = getComparisonLabels(selectedRange.value)
  const data = generateComparisonData(labels.length)
  
  return {
    legend: ['当前周期', '上一周期'],
    xAxis: labels,
    series: [
      {
        name: '当前周期',
        type: 'bar',
        data: data.current,
        itemStyle: { color: '#4CAF50' }
      },
      {
        name: '上一周期',
        type: 'bar',
        data: data.previous,
        itemStyle: { color: '#BDBDBD' }
      }
    ]
  }
})

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

const formatProgress = (percentage: number) => {
  return `${percentage}%`
}

const openGoalDialog = () => {
  goalForm.value.reductionGoal = reductionGoal.value
  goalDialogVisible.value = true
}

const submitGoal = async () => {
  if (!goalFormRef.value) return
  
  await goalFormRef.value.validate(async (valid) => {
    if (valid) {
      carbonStore.setReductionGoal(goalForm.value.reductionGoal)
      goalDialogVisible.value = false
    }
  })
}

const handleTimeRangeChange = (range: string) => {
  selectedRange.value = range
  // 这里可以添加数据重新加载逻辑
}

const handleChartDrillDown = (params: any) => {
  drillDownTitle.value = `详细数据 - ${params.name}`
  drillDownChartType.value = 'bar'
  
  // 模拟钻取数据
  drillDownData.value = {
    legend: ['详细数据'],
    xAxis: ['1日', '2日', '3日', '4日', '5日', '6日', '7日'],
    series: [{
      name: '排放量',
      type: 'bar',
      data: [12, 19, 3, 5, 2, 3, 7],
      itemStyle: { color: '#4CAF50' }
    }]
  }
  
  drillDownDetails.value = [
    { date: '1日', amount: 12, category: '交通', description: '上班通勤' },
    { date: '2日', amount: 19, category: '饮食', description: '外出就餐' },
    { date: '3日', amount: 3, category: '用电', description: '家庭用电' },
    { date: '4日', amount: 5, category: '交通', description: '购物出行' },
    { date: '5日', amount: 2, category: '用电', description: '家庭用电' },
    { date: '6日', amount: 3, category: '饮食', description: '家庭餐饮' },
    { date: '7日', amount: 7, category: '交通', description: '周末出行' }
  ]
  
  drillDownColumns.value = [
    { prop: 'date', label: '日期' },
    { prop: 'amount', label: '排放量 (kg CO₂e)' },
    { prop: 'category', label: '分类' },
    { prop: 'description', label: '描述' }
  ]
  
  drillDownDialogVisible.value = true
}

const handleExportAll = () => {
  const exportData: ExportData[] = []
  
  const labels = getTimeLabels(selectedRange.value)
  const trendData = generateTrendData(labels.length)
  
  labels.forEach((label, index) => {
    exportData.push({
      date: label,
      category: '交通',
      type: '交通排放',
      amount: trendData.transport[index],
      description: '日常出行'
    })
    exportData.push({
      date: label,
      category: '饮食',
      type: '饮食排放',
      amount: trendData.diet[index],
      description: '日常饮食'
    })
    exportData.push({
      date: label,
      category: '用电',
      type: '用电排放',
      amount: trendData.electricity[index],
      description: '日常用电'
    })
  })
  
  ElMessage.success('正在生成报表...')
  
  setTimeout(async () => {
    await ExportService.exportToPDFWithCanvas(exportData, '碳足迹数据报表')
    ElMessage.success('报表导出成功')
  }, 500)
}

const getTimeLabels = (range: string) => {
  switch (range) {
    case 'day':
      return ['0时', '4时', '8时', '12时', '16时', '20时']
    case 'week':
      return ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    case 'month':
      return ['第1周', '第2周', '第3周', '第4周']
    case 'year':
      return ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    default:
      return ['1月', '2月', '3月', '4月', '5月', '6月']
  }
}

const getComparisonLabels = (range: string) => {
  switch (range) {
    case 'day':
      return ['交通', '饮食', '用电']
    case 'week':
      return ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    case 'month':
      return ['第1周', '第2周', '第3周', '第4周']
    case 'year':
      return ['1月', '2月', '3月', '4月', '5月', '6月']
    default:
      return ['交通', '饮食', '用电']
  }
}

const generateTrendData = (length: number) => {
  const total: number[] = []
  const transport: number[] = []
  const diet: number[] = []
  const electricity: number[] = []
  
  for (let i = 0; i < length; i++) {
    const t = Math.random() * 50 + 50
    const tr = Math.random() * 20 + 10
    const d = Math.random() * 15 + 15
    const e = Math.random() * 10 + 5
    
    total.push(t)
    transport.push(tr)
    diet.push(d)
    electricity.push(e)
  }
  
  return { total, transport, diet, electricity }
}

const generateComparisonData = (length: number) => {
  const current: number[] = []
  const previous: number[] = []
  
  for (let i = 0; i < length; i++) {
    const c = Math.random() * 30 + 20
    const p = c * (Math.random() * 0.4 + 0.8)
    
    current.push(c)
    previous.push(p)
  }
  
  return { current, previous }
}

// 全局消息提示
const elMessage = {
  success: (message: string) => {
    // 实际项目中使用Element Plus的ElMessage
    console.log('Success:', message)
  }
}
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left .logo-link {
  text-decoration: none;
  color: inherit;
  display: inline-block;
  transition: color 0.3s ease, transform 0.3s ease;
}

.header-left .logo-link:hover {
  color: #4CAF50;
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

.dashboard-main {
  padding: 20px;
}

.time-range-selector {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.export-all {
  margin-left: auto;
}

.overview-card {
  height: 120px;
}

.overview-item {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.overview-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.overview-value {
  font-size: 24px;
  font-weight: bold;
  color: #4CAF50;
  flex: 1;
}

.overview-change {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.overview-change.positive {
  color: #4CAF50;
}

.overview-change:not(.positive) {
  color: #F44336;
}

/* 积分卡片样式 */
.points-card {
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-left: 4px solid #4CAF50;
}

.points-value {
  color: #4CAF50;
  font-size: 28px;
  font-weight: bold;
}

.points-change {
  color: #4CAF50;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.chart-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goal-card {
  margin-top: 20px;
}

.goal-content {
  padding: 20px 0;
}

.goal-progress {
  margin-bottom: 20px;
}

.goal-info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.unit {
  margin-left: 10px;
  color: #999;
}

.drill-down-details {
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .overview-card {
    height: 140px;
  }
  
  .chart-card {
    min-height: 350px;
  }
}

@media (max-width: 992px) {
  .overview-card {
    height: 120px;
  }
  
  .overview-value {
    font-size: 20px;
  }
  
  .chart-card {
    min-height: 320px;
  }
}

@media (max-width: 768px) {
  .dashboard-aside {
    display: none;
  }
  
  .dashboard-main {
    padding: 15px 10px;
  }
  
  .dashboard-header h1 {
    font-size: 16px;
  }
  
  .time-range-selector {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .export-all {
    margin-left: 0;
    width: 100%;
  }
  
  .export-all .el-button {
    width: 100%;
  }
  
  .overview-card {
    height: auto;
    min-height: 100px;
  }
  
  .overview-item {
    padding: 15px 0;
  }
  
  .overview-value {
    font-size: 18px;
  }
  
  .chart-card {
    min-height: 280px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .card-header .el-button {
    width: 100%;
  }
  
  .goal-content {
    padding: 15px 0;
  }
  
  .goal-info {
    flex-direction: column;
    gap: 8px;
  }
  
  .el-dialog {
    width: 95% !important;
  }
}

@media (max-width: 576px) {
  .dashboard-header h1 {
    font-size: 14px;
  }
  
  .overview-value {
    font-size: 16px;
  }
  
  .overview-label {
    font-size: 12px;
  }
  
  .overview-change {
    font-size: 11px;
  }
  
  .chart-card {
    min-height: 250px;
  }
  
  .time-range-selector .el-button {
    padding: 8px 12px;
    font-size: 14px;
  }
}
</style>
