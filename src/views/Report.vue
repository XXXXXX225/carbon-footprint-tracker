<template>
  <el-container class="report-container">
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
      <el-main class="report-main">
        <h2>碳足迹报表</h2>
        
        <!-- 报表筛选 -->
        <el-card class="filter-card">
          <el-form :inline="true" :model="filterForm" class="report-filter">
            <el-form-item label="时间范围">
              <el-date-picker 
                v-model="filterForm.dateRange" 
                type="daterange" 
                range-separator="至" 
                start-placeholder="开始日期" 
                end-placeholder="结束日期" 
                style="width: 250px;"
              />
            </el-form-item>
            <el-form-item label="排放类型">
              <el-select v-model="filterForm.emissionType" placeholder="请选择排放类型" style="width: 120px;">
                <el-option label="全部" value="all" />
                <el-option label="交通" value="transport" />
                <el-option label="饮食" value="diet" />
                <el-option label="用电" value="electricity" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="filterData">查询</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <!-- 报表概览 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">总碳排放量</div>
                <div class="overview-value">{{ totalEmission.toFixed(2) }} kg CO₂e</div>
                <div class="overview-change" :class="{ positive: totalEmissionChange < 0 }">
                  <el-icon v-if="totalEmissionChange < 0"><ArrowDown /></el-icon>
                  <el-icon v-else><ArrowUp /></el-icon>
                  <span>{{ Math.abs(totalEmissionChange).toFixed(2) }}%</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">平均日排放量</div>
                <div class="overview-value">{{ averageDailyEmission.toFixed(2) }} kg CO₂e</div>
                <div class="overview-change" :class="{ positive: averageDailyEmissionChange < 0 }">
                  <el-icon v-if="averageDailyEmissionChange < 0"><ArrowDown /></el-icon>
                  <el-icon v-else><ArrowUp /></el-icon>
                  <span>{{ Math.abs(averageDailyEmissionChange).toFixed(2) }}%</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card">
              <div class="overview-item">
                <div class="overview-label">减排目标完成度</div>
                <div class="overview-value">{{ reductionGoalProgress.toFixed(0) }}%</div>
                <div class="overview-change" :class="{ positive: reductionGoalProgress >= 80 }">
                  {{ reductionGoalProgress >= 80 ? '目标达成' : '继续努力' }}
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 数据可视化图表 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :xs="24" :md="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>排放类型占比</span>
                </div>
              </template>
              <div ref="pieChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>排放趋势分析</span>
                </div>
              </template>
              <div ref="lineChartRef" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 历史记录表格 -->
        <el-card class="history-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>历史排放记录</span>
              <el-button type="primary" size="small" @click="exportReport">导出报表</el-button>
            </div>
          </template>
          <el-table :data="filteredRecords" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="type" label="排放类型" width="100">
              <template #default="scope">
                {{ getEmissionTypeName(scope.row.type) }}
              </template>
            </el-table-column>
            <el-table-column prop="value" label="排放量（kg CO₂e）" width="150" />
            <el-table-column prop="description" label="备注" />
          </el-table>
          <el-pagination 
            v-if="filteredRecords.length > 0" 
            layout="prev, pager, next" 
            :total="filteredRecords.length" 
            :page-size="10" 
            style="margin-top: 20px; text-align: right;"
          />
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import * as echarts from 'echarts'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, CollectionTag } from '@element-plus/icons-vue'
import { ExportService, type ExportData } from '../utils/export'
import { ElMessage } from 'element-plus'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/report')
const pieChartRef = ref<HTMLElement>()
const lineChartRef = ref<HTMLElement>()
const pieChart = ref<echarts.ECharts>()
const lineChart = ref<echarts.ECharts>()

const user = computed(() => carbonStore.user)
const records = computed(() => carbonStore.records)

const filterForm = reactive({
  dateRange: null as [Date, Date] | null,
  emissionType: 'all'
})

// 计算报表数据
const totalEmission = computed(() => {
  return filteredRecords.value.reduce((sum, record) => sum + record.value, 0)
})

const averageDailyEmission = computed(() => {
  if (filteredRecords.value.length === 0) return 0
  // 假设时间范围为30天
  return totalEmission.value / 30
})

const totalEmissionChange = ref(-5.2) // 假设比上月减少5.2%
const averageDailyEmissionChange = ref(-4.8) // 假设比上月减少4.8%
const reductionGoalProgress = ref(65) // 假设减排目标完成度65%

// 筛选后的记录
const filteredRecords = computed(() => {
  let result = [...records.value]
  
  if (filterForm.emissionType !== 'all') {
    result = result.filter(record => record.type === filterForm.emissionType)
  }
  
  if (filterForm.dateRange) {
    const [start, end] = filterForm.dateRange
    const startDate = new Date(start)
    const endDate = new Date(end)
    
    result = result.filter(record => {
      const recordDate = new Date(record.date)
      return recordDate >= startDate && recordDate <= endDate
    })
  }
  
  // 按日期降序排序
  return result.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
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

const filterData = () => {
  // 筛选逻辑已在computed中处理
  initCharts()
}

const resetFilter = () => {
  filterForm.dateRange = null
  filterForm.emissionType = 'all'
  initCharts()
}

const exportReport = async () => {
  try {
    ElMessage.success('正在生成报表...')
    
    const exportData: ExportData[] = filteredRecords.value.map(record => ({
      date: record.date,
      category: getEmissionTypeName(record.type),
      type: getEmissionTypeName(record.type),
      amount: record.value,
      description: record.description || '-'
    }))
    
    await ExportService.exportToPDFWithCanvas(exportData, '碳足迹数据报表')
    ElMessage.success('报表导出成功')
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败')
  }
}

const getEmissionTypeName = (type: string) => {
  const typeMap = {
    transport: '交通',
    diet: '饮食',
    electricity: '用电'
  }
  return typeMap[type as keyof typeof typeMap] || type
}

const initCharts = () => {
  initPieChart()
  initLineChart()
}

const initPieChart = () => {
  if (pieChartRef.value) {
    pieChart.value = echarts.init(pieChartRef.value)
    
    // 计算各类型排放量
    const transportEmission = filteredRecords.value.filter(r => r.type === 'transport').reduce((sum, r) => sum + r.value, 0)
    const dietEmission = filteredRecords.value.filter(r => r.type === 'diet').reduce((sum, r) => sum + r.value, 0)
    const electricityEmission = filteredRecords.value.filter(r => r.type === 'electricity').reduce((sum, r) => sum + r.value, 0)
    
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: ['交通', '饮食', '用电']
      },
      series: [
        {
          name: '碳足迹',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: transportEmission, name: '交通', itemStyle: { color: '#4CAF50' } },
            { value: dietEmission, name: '饮食', itemStyle: { color: '#81C784' } },
            { value: electricityEmission, name: '用电', itemStyle: { color: '#A5D6A7' } }
          ]
        }
      ]
    }
    pieChart.value.setOption(option)
  }
}

const initLineChart = () => {
  if (lineChartRef.value) {
    lineChart.value = echarts.init(lineChartRef.value)
    
    // 生成最近7天的数据
    const days = 7
    const dates = []
    const transportData = []
    const dietData = []
    const electricityData = []
    
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date()
      date.setDate(date.getDate() - i)
      const dateStr = date.toISOString().split('T')[0]
      dates.push(dateStr.substring(5)) // 只显示月-日
      
      // 模拟每日数据
      transportData.push(Math.random() * 2 + 1)
      dietData.push(Math.random() * 1.5 + 0.5)
      electricityData.push(Math.random() * 3 + 1.5)
    }
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['交通', '饮食', '用电']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: dates
      },
      yAxis: {
        type: 'value',
        name: 'kg CO₂e'
      },
      series: [
        {
          name: '交通',
          type: 'line',
          stack: 'Total',
          data: transportData,
          lineStyle: {
            color: '#4CAF50'
          },
          itemStyle: {
            color: '#4CAF50'
          }
        },
        {
          name: '饮食',
          type: 'line',
          stack: 'Total',
          data: dietData,
          lineStyle: {
            color: '#81C784'
          },
          itemStyle: {
            color: '#81C784'
          }
        },
        {
          name: '用电',
          type: 'line',
          stack: 'Total',
          data: electricityData,
          lineStyle: {
            color: '#A5D6A7'
          },
          itemStyle: {
            color: '#A5D6A7'
          }
        }
      ]
    }
    lineChart.value.setOption(option)
  }
}

const handleResize = () => {
  pieChart.value?.resize()
  lineChart.value?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart.value?.dispose()
  lineChart.value?.dispose()
})
</script>

<style scoped>
.report-container {
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

.report-main {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.report-filter {
  display: flex;
  align-items: center;
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

.chart-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 350px;
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
  
  .report-filter {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .el-form-item {
    margin-bottom: 10px;
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>