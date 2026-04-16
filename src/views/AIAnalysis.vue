<template>
  <el-container class="ai-container">
    <el-header height="60px" class="dashboard-header">
      <div class="header-left">
        <router-link to="/home" class="logo-link">
          <el-icon class="brand-icon"><TrendCharts /></el-icon>
          <h1>碳足迹追踪平台</h1>
        </router-link>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar :size="30" :icon="UserFilled" />
            {{ user.name }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateToProfile">
                <el-icon><User /></el-icon>
                <span>个人中心</span>
              </el-dropdown-item>
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

      <el-main class="ai-main">
        <div class="page-hero">
          <div>
            <p class="eyebrow">AI 分析与预测</p>
            <h2>下一月碳排放预测与优化建议</h2>
            <p class="hero-copy">
              结合你的历史排放数据，自动生成趋势判断、风险提示和可执行的减排建议。
            </p>
          </div>
          <div style="display: flex; gap: 12px; align-items: center;">
            <el-button type="success" size="large" :loading="planGenerating" @click="generateWeeklyPlan">
              <el-icon style="margin-right: 6px;"><MagicStick /></el-icon>
              生成本周无痛减排计划
            </el-button>
            <el-button type="primary" size="large" :loading="loading" @click="refreshData">
              <el-icon style="margin-right: 6px;"><Refresh /></el-icon>
              刷新分析
            </el-button>
          </div>
        </div>

        <!-- AI 减排计划弹窗 -->
        <el-dialog v-model="planVisible" title="本周无痛减排计划 (AI生成)" width="650px" class="plan-dialog">
          <div v-if="weeklyPlan.length > 0">
            <p class="plan-intro">基于对你近期 <strong>出行</strong>、<strong>饮食</strong> 以及 <strong>用电</strong> 习惯的深度学习，减碳管家为你量身定制了未来 7 天的改善计划。</p>
            <el-timeline>
              <el-timeline-item
                v-for="(day, index) in weeklyPlan"
                :key="index"
                :type="day.type"
                :color="day.color"
                :icon="day.icon"
                :timestamp="day.dateStr"
                placement="top"
              >
                <el-card class="plan-card">
                  <h4>{{ day.title }}</h4>
                  <p>{{ day.description }}</p>
                  <div class="plan-meta">
                    <span class="reduction-estimate">
                      <el-icon><DataLine /></el-icon> 预计减排: {{ day.reduction }} kg CO₂e
                    </span>
                    <el-tag size="small" :type="day.tagType">{{ day.category }}</el-tag>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="planVisible = false">看完啦</el-button>
              <el-button type="success" @click="acceptPlan">
                <el-icon><Check /></el-icon> 接受计划并立即执行
              </el-button>
            </div>
          </template>
        </el-dialog>

        <el-alert
          v-if="!hasData && !loading"
          type="warning"
          show-icon
          :closable="false"
          title="当前历史数据较少，AI 预测结果会偏保守。"
          description="建议先录入交通、饮食和用电排放记录，再查看更稳定的趋势判断。"
          class="data-alert"
        />

        <el-alert
          v-if="aiAnalysisError"
          type="info"
          show-icon
          :closable="false"
          :title="aiAnalysisError"
          class="data-alert"
        />

        <el-alert
          v-else-if="isFallbackAnalysis"
          type="info"
          show-icon
          :closable="false"
          title="当前展示的是本地规则分析结果。"
          description="页面继续展示趋势判断、历史误差和减排建议，不依赖外部模型。"
          class="data-alert"
        />

        <el-skeleton v-if="loading" animated :rows="8" />

        <template v-else>
          <el-row :gutter="20" class="metric-row">
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card class="metric-card metric-card-primary">
                <div class="metric-label">本月总排放</div>
                <div class="metric-value">{{ currentEmission.toFixed(2) }}</div>
                <div class="metric-suffix">kg CO₂e</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card class="metric-card">
                <div class="metric-label">预测下月排放</div>
                <div class="metric-value">{{ predictedEmission.toFixed(2) }}</div>
                <div class="metric-suffix">kg CO₂e</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card class="metric-card">
                <div class="metric-label">预测变化</div>
                <div class="metric-value" :class="predictionDeltaClass">{{ predictionDeltaLabel }}</div>
                <div class="metric-suffix">相较本月</div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card class="metric-card">
                <div class="metric-label">预测置信度</div>
                <div class="metric-value">{{ predictionConfidence.toFixed(0) }}%</div>
                <div class="metric-suffix">{{ confidenceLabel }}</div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="chart-row">
            <el-col :xs="24" :lg="14">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span>未来 30 天预测曲线</span>
                  </div>
                </template>
                <CarbonChart
                  type="line"
                  :data="predictionLineData"
                  :height="340"
                  :showActions="false"
                />
              </el-card>
            </el-col>

            <el-col :xs="24" :lg="10">
              <el-card class="chart-card">
                <template #header>
                  <div class="card-header">
                    <span>当前排放结构</span>
                  </div>
                </template>
                <CarbonChart
                  type="pie"
                  :data="categoryPieData"
                  :height="340"
                  :showActions="false"
                />
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="analysis-row">
            <el-col :xs="24" :lg="10">
              <el-card class="analysis-card">
                <template #header>
                  <div class="card-header">
                    <span>AI 分析结论</span>
                  </div>
                </template>

                <div class="analysis-summary">
                  <h3>{{ analysisHeadline }}</h3>
                  <p>{{ analysisDescription }}</p>
                </div>

                <div class="analysis-meta">
                  <el-tag :type="aiAnalysis ? (aiAnalysis.riskLevel === 'HIGH' ? 'danger' : aiAnalysis.riskLevel === 'LOW' ? 'success' : 'warning') : 'info'">
                    {{ aiRiskLabel }}
                  </el-tag>
                  <span>{{ analysisMetaText }}</span>
                </div>

                <el-divider />

                <div class="insight-list">
                  <div v-for="item in analysisInsights" :key="item.title" class="insight-item">
                    <div class="insight-title">{{ item.title }}</div>
                    <div class="insight-text">{{ item.text }}</div>
                  </div>
                </div>

                <el-divider />

                <div v-if="prediction?.suggestion" class="suggestion-box">
                  <div class="suggestion-title">推荐优先执行</div>
                  <div class="suggestion-category">{{ prediction.suggestion.category }}</div>
                  <div class="suggestion-text">{{ prediction.suggestion.suggestion }}</div>
                  <div class="suggestion-meta">
                    <span>预计可减少 {{ prediction.suggestion.potentialReduction.toFixed(2) }} kg CO₂e</span>
                    <span>优先级 {{ prediction.suggestion.priority }}</span>
                  </div>
                </div>

                <div v-if="aiRecommendations.length > 0" class="suggestion-box" style="margin-top: 16px;">
                  <div class="suggestion-title">AI 建议动作</div>
                  <ul class="ai-action-list">
                    <li v-for="item in aiRecommendations" :key="item">{{ item }}</li>
                  </ul>
                </div>
              </el-card>
            </el-col>

            <el-col :xs="24" :lg="14">
              <el-card class="analysis-card">
                <template #header>
                  <div class="card-header">
                    <span>日级预测明细</span>
                  </div>
                </template>

                <el-table :data="dailyPredictions" style="width: 100%" max-height="420">
                  <el-table-column prop="date" label="日期" width="120" />
                  <el-table-column prop="predictedEmission" label="预测值" width="120">
                    <template #default="scope">
                      {{ scope.row.predictedEmission.toFixed(2) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="lowerBound" label="下限" width="120">
                    <template #default="scope">
                      {{ scope.row.lowerBound.toFixed(2) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="upperBound" label="上限" width="120">
                    <template #default="scope">
                      {{ scope.row.upperBound.toFixed(2) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="波动范围">
                    <template #default="scope">
                      {{ (scope.row.upperBound - scope.row.lowerBound).toFixed(2) }} kg CO₂e
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="analysis-row">
            <el-col :xs="24" :lg="14">
              <el-card class="analysis-card">
                <template #header>
                  <div class="card-header">
                    <span>预测 vs 实际</span>
                  </div>
                </template>

                <el-alert
                  v-if="completedHistoryRecords.length === 0"
                  type="info"
                  show-icon
                  :closable="false"
                  title="暂无可对比的历史记录"
                  description="当某个月份结束并生成实际月汇总后，这里会自动显示预测值与实际值的对比曲线。"
                />

                <CarbonChart
                  v-else
                  type="line"
                  :data="comparisonLineData"
                  :height="320"
                  :showActions="false"
                />
              </el-card>
            </el-col>

            <el-col :xs="24" :lg="10">
              <el-card class="analysis-card">
                <template #header>
                  <div class="card-header">
                    <span>预测历史记录</span>
                  </div>
                </template>

                <el-table :data="historyRecords" style="width: 100%" max-height="320">
                  <el-table-column prop="targetMonth" label="月份" width="100" />
                  <el-table-column prop="predictedEmission" label="预测值" width="100">
                    <template #default="scope">
                      {{ scope.row.predictedEmission.toFixed(2) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="actualEmission" label="实际值" width="100">
                    <template #default="scope">
                      {{ scope.row.actualEmission !== null ? scope.row.actualEmission.toFixed(2) : '待更新' }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="errorRate" label="误差率" width="90">
                    <template #default="scope">
                      {{ scope.row.errorRate !== null ? `${scope.row.errorRate.toFixed(1)}%` : '待更新' }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="90" />
                </el-table>
              </el-card>
            </el-col>
          </el-row>
        </template>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import CarbonChart from '../components/CarbonChart.vue'
import { aiAnalysisApi, carbonApi, predictionApi } from '../api'
import RoleSidebar from '../components/RoleSidebar.vue'
import {
  House,
  Van,
  KnifeFork,
  Lightning,
  DataLine,
  TrendCharts,
  Star,
  CollectionTag,
  ArrowDown,
  User,
  UserFilled,
  MagicStick,
  Refresh,
  Check,
  Calendar
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// AI 管家计划数据结构
interface PlanDay {
  dateStr: string
  title: string
  description: string
  reduction: number
  category: string
  type: string
  color: string
  tagType: string
  icon: any
}

interface EmissionSummary {
  totalEmission: number
  transportEmission: number
  dietEmission: number
  electricityEmission: number
  averageDailyEmission: number
  recordCount: number
}

interface PredictionSuggestion {
  category: string
  suggestion: string
  potentialReduction: number
  priority: number
}

interface DailyPrediction {
  date: string
  predictedEmission: number
  lowerBound: number
  upperBound: number
}

interface CarbonPrediction {
  predictedEmission: number
  confidence: number
  trend: string
  dailyPredictions: DailyPrediction[]
  suggestion: PredictionSuggestion | null
}

interface PredictionHistoryRecord {
  id: number
  targetMonth: string
  predictionDate: string
  predictedEmission: number
  confidence: number
  trend: string
  actualEmission: number | null
  absoluteError: number | null
  errorRate: number | null
  status: string
  createdAt: string
  updatedAt: string
}

interface AiAnalysisInsight {
  title: string
  text: string
}

interface AiAnalysisResult {
  model: string
  generatedAt: string
  headline: string
  summary: string
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  confidence: number
  insights: AiAnalysisInsight[]
  recommendations: string[]
  nextActions: string[]
  source: string
}

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/ai-analysis')
const loading = ref(false)
const summary = ref<EmissionSummary | null>(null)
const prediction = ref<CarbonPrediction | null>(null)
const historyRecords = ref<PredictionHistoryRecord[]>([])
const aiAnalysis = ref<AiAnalysisResult | null>(null)
const aiAnalysisError = ref('')

// 管家功能状态
const planGenerating = ref(false)
const planVisible = ref(false)
const weeklyPlan = ref<PlanDay[]>([])

const user = computed(() => carbonStore.user)

const hasData = computed(() => {
  return (summary.value?.recordCount || 0) > 0
})

const currentEmission = computed(() => summary.value?.totalEmission || 0)
const predictedEmission = computed(() => prediction.value?.predictedEmission || 0)
const predictionConfidence = computed(() => (prediction.value?.confidence || 0) * 100)

const predictionDelta = computed(() => predictedEmission.value - currentEmission.value)
const predictionDeltaPercent = computed(() => {
  if (currentEmission.value <= 0) return 0
  return (predictionDelta.value / currentEmission.value) * 100
})

const predictionDeltaLabel = computed(() => {
  const value = predictionDeltaPercent.value
  const sign = value > 0 ? '+' : ''
  return `${sign}${value.toFixed(1)}%`
})

const predictionDeltaClass = computed(() => {
  if (predictionDelta.value > 0) return 'metric-up'
  if (predictionDelta.value < 0) return 'metric-down'
  return ''
})

const confidenceLabel = computed(() => {
  if (predictionConfidence.value >= 80) return '高可信'
  if (predictionConfidence.value >= 60) return '中等可信'
  return '低可信'
})

const aiRiskLabel = computed(() => {
  if (aiAnalysis.value?.riskLevel === 'HIGH') return '高风险'
  if (aiAnalysis.value?.riskLevel === 'LOW') return '低风险'
  if (aiAnalysis.value?.riskLevel === 'MEDIUM') return '中风险'
  return '未知'
})

const isFallbackAnalysis = computed(() => aiAnalysis.value?.source === 'LOCAL_ANALYSIS')

const dailyPredictions = computed(() => prediction.value?.dailyPredictions || [])

const completedHistoryRecords = computed(() => {
  return [...historyRecords.value]
    .filter(item => item.actualEmission !== null)
    .sort((a, b) => a.targetMonth.localeCompare(b.targetMonth))
})

const comparisonLineData = computed(() => ({
  legend: ['预测值', '实际值'],
  xAxis: completedHistoryRecords.value.map(item => item.targetMonth),
  series: [
    {
      name: '预测值',
      type: 'line',
      data: completedHistoryRecords.value.map(item => item.predictedEmission),
      smooth: true,
      lineStyle: { color: '#4CAF50', width: 3 }
    },
    {
      name: '实际值',
      type: 'line',
      data: completedHistoryRecords.value.map(item => item.actualEmission),
      smooth: true,
      lineStyle: { color: '#2196F3', width: 3 }
    }
  ]
}))

const categoryPieData = computed(() => ({
  legend: ['交通', '饮食', '用电'],
  series: [
    {
      name: '交通',
      value: summary.value?.transportEmission || 0,
      itemStyle: { color: '#4CAF50' }
    },
    {
      name: '饮食',
      value: summary.value?.dietEmission || 0,
      itemStyle: { color: '#81C784' }
    },
    {
      name: '用电',
      value: summary.value?.electricityEmission || 0,
      itemStyle: { color: '#A5D6A7' }
    }
  ]
}))

const predictionLineData = computed(() => ({
  legend: ['预测值', '下限', '上限'],
  xAxis: dailyPredictions.value.map(item => formatDateLabel(item.date)),
  series: [
    {
      name: '预测值',
      type: 'line',
      data: dailyPredictions.value.map(item => item.predictedEmission),
      smooth: true,
      lineStyle: { color: '#4CAF50', width: 3 },
      areaStyle: {
        color: 'rgba(76, 175, 80, 0.12)'
      }
    },
    {
      name: '下限',
      type: 'line',
      data: dailyPredictions.value.map(item => item.lowerBound),
      smooth: true,
      lineStyle: { color: '#FFC107', width: 2, type: 'dashed' }
    },
    {
      name: '上限',
      type: 'line',
      data: dailyPredictions.value.map(item => item.upperBound),
      smooth: true,
      lineStyle: { color: '#F44336', width: 2, type: 'dashed' }
    }
  ]
}))

const analysisHeadline = computed(() => {
  if (aiAnalysis.value?.headline) return aiAnalysis.value.headline
  if (!prediction.value) return '等待数据分析'
  if (predictionDelta.value > 0) return '你的下月排放预计上升，需要提前干预'
  if (predictionDelta.value < 0) return '你的下月排放预计下降，当前策略有效'
  return '你的下月排放预计保持稳定'
})

const analysisDescription = computed(() => {
  if (aiAnalysis.value?.summary) {
    return aiAnalysis.value.summary
  }

  if (!prediction.value) {
    return '当前没有可用于建模的历史数据，请先录入交通、饮食和用电记录。'
  }

  const trendText = prediction.value.trend || '暂无趋势结论'
  const deltaText = predictionDelta.value > 0
    ? `预计比本月增加 ${Math.abs(predictionDelta.value).toFixed(2)} kg CO₂e。`
    : `预计比本月减少 ${Math.abs(predictionDelta.value).toFixed(2)} kg CO₂e。`

  return `${trendText}，模型判断下月总排放为 ${predictedEmission.value.toFixed(2)} kg CO₂e，${deltaText}`
})

const analysisInsights = computed(() => {
  if (aiAnalysis.value?.insights?.length) {
    return aiAnalysis.value.insights
  }

  const items: Array<{ title: string; text: string }> = []

  if (summary.value) {
    const categories = [
      { label: '交通', value: summary.value.transportEmission },
      { label: '饮食', value: summary.value.dietEmission },
      { label: '用电', value: summary.value.electricityEmission }
    ].sort((a, b) => b.value - a.value)

    const topCategory = categories[0]
    if (topCategory && topCategory.value > 0) {
      items.push({
        title: `${topCategory.label}是当前主排放源`,
        text: `本月 ${topCategory.label} 排放约 ${topCategory.value.toFixed(2)} kg CO₂e，优先优化这里最有效。`
      })
    }
  }

  if (prediction.value) {
    items.push({
      title: '下一月趋势判断',
      text: prediction.value.trend || '暂时无法识别趋势'
    })
  }

  if (prediction.value?.confidence) {
    items.push({
      title: '模型可信度',
      text: `当前预测置信度为 ${predictionConfidence.value.toFixed(0)}%，适合做趋势参考，但不建议单独作为决策依据。`
    })
  }

  if (prediction.value?.suggestion) {
    items.push({
      title: '建议优先执行项',
      text: prediction.value.suggestion.suggestion
    })
  }

  return items.slice(0, 4)
})

const aiRecommendations = computed(() => {
  if (aiAnalysis.value?.nextActions?.length) {
    return aiAnalysis.value.nextActions
  }

  return prediction.value?.suggestion ? [prediction.value.suggestion.suggestion] : []
})

const analysisMetaText = computed(() => {
  if (!aiAnalysis.value) {
    return '当前显示的是本地规则分析结果'
  }

  const generatedAt = formatAiTime(aiAnalysis.value.generatedAt)
  const sourceLabel = aiAnalysis.value.source === 'LOCAL_ANALYSIS' ? '本地规则分析' : aiAnalysis.value.source
  return `模型：${aiAnalysis.value.model} · 来源：${sourceLabel} · 生成于 ${generatedAt}`
})

const loadData = async () => {
  loading.value = true
  try {
    const [summaryResult, predictionResult, historyResult] = await Promise.all([
      carbonApi.getSummary('month'),
      predictionApi.getNextMonthPrediction(),
      predictionApi.getHistory()
    ])

    summary.value = summaryResult
    prediction.value = predictionResult
    historyRecords.value = historyResult

    try {
      aiAnalysis.value = await aiAnalysisApi.getAnalysis()
      aiAnalysisError.value = ''
    } catch (error) {
      console.error('加载 AI 分析失败:', error)
      aiAnalysis.value = null
      aiAnalysisError.value = '本地分析加载失败，请稍后重试。'
    }
  } catch (error) {
    console.error('加载 AI 分析数据失败:', error)
    ElMessage.error('加载 AI 分析数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadData()
}

const handleMenuSelect = (key: string) => {
  router.push(key)
  activeMenu.value = key
}

const generateWeeklyPlan = () => {
  planGenerating.value = true
  ElMessage.info('AI 管家正在根据您的历史数据生成专属减排计划...')
  
  setTimeout(() => {
    // 根据当前时间向后排 7 天
    const startDate = new Date()
    
    // 模拟数据分析：查看用户是否经常吃肉、开车等（通过访问 store）
    const totalRecords = carbonStore.records
    const hasCar = totalRecords.some(r => r.type === 'transport' && (r as any).vehicleType === 'car')
    const hasMeat = totalRecords.some(r => r.type === 'diet' && (r as any).foodType === 'meat')
    
    const plan: PlanDay[] = []
    
    for(let i=0; i<7; i++) {
        const date = new Date(startDate)
        date.setDate(startDate.getDate() + i)
        const dateStr = `${date.getMonth() + 1}月${date.getDate()}日`
        const isWeekend = date.getDay() === 0 || date.getDay() === 6
        
        let title = ''
        let desc = ''
        let red = 0
        let cat = ''
        let tagType : "success" | "warning" | "info" | "primary" | "danger" = "success"
        let icon: any = null
        
        // 伪随机加上一些规则判断，生成每天的独特建议
        if (i === 0 && hasCar) {
            title = '明日预报晴好，尝试骑行通勤'
            desc = '根据天气预报，明天非常适合骑行。既然你平时习惯开车，不如明天将上下班交通改为骑行或地铁，既锻炼身体又大幅减排。'
            red = 2.5
            cat = '交通出行'
            tagType = 'primary'
            icon = Van
        } else if (i === 1 && hasMeat) {
            title = '开启周二“植物性饮食”挑战'
            desc = '分析到你近日红肉摄入较多。这一天试着将午餐的牛肉换成鸡肉或豆腐，不仅对肠胃好，还能大幅降低食物碳足迹。'
            red = 1.2
            cat = '饮食习惯'
            tagType = 'success'
            icon = KnifeFork
        } else if (i === 3) {
            title = '家庭用电“随手关”小突击'
            desc = '周四晚上洗完澡，顺手拔掉热水器、电视机等电器的待机插头，关闭不必要的灯光。积少成多也是大贡献。'
            red = 0.5
            cat = '电器使用'
            tagType = 'warning'
            icon = Lightning
        } else if (isWeekend) {
            title = '周末低碳周边游'
            desc = '周末别宅在家里吹空调啦，带上家人坐公交去周边的公园呼吸新鲜空气，记得自带水杯哦。'
            red = 3.0
            cat = '综合减碳'
            tagType = 'danger'
            icon = House
        } else {
            title = '自带环保袋/餐具的一天'
            desc = '今天买咖啡或者点外卖时，试试使用自带的环保杯和餐具吧。向商家说一句“不需要一次性餐具”。'
            red = 0.3
            cat = '绿色生活'
            tagType = 'info'
            icon = Star
        }
        
        plan.push({
            dateStr,
            title,
            description: desc,
            reduction: red,
            category: cat,
            type: tagType === 'danger' ? 'primary' : tagType,
            color: '',
            tagType,
            icon
        })
    }
    
    weeklyPlan.value = plan
    planGenerating.value = false
    planVisible.value = true
  }, 1500)
}

const acceptPlan = () => {
  planVisible.value = false
  ElMessage.success('成功接跑计划！每天登录来打卡吧~')
}

const handleLogout = () => {
  router.push('/login')
}

const navigateToProfile = () => {
  router.push('/profile')
}

function formatDateLabel(dateText: string) {
  const date = new Date(dateText)
  if (Number.isNaN(date.getTime())) {
    return dateText
  }

  return `${date.getMonth() + 1}/${date.getDate()}`
}

function formatAiTime(dateText: string) {
  const date = new Date(dateText)
  if (Number.isNaN(date.getTime())) {
    return dateText
  }

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  loadData()
})
</script>

<style scoped>
/* 管家弹窗样式 */
.plan-dialog .el-dialog__body {
  padding: 10px 25px 20px;
}
.plan-intro {
  color: #606266;
  margin-bottom: 24px;
  line-height: 1.6;
  font-size: 14px;
}
.plan-intro strong {
  color: #388e3c;
}
.plan-card {
  margin-bottom: 12px;
  border-left: 4px solid var(--el-color-primary);
  border-radius: 8px;
}
.plan-card h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}
.plan-card p {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}
.plan-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}
.reduction-estimate {
  color: #67c23a;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 4px;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.ai-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #eef8ef 0%, #f8fbf8 100%);
}

.ai-main {
  padding: 24px;
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
  display: inline-flex;
  align-items: center;
  gap: 10px;
  transition: color 0.3s ease, transform 0.3s ease;
}

.header-left .logo-link:hover {
  color: #e8f5e9;
  transform: translateY(-2px);
}

.brand-icon {
  font-size: 20px;
}

.header-left h1 {
  font-size: 20px;
  margin: 0;
  line-height: 1;
}

.user-info {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-end;
  margin-bottom: 20px;
  padding: 24px;
  border-radius: 20px;
  background: linear-gradient(135deg, #1b5e20 0%, #4caf50 55%, #81c784 100%);
  color: #fff;
  box-shadow: 0 16px 40px rgba(76, 175, 80, 0.22);
}

.eyebrow {
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 12px;
  opacity: 0.82;
}

.page-hero h2 {
  font-size: 28px;
  line-height: 1.2;
  margin-bottom: 10px;
}

.hero-copy {
  max-width: 720px;
  opacity: 0.92;
}

.data-alert {
  margin-bottom: 20px;
}

.metric-row,
.chart-row,
.analysis-row {
  margin-top: 20px;
}

.metric-card {
  min-height: 148px;
  border-radius: 18px;
  border: 1px solid rgba(76, 175, 80, 0.12);
  box-shadow: 0 8px 28px rgba(34, 79, 39, 0.08);
}

.metric-card-primary {
  background: linear-gradient(135deg, #e8f5e9 0%, #ffffff 100%);
}

.metric-label {
  color: #61806a;
  font-size: 14px;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 30px;
  font-weight: 700;
  color: #17351b;
  line-height: 1.1;
}

.metric-suffix {
  margin-top: 8px;
  color: #7c9480;
  font-size: 13px;
}

.metric-up {
  color: #d84315;
}

.metric-down {
  color: #2e7d32;
}

.chart-card,
.analysis-card {
  border-radius: 18px;
  border: 1px solid rgba(76, 175, 80, 0.12);
  box-shadow: 0 8px 28px rgba(34, 79, 39, 0.08);
}

.analysis-summary h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #17351b;
}

.analysis-summary p {
  color: #506155;
  line-height: 1.75;
}

.analysis-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 14px;
  color: #5f7263;
  font-size: 13px;
}

.insight-list {
  display: grid;
  gap: 14px;
}

.insight-item {
  padding: 14px 16px;
  border-radius: 14px;
  background: #f7fbf7;
  border: 1px solid #dce8dd;
}

.insight-title {
  font-weight: 600;
  color: #214326;
  margin-bottom: 6px;
}

.insight-text {
  color: #5f7263;
  line-height: 1.7;
}

.suggestion-box {
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ecf8ed 0%, #ffffff 100%);
  border: 1px solid #d7ead8;
}

.suggestion-title {
  font-size: 14px;
  color: #5c7a60;
  margin-bottom: 8px;
}

.suggestion-category {
  font-size: 18px;
  font-weight: 700;
  color: #17351b;
  margin-bottom: 8px;
}

.suggestion-text {
  color: #4f6353;
  line-height: 1.75;
}

.ai-action-list {
  margin: 0;
  padding-left: 18px;
  color: #4f6353;
  line-height: 1.8;
}

.suggestion-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  color: #5f7263;
  font-size: 13px;
}

@media (max-width: 768px) {
  .ai-main {
    padding: 12px;
  }

  .page-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .suggestion-meta {
    flex-direction: column;
  }
}
</style>