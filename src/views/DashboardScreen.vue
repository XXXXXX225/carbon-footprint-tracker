<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="header-left">
        <router-link to="/" class="title-link">
          <h1>碳足迹追踪平台 - 数据可视化大屏</h1>
        </router-link>
      </div>
      <div class="current-time">{{ currentTime }}</div>
    </div>

    <div class="dashboard-content">
      <div class="left-panel">
        <div class="panel-item">
          <div class="panel-title">总览数据</div>
          <div class="overview-stats">
            <div class="stat-item">
              <div class="stat-value">{{ overview.totalUsers || 0 }}</div>
              <div class="stat-label">注册用户</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ overview.totalEmission?.toFixed(2) || 0 }} kg</div>
              <div class="stat-label">总碳排放</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ overview.totalReduction?.toFixed(2) || 0 }} kg</div>
              <div class="stat-label">总减碳量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ overview.totalPoints || 0 }}</div>
              <div class="stat-label">总积分</div>
            </div>
          </div>
        </div>

        <div class="panel-item">
          <div class="panel-title">碳排放趋势</div>
          <div ref="trendChart" class="chart-container"></div>
        </div>

        <div class="panel-item">
          <div class="panel-title">排放类别分布</div>
          <div ref="categoryChart" class="chart-container"></div>
        </div>
      </div>

      <div class="center-panel">
        <div class="panel-item large">
          <div class="panel-title">全国用户分布</div>
          <div ref="mapChart" class="chart-container large"></div>
        </div>

        <div class="panel-item">
          <div class="panel-title">碳排放热点分析</div>
          <div class="hotspot-info">
            <div class="hotspot-item">
              <div class="hotspot-label">排放最高地区</div>
              <div class="hotspot-value">上海</div>
              <div class="hotspot-detail">2,845 kg/月</div>
            </div>
            <div class="hotspot-item">
              <div class="hotspot-label">排放最低地区</div>
              <div class="hotspot-value">青海</div>
              <div class="hotspot-detail">326 kg/月</div>
            </div>
            <div class="hotspot-item">
              <div class="hotspot-label">增长最快行业</div>
              <div class="hotspot-value">交通出行</div>
              <div class="hotspot-detail">+12.5%/月</div>
            </div>
            <div class="hotspot-item">
              <div class="hotspot-label">减排效果最好</div>
              <div class="hotspot-value">饮食消费</div>
              <div class="hotspot-detail">-8.3%/月</div>
            </div>
            <div class="hotspot-item full-width">
              <div class="hotspot-label">排放趋势</div>
              <div ref="hotspotChart" class="hotspot-chart"></div>
            </div>
            <div class="hotspot-item full-width">
              <div class="hotspot-label">减排建议</div>
              <div class="hotspot-suggestions">
                <div class="suggestion-item">• 推广公共交通和新能源车辆</div>
                <div class="suggestion-item">• 优化饮食结构，减少肉类消费</div>
                <div class="suggestion-item">• 提高能源利用效率</div>
                <div class="suggestion-item">• 增加绿色建筑比例</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="panel-item">
          <div class="panel-title">减碳排行榜</div>
          <div class="ranking-list">
            <div v-for="user in topUsers" :key="user.userId" class="ranking-item">
              <div class="ranking-rank" :class="'rank-' + user.rank">{{ user.rank }}</div>
              <div class="ranking-name">{{ user.username }}</div>
              <div class="ranking-value">{{ user.totalReduction?.toFixed(2) }} kg</div>
            </div>
          </div>
        </div>

        <div class="panel-item">
          <div class="panel-title">区域统计</div>
          <div ref="regionChart" class="chart-container"></div>
        </div>

        <div class="panel-item">
          <div class="panel-title">减排趋势</div>
          <div class="trend-info">
            <div class="trend-item">
              <div class="trend-label">本月减排目标</div>
              <div class="trend-value">{{ overview.totalReduction ? (overview.totalReduction * 1.2).toFixed(2) : '0.00' }} kg</div>
            </div>
            <div class="trend-item">
              <div class="trend-label">已完成</div>
              <div class="trend-value" :class="{ 'positive': overview.totalReduction > 5000 }">
                {{ overview.totalReduction ? overview.totalReduction.toFixed(2) : '0.00' }} kg
              </div>
            </div>
            <div class="trend-item">
              <div class="trend-label">完成率</div>
              <div class="trend-value" :class="{ 'positive': overview.totalReduction > 5000 }">
                {{ overview.totalReduction ? Math.min(100, (overview.totalReduction / (overview.totalReduction * 1.2 || 1) * 100)).toFixed(1) : '0.0' }}%
              </div>
            </div>
          </div>
        </div>

        <div class="panel-item">
          <div class="panel-title">
            实时动态
            <button class="refresh-btn" @click="refreshRealTimeData">刷新</button>
          </div>
          <div class="activity-list">
            <div v-for="activity in realTimeActivities.slice(0, 10)" :key="activity.time" class="activity-item">
              <div class="activity-time">{{ activity.time }}</div>
              <div class="activity-content">
                <span class="activity-user">{{ activity.username }}</span>
                <span class="activity-action">{{ activity.activity }}</span>
                <span class="activity-emission">{{ activity.emission.toFixed(2) }} kg</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { dashboardApi } from '../api'
import { UserFilled, User, House, CollectionTag, SwitchButton, ArrowDown, Setting, DataLine, ArrowLeft } from '@element-plus/icons-vue'

interface OverviewStats {
  totalUsers: number
  totalEmission: number
  totalReduction: number
  totalPoints: number
  avgDailyEmission: number
  activeUsersToday: number
}

interface EmissionTrend {
  date: string
  emission: number
  transportEmission: number
  dietEmission: number
  electricityEmission: number
}

interface CategoryDistribution {
  category: string
  value: number
  percentage: number
}

interface TopUser {
  userId: number
  username: string
  totalPoints: number
  totalReduction: number
  rank: number
}

interface RegionalStats {
  region: string
  userCount: number
  totalEmission: number
  avgEmission: number
}

interface RealTimeActivity {
  time: string
  username: string
  activity: string
  emission: number
  type: string
}

const currentTime = ref('')
const overview = ref<OverviewStats>({
  totalUsers: 0,
  totalEmission: 0,
  totalReduction: 0,
  totalPoints: 0,
  avgDailyEmission: 0,
  activeUsersToday: 0
})
const emissionTrends = ref<EmissionTrend[]>([])
const categoryDistribution = ref<CategoryDistribution[]>([])
const topUsers = ref<TopUser[]>([])
const regionalStats = ref<RegionalStats[]>([])
const realTimeActivities = ref<RealTimeActivity[]>([])

const trendChart = ref<HTMLElement>()
const categoryChart = ref<HTMLElement>()
const mapChart = ref<HTMLElement>()
const regionChart = ref<HTMLElement>()
const hotspotChart = ref<HTMLElement>()

let trendChartInstance: echarts.ECharts | null = null
let categoryChartInstance: echarts.ECharts | null = null
let mapChartInstance: echarts.ECharts | null = null
let regionChartInstance: echarts.ECharts | null = null
let timer: number | null = null
let dataRefreshTimer: number | null = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const fetchDashboardData = async () => {
  try {
    const response = await dashboardApi.getDashboardData()
    overview.value = response.overview
    emissionTrends.value = response.emissionTrends
    categoryDistribution.value = response.categoryDistribution
    topUsers.value = response.topUsers
    regionalStats.value = response.regionalStats
    realTimeActivities.value = response.realTimeActivities
    
    // 限制实时动态最多显示10条
    if (realTimeActivities.value.length > 10) {
      realTimeActivities.value = realTimeActivities.value.slice(0, 10)
    }
    
    // 如果数据为空，使用案例数据
    if (regionalStats.value.length === 0) {
      regionalStats.value = getMockRegionalStats()
    }
    if (topUsers.value.length === 0) {
      topUsers.value = getMockTopUsers()
    }
    if (realTimeActivities.value.length === 0) {
      realTimeActivities.value = getMockRealTimeActivities().slice(0, 10)
    }
    if (emissionTrends.value.length === 0) {
      emissionTrends.value = getMockEmissionTrends()
    }
    if (categoryDistribution.value.length === 0) {
      categoryDistribution.value = getMockCategoryDistribution()
    }
    
    updateCharts()
  } catch (error: any) {
    console.error('获取大屏数据失败:', error)
    // 使用案例数据
    overview.value = getMockOverview()
    emissionTrends.value = getMockEmissionTrends()
    categoryDistribution.value = getMockCategoryDistribution()
    topUsers.value = getMockTopUsers()
    regionalStats.value = getMockRegionalStats()
    realTimeActivities.value = getMockRealTimeActivities().slice(0, 10)
    updateCharts()
  }
}

const getMockOverview = (): OverviewStats => {
  return {
    totalUsers: 12580,
    totalEmission: 45678.5,
    totalReduction: 12345.8,
    totalPoints: 89650,
    avgDailyEmission: 3.63,
    activeUsersToday: 892
  }
}

const getMockEmissionTrends = (): EmissionTrend[] => {
  const trends: EmissionTrend[] = []
  const now = new Date()
  for (let i = 29; i >= 0; i--) {
    const date = new Date(now)
    date.setDate(date.getDate() - i)
    const baseEmission = 1500 + Math.random() * 500
    trends.push({
      date: date.toISOString().split('T')[0],
      emission: baseEmission,
      transportEmission: baseEmission * 0.4 + Math.random() * 100,
      dietEmission: baseEmission * 0.35 + Math.random() * 100,
      electricityEmission: baseEmission * 0.25 + Math.random() * 100
    })
  }
  return trends
}

const getMockCategoryDistribution = (): CategoryDistribution[] => {
  return [
    { category: '交通排放', value: 18271.4, percentage: 40 },
    { category: '饮食排放', value: 15987.4, percentage: 35 },
    { category: '用电排放', value: 11419.7, percentage: 25 }
  ]
}

const getMockTopUsers = (): TopUser[] => {
  return [
    { userId: 1, username: '环保达人', totalPoints: 12580, totalReduction: 1258.5, rank: 1 },
    { userId: 2, username: '绿色先锋', totalPoints: 11250, totalReduction: 1125.0, rank: 2 },
    { userId: 3, username: '低碳生活', totalPoints: 9870, totalReduction: 987.0, rank: 3 },
    { userId: 4, username: '节能专家', totalPoints: 8640, totalReduction: 864.0, rank: 4 },
    { userId: 5, username: '减排先锋', totalPoints: 7320, totalReduction: 732.0, rank: 5 },
    { userId: 6, username: '绿色使者', totalPoints: 6580, totalReduction: 658.0, rank: 6 },
    { userId: 7, username: '环保卫士', totalPoints: 5940, totalReduction: 594.0, rank: 7 },
    { userId: 8, username: '低碳达人', totalPoints: 5210, totalReduction: 521.0, rank: 8 }
  ]
}

const getMockRegionalStats = (): RegionalStats[] => {
  return [
    { region: '广东', userCount: 2850, totalEmission: 10260.5, avgEmission: 3.6 },
    { region: '江苏', userCount: 2140, totalEmission: 7704.0, avgEmission: 3.6 },
    { region: '浙江', userCount: 1890, totalEmission: 6804.0, avgEmission: 3.6 },
    { region: '山东', userCount: 1630, totalEmission: 5868.0, avgEmission: 3.6 },
    { region: '河南', userCount: 1260, totalEmission: 4536.0, avgEmission: 3.6 },
    { region: '四川', userCount: 1140, totalEmission: 4104.0, avgEmission: 3.6 },
    { region: '湖北', userCount: 980, totalEmission: 3528.0, avgEmission: 3.6 },
    { region: '福建', userCount: 850, totalEmission: 3060.0, avgEmission: 3.6 },
    { region: '湖南', userCount: 720, totalEmission: 2592.0, avgEmission: 3.6 },
    { region: '安徽', userCount: 680, totalEmission: 2448.0, avgEmission: 3.6 },
    { region: '北京', userCount: 620, totalEmission: 2232.0, avgEmission: 3.6 },
    { region: '上海', userCount: 580, totalEmission: 2088.0, avgEmission: 3.6 },
    { region: '河北', userCount: 540, totalEmission: 1944.0, avgEmission: 3.6 },
    { region: '江西', userCount: 490, totalEmission: 1764.0, avgEmission: 3.6 },
    { region: '重庆', userCount: 450, totalEmission: 1620.0, avgEmission: 3.6 },
    { region: '辽宁', userCount: 410, totalEmission: 1476.0, avgEmission: 3.6 },
    { region: '陕西', userCount: 380, totalEmission: 1368.0, avgEmission: 3.6 },
    { region: '云南', userCount: 340, totalEmission: 1224.0, avgEmission: 3.6 },
    { region: '广西', userCount: 310, totalEmission: 1116.0, avgEmission: 3.6 },
    { region: '山西', userCount: 280, totalEmission: 1008.0, avgEmission: 3.6 },
    { region: '内蒙古', userCount: 250, totalEmission: 900.0, avgEmission: 3.6 },
    { region: '吉林', userCount: 220, totalEmission: 792.0, avgEmission: 3.6 },
    { region: '黑龙江', userCount: 190, totalEmission: 684.0, avgEmission: 3.6 },
    { region: '贵州', userCount: 170, totalEmission: 612.0, avgEmission: 3.6 },
    { region: '新疆', userCount: 150, totalEmission: 540.0, avgEmission: 3.6 },
    { region: '甘肃', userCount: 130, totalEmission: 468.0, avgEmission: 3.6 },
    { region: '海南', userCount: 110, totalEmission: 396.0, avgEmission: 3.6 },
    { region: '宁夏', userCount: 90, totalEmission: 324.0, avgEmission: 3.6 },
    { region: '青海', userCount: 70, totalEmission: 252.0, avgEmission: 3.6 },
    { region: '西藏', userCount: 50, totalEmission: 180.0, avgEmission: 3.6 },
    { region: '天津', userCount: 60, totalEmission: 216.0, avgEmission: 3.6 },
    { region: '香港', userCount: 40, totalEmission: 144.0, avgEmission: 3.6 },
    { region: '澳门', userCount: 20, totalEmission: 72.0, avgEmission: 3.6 },
    { region: '台湾', userCount: 30, totalEmission: 108.0, avgEmission: 3.6 }
  ]
}

const getMockRealTimeActivities = (): RealTimeActivity[] => {
  const activities: RealTimeActivity[] = []
  const actions = ['记录了交通排放', '记录了饮食排放', '记录了用电排放', '完成了减排目标', '获得了积分奖励']
  const usernames = ['环保达人', '绿色先锋', '低碳生活', '节能专家', '减排先锋', '绿色使者', '环保卫士', '低碳达人']
  
  const now = new Date()
  for (let i = 0; i < 10; i++) {
    const time = new Date(now)
    time.setMinutes(time.getMinutes() - i * 5)
    activities.push({
      time: time.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      username: usernames[Math.floor(Math.random() * usernames.length)],
      activity: actions[Math.floor(Math.random() * actions.length)],
      emission: Math.random() * 5 + 1,
      type: ['transport', 'diet', 'electricity'][Math.floor(Math.random() * 3)]
    })
  }
  return activities
}

const initTrendChart = () => {
  if (!trendChart.value) return
  
  trendChartInstance = echarts.init(trendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#4CAF50',
      textStyle: { color: '#2e7d32' }
    },
    legend: {
      data: ['总排放', '交通', '饮食', '用电'],
      textStyle: { color: '#2e7d32' }
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
      data: emissionTrends.value.map(t => t.date),
      axisLabel: { color: '#2e7d32' },
      axisLine: { lineStyle: { color: '#81c784' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#2e7d32' },
      axisLine: { lineStyle: { color: '#81c784' } },
      splitLine: { lineStyle: { color: 'rgba(76, 175, 80, 0.1)' } }
    },
    series: [
      {
        name: '总排放',
        type: 'line',
        smooth: true,
        data: emissionTrends.value.map(t => t.emission),
        itemStyle: { color: '#2e7d32' }
      },
      {
        name: '交通',
        type: 'line',
        smooth: true,
        data: emissionTrends.value.map(t => t.transportEmission),
        itemStyle: { color: '#4CAF50' }
      },
      {
        name: '饮食',
        type: 'line',
        smooth: true,
        data: emissionTrends.value.map(t => t.dietEmission),
        itemStyle: { color: '#81c784' }
      },
      {
        name: '用电',
        type: 'line',
        smooth: true,
        data: emissionTrends.value.map(t => t.electricityEmission),
        itemStyle: { color: '#a5d6a7' }
      }
    ]
  }
  
  trendChartInstance.setOption(option)
}

const initCategoryChart = () => {
  if (!categoryChart.value) return
  
  categoryChartInstance = echarts.init(categoryChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} kg ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#4CAF50',
      textStyle: { color: '#2e7d32' }
    },
    legend: {
      show: false
    },
    series: [
      {
        type: 'pie',
        radius: '45%',
        center: ['50%', '50%'],
        data: categoryDistribution.value.map(c => ({
          name: c.category,
          value: c.value
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(76, 175, 80, 0.5)'
          }
        },
        itemStyle: {
          color: (params: any) => {
            const colors = ['#4CAF50', '#81c784', '#a5d6a7']
            return colors[params.dataIndex % colors.length]
          }
        },
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}\n{d}%',
          color: '#2e7d32',
          fontSize: 13,
          fontWeight: 'bold'
        },
        labelLine: {
          show: true,
          length: 15,
          length2: 20,
          lineStyle: {
            color: '#2e7d32',
            width: 1.5
          }
        }
      }
    ]
  }
  
  categoryChartInstance.setOption(option)
}

const initMapChart = () => {
  if (!mapChart.value) return
  
  mapChartInstance = echarts.init(mapChart.value)
  
  // 使用柱状图模拟地图效果
  const sortedRegions = [...regionalStats.value].sort((a, b) => b.userCount - a.userCount).slice(0, 10)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#4CAF50',
      textStyle: { color: '#2e7d32' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: { 
        color: '#2e7d32',
        formatter: '{value}'
      },
      axisLine: { lineStyle: { color: '#81c784' } },
      splitLine: { lineStyle: { color: 'rgba(76, 175, 80, 0.1)' } }
    },
    yAxis: {
      type: 'category',
      data: sortedRegions.map(r => r.region),
      axisLabel: { 
        color: '#2e7d32'
      },
      axisLine: { lineStyle: { color: '#81c784' } }
    },
    series: [
      {
        name: '用户数量',
        type: 'bar',
        data: sortedRegions.map(r => r.userCount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#4CAF50' },
            { offset: 0.5, color: '#81c784' },
            { offset: 1, color: '#a5d6a7' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          color: '#2e7d32',
          formatter: '{c}'
        },
        animationDuration: 1000,
        animationEasing: 'cubicOut'
      }
    ]
  }
  
  mapChartInstance.setOption(option)
}

const initRegionChart = () => {
  if (!regionChart.value) return
  
  regionChartInstance = echarts.init(regionChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#4CAF50',
      textStyle: { color: '#2e7d32' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: { 
        color: '#2e7d32' 
      },
      axisLine: { lineStyle: { color: '#81c784' } },
      splitLine: { lineStyle: { color: 'rgba(76, 175, 80, 0.1)' } }
    },
    yAxis: {
      type: 'category',
      data: regionalStats.value.slice(0, 5).map(r => r.region),
      axisLabel: { 
        color: '#2e7d32' 
      },
      axisLine: { lineStyle: { color: '#81c784' } }
    },
    series: [
      {
        name: '用户数',
        type: 'bar',
        data: regionalStats.value.slice(0, 5).map(r => r.userCount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#4CAF50' },
            { offset: 1, color: '#81c784' }
          ])
        }
      }
    ]
  }
  
  regionChartInstance.setOption(option)
}

const initHotspotChart = () => {
  if (!hotspotChart.value) return
  
  const hotspotChartInstance = echarts.init(hotspotChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#4CAF50',
      textStyle: { color: '#2e7d32' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月'],
      axisLabel: { 
        color: '#2e7d32' 
      },
      axisLine: { lineStyle: { color: '#81c784' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { 
        color: '#2e7d32',
        formatter: '{value}',
        fontSize: 10,
        interval: 'auto'
      },
      axisLine: { lineStyle: { color: '#81c784' } },
      splitLine: { lineStyle: { color: 'rgba(76, 175, 80, 0.1)' } },
      min: 1000,
      max: 3000,
      interval: 500
    },
    series: [
      {
        data: [1200, 1900, 1500, 2100, 1800, 2500],
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#4CAF50',
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(76, 175, 80, 0.3)' },
            { offset: 1, color: 'rgba(76, 175, 80, 0.1)' }
          ])
        }
      }
    ]
  }
  
  hotspotChartInstance.setOption(option)
}

const updateCharts = () => {
  initTrendChart()
  initCategoryChart()
  initMapChart()
  initRegionChart()
  initHotspotChart()
}

// 生成单条实时动态记录
const generateSingleActivity = (): RealTimeActivity => {
  const actions = ['记录了交通排放', '记录了饮食排放', '记录了用电排放', '完成了减排目标', '获得了积分奖励']
  const usernames = ['环保达人', '绿色先锋', '低碳生活', '节能专家', '减排先锋', '绿色使者', '环保卫士', '低碳达人']
  
  return {
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    username: usernames[Math.floor(Math.random() * usernames.length)],
    activity: actions[Math.floor(Math.random() * actions.length)],
    emission: Math.random() * 5 + 1,
    type: ['transport', 'diet', 'electricity'][Math.floor(Math.random() * 3)]
  }
}

const refreshRealTimeData = () => {
  // 生成单条新记录
  const newActivity = generateSingleActivity()
  // 使用splice方法在数组开头添加新元素
  realTimeActivities.value.splice(0, 0, newActivity)
  // 严格限制最多10条，删除多余的
  while (realTimeActivities.value.length > 10) {
    realTimeActivities.value.pop()
  }
  
  // 模拟数据动态变化
  if (overview.value.totalEmission) {
    overview.value.totalEmission += Math.random() * 10 - 5
  }
  if (overview.value.totalReduction) {
    overview.value.totalReduction += Math.random() * 2 - 1
  }
  if (overview.value.activeUsersToday) {
    overview.value.activeUsersToday += Math.floor(Math.random() * 3) - 1
  }
}

const startDataRefresh = () => {
  // 手动刷新模式，不启动自动定时器
  console.log('实时动态已切换到手动刷新模式')
}

const handleResize = () => {
  trendChartInstance?.resize()
  categoryChartInstance?.resize()
  mapChartInstance?.resize()
  regionChartInstance?.resize()
  // 热点图表也需要调整大小
  const hotspotChartElement = document.querySelector('.hotspot-chart')
  if (hotspotChartElement) {
    const chart = echarts.getInstanceByDom(hotspotChartElement)
    chart?.resize()
  }
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
  
  fetchDashboardData()
  startDataRefresh()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  
  if (dataRefreshTimer) {
    clearInterval(dataRefreshTimer)
  }
  
  trendChartInstance?.dispose()
  categoryChartInstance?.dispose()
  mapChartInstance?.dispose()
  regionChartInstance?.dispose()
  
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 强制100%缩放 */
:deep(html) {
  zoom: 100%;
  text-size-adjust: 100%;
  -webkit-text-size-adjust: 100%;
}

.dashboard-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fff9 0%, #e8f5e8 50%, #d4edda 100%);
  color: #2e7d32;
  overflow-y: auto;
  overflow-x: hidden;
  /* 固定字体大小，不受浏览器缩放影响 */
  font-size: 16px;
}

.dashboard-header {
  height: 80px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  background: rgba(76, 175, 80, 0.1);
  border-bottom: 2px solid rgba(76, 175, 80, 0.3);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.title-link {
  text-decoration: none;
  color: inherit;
  display: inline-block;
  transition: all 0.3s ease;
}

.title-link:hover h1 {
  background: linear-gradient(90deg, #4CAF50, #81C784);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transform: translateY(-2px);
}

.dashboard-header h1 {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, #2e7d32, #4CAF50);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  transition: all 0.3s ease;
}

.current-time {
  font-size: 18px;
  color: #2e7d32;
  font-weight: 500;
}

.dashboard-content {
  min-height: calc(100vh - 80px);
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  padding: 20px;
  gap: 20px;
}

.left-panel,
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 280px;
}

.center-panel {
  display: flex;
  flex-direction: column;
  min-width: 400px;
}

.panel-item {
  background: white;
  border: 1px solid rgba(76, 175, 80, 0.2);
  border-radius: 10px;
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.panel-item:hover {
  box-shadow: 0 6px 12px rgba(76, 175, 80, 0.15);
  transform: translateY(-2px);
}

.panel-item.large {
  flex: 1;
}

.panel-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(76, 175, 80, 0.3);
  color: #2e7d32;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.refresh-btn {
  background: linear-gradient(135deg, #4CAF50, #81c784);
  color: white;
  border: none;
  padding: 5px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background: linear-gradient(135deg, #45a049, #66bb6a);
  transform: scale(1.05);
}

.chart-container {
  flex: 1;
  min-height: 200px;
}

.chart-container.large {
  min-height: 400px;
}

.overview-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  flex: 1;
}

.stat-item {
  background: rgba(76, 175, 80, 0.05);
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  border: 1px solid rgba(76, 175, 80, 0.1);
  animation: fadeIn 0.6s ease-out;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(76, 175, 80, 0.1);
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.1);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #2e7d32;
  margin-bottom: 5px;
  animation: pulse 2s ease-in-out infinite;
}

.stat-label {
  font-size: 14px;
  color: #66bb6a;
}

.ranking-list {
  flex: 1;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 10px;
  background: rgba(76, 175, 80, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(76, 175, 80, 0.1);
  animation: fadeIn 0.5s ease-out;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  background: rgba(76, 175, 80, 0.15);
  transform: translateX(5px);
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.1);
}

.ranking-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 12px;
  background: rgba(76, 175, 80, 0.2);
  color: #2e7d32;
  font-size: 14px;
}

.ranking-rank.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #000;
}

.ranking-rank.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  color: #000;
}

.ranking-rank.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #daa06d);
  color: #000;
}

.ranking-name {
  flex: 1;
  font-size: 14px;
  color: #2e7d32;
  font-weight: 500;
}

.ranking-value {
  font-size: 14px;
  color: #2e7d32;
  font-weight: 600;
}

.activity-list {
  flex: 1;
  overflow-y: auto;
}

.activity-item {
  padding: 12px;
  margin-bottom: 10px;
  background: rgba(76, 175, 80, 0.05);
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
  border: 1px solid rgba(76, 175, 80, 0.1);
  animation: slideIn 0.5s ease-out;
  transition: all 0.3s ease;
}

.activity-item:hover {
  background: rgba(76, 175, 80, 0.15);
  border-left-color: #2e7d32;
  box-shadow: 0 2px 4px rgba(76, 175, 80, 0.1);
}

.activity-time {
  font-size: 12px;
  color: #81c784;
  margin-bottom: 6px;
}

.activity-content {
  font-size: 14px;
  line-height: 1.4;
}

.activity-user {
  color: #2e7d32;
  font-weight: 600;
  margin-right: 5px;
}

.activity-action {
  color: #4caf50;
  margin-right: 5px;
}

.activity-emission {
  color: #1b5e20;
  font-weight: 500;
}

/* 减排趋势样式 */
.trend-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 15px;
  background: rgba(76, 175, 80, 0.05);
  border-radius: 8px;
}

.trend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  border-left: 4px solid #4CAF50;
  transition: all 0.3s ease;
}

.trend-item:hover {
  background: rgba(76, 175, 80, 0.1);
  transform: translateX(5px);
}

.trend-label {
  font-size: 14px;
  color: #2e7d32;
  font-weight: 500;
}

.trend-value {
  font-size: 16px;
  font-weight: bold;
  color: #4CAF50;
}

.trend-value.positive {
  color: #2e7d32;
  animation: pulse 2s infinite;
}

/* 碳排放热点分析样式 */
.hotspot-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  padding: 15px;
  background: rgba(76, 175, 80, 0.05);
  border-radius: 8px;
}

.hotspot-item {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  padding: 15px;
  border-left: 4px solid #4CAF50;
  transition: all 0.3s ease;
}

.hotspot-item:hover {
  background: rgba(76, 175, 80, 0.1);
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.1);
}

.hotspot-item.full-width {
  grid-column: span 2;
}

.hotspot-label {
  font-size: 13px;
  color: #2e7d32;
  font-weight: 500;
  margin-bottom: 8px;
}

.hotspot-value {
  font-size: 18px;
  font-weight: bold;
  color: #1b5e20;
  background: linear-gradient(90deg, #4CAF50, #81c784);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 4px;
}

.hotspot-detail {
  font-size: 12px;
  color: #81c784;
  font-weight: 500;
}

.hotspot-chart {
  height: 180px;
  margin-top: 10px;
  border-radius: 4px;
  background: rgba(248, 255, 249, 0.8);
}

.hotspot-suggestions {
  margin-top: 10px;
}

.suggestion-item {
  font-size: 13px;
  color: #2e7d32;
  margin-bottom: 6px;
  padding-left: 10px;
  position: relative;
}

.suggestion-item::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #4CAF50;
  font-weight: bold;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

@keyframes slideIn {
  from {
    transform: translateX(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-content {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
  }
  
  .left-panel,
  .center-panel,
  .right-panel {
    min-width: 100%;
  }
  
  .center-panel {
    grid-row: 1;
  }
  
  .left-panel {
    grid-row: 2;
  }
  
  .right-panel {
    grid-row: 3;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 15px;
    height: 60px;
  }
  
  .dashboard-header h1 {
    font-size: 18px;
  }
  
  .current-time {
    font-size: 14px;
  }
  
  .dashboard-content {
    padding: 10px;
    gap: 10px;
  }
  
  .current-time {
    font-size: 16px;
  }
  
  .dashboard-content {
    padding: 15px;
    gap: 15px;
  }
  
  .overview-stats {
    grid-template-columns: 1fr;
  }
  
  .panel-item {
    padding: 15px;
  }
}
</style>
