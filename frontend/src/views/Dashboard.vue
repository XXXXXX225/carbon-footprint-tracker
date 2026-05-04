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
        <RoleSidebar />
      </el-aside>
      <el-main class="dashboard-main">
        <div class="dashboard-shell page-shell">
          <section class="page-hero">
            <div class="page-section-header">
              <div>
                <p class="page-kicker">个人碳足迹管理</p>
                <h2 class="page-title">个人碳足迹仪表盘</h2>
                <p class="page-desc">按时间范围统一查看排放、积分和趋势变化，支持快速导出和钻取分析。</p>
              </div>
              <div class="page-actions">
                <el-button type="primary" @click="openGoalDialog">设置目标</el-button>
                <el-button type="primary" @click="handleExportAll">
                  <el-icon><Download /></el-icon>
                  导出完整报表
                </el-button>
              </div>
            </div>
          </section>

          <section class="page-section kpi-section">
            <div class="page-section-header kpi-header">
              <div>
                <p class="page-kicker">核心指标</p>
                <h3 class="page-title" style="font-size: 22px; margin-bottom: 0;">本期碳足迹总览</h3>
              </div>
              <div class="kpi-hint">突出总量和积分，辅助指标放在下层</div>
            </div>

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
            </div>

            <!-- 碳足迹概览卡片 -->
            <el-row :gutter="20" class="kpi-hero-row">
              <el-col :xs="24" :md="12" class="stagger-item delay-0">
                <el-card class="overview-card hero-overview-card glow-card">
                  <div class="overview-item">
                    <div class="overview-label">总碳足迹</div>
                    <div class="overview-value">{{ (realDashboardData?.overview?.totalEmission || 0).toFixed(2) }} kg CO₂e</div>
                    <div class="overview-subtitle">当前周期所有来源的综合排放</div>
                    <div class="overview-change" :class="{ positive: totalChange > 0 }">
                      <el-icon v-if="totalChange > 0"><ArrowDown /></el-icon>
                      <el-icon v-else><ArrowUp /></el-icon>
                      <span>{{ Math.abs(totalChange).toFixed(1) }}%</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :md="12" class="stagger-item delay-1">
                <el-card class="overview-card hero-overview-card points-card glow-card">
                  <div class="overview-badge">最活跃指标</div>
                  <div class="overview-item">
                    <div class="overview-label">减碳积分</div>
                    <div class="overview-value points-value">{{ totalPoints }}</div>
                    <div class="overview-subtitle">累计减碳 {{ totalEmissionReduced.toFixed(2) }} kg</div>
                    <div class="overview-change points-change">
                      <el-icon><Star /></el-icon>
                      <span>持续积累中</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
                        <!-- 碳减排目标 -->
            <el-row :gutter="20" class="kpi-hero-row" style="margin-top: 20px;">
              <el-col :span="24">
                <el-card class="overview-card glow-card" style="margin-bottom: 20px;">
                  <template #header>
                    <div class="card-header flexible-header">
                      <span class="card-title">碳减排目标</span>
                    </div>
                  </template>
                  <div v-if="!activeGoal" style="text-align: center; padding: 20px;">
                    <p style="margin-bottom: 10px; color: #666;">您当前没有正在进行的减排目标</p>
                    <el-button type="primary" @click="openGoalDialog">设置新目标</el-button>
                  </div>
                  <div v-else style="display: flex; align-items: center; justify-content: space-around; padding: 10px 0;">
                    <div>
                      <h4 style="margin: 0 0 10px 0; color: #409EFF;">目标减排: {{ activeGoal.targetPercentage }}%</h4>
                      <p style="margin: 5px 0; color: #666; font-size: 14px;"><strong>截止时间:</strong> {{ activeGoal.endDate }}</p>
                      <p style="margin: 5px 0; font-size: 14px;"><strong>基准排放:</strong> {{ activeGoal.baselineEmission.toFixed(2) }} kg</p>
                      <p style="margin: 5px 0; font-size: 14px;"><strong>目标排放:</strong> {{ activeGoal.targetEmission.toFixed(2) }} kg</p>
                    </div>
                    <div style="text-align: center;">
                      <el-progress 
                        type="dashboard" 
                        :percentage="progressPercentage" 
                        :color="[ {color: '#85ce61', percentage: 50}, {color: '#e6a23c', percentage: 80}, {color: '#f56c6c', percentage: 100} ]"
                      >
                        <template #default="{ percentage }">
                          <span style="display: block; font-size: 20px; font-weight: bold;">{{ percentage }}%</span>
                          <span style="font-size: 12px; color: #999;">额度消耗率</span>
                        </template>
                      </el-progress>
                      <div style="margin-top: 5px; font-size: 14px;">
                        <strong>当前排放:</strong> {{ (activeGoal.currentEmission || 0).toFixed(2) }} kg
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <el-row :gutter="20" class="kpi-secondary-row">
              <el-col :xs="24" :md="8" class="stagger-item delay-2">
                <el-card class="overview-card glow-card">
                  <div class="overview-item">
                    <div class="overview-label">交通排放</div>
                    <div class="overview-value">{{ realTransportEmission.toFixed(2) }} kg CO₂e</div>
                    <div class="overview-subtitle">出行方式对排放的影响</div>
                    <div class="overview-change" :class="{ positive: transportChange > 0 }">
                      <el-icon v-if="transportChange > 0"><ArrowDown /></el-icon>
                      <el-icon v-else><ArrowUp /></el-icon>
                      <span>{{ Math.abs(transportChange).toFixed(1) }}%</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :md="8" class="stagger-item delay-3">
                <el-card class="overview-card glow-card">
                  <div class="overview-item">
                    <div class="overview-label">饮食排放</div>
                    <div class="overview-value">{{ realDietEmission.toFixed(2) }} kg CO₂e</div>
                    <div class="overview-subtitle">饮食结构的碳排放变化</div>
                    <div class="overview-change" :class="{ positive: dietChange > 0 }">
                      <el-icon v-if="dietChange > 0"><ArrowDown /></el-icon>
                      <el-icon v-else><ArrowUp /></el-icon>
                      <span>{{ Math.abs(dietChange).toFixed(1) }}%</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :md="8" class="stagger-item delay-4">
                <el-card class="overview-card glow-card">
                  <div class="overview-item">
                    <div class="overview-label">用电排放</div>
                    <div class="overview-value">{{ realElectricityEmission.toFixed(2) }} kg CO₂e</div>
                    <div class="overview-subtitle">家庭和办公用电消耗</div>
                    <div class="overview-change" :class="{ positive: electricityChange > 0 }">
                      <el-icon v-if="electricityChange > 0"><ArrowDown /></el-icon>
                      <el-icon v-else><ArrowUp /></el-icon>
                      <span>{{ Math.abs(electricityChange).toFixed(1) }}%</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 新增：社交与等效转换视窗 -->
            <el-row :gutter="20" class="social-hero-row" style="margin-top: 20px;">
              <el-col :xs="24" :md="8" class="stagger-item delay-5">
                <el-card class="overview-card glow-card" style="height: 100%; border-left: 4px solid #4CAF50; display: flex; flex-direction: column; justify-content: center;">
                  <div class="mbti-container">
                    <div class="overview-label">碳足迹标签</div>
                    <div class="mbti-title" style="font-size: 24px; font-weight: bold; margin: 10px 0; color: #2c3e50;">{{ carbonMBTI.title }}</div>
                    <div class="mbti-desc" style="color: #7f8c8d; font-size: 14px; line-height: 1.5;">{{ carbonMBTI.desc }}</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :xs="24" :md="16" class="stagger-item delay-6">
                <el-card class="overview-card glow-card" style="height: 100%;">
                  <div class="overview-label" style="display:flex; justify-content: space-between;">
                    <span>累计减排等效收益 ({{ totalEmissionReduced }} kg)</span>
                    <el-tag size="small" type="warning" round>可前往积分商城兑换</el-tag>
                  </div>
                  <el-row :gutter="20" style="margin-top: 15px; text-align: center; align-items: center;">
                    <el-col :span="12" style="border-right: 1px dashed #ebeef5;">
                      <div class="eq-item">
                        <div style="font-size: 32px; font-weight: 800; color: #f56c6c; display: flex; align-items: baseline; justify-content: center;">
                          <span style="font-size:18px; margin-right:4px;">¥</span>{{ equivalentMoney }}
                        </div>
                        <div style="color: #909399; font-size: 13px; margin-top: 5px;">约合节省生活开支 (如油费/电费等)</div>
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="eq-item">
                        <div style="font-size: 32px; font-weight: 800; color: #e6a23c; display: flex; align-items: baseline; justify-content: center;">
                          {{ equivalentMilkTea }} <span style="font-size:16px; margin-left:4px;">杯</span>
                        </div>
                        <div style="color: #909399; font-size: 13px; margin-top: 5px;">相当于多燃烧了同等卡路理的奶茶</div>
                      </div>
                    </el-col>
                  </el-row>
                </el-card>
              </el-col>
            </el-row>
          </section>

          <section class="page-section saas-enhanced-section">
            <div class="page-section-header saas-section-header">
              <div>
                <p class="page-kicker modern-kicker">数据视图 <el-tag size="small" type="success" round effect="dark" style="margin-left:8px;">实时分析</el-tag></p>
                <h3 class="page-title modern-title">排放趋势与对比</h3>
              </div>
            </div>

            <!-- 数据可视化全能视窗 -->
            <el-row :gutter="24">
              <!-- 左侧沉浸式数据追踪 (包含折线与柱状图对比) -->
              <el-col :xs="24" :lg="15">
                <el-card class="chart-card saas-main-card">
                  <template #header>
                    <div class="card-header flexible-header">
                      <span class="card-title"><el-icon class="title-icon"><TrendCharts /></el-icon> 历史排放趋势与对比</span>
                    </div>
                  </template>
                  <CarbonChart 
                    type="line"
                    :data="lineData"
                    :height="320"
                    @drillDown="handleChartDrillDown"
                  />
                  
                  <el-divider border-style="dashed" style="margin: 20px 0;" />
                  
                  <div class="card-header flexible-header" style="margin-bottom: 12px;">
                    <span class="card-title"><el-icon class="title-icon"><Histogram /></el-icon> 跨周期排放对比</span>
                  </div>
                  <CarbonChart 
                    type="bar" 
                    :data="barData" 
                    :height="260" 
                    @drillDown="handleChartDrillDown" 
                  />
                </el-card>
              </el-col>

              <!-- 右侧组合面板: 目标达成与结构拆解 -->
              <el-col :xs="24" :lg="9">
                <el-card class="chart-card saas-side-card">
                  <template #header>
                    <div class="card-header flexible-header">
                      <span class="card-title"><el-icon class="title-icon"><Aim /></el-icon> 目标完成量与排放结构</span>
                      <el-button type="primary" link size="small" @click="openGoalDialog" style="font-weight:bold;">设置</el-button>
                    </div>
                  </template>
                  
                  <!-- 顶部进度仪 -->
                  <div class="goal-modern-content tracker-box">
                    <div class="ring-tracker">
                      <el-progress 
                        type="dashboard" 
                        :percentage="progress" 
                        :color="[ {color: '#f56c6c', percentage: 20}, {color: '#e6a23c', percentage: 50}, {color: '#85ce61', percentage: 80}, {color: '#4CAF50', percentage: 100} ]"
                        :width="190"
                        :stroke-width="16"
                      >
                        <template #default="{ percentage }">
                          <div class="progress-inner">
                            <span class="progress-val">{{ percentage }}<span class="pct">%</span></span>
                            <span class="progress-sub">目标完成度</span>
                          </div>
                        </template>
                      </el-progress>
                    </div>

                    <div class="goal-metrics mt-4">
                      <div class="metric-box">
                        <div class="metric-lbl">本月满额剩余</div>
                        <div class="metric-val gold">{{ remainingDays }} <span class="unit">天</span></div>
                      </div>
                      <div class="divider-col"></div>
                      <div class="metric-box">
                        <div class="metric-lbl">当前减排目标</div>
                        <div class="metric-val green">{{ reductionGoal }} <span class="unit">%</span></div>
                      </div>
                    </div>
                  </div>

                  <el-divider border-style="dashed" style="margin: 24px 0 12px 0;" />

                  <!-- 下部饼图 -->
                  <div class="card-header flexible-header" style="margin-bottom: 8px;">
                    <span class="card-title" style="font-size: 15px;"><el-icon class="title-icon" style="font-size: 16px;"><PieChart /></el-icon> 碳足迹分类占比</span>
                  </div>
                  <CarbonChart 
                    type="pie"
                    :data="pieData"
                    :height="260"
                    @drillDown="handleChartDrillDown"
                  />
                  <div class="pie-insight">
                    <p style="margin: 0; color: #6b7280; font-size: 13px; text-align: center; padding-top: 10px;">
                       提示: 点击分类图表可钻取详细记录
                    </p>
                  </div>

                </el-card>
              </el-col>
            </el-row>
          </section>

          
        </div>
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
          <el-date-picker v-model="goalForm.deadline" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
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
import RoleSidebar from '../components/RoleSidebar.vue'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, ArrowUp, Download, Document, CollectionTag, TrendCharts, Aim, PieChart, Histogram } from '@element-plus/icons-vue'
import { ExportService, type ExportData } from '../utils/export'
import { ElMessage } from 'element-plus'
import { goalApi, dashboardApi, type ReductionGoal } from '@/api'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/dashboard')
const goalDialogVisible = ref(false)
const activeGoal = ref<ReductionGoal | null>(null)
const progressPercentage = computed(() => {
  if (!activeGoal.value) return 0;
  const current = activeGoal.value.currentEmission || 0;
  const target = activeGoal.value.targetEmission || 1;
  return Math.min(100, Math.round((current / target) * 100));
})

const loadActiveGoal = async () => {
  try {
    const res = await goalApi.getActiveGoal();
    activeGoal.value = res;
  } catch (error: any) {
    // Ignore 404 or other errors for now, maybe notify
  }
}

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
  loadActiveGoal()
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

const realTransportEmission = computed(() => {
  const data = realDashboardData.value;
  if (data && data.emissionTrends && data.emissionTrends.length > 0) {
    return data.emissionTrends.reduce((sum: number, item: any) => sum + (item.transportEmission || 0), 0);
  }
  return 0;
});

const realDietEmission = computed(() => {
  const data = realDashboardData.value;
  if (data && data.emissionTrends && data.emissionTrends.length > 0) {
    return data.emissionTrends.reduce((sum: number, item: any) => sum + (item.dietEmission || 0), 0);
  }
  return 0;
});

const realElectricityEmission = computed(() => {
  const data = realDashboardData.value;
  if (data && data.emissionTrends && data.emissionTrends.length > 0) {
    return data.emissionTrends.reduce((sum: number, item: any) => sum + (item.electricityEmission || 0), 0);
  }
  return 0;
});

const progress = computed(() => {
  const currentMonthRecords = carbonStore.monthlyRecords || []
  const monthlyTotal = currentMonthRecords.reduce((sum, r) => sum + r.value, 0)
  
  // 假设每月基础碳排放限额 450kg，再结合减排目标计算出当月可用额度
  const monthlyBaseline = 450
  const targetEmission = monthlyBaseline * (1 - reductionGoal.value / 100)
  
  if (targetEmission <= 0) return 0
  
  // 依据剩余可用额度计算完成度：(剩余额度 / 目标额度) * 100
  let percentage = ((targetEmission - monthlyTotal) / targetEmission) * 100
  return Math.max(0, Math.min(100, Math.round(percentage)))
})

const remainingDays = computed(() => {
  const today = new Date()
  const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0)
  return endOfMonth.getDate() - today.getDate()
})
const totalChange = ref(-5.2)
const transportChange = ref(3.1)
const dietChange = ref(-1.8)
const electricityChange = ref(2.5)
const totalPoints = ref(0)
const totalEmissionReduced = ref(0)

// 衍生计算：碳足迹标签与等效收益
const carbonMBTI = computed(() => {
  const t = footprint.value.transport || 0
  const d = footprint.value.diet || 0
  const e = footprint.value.electricity || 0
  const total = totalFootprint.value || 0
  if (total === 0) return { title: '🌱 神秘隐身人', desc: '暂时还没有记录排放足迹' }
  const max = Math.max(t, d, e)
  if (max === t) return { title: '🚗 暴走流浪者', desc: '主要的排碳来自于出行，是在四处奔波吗？' }
  if (max === d) return { title: '🍔 高能干饭王', desc: '唯有美食与碳排不可辜负' }
  return { title: '⚡ 赛博充能狂', desc: '耗电大户，永远与屏幕灯光同在' }
})

const equivalentMoney = computed(() => {
  // 1kg = 省约 5.2 元 (电/油/综合参考)
  return (totalEmissionReduced.value * 5.2).toFixed(1)
})

const equivalentMilkTea = computed(() => {
  // 1kg 减碳相当于运动消耗补充的 120 千焦，一杯全糖奶茶约 300 千焦
  const kj = totalEmissionReduced.value * 120
  return (kj / 300).toFixed(1)
})

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
    { required: true, message: '请选择目标期限', trigger: 'change' }
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
  if (!realDashboardData.value || !realDashboardData.value.emissionTrends) {
    const labels = getTimeLabels(selectedRange.value)
    return {
      legend: ['总排放', '交通', '饮食', '用电'],
      xAxis: labels,
      series: [
        { name: '总排放', type: 'line', data: [], smooth: true, areaStyle: { color: 'rgba(76, 175, 80, 0.3)' }, lineStyle: { color: '#4CAF50' } },
        { name: '交通', type: 'line', data: [], smooth: true, lineStyle: { color: '#2196F3' } },
        { name: '饮食', type: 'line', data: [], smooth: true, lineStyle: { color: '#FF9800' } },
        { name: '用电', type: 'line', data: [], smooth: true, lineStyle: { color: '#9C27B0' } }
      ]
    }
  }

  const trends = realDashboardData.value.emissionTrends

  const labels = trends.map((item: any) => item.date)
  const total = trends.map((item: any) => item.emission)
  const transport = trends.map((item: any) => item.transportEmission)
  const diet = trends.map((item: any) => item.dietEmission)
  const electricity = trends.map((item: any) => item.electricityEmission)

  return {
    legend: ['总排放', '交通', '饮食', '用电'],
    xAxis: labels,
    series: [
      {
        name: '总排放',
        type: 'line',
        data: total,
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
        data: transport,
        smooth: true,
        lineStyle: { color: '#2196F3' }
      },
      {
        name: '饮食',
        type: 'line',
        data: diet,
        smooth: true,
        lineStyle: { color: '#FF9800' }
      },
      {
        name: '用电',
        type: 'line',
        data: electricity,
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
  await goalFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        await goalApi.createGoal(goalForm.value.reductionGoal, goalForm.value.deadline);
        ElMessage.success('目标设置成功');
        goalDialogVisible.value = false;
        loadActiveGoal();
      } catch (err: any) {
        ElMessage.error(err.message || '目标设置失败');
      }
    } else {
      console.warn("表单验证未通过，请检查填写内容");
    }
  })
}


const realDashboardData = ref<any>(null)

const handleTimeRangeChange = async (range: string) => {
  selectedRange.value = range
  try {
    const res = await dashboardApi.getDashboardData(range)
    // 直接赋值！因为你的 request 拦截器已经帮你把 data 提取出来了
    if (res) {
      realDashboardData.value = res
    }
  } catch (error) {
    console.error("Failed to fetch real data", error)
  }
}

import { onMounted } from "vue"
onMounted(() => {
  handleTimeRangeChange(selectedRange.value)
})


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

  const trends = realDashboardData.value?.emissionTrends || []
  const labels = trends.length > 0 ? trends.map((item: any) => item.date) : getTimeLabels(selectedRange.value)

  if (trends.length > 0) {
    trends.forEach((item: any) => {
      exportData.push({
        date: item.date,
        category: '交通',
        type: '交通排放',
        amount: item.transportEmission,
        description: '日常出行'
      })
      exportData.push({
        date: item.date,
        category: '饮食',
        type: '饮食排放',
        amount: item.dietEmission,
        description: '日常饮食'
      })
      exportData.push({
        date: item.date,
        category: '用电',
        type: '用电排放',
        amount: item.electricityEmission,
        description: '日常用电'
      })
    })
  } else {
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
  }

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
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&family=Fira+Code:wght@500;700&display=swap');


/* =========== SaaS Enhanced Data View Custom CSS =========== */
.saas-enhanced-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.03);
  margin-top: 20px;
}
.saas-section-header {
  margin-bottom: 24px;
}
.modern-kicker {
  font-size: 13px;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 600;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
}
.modern-title {
  font-size: 24px;
  font-weight: 800;
  color: #111827;
  letter-spacing: -0.5px;
}
.saas-main-card, .saas-side-card {
  border-radius: 14px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02) !important;
  transition: all 0.3s ease;
  height: 100%;
}
.saas-main-card:hover, .saas-side-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06) !important;
  border-color: #e2e8f0;
}
.flexible-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-icon {
  color: #4CAF50;
  font-size: 18px;
}
.tracker-box {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.ring-tracker {
  display: flex;
  justify-content: center;
  padding: 20px 0 10px 0;
}
.progress-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.progress-val {
  font-size: 38px;
  font-weight: 800;
  color: #111827;
  line-height: 1;
}
.pct {
  font-size: 18px;
  color: #6b7280;
  margin-left: 2px;
}
.progress-sub {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 8px;
  font-weight: 500;
}
.goal-metrics {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}
.metric-box {
  text-align: center;
  flex: 1;
}
.metric-lbl {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
  font-weight: 500;
}
.metric-val {
  font-size: 20px;
  font-weight: 800;
}
.metric-val.gold { color: #f59e0b; }
.metric-val.green { color: #10b981; }
.unit { font-size: 12px; font-weight: 600; opacity: 0.8; }
.divider-col {
  width: 1px;
  height: 36px;
  background: #e2e8f0;
  margin: 0 16px;
}


.dashboard-container {
  font-family: 'Noto Sans SC', sans-serif;
  min-height: 100vh;
  background: transparent !important;
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
  background-color: transparent !important;
}

.dashboard-menu {
  height: 100%;
  border-right: none;
  background-color: transparent !important;
}

.dashboard-main {
  padding: 20px;
}

.dashboard-shell {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.kpi-section {
  padding-bottom: 24px;
}

.kpi-header {
  margin-bottom: 16px;
}

.kpi-hint {
  color: #6b7b6e;
  font-size: 13px;
  background: rgba(76, 175, 80, 0.08);
  border: 1px solid rgba(76, 175, 80, 0.12);
  padding: 10px 14px;
  border-radius: 14px;
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

.kpi-hero-row {
  margin-top: 16px;
}

.kpi-secondary-row {
  margin-top: 20px;
}

.overview-card {
  min-height: 160px;
  border-radius: 16px;
}

.hero-overview-card {
  min-height: 180px;
}

.overview-item {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 120px;
  position: relative;
}

.overview-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.overview-value {
  font-family: 'Bahnschrift', sans-serif;
  font-size: 32px;
  font-weight: 900;
  color: #4CAF50;
  line-height: 1.2;
  margin-bottom: 8px;
  word-wrap: break-word;
}

.overview-subtitle {
  font-size: 13px;
  color: #8ba390;
  margin-bottom: 12px;
  flex: 1;
}

.overview-change {
  font-family: 'Bahnschrift', sans-serif;
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
  background: linear-gradient(135deg, rgba(236, 255, 239, 0.9) 0%, rgba(248, 255, 249, 0.8) 100%) !important;
  border-left: 4px solid #4CAF50 !important;
  box-shadow: 0 8px 32px rgba(76, 175, 80, 0.15) !important;
}

.points-value {
  font-family: 'Bahnschrift', sans-serif;
  color: #4CAF50;
  font-size: 36px;
  font-weight: bold;
}

.points-change {
  color: #4CAF50;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.overview-badge {
  position: absolute;
  top: 14px;
  right: 16px;
  font-size: 11px;
  color: #2e7d32;
  background: rgba(76, 175, 80, 0.12);
  border-radius: 999px;
  padding: 4px 10px;
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
    min-height: 140px;
  }

  .hero-overview-card {
    min-height: 160px;
  }
  
  .chart-card {
    min-height: 350px;
  }
}

@media (max-width: 992px) {
  .overview-card {
    min-height: 120px;
  }

  .hero-overview-card {
    min-height: 150px;
  }
  
  .overview-value {
    font-family: 'Bahnschrift', sans-serif;
  font-size: 20px;
  }

  .points-value {
    font-family: 'Bahnschrift', sans-serif;
  font-size: 26px;
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

  .hero-overview-card {
    height: auto;
    min-height: 120px;
  }
  
  .overview-item {
    padding: 15px 0;
  }
  
  .overview-value {
    font-family: 'Bahnschrift', sans-serif;
  font-size: 18px;
  }

  .points-value {
    font-family: 'Bahnschrift', sans-serif;
  font-size: 22px;
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
    font-family: 'Bahnschrift', sans-serif;
  font-size: 16px;
  }

  .points-value {
    font-family: 'Bahnschrift', sans-serif;
  font-size: 20px;
  }
  
  .overview-label {
    font-size: 12px;
  }
  
  .overview-change {
    font-family: 'Bahnschrift', sans-serif;
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
