<template>
  <div class="dashboard-screen" :style="layoutVars" @scroll="handleScroll">
    <div class="ambient ambient-a"></div>
    <div class="ambient ambient-b"></div>
    <div class="ambient ambient-c"></div>

    <header class="topbar" :class="{ scrolled: isScrolled }">
      <router-link to="/home" class="brand">
        <div class="brand-mark">CFP</div>
        <div class="brand-copy">
          <div class="brand-title">碳足迹追踪平台 · 数据指挥中心</div>
          <div class="brand-subtitle">Carbon Footprint Command Center</div>
        </div>
      </router-link>

      <div class="status-group">
        <div class="status-pill live">实时同步</div>
        <div class="status-pill">数据延迟 2s</div>
        <div class="status-pill">区域覆盖 {{ regionalStats.length || 0 }}</div>
      </div>

      <div class="clock">{{ currentTime }}</div>

      <div class="toolbar">
        <button class="toolbar-btn primary" @click="refreshRealTimeData">刷新动态</button>
        <router-link to="/dashboard" class="toolbar-btn" style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">返回主界面</router-link>
      </div>
    </header>

    <!-- 在顶部新增滚动跑马灯 -->
    <div class="marquee-container">
      <div class="marquee-content">
        <span class="marquee-item" v-for="(msg, i) in marqueeMessages" :key="i">
          <el-icon style="margin-right: 4px;"><Lightning /></el-icon> {{ msg }}
        </span>
        <!-- 重复一遍用来实现无缝滚动 -->
        <span class="marquee-item" v-for="(msg, i) in marqueeMessages" :key="'dup-'+i">
          <el-icon style="margin-right: 4px;"><Lightning /></el-icon> {{ msg }}
        </span>
      </div>
    </div>

    <main class="dashboard-grid">
      <aside class="rail rail-left" :class="{ collapsed: leftCollapsed }">
        <button class="rail-toggle-btn toggle-left" title="收起/展开左舱" @click="toggleLeftRail">
          <span class="toggle-icon">{{ leftCollapsed ? '\u25B6' : '\u25C0' }}</span>
        </button>
        <section class="panel rail-summary">
          <div class="panel-head">
            <span>左侧态势</span>
            <span class="panel-sub">Overview</span>
          </div>
          <div class="summary-number"><NumberRoll :value="overview.totalUsers" :decimals="0" /></div>
          <div class="summary-label">注册用户</div>
          <div class="summary-mini-grid">
            <div class="mini-chip">
              <span class="mini-chip-label">今日活跃</span>
              <strong><NumberRoll :value="overview.activeUsersToday" :decimals="0" /></strong>
            </div>
            <div class="mini-chip">
              <span class="mini-chip-label">减排率</span>
              <strong><NumberRoll :value="reductionRate" :decimals="1" />%</strong>
            </div>
          </div>
        </section>

        <div class="rail-body">
          <section class="panel">
            <div class="panel-head">
              <span>趋势中枢</span>
              <span class="panel-sub">30D</span>
            </div>
            <div ref="trendChart" class="chart-box tall"></div>
          </section>

          <section class="panel">
            <div class="panel-head">
              <span>实时动态</span>
              <span class="panel-sub">Live Feed</span>
            </div>
            <transition-group name="list" tag="div" class="activity-list">
              <div
                v-for="(activity, index) in recentActivities"
                :key="activity.id"
                class="activity-item"
                :class="{ active: index === activeActivityIndex }"
              >
                <div class="activity-time">{{ activity.time }}</div>
                <div class="activity-main">
                  <div class="activity-row">
                    <span class="activity-user">{{ activity.username }}</span>
                    <span class="activity-tag" :class="activity.type">{{ activityTypeLabel(activity.type) }}</span>
                  </div>
                  <div class="activity-desc">{{ activity.activity }}</div>
                  <div class="activity-emission">{{ activity.emission.toFixed(2) }} kg CO₂e</div>
                </div>
              </div>
            </transition-group>
          </section>
        </div>
      </aside>

      <section class="center-stage">
        <section class="hero-panel panel">
          <div class="hero-copy">
            <div class="hero-kicker">Central Carbon Pulse</div>
            <h2>{{ selectedRegion === '全国' ? '全域' : selectedRegion }}碳足迹态势总览</h2>
            <p>将总排放、减排进度、活跃度和区域分布统一聚合到一张态势图中，点击地图下级行政区联动查看详情。当前选中：<strong style="color: #4CAF50">{{ selectedRegion }}</strong></p>
            <div class="focus-strip">
              <span class="focus-dot"></span>
              <span class="focus-label">{{ focusState.label }}</span>
              <span class="focus-copy">{{ focusState.detail }}</span>
            </div>
          </div>

          <div class="core-visual">
            <div ref="centerMap" class="center-map"></div>
          </div>

          <div class="hero-metrics">
            <div v-for="item in heroMetrics" :key="item.label" class="hero-metric">
              <span>{{ item.label }}</span>
              <strong>
                <NumberRoll :value="item.value" :decimals="item.decimals" />{{ item.suffix }}
              </strong>
              <em>{{ item.tip }}</em>
            </div>
          </div>
        </section>

        <section class="center-grid">
          <section class="panel panel-wide" :class="{ active: focusState.panel === 'trend' }">
            <div class="panel-head">
              <span>排放结构</span>
              <span class="panel-sub">Pie + KPI</span>
            </div>
            <div class="panel-split">
              <div ref="categoryChart" class="chart-box center-chart"></div>
              <div class="insight-list">
                <div class="insight-card">
                  <div class="insight-title">结构结论</div>
                  <div class="insight-value">{{ topCategoryName }}</div>
                  <div class="insight-text">占比 {{ topCategoryShare.toFixed(1) }}%，是当前最需要持续优化的排放源。</div>
                </div>
                <div class="insight-card">
                  <div class="insight-title">减排强度</div>
                  <div class="insight-value">{{ reductionRate.toFixed(1) }}%</div>
                  <div class="insight-text">相比总排放，减排进展已形成可见趋势。</div>
                </div>
                <div class="insight-card accent">
                  <div class="insight-title">区域焦点</div>
                  <div class="insight-value">{{ topRegion }}</div>
                  <div class="insight-text">{{ topRegionEmission.toFixed(1) }} kg CO₂e，优先关注节流策略。</div>
                </div>
              </div>
            </div>
          </section>

          <section class="panel panel-wide" :class="{ active: focusState.panel === 'radar' }">
            <div class="panel-head">
              <span>AI 评估雷达</span>
              <span class="panel-sub">Strategy Radar</span>
            </div>
            <div class="panel-split">
              <div ref="radarChart" class="chart-box center-chart"></div>
              <div class="recommend-list">
                <div v-for="item in recommendations" :key="item.title" class="recommend-item">
                  <div class="recommend-top">
                    <span class="recommend-title">{{ item.title }}</span>
                    <span class="recommend-badge">{{ item.value }}</span>
                  </div>
                  <div class="recommend-text">{{ item.text }}</div>
                </div>
              </div>
            </div>
          </section>
        </section>
      </section>

      <aside class="rail rail-right" :class="{ collapsed: rightCollapsed }">
        <button class="rail-toggle-btn toggle-right" title="收起/展开右舱" @click="toggleRightRail">
          <span class="toggle-icon">{{ rightCollapsed ? '\u25C0' : '\u25B6' }}</span>
        </button>
        <section class="panel rail-summary">
          <div class="panel-head">
            <span>右侧态势</span>
            <span class="panel-sub">Focus</span>
          </div>
          <div class="summary-number">{{ formatNumber(overview.totalReduction, 1) }}</div>
          <div class="summary-label">累计减排 kg CO₂e</div>
          <div class="summary-mini-grid">
            <div class="mini-chip">
              <span class="mini-chip-label">完成率</span>
              <strong>{{ reductionRate.toFixed(1) }}%</strong>
            </div>
            <div class="mini-chip">
              <span class="mini-chip-label">热点区</span>
              <strong>{{ topRegion }}</strong>
            </div>
          </div>
        </section>

        <div class="rail-body">
          <section class="panel" :class="{ active: focusState.panel === 'region' }">
            <div class="panel-head">
              <span>区域热力</span>
              <span class="panel-sub">Top Regions</span>
            </div>
            <div ref="regionChart" class="chart-box tall"></div>
          </section>

          <section class="panel" :class="{ active: focusState.panel === 'ranking' }">
            <div class="panel-head">
              <span>减排排行榜</span>
              <span class="panel-sub">Top Users</span>
            </div>
            <div class="ranking-list">
              <div
                v-for="(user, index) in topUsers.slice(0, 8)"
                :key="user.userId"
                class="ranking-item"
                :class="{ active: index === activeRankIndex }"
              >
                <div class="ranking-rank" :class="`rank-${user.rank}`">{{ user.rank }}</div>
                <div class="ranking-main">
                  <div class="ranking-name">{{ user.username }}</div>
                  <div class="ranking-meta">{{ user.totalPoints }} 积分 · {{ user.totalReduction.toFixed(1) }} kg</div>
                </div>
              </div>
            </div>
          </section>

          <section class="panel">
            <div class="panel-head">
              <span>管理摘要</span>
              <span class="panel-sub">Snapshot</span>
            </div>
            <div class="snapshot-grid">
              <div class="snapshot-card">
                <span>平均日排放</span>
                <strong>{{ overview.avgDailyEmission.toFixed(2) }}</strong>
              </div>
              <div class="snapshot-card">
                <span>注册用户</span>
                <strong>{{ formatNumber(overview.totalUsers) }}</strong>
              </div>
              <div class="snapshot-card">
                <span>总积分</span>
                <strong>{{ formatNumber(overview.totalPoints) }}</strong>
              </div>
              <div class="snapshot-card">
                <span>减排效率</span>
                <strong>{{ efficiencyScore.toFixed(1) }}%</strong>
              </div>
            </div>
          </section>
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { dashboardApi } from '../api'

// @ts-ignore
import chinaMap from '../assets/map/china.json'
echarts.registerMap('china', chinaMap as any)

import NumberRoll from '@/components/NumberRoll.vue'
import { getGeoCoord } from '@/utils/geoCoords'
import { Lightning } from '@element-plus/icons-vue'

// 跑马灯数据
const marqueeMessages = ref([
  '北京节点 张先生 刚才通过公交出行减排了 5.2 kg CO₂e',
  '上海节点 某企业 刚刚完成了 1000度 绿电置换',
  '广州节点 王女士 点击生成了环保海报并分享，获得成就徽章',
  '成都节点 餐饮行业 平均蔬菜消费比例提升了 12%',
  '平台公告：碳中和知识竞赛将于下周开启，敬请期待！'
])

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
  reduction: number
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
  id?: string
  time: string
  username: string
  activity: string
  emission: number
  type: string
  region: string
}

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
const currentTime = ref('')
const leftCollapsed = ref(false)
const rightCollapsed = ref(false)
const isScrolled = ref(false)
const selectedRegion = ref('全国')

const handleScroll = (e: any) => {
  if (!e.target) return;
  isScrolled.value = e.target.scrollTop > 20
}

const trendChart = ref<HTMLElement | null>(null)
const categoryChart = ref<HTMLElement | null>(null)
const regionChart = ref<HTMLElement | null>(null)
const radarChart = ref<HTMLElement | null>(null)

const centerMap = ref<HTMLElement | null>(null)
let centerMapInstance: echarts.ECharts | null = null
let trendChartInstance: echarts.ECharts | null = null
let categoryChartInstance: echarts.ECharts | null = null
let regionChartInstance: echarts.ECharts | null = null
let radarChartInstance: echarts.ECharts | null = null
let timer: number | null = null
let focusTimer: number | null = null
let dataTimer: number | null = null

const layoutVars = computed(() => ({
  '--left-width': leftCollapsed.value ? '96px' : '380px',
  '--right-width': rightCollapsed.value ? '96px' : '380px'
}))

const reductionRate = computed(() => {
  if (!overview.value.totalEmission) {
    return 0
  }
  return Math.min(100, (overview.value.totalReduction / overview.value.totalEmission) * 100)
})

const efficiencyScore = computed(() => {
  return Math.min(100, reductionRate.value * 0.8 + (overview.value.activeUsersToday / 30))
})

const topRegion = computed(() => {
  const sorted = [...regionalStats.value].sort((a, b) => b.totalEmission - a.totalEmission)
  return sorted[0]?.region || '暂无'
})

const topRegionEmission = computed(() => {
  const sorted = [...regionalStats.value].sort((a, b) => b.totalEmission - a.totalEmission)
  return sorted[0]?.totalEmission || 0
})

const topCategoryName = computed(() => {
  const sorted = [...categoryDistribution.value].sort((a, b) => b.value - a.value)
  return sorted[0]?.category || '暂无'
})

const topCategoryShare = computed(() => {
  const sorted = [...categoryDistribution.value].sort((a, b) => b.value - a.value)
  return sorted[0]?.percentage || 0
})

const heroMetrics = computed(() => [
  {
    label: '今日活跃',
    value: overview.value.activeUsersToday,
    decimals: 0,
    suffix: '',
    tip: '在线互动用户'
  },
  {
    label: '日均排放',
    value: overview.value.avgDailyEmission,
    decimals: 2,
    suffix: ' kg',
    tip: '最近均值'
  },
  {
    label: '减排总量',
    value: overview.value.totalReduction,
    decimals: 1,
    suffix: ' kg',
    tip: '累计成果'
  },
  {
    label: '完成率',
    value: reductionRate.value,
    decimals: 1,
    suffix: '%',
    tip: '目标推进'
  }
])

const recentActivities = computed(() => realTimeActivities.value.slice(0, 5))

const recommendations = computed(() => [
  {
    title: '交通优化',
    value: '高优先级',
    text: '优先推动公共交通、拼车和新能源出行，能最快降低结构性排放。'
  },
  {
    title: '饮食结构',
    value: '中优先级',
    text: '减少高碳食材占比，提升低碳饮食的日常触达频率。'
  },
  {
    title: '能源管理',
    value: '持续跟踪',
    text: '强化用电监测和设备效率评估，稳定压降用电排放。'
  }
])

const focusPanels = [
  {
    panel: 'trend',
    label: '趋势中枢',
    detail: '30 天排放变化会持续轮播高亮，便于先看走势再看结构。'
  },
  {
    panel: 'radar',
    label: 'AI 评估雷达',
    detail: '综合健康度会和侧栏摘要同步闪动，突出整体风险判断。'
  },
  {
    panel: 'region',
    label: '区域热力',
    detail: '高负荷地区会自动进入视野，强化空间维度的发现能力。'
  },
  {
    panel: 'ranking',
    label: '减排排行榜',
    detail: '领先用户会轮流被聚焦，用于强调行为示范效应。'
  }
] as const

const focusIndex = ref(0)

const focusState = computed(() => focusPanels[focusIndex.value % focusPanels.length])

const activeActivityIndex = computed(() => {
  const size = recentActivities.value.length || 1
  return focusIndex.value % size
})

const activeRankIndex = computed(() => {
  const size = topUsers.value.slice(0, 8).length || 1
  return focusIndex.value % size
})

const activityTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    transport: '交通',
    diet: '饮食',
    electricity: '用电'
  }
  return map[type] || '动态'
}

const formatNumber = (value: number, fractionDigits = 0) => {
  return new Intl.NumberFormat('zh-CN', {
    minimumFractionDigits: fractionDigits,
    maximumFractionDigits: fractionDigits
  }).format(value || 0)
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getMockOverview = (): OverviewStats => ({
  totalUsers: 12580,
  totalEmission: 45678.5,
  totalReduction: 12345.8,
  totalPoints: 89650,
  avgDailyEmission: 3.63,
  activeUsersToday: 892
})

const getMockEmissionTrends = (): EmissionTrend[] => {
  const trends: EmissionTrend[] = []
  const now = new Date()
  
  // Create a 30-day cyclical trend with downward direction
  for (let i = 29; i >= 0; i -= 1) {
    const date = new Date(now)
    date.setDate(date.getDate() - i)
    // 假设双休日代表周末
    const isWeekend = date.getDay() === 0 || date.getDay() === 6
    
    // 生成波动和长期走向：随着天数靠近现在(i接近0)，基础值在降低（模拟减排效果有成效）
    const downwardTrend = i * 18
    const weekendDrop = isWeekend ? -350 : 150
    const randomNoise = (Math.random() - 0.5) * 180
    
    const total = 1400 + downwardTrend + weekendDrop + randomNoise
    
    // 结构比例模拟（周末通勤大幅降低用电升高，工作日相反）
    const transportShare = isWeekend ? (0.2 + Math.random() * 0.05) : (0.42 + Math.random() * 0.05)
    // 餐饮较为平稳
    const dietShare = 0.3 + Math.random() * 0.05
    const electricityShare = 1 - transportShare - dietShare

    const isoDateStr = new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().split('T')[0]

 trends.push({
      date: isoDateStr,
      emission: total,
      transportEmission: total * transportShare,
      dietEmission: total * dietShare,
      electricityEmission: total * electricityShare,
     reduction: Math.random() * 2
    })
  }
  return trends
}
const getMockCategoryDistribution = (): CategoryDistribution[] => [
  { category: '交通排放', value: 18271.4, percentage: 40 },
  { category: '饮食排放', value: 15987.4, percentage: 35 },
  { category: '用电排放', value: 11419.7, percentage: 25 }
]

const getMockTopUsers = (): TopUser[] => [
  { userId: 1, username: '环保达人', totalPoints: 12580, totalReduction: 1258.5, rank: 1 },
  { userId: 2, username: '绿色先锋', totalPoints: 11250, totalReduction: 1125.0, rank: 2 },
  { userId: 3, username: '低碳生活', totalPoints: 9870, totalReduction: 987.0, rank: 3 },
  { userId: 4, username: '节能专家', totalPoints: 8640, totalReduction: 864.0, rank: 4 },
  { userId: 5, username: '减排先锋', totalPoints: 7320, totalReduction: 732.0, rank: 5 },
  { userId: 6, username: '绿色使者', totalPoints: 6580, totalReduction: 658.0, rank: 6 },
  { userId: 7, username: '环保卫士', totalPoints: 5940, totalReduction: 594.0, rank: 7 },
  { userId: 8, username: '低碳达人', totalPoints: 5210, totalReduction: 521.0, rank: 8 }
]

const provinceList = [
  '广东', '江苏', '山东', '浙江', '河南', '四川', '湖北', '福建', '湖南', '安徽',
  '河北', '北京', '上海', '陕西', '江西', '重庆', '辽宁', '云南', '广西', '山西',
  '黑龙江', '内蒙古', '贵州', '吉林', '天津', '新疆', '甘肃', '海南', '宁夏', '青海',
  '西藏', '香港', '澳门', '台湾'
];

const getMockRegionalStats = (): RegionalStats[] => {
  return provinceList.map((region, i) => {
    // 制造一些递减的模拟数据，避免数据显示0
    const weight = Math.max(0.1, 1 - (i * 0.025)); // 权重从1.0渐减
    const userCount = Math.floor(2850 * weight + Math.random() * 500);
    const totalEmission = userCount * (3.0 + Math.random() * 1.5);
    return {
      region,
      userCount,
      totalEmission,
      avgEmission: totalEmission / userCount
    };
  }).sort((a, b) => b.totalEmission - a.totalEmission);
}

const getMockRealTimeActivities = (): RealTimeActivity[] => {
  const activities: RealTimeActivity[] = []
  const actions = ['记录了交通排放', '记录了饮食排放', '记录了用电排放', '完成了减排目标', '获得了积分奖励']
  const usernames = ['环保达人', '绿色先锋', '低碳生活', '节能专家', '减排先锋', '绿色使者', '环保卫士', '低碳达人']
  const regions = ['北京', '上海', '广东', '浙江', '江苏', '四川', '湖北', '陕西', '山东']
  const now = new Date()

  for (let i = 0; i < 10; i += 1) {
    const time = new Date(now)
    time.setMinutes(time.getMinutes() - i * 5)
    activities.push({
      id: Math.random().toString(36).substr(2, 9),
      time: time.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      username: usernames[Math.floor(Math.random() * usernames.length)],
      activity: actions[Math.floor(Math.random() * actions.length)],
      emission: Math.random() * 5 + 1,
      type: ['transport', 'diet', 'electricity'][Math.floor(Math.random() * 3)],
      region: regions[Math.floor(Math.random() * regions.length)]
    })
  }

  return activities
}

let nationalDataBackup: any = null;

const fetchDashboardData = async () => {
  try {
    const response = await dashboardApi.getDashboardData()
    overview.value = response.overview || getMockOverview()

    const rawTrends = Array.isArray(response.emissionTrends) && response.emissionTrends.length ? response.emissionTrends : getMockEmissionTrends()
    // Enhance structural data if it is 0 from backend
    emissionTrends.value = rawTrends.map((item: any) => {
      const isMissingStructure = !item.transportEmission && !item.dietEmission && !item.electricityEmission;
      if (isMissingStructure && item.emission > 0) {
        const date = new Date(item.date);
        const isWeekend = date.getDay() === 0 || date.getDay() === 6;
        const transportShare = isWeekend ? (0.2 + Math.random() * 0.05) : (0.42 + Math.random() * 0.05);
        const dietShare = 0.3 + Math.random() * 0.05;
        const electricityShare = 1 - transportShare - dietShare;
        return {
          ...item,
          transportEmission: item.emission * transportShare,
          dietEmission: item.emission * dietShare,
          electricityEmission: item.emission * electricityShare
        };
      }
      return item;
    });

    categoryDistribution.value = Array.isArray(response.categoryDistribution) && response.categoryDistribution.length ? response.categoryDistribution : getMockCategoryDistribution()
    topUsers.value = Array.isArray(response.topUsers) && response.topUsers.length ? response.topUsers : getMockTopUsers()
    
    // 让模拟数据作为底座，覆盖后端返回的非零真实数据
    const baseRegionalData = getMockRegionalStats()
    if (Array.isArray(response.regionalStats) && response.regionalStats.length > 0) {
      response.regionalStats.forEach((realRegion: any) => {
        const match = baseRegionalData.find(m => m.region === realRegion.region || m.region + '省' === realRegion.region || m.region + '市' === realRegion.region)
        if (match && realRegion.totalEmission > 0) {
          match.userCount = realRegion.userCount || match.userCount
          match.totalEmission = realRegion.totalEmission
          match.avgEmission = realRegion.avgEmission || match.avgEmission
        } else if (!match && realRegion.totalEmission > 0) {
          baseRegionalData.push({
            region: realRegion.region,
            userCount: realRegion.userCount || 10,
            totalEmission: realRegion.totalEmission,
            avgEmission: realRegion.avgEmission || 0
          })
        }
      })
      baseRegionalData.sort((a, b) => b.totalEmission - a.totalEmission)
    }
    regionalStats.value = baseRegionalData
    
   // --- 修改后的 try 块逻辑 ---
    const rawActivities = Array.isArray(response.realTimeActivities) && response.realTimeActivities.length 
      ? response.realTimeActivities.slice(0, 10) 
      : getMockRealTimeActivities().slice(0, 10);

    // 使用 .map 统一补全 region 属性，确保符合 RealTimeActivity 类型定义
    realTimeActivities.value = rawActivities.map((item: any) => ({
      ...item,
      region: item.region || '全域' 
    }));

  } catch (error) {
    console.error('获取大屏数据失败:', error)
    overview.value = getMockOverview()
    emissionTrends.value = getMockEmissionTrends()
    categoryDistribution.value = getMockCategoryDistribution()
    topUsers.value = getMockTopUsers()
    regionalStats.value = getMockRegionalStats()
    
    // --- 修改后的 catch 块逻辑 ---
    // 即使是报错后用的 Mock 数据，也要确保包含 region 字段
    realTimeActivities.value = getMockRealTimeActivities().slice(0, 10).map((item: any) => ({
      ...item,
      region: item.region || '模拟区域'
    }));
  }

  // 保存一份全国的基准备份，用于点击省份时做模拟数据比例缩放
  nationalDataBackup = {
    overview: { ...overview.value },
    emissionTrends: emissionTrends.value.map(item => ({ ...item })),
    categoryDistribution: categoryDistribution.value.map(item => ({ ...item })),
    topUsers: topUsers.value.map(item => ({ ...item }))
  }

  await nextTick()
  renderCharts()
}

const startFocusRotation = () => {
  focusIndex.value = (focusIndex.value + 1) % focusPanels.length
}

let autoPlayMapTimer: number | null = null
let currentAutoPlayIndex = -1
let idleTimer: number | null = null

const startMapAutoPlay = () => {
  if (autoPlayMapTimer) stopMapAutoPlay()
  autoPlayMapTimer = window.setInterval(() => {
    if (!regionalStats.value.length || !centerMapInstance) return
    
    // --- 1. 修改取消选择逻辑 ---
if (selectedRegion.value !== '全国') {
  // 核心修复：对 getOption() 的结果进行 (as any) 断言
  const option = centerMapInstance.getOption() as any;
  const prevName = option?.series?.[0]?.data?.find((d: any) => 
    d.name.includes(selectedRegion.value)
  )?.name || selectedRegion.value;
  
  centerMapInstance.dispatchAction({ type: 'unselect', name: prevName });
}

currentAutoPlayIndex = (currentAutoPlayIndex + 1) % regionalStats.value.length;
const nextRegion = regionalStats.value[currentAutoPlayIndex].region;

// --- 2. 修改获取地图列表逻辑 ---
// 核心修复：同样使用 (as any) 确保 series[0] 可以被正常索引
const mapDataList: any[] = (centerMapInstance.getOption() as any)?.series?.[0]?.data || [];
const nextMatch = mapDataList.find(d => d.name.includes(nextRegion));
const dispatchName = nextMatch ? nextMatch.name : nextRegion;

    // Auto select
    centerMapInstance.dispatchAction({ type: 'select', name: dispatchName })
    selectedRegion.value = nextRegion

  }, 6000)
}

const stopMapAutoPlay = () => {
  if (autoPlayMapTimer) {
    clearInterval(autoPlayMapTimer)
    autoPlayMapTimer = null
  }
}

const resetUserIdle = () => {
  stopMapAutoPlay()
  if (idleTimer) clearTimeout(idleTimer)
  idleTimer = window.setTimeout(() => {
    startMapAutoPlay()
  }, 10000) // 10 seconds of idle to resume auto-play
}

const createChart = (container: HTMLElement | null, existing: echarts.ECharts | null) => {
  if (!container) {
    return existing
  }
  return existing ?? echarts.init(container)
}

const renderCenterMap = () => {
  const isInit = !centerMapInstance;
  centerMapInstance = createChart(centerMap.value, centerMapInstance);
  if (!centerMapInstance) return;

  const mapData = regionalStats.value.map(item => {
    let name = item.region;
    if (['北京', '天津', '上海', '重庆'].includes(name)) name += '市';
    else if (['内蒙古', '西藏'].includes(name)) name += '自治区';
    else if (name === '新疆') name = '新疆维吾尔自治区';
    else if (name === '宁夏') name = '宁夏回族自治区';
    else if (name === '广西') name = '广西壮族自治区';
    else if (name === '香港' || name === '澳门') name += '特别行政区';
    else if (!name.endsWith('省') && !name.endsWith('市') && !name.endsWith('区') && !name.endsWith('自治区') && !name.endsWith('行政区')) name += '省';

    // 伪随机生成当地的结构拆分（为了展示 Tooltip）
    const hash = name.charCodeAt(0) + Math.floor(item.totalEmission);
    const transportP = 25 + (hash % 15);
    const electricityP = 35 + (hash % 20);
    const dietP = 100 - transportP - electricityP;

    return { 
      name: name, 
      value: item.totalEmission,
      breakdown: {
        transport: transportP,
        electricity: electricityP,
        diet: dietP
      }
    };
  })
  
  console.log('---- Map Data Update ----', isInit, mapData)

  const maxVal = Math.max(...regionalStats.value.map(s => s.totalEmission), 100);

  // Preserve the current zoom and center offset from the instance if it exists
  let currentZoom = undefined;
  let currentCenter = undefined;
  if (!isInit && centerMapInstance) {
    const currentOption: any = centerMapInstance.getOption();
    if (currentOption && currentOption.geo && currentOption.geo.length > 0) {
      currentZoom = currentOption.geo[0].zoom;
      currentCenter = currentOption.geo[0].center;
    } else if (currentOption && currentOption.series && currentOption.series.length > 0) {
      currentZoom = currentOption.series[0].zoom;
      currentCenter = currentOption.series[0].center;
    }
  }

  centerMapInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(7, 18, 30, 0.92)',
      borderColor: 'rgba(90, 210, 166, 0.45)',
      textStyle: { color: '#effff8' },
      formatter: (params: any) => {
        // Handle effectScatter vs map data slightly differently
        const data = params.data || {};
        let displayVal = '0';
        
        let t = 25, e = 35, d = 40;
        if (params.seriesType === 'effectScatter') {
           displayVal = isNaN(data.value[2]) ? '0' : Number(data.value[2]).toFixed(2);
           return `<div style="font-family: Arial, sans-serif; padding: 4px;">
             <div style="font-size: 14px; font-weight: bold; margin-bottom: 6px;">
               ${data.name} <span style="font-weight: normal; color: rgba(255,255,255,0.7); margin-left: 8px;">活动涟漪</span>
             </div>
             <div>单次排放: <span style="color: #ffeb3b;">${displayVal} kg CO₂e</span></div>
           </div>`;
        } else {
           displayVal = isNaN(params.value) ? '0' : Number(params.value).toFixed(2);
           if (data.breakdown) {
             t = data.breakdown.transport;
             e = data.breakdown.electricity;
             d = data.breakdown.diet;
           }
           return `
             <div style="font-family: Arial, sans-serif; min-width: 190px; padding: 4px;">
               <div style="font-size: 15px; font-weight: bold; margin-bottom: 8px; border-bottom: 1px solid rgba(255,255,255,0.15); padding-bottom: 8px;">
                 ${params.name} 
                 <span style="font-size: 12px; font-weight: normal; color: #57f287; float: right; margin-top:2px;">
                   ${displayVal} kg CO₂e
                 </span>
               </div>
               <div style="display: flex; align-items: center; gap: 14px; margin-top: 8px;">
                 <div style="width: 54px; height: 54px; border-radius: 50%; background: conic-gradient(#57f287 0% ${t}%, #7dd3fc ${t}% ${t+e}%, #f8b26a ${t+e}% 100%); display: flex; align-items: center; justify-content: center; box-shadow: 0 0 10px rgba(0,0,0,0.5);">
                   <div style="width: 32px; height: 32px; background: rgba(7, 18, 30, 0.92); border-radius: 50%;"></div>
                 </div>
                 <div style="flex: 1; font-size: 12px; line-height: 1.9;">
                   <div style="display: flex; justify-content: space-between;"><span style="color:#57f287">● 交通</span><span style="font-weight:600">${t}%</span></div>
                   <div style="display: flex; justify-content: space-between;"><span style="color:#7dd3fc">● 用电</span><span style="font-weight:600">${e}%</span></div>
                   <div style="display: flex; justify-content: space-between;"><span style="color:#f8b26a">● 饮食</span><span style="font-weight:600">${d}%</span></div>
                 </div>
               </div>
             </div>
           `;
        }
      }
    },
    visualMap: {
      min: 0,
      max: isNaN(maxVal) ? 100 : maxVal,
      text: ['高', '低'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['rgba(87, 242, 135, 0.1)', 'rgba(87, 242, 135, 0.5)', '#57f287']
      },
      textStyle: { color: '#effff8' },
      bottom: 20,
      left: 20
    },
    geo: {
      map: 'china',
      roam: true,
      zoom: currentZoom !== undefined ? currentZoom : (isInit ? 1.25 : undefined),
      center: currentCenter,
      label: {
        show: true,
        color: 'rgba(255, 255, 255, 0.85)',
        fontSize: 11,
        fontWeight: 400
      },
      emphasis: {
        label: {
          show: true,
          color: '#fff',
          fontSize: 14,
          fontWeight: 'bold',
          textBorderColor: 'rgba(0,0,0,0.8)',
          textBorderWidth: 2
        },
        itemStyle: {
          areaColor: '#7dd3fc',
          borderColor: '#fff',
          borderWidth: 2,
          shadowColor: 'rgba(125, 211, 252, 0.4)',
          shadowBlur: 10
        }
      },
      select: {
        label: {
          show: true,
          color: '#000',
          fontSize: 14,
          fontWeight: 'bold'
        },
        itemStyle: {
          areaColor: '#57f287',
          borderColor: '#fff',
          borderWidth: 2,
          shadowColor: 'rgba(87, 242, 135, 0.6)',
          shadowBlur: 14
        }
      },
      itemStyle: {
        areaColor: 'rgba(87, 242, 135, 0.1)',
        borderColor: 'rgba(255, 255, 255, 0.25)',
        borderWidth: 1
      }
    },
    series: [
      {
        name: '区域碳分布',
        type: 'map',
        geoIndex: 0,
        data: mapData
      }
    ]
  }, true); // Use true for `notMerge` to ensure clean overwriting

  centerMapInstance.off('click')
  centerMapInstance.on('click', (params: any) => {
    stopMapAutoPlay();
    if (selectedRegion.value === params.name) {
      selectedRegion.value = '全国'
      centerMapInstance?.dispatchAction({ type: 'unselect', geoIndex: 0, name: params.name })
    } else {
      centerMapInstance?.dispatchAction({ type: 'select', geoIndex: 0, name: params.name })
      selectedRegion.value = params.name
    }
  })

  updateMapScatter()
}

const updateMapScatter = () => {
  if (!centerMapInstance) return;
  const scatterData = recentActivities.value
    .map(activity => {
      const coord = getGeoCoord(activity.region || '');
      if (coord) {
        return {
          name: activity.region,
          value: [...coord, activity.emission],
          itemStyle: {
            color: activity.type === 'transport' ? '#57f287' :
                   activity.type === 'electricity' ? '#7dd3fc' : '#f5da4d'
          }
        }
      }
      return null;
    })
    .filter(Boolean);

  centerMapInstance.setOption({
    series: [
      { name: '区域碳分布' }, // Must keep index matching
      {
        name: '活动涟漪',
        type: 'effectScatter',
        coordinateSystem: 'geo',
        geoIndex: 0,
        data: scatterData,
        symbolSize: 8,
        showEffectOn: 'render',
        rippleEffect: {
          brushType: 'stroke',
          scale: 4
        },
        itemStyle: {
          shadowBlur: 10,
          shadowColor: '#fff'
        },
        zlevel: 1
      }
    ]
  });
}

const renderTrendChart = () => {
  trendChartInstance = createChart(trendChart.value, trendChartInstance)
  if (!trendChartInstance) {
    return
  }

  trendChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(7, 18, 30, 0.92)',
      borderColor: 'rgba(90, 210, 166, 0.45)',
      textStyle: { color: '#effff8' },
      formatter: (params: any[]) => {
        const dateStr = params[0].name;
        let html = `<div style="font-family: Arial, sans-serif; min-width: 140px; padding: 4px;">
          <div style="font-size: 14px; font-weight: bold; margin-bottom: 8px; border-bottom: 1px solid rgba(255,255,255,0.15); padding-bottom: 8px;">
            ${dateStr} (趋势分析)
          </div>
          <div style="display: flex; flex-direction: column; gap: 6px;">`;

        const totalNode = params.find(p => p.seriesName === '总排放')
        if (totalNode) {
          html += `<div style="display: flex; justify-content: space-between;">
            <span style="color: #57f287; font-weight: bold;">● 总排量</span>
            <span style="font-weight: 600;">${Number(totalNode.value).toFixed(1)} kg</span>
          </div><div style="height: 4px; border-bottom: 1px dashed rgba(255,255,255,0.1); margin-bottom: 2px;"></div>`
        }

        params.forEach(p => {
          if (p.seriesName !== '总排放') {
            html += `<div style="display: flex; justify-content: space-between; font-size: 12px; color: rgba(255,255,255,0.85);">
              <span>${p.marker} ${p.seriesName}</span>
              <span style="font-family: monospace;">${Number(p.value).toFixed(1)} kg</span>
            </div>`
          }
        })
        
        html += `</div></div>`;
        return html;
      }
    },
    legend: {
      top: 6,
      textStyle: { color: 'rgba(226, 255, 245, 0.8)' }
    },
    grid: {
      left: 12,
      right: 16,
      top: 44,
      bottom: 16,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: emissionTrends.value.map(item => item.date.slice(5)),
      axisLabel: { color: 'rgba(228, 248, 239, 0.65)' },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.14)' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: 'rgba(228, 248, 239, 0.65)' },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.08)' } }
    },
    series: [
      {
        name: '总排放',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#57f287',
          borderColor: '#ffffff',
          borderWidth: 2
        },
        lineStyle: { width: 3, color: '#57f287' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(87, 242, 135, 0.38)' },
            { offset: 1, color: 'rgba(87, 242, 135, 0.02)' }
          ])
        },
        data: emissionTrends.value.map(item => item.emission)
      },
      {
        name: '交通',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#7dd3fc',
          borderColor: '#ffffff',
          borderWidth: 2
        },
        lineStyle: { width: 2, color: '#7dd3fc' },
        data: emissionTrends.value.map(item => item.transportEmission)
      },
      {
        name: '饮食',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#f8b26a',
          borderColor: '#ffffff',
          borderWidth: 2
        },
        lineStyle: { width: 2, color: '#f8b26a' },
        data: emissionTrends.value.map(item => item.dietEmission)
      },
      {
        name: '用电',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#c4b5fd',
          borderColor: '#ffffff',
          borderWidth: 2
        },
        lineStyle: { width: 2, color: '#c4b5fd' },
        data: emissionTrends.value.map(item => item.electricityEmission)
      }
    ]
  })
}

const renderCategoryChart = () => {
  categoryChartInstance = createChart(categoryChart.value, categoryChartInstance)
  if (!categoryChartInstance) {
    return
  }

  categoryChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(7, 18, 30, 0.92)',
      borderColor: 'rgba(90, 210, 166, 0.45)',
      textStyle: { color: '#effff8' },
      formatter: '{b}<br/>{c} kg CO₂e ({d}%)'
    },
    legend: {
      top: 8,
      left: 'center',
      textStyle: { color: 'rgba(228, 248, 239, 0.75)' }
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['50%', '56%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderColor: 'rgba(7, 18, 30, 0.95)',
          borderWidth: 2
        },
        label: {
          color: '#eafff6',
          formatter: '{b}\n{d}%'
        },
        labelLine: {
          lineStyle: { color: 'rgba(228, 248, 239, 0.4)' }
        },
        data: categoryDistribution.value.map((item, index) => ({
          name: item.category,
          value: item.value,
          itemStyle: {
            color: ['#57f287', '#7dd3fc', '#f8b26a'][index % 3]
          }
        }))
      }
    ]
  })
}

const renderRegionChart = () => {
  regionChartInstance = createChart(regionChart.value, regionChartInstance)
  if (!regionChartInstance) {
    return
  }

  const sortedRegions = [...regionalStats.value].sort((a, b) => b.totalEmission - a.totalEmission).slice(0, 8)

  regionChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(7, 18, 30, 0.92)',
      borderColor: 'rgba(90, 210, 166, 0.45)',
      textStyle: { color: '#effff8' }
    },
    grid: {
      left: 14,
      right: 20,
      top: 8,
      bottom: 8,
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: { color: 'rgba(228, 248, 239, 0.65)' },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.08)' } }
    },
    yAxis: {
      type: 'category',
      inverse: true,
      data: sortedRegions.map(item => item.region),
      axisLabel: { color: 'rgba(228, 248, 239, 0.75)' },
      axisTick: { show: false },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.14)' } }
    },
    series: [
      {
        type: 'bar',
        data: sortedRegions.map(item => item.totalEmission),
        barWidth: 14,
        itemStyle: {
          borderRadius: [0, 999, 999, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#57f287' },
            { offset: 1, color: '#7dd3fc' }
          ])
        },
        label: {
          show: true,
          position: 'right',
          color: '#eafff6',
          formatter: '{c}'
        }
      }
    ]
  })
}

const renderRadarChart = () => {
  radarChartInstance = createChart(radarChart.value, radarChartInstance)
  if (!radarChartInstance) {
    return
  }

  const totalCategoryValue = categoryDistribution.value.reduce((sum, item) => sum + item.value, 0) || 1
  const categoryMap = new Map(categoryDistribution.value.map(item => [item.category, item]))
  const transportPercent = (categoryMap.get('交通排放')?.value || 0) / totalCategoryValue * 100
  const dietPercent = (categoryMap.get('饮食排放')?.value || 0) / totalCategoryValue * 100
  const electricityPercent = (categoryMap.get('用电排放')?.value || 0) / totalCategoryValue * 100

  radarChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      backgroundColor: 'rgba(7, 18, 30, 0.92)',
      borderColor: 'rgba(90, 210, 166, 0.45)',
      textStyle: { color: '#effff8' }
    },
    radar: {
      center: ['50%', '54%'],
      radius: '62%',
      splitNumber: 4,
      axisName: { color: 'rgba(228, 248, 239, 0.8)' },
      splitArea: {
        areaStyle: {
          color: ['rgba(87, 242, 135, 0.04)', 'rgba(125, 211, 252, 0.04)']
        }
      },
      splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.12)' } },
      axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.14)' } },
      indicator: [
        { name: '交通压力', max: 100 },
        { name: '饮食压力', max: 100 },
        { name: '用电压力', max: 100 },
        { name: '活跃度', max: 100 },
        { name: '减排率', max: 100 },
        { name: '积分活跃', max: 100 }
      ]
    },
    series: [
      {
        type: 'radar',
        symbol: 'none',
        lineStyle: { width: 2.5, color: '#57f287' },
        areaStyle: {
          color: 'rgba(87, 242, 135, 0.22)'
        },
        data: [
          {
            value: [
              Math.min(100, transportPercent),
              Math.min(100, dietPercent),
              Math.min(100, electricityPercent),
              Math.min(100, overview.value.activeUsersToday / 12),
              Math.min(100, reductionRate.value * 1.8),
              Math.min(100, overview.value.totalPoints / 1200)
            ],
            name: '平台健康度'
          }
        ]
      }
    ]
  })
}

const renderCharts = () => {
  renderCenterMap()
  renderTrendChart()
  renderCategoryChart()
  renderRegionChart()
  renderRadarChart()
}

watch(selectedRegion, (newVal) => {
  if (nationalDataBackup) {
    if (newVal === '全国') {
      // 恢复全国数据
      overview.value = { ...nationalDataBackup.overview }
      emissionTrends.value = nationalDataBackup.emissionTrends.map((item: any) => ({ ...item }))
      categoryDistribution.value = nationalDataBackup.categoryDistribution.map((item: any) => ({ ...item }))
      topUsers.value = nationalDataBackup.topUsers.map((item: any) => ({ ...item }))
    } else {
      // 模拟各省的区划数据
      // 找一下该省在 regionalStats 的原本排放比例作为基础 scale
      const rData = regionalStats.value.find(r => r.region === newVal)
      // 如果找到了，计算大致比例，否则给个默认小比例（如 0.05）
      let scale = 0.05
      if (rData && nationalDataBackup.overview.totalEmission) {
        scale = rData.totalEmission / nationalDataBackup.overview.totalEmission
      }
      scale = Math.max(0.01, Math.min(scale, 0.4)) // 限制在 1% ~ 40% 之间，让数据显得合理不为0

      // 使用 random() 增加一点波动感
      const randomScale = () => scale * (0.8 + Math.random() * 0.4)

      overview.value = {
        totalUsers: Math.floor(nationalDataBackup.overview.totalUsers * scale),
        totalEmission: nationalDataBackup.overview.totalEmission * randomScale(),
        totalReduction: nationalDataBackup.overview.totalReduction * randomScale(),
        totalPoints: Math.floor(nationalDataBackup.overview.totalPoints * scale),
        avgDailyEmission: nationalDataBackup.overview.avgDailyEmission * (0.9 + Math.random() * 0.2), // 人均差不多
        activeUsersToday: Math.floor(nationalDataBackup.overview.activeUsersToday * scale * (0.5 + Math.random()))
      }

      emissionTrends.value = nationalDataBackup.emissionTrends.map((item: any) => ({
        date: item.date,
        emission: item.emission * scale,
        transportEmission: item.transportEmission * scale,
        dietEmission: item.dietEmission * scale,
        electricityEmission: item.electricityEmission * scale
      }))

      categoryDistribution.value = nationalDataBackup.categoryDistribution.map((item: any) => ({
        category: item.category,
        value: item.value * scale,
        percentage: item.percentage // 暂且不变
      }))

      topUsers.value = nationalDataBackup.topUsers.map((item: any) => ({
        ...item,
        totalPoints: Math.floor(item.totalPoints * scale),
        totalReduction: item.totalReduction * scale
      }))
    }
  }

  nextTick(() => {
    renderTrendChart()
    renderCategoryChart()
    renderRegionChart()
    renderRadarChart()
  })
})

const toggleLeftRail = () => {
  leftCollapsed.value = !leftCollapsed.value
  setTimeout(() => {
    handleResize()
  }, 300)
}

const toggleRightRail = () => {
  rightCollapsed.value = !rightCollapsed.value
  setTimeout(() => {
    handleResize()
  }, 300)
}

const generateSingleActivity = (): RealTimeActivity => {
  const actions = ['记录了交通排放', '记录了饮食排放', '记录了用电排放', '完成了减排目标', '获得了积分奖励']
  const usernames = ['环保达人', '绿色先锋', '低碳生活', '节能专家', '减排先锋', '绿色使者', '环保卫士', '低碳达人']
  const regions = ['北京', '上海', '广东', '浙江', '江苏', '四川', '湖北', '陕西', '山东']

  return {
    id: Math.random().toString(36).substr(2, 9),
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    username: usernames[Math.floor(Math.random() * usernames.length)],
    activity: actions[Math.floor(Math.random() * actions.length)],
    emission: Math.random() * 5 + 1,
    type: ['transport', 'diet', 'electricity'][Math.floor(Math.random() * 3)],
    region: regions[Math.floor(Math.random() * regions.length)]
  }
}

const refreshRealTimeData = () => {
  const newActivity = generateSingleActivity()
  realTimeActivities.value = [newActivity, ...realTimeActivities.value].slice(0, 10)

  // 更新总指标
  if (overview.value.totalEmission !== undefined) {
    overview.value.totalEmission = Math.max(0, overview.value.totalEmission + (Math.random() * 20 - 5))
  }
  if (overview.value.totalReduction !== undefined) {
    overview.value.totalReduction = Math.max(0, overview.value.totalReduction + (Math.random() * 5 - 1))
  }
  if (overview.value.activeUsersToday !== undefined) {
    overview.value.activeUsersToday = Math.max(0, overview.value.activeUsersToday + Math.floor(Math.random() * 8) - 2)
  }
  
  // 随机更新排放分布饼图
  if (categoryDistribution.value.length > 0) {
    const rIdx = Math.floor(Math.random() * categoryDistribution.value.length)
    categoryDistribution.value[rIdx].value += Math.random() * 5
  }

  // 随机更新省份排行的前几名（让柱状图和地图有肉眼可见变化）
  if (regionalStats.value.length > 0) {
    for (let i = 0; i < 3; i++) {
        const randId = Math.floor(Math.random() * Math.min(8, regionalStats.value.length))
        regionalStats.value[randId].totalEmission += Math.random() * 15
        regionalStats.value[randId].userCount += Math.floor(Math.random() * 12)
    }
    // 排序保证省份图始终是从高到低
    regionalStats.value.sort((a, b) => b.totalEmission - a.totalEmission)
  }
  
  // 随机更新近期趋势折线（拿最新的一天追加一点数据）
  if (emissionTrends.value.length > 0) {
    const lastIdx = emissionTrends.value.length - 1
    const jitter = Math.random() * 10 - 2 // [-2, 8]
    emissionTrends.value[lastIdx].emission += jitter
    emissionTrends.value[lastIdx].transportEmission += jitter * 0.4
    emissionTrends.value[lastIdx].dietEmission += jitter * 0.4
    emissionTrends.value[lastIdx].electricityEmission += jitter * 0.2
    // reduction could also bounce
    emissionTrends.value[lastIdx].reduction += Math.random() * 4 - 1
  }

  nextTick(() => {
    renderCharts()
    updateMapScatter()
  })
}

const handleResize = () => {
  centerMapInstance?.resize()
  trendChartInstance?.resize()
  categoryChartInstance?.resize()
  regionChartInstance?.resize()
  radarChartInstance?.resize()
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)
  focusTimer = window.setInterval(startFocusRotation, 4200)
  dataTimer = window.setInterval(refreshRealTimeData, 3000)
  fetchDashboardData()
  window.addEventListener('resize', handleResize)
  window.addEventListener('mousemove', resetUserIdle)
  window.addEventListener('click', resetUserIdle)
  resetUserIdle() // Start the timer initially
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  if (focusTimer) {
    clearInterval(focusTimer)
  }
  if (dataTimer) {
    clearInterval(dataTimer)
  }
  stopMapAutoPlay()
  if (idleTimer) clearTimeout(idleTimer)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', resetUserIdle)
  window.removeEventListener('click', resetUserIdle)

  trendChartInstance?.dispose()
  categoryChartInstance?.dispose()
  regionChartInstance?.dispose()
  radarChartInstance?.dispose()

  window.removeEventListener('resize', handleResize)
})
</script>

<style  scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&family=Fira+Code:wght@500;700&display=swap');


.marquee-container {
  width: 100%;
  height: 36px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  position: relative;
  z-index: 10;
  overflow: hidden;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  margin: 12px 0 0 0;
}

.marquee-content {
  display: flex;
  white-space: nowrap;
  animation: scrollMarquee 40s linear infinite;
}

.marquee-content:hover {
  animation-play-state: paused;
}

.marquee-item {
  display: inline-flex;
  align-items: center;
  color: #00ffcc;
  font-size: 14px;
  margin-right: 80px;
  letter-spacing: 1px;
}

@keyframes scrollMarquee {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); } /* Half translation because content is duplicated */
}

.dashboard-screen {
  font-family: 'Noto Sans SC', sans-serif;
  --bg-0: #04111c;
  --bg-1: #071a28;
  --panel: rgba(7, 18, 30, 0.72);
  --panel-strong: rgba(10, 23, 36, 0.9);
  --line: rgba(141, 255, 198, 0.16);
  --line-strong: rgba(141, 255, 198, 0.28);
  --accent: #57f287;
  --accent-2: #7dd3fc;
  --accent-3: #f8b26a;
  --text: #eafff6;
  --muted: rgba(228, 248, 239, 0.68);
  width: 100%;
  height: 100vh;
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
  box-sizing: border-box;
  padding: 0 18px 18px;
  color: var(--text);
  background:
    radial-gradient(circle at 15% 12%, rgba(87, 242, 135, 0.18), transparent 22%),
    radial-gradient(circle at 82% 18%, rgba(125, 211, 252, 0.14), transparent 20%),
    radial-gradient(circle at 50% 110%, rgba(248, 178, 106, 0.12), transparent 24%),
    linear-gradient(135deg, #031019 0%, #051521 40%, #071a28 100%);
}

.ambient {
  position: absolute;
  border-radius: 999px;
  filter: blur(24px);
  pointer-events: none;
  opacity: 0.9;
  animation: drift 12s ease-in-out infinite;
}

.ambient-a {
  width: 240px;
  height: 240px;
  left: -60px;
  top: 80px;
  background: rgba(87, 242, 135, 0.12);
}

.ambient-b {
  width: 300px;
  height: 300px;
  right: -90px;
  top: 120px;
  background: rgba(125, 211, 252, 0.12);
  animation-delay: -3s;
}

.ambient-c {
  width: 320px;
  height: 320px;
  left: 28%;
  bottom: -130px;
  background: rgba(248, 178, 106, 0.08);
  animation-delay: -5s;
}


.topbar {
  position: sticky;
  top: 12px;
  z-index: 100;
  display: grid;
  grid-template-columns: minmax(280px, 1.2fr) auto auto 1fr;
  align-items: center;
  gap: 24px;
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
  padding: 12px 24px;
  margin-top: 12px;
  margin-bottom: 24px;
  border-radius: 16px;
  border: 1px solid transparent;
}

.topbar.scrolled {
  background: rgba(10, 15, 26, 0.85); /* Dark solidish glass */
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4), inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(28px);
  -webkit-backdrop-filter: blur(28px);
  transform: translateY(-50%);
  width: 24px;
  height: 64px;
  background: rgba(87, 242, 135, 0.15);
  border: 1px solid rgba(87, 242, 135, 0.3);
  border-radius: 6px;
  color: #57f287;
  cursor: pointer;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  backdrop-filter: blur(12px);
}

.rail:hover .rail-toggle-btn {
  opacity: 1;
  visibility: visible;
}

.rail-toggle-btn:hover {
  background: rgba(87, 242, 135, 0.3);
  box-shadow: 0 0 16px rgba(87, 242, 135, 0.4);
}

.toggle-left {
  right: -12px; /* Hang over the edge */
}

.toggle-right {
  left: -12px; /* Hang over the edge */
}


/* --- Advanced Premium Animations --- */

/* 1. Staggered Entrance (Slide & Fade) */
@keyframes slideUpFade {
  0% { opacity: 0; transform: translateY(30px) scale(0.98); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

/* 2. Glass Shine Sweep (Runs once on load) */
@keyframes glassShine {
  0% { transform: translateX(-100%) skewX(-15deg); }
  100% { transform: translateX(200%) skewX(-15deg); }
}

/* 3. Deep Breathing Shadow (For Hero/Active panels) */
@keyframes deepBreathe {
  0%, 100% { box-shadow: inset 0 0 0 1px rgba(87, 242, 135, 0.05), 0 16px 48px rgba(0,0,0,0.3), 0 0 20px rgba(87, 242, 135, 0.02); }
  50% { box-shadow: inset 0 0 0 1px rgba(87, 242, 135, 0.15), 0 24px 64px rgba(0,0,0,0.5), 0 0 40px rgba(87, 242, 135, 0.08); }
}

/* 4. Ambient Micro-Float (Extremely subtle to prevent reading issues) */
@keyframes microFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

/* Base Panel Styles Refined */
.rail .panel, .center-stage .panel {
  animation: slideUpFade 0.8s cubic-bezier(0.16, 1, 0.3, 1) backwards, 
             microFloat 12s ease-in-out infinite !important; /* Infinite slow drift */
  overflow: hidden; /* For glass shine */
}

/* Glass Shine pseudo-element on panel */
.rail .panel::after, .center-stage .panel::after {
  content: "";
  position: absolute;
  top: 0; left: 0;
  width: 40%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.04), transparent);
  pointer-events: none;
  animation: glassShine 2.5s ease-out forwards;
}

/* Stagger logic for children */
.rail-left .panel:nth-child(1) { animation-delay: 0.1s, 0s !important; }
.rail-left .panel:nth-child(1)::after { animation-delay: 0.1s; }
.rail-left .panel:nth-child(2) { animation-delay: 0.2s, -2s !important; }
.rail-left .panel:nth-child(2)::after { animation-delay: 0.2s; }
.hero-panel { animation-delay: 0.3s, -4s !important; animation: slideUpFade 0.8s cubic-bezier(0.16, 1, 0.3, 1) backwards, deepBreathe 6s ease-in-out infinite !important; }
.hero-panel::after { animation-delay: 0.3s; width: 20%; background: linear-gradient(90deg, transparent, rgba(87,242,135,0.03), transparent); }
.panel-wide:nth-child(1) { animation-delay: 0.4s, -1s !important; }
.panel-wide:nth-child(1)::after { animation-delay: 0.4s; }
.panel-wide:nth-child(2) { animation-delay: 0.5s, -3s !important; }
.panel-wide:nth-child(2)::after { animation-delay: 0.5s; }
.rail-right .panel { animation-delay: 0.6s, -5s !important; }
.rail-right .panel::after { animation-delay: 0.6s; }

/* Advanced Hover Impact */
.rail .panel:hover, .center-stage .panel:hover {
  transform: translateY(-4px) scale(1.005) !important;
  box-shadow: inset 0 0 0 1px rgba(87, 242, 135, 0.2), 0 32px 80px rgba(0, 0, 0, 0.6), 0 0 30px rgba(87, 242, 135, 0.05);
  border-color: transparent; /* let inner shadow do the work */
  z-index: 10;
  animation-play-state: paused, paused !important; /* Stop floating & breathing while reading */
}

/* Text Shimmer Sweep for main numbers */
@keyframes textShimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

.summary-number, .core-value {
  background: linear-gradient(90deg, #f1fff7 0%, #57f287 30%, #f1fff7 50%, #7dd3fc 80%, #f1fff7 100%);
  background-size: 300% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: textShimmer 6s linear infinite;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  text-decoration: none;
  color: inherit;
}

.brand-mark {
  width: 52px;
  height: 52px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  border: 1px solid rgba(87, 242, 135, 0.45);
  background: linear-gradient(135deg, rgba(87, 242, 135, 0.18), rgba(125, 211, 252, 0.1));
  box-shadow: inset 0 0 24px rgba(87, 242, 135, 0.22);
  font-weight: 800;
  letter-spacing: 0.08em;
}

.brand-title {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.04em;
}

.brand-subtitle {
  margin-top: 4px;
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.status-group {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.status-pill {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: var(--muted);
  font-size: 12px;
  letter-spacing: 0.04em;
}

.status-pill.live {
  color: #eafff6;
  border-color: rgba(87, 242, 135, 0.34);
  background: rgba(87, 242, 135, 0.12);
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.clock {
  justify-self: center;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid rgba(87, 242, 135, 0.22);
  background: rgba(87, 242, 135, 0.08);
  color: #eafff6;
  font-size: 13px;
  letter-spacing: 0.08em;
  white-space: nowrap;
}

.toolbar-btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text);
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 13px;
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease;
}

.toolbar-btn:hover {
  transform: translateY(-1px);
  border-color: rgba(87, 242, 135, 0.42);
  background: rgba(87, 242, 135, 0.11);
}

.toolbar-btn.primary {
  border-color: rgba(87, 242, 135, 0.4);
  background: linear-gradient(135deg, rgba(87, 242, 135, 0.2), rgba(125, 211, 252, 0.12));
}

.dashboard-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: var(--left-width) minmax(0, 1fr) var(--right-width);
  gap: 24px;
  min-height: calc(100vh - 106px);
  transition: grid-template-columns 0.28s ease;
}

.rail,
.center-stage {
  min-width: 0;
}

.rail {
  position: relative;
  z-index: 20;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.rail-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.panel.active {
  border-color: rgba(87, 242, 135, 0.42);
  box-shadow: inset 0 0 0 1px rgba(87, 242, 135, 0.1), 0 24px 72px rgba(0, 0, 0, 0.4), 0 0 36px rgba(87, 242, 135, 0.15);
  min-width: 0;
  transform: translateY(-2px);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.rail .panel,
.hero-panel,
.panel {
  position: relative;
  padding: 20px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  background: rgba(10, 15, 26, 0.55);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.03), 0 16px 48px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 0 0 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 0.02em;
}

.panel-head::before {
  content: '';
  position: absolute;
  left: 20px;
  top: 22px;
  width: 4px;
  height: 14px;
  background: #57f287;
  border-radius: 4px;
  box-shadow: 0 0 12px rgba(87, 242, 135, 0.4);
}

.panel-head > span:first-child {
  padding-left: 12px;
}

.panel-sub {
  color: rgba(255, 255, 255, 0.65);
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.rail-summary {
  display: grid;
  gap: 10px;
  align-content: start;
}

.summary-number {
  font-family: 'Bahnschrift', sans-serif;
  font-size: 34px;
  font-weight: 800;
  line-height: 1;
  background: linear-gradient(90deg, #f1fff7, #57f287 60%, #7dd3fc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.summary-label {
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
}

.summary-mini-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.mini-chip {
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.mini-chip-label {
  display: block;
  margin-bottom: 6px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  font-weight: 500;
}

.mini-chip strong {
  font-size: 16px;
}

.rail.collapsed .rail-body {
  display: none;
}

.rail.collapsed .panel {
  padding: 12px;
}

.rail.collapsed .panel-head::before {
  left: 12px;
  top: 14px;
}

.rail.collapsed .summary-number {
  font-family: 'Bahnschrift', sans-serif;
  font-size: 24px;
}

.center-stage {
  display: flex;
  flex-direction: column;
  gap: 24px;
  min-width: 0;
}

.hero-panel {
  display: grid;
  /* 🌟 核心魔法：左侧文字锁定在 300px 到 380px 之间，右侧地图独占剩余的 1fr（所有空间） */
  grid-template-columns: minmax(300px, 380px) 1fr;
  gap: 32px;
  align-items: center;
  padding: 40px 48px;
  background: radial-gradient(circle at 75% 50%, rgba(10, 18, 30, 0.2), rgba(11, 25, 39, 0.8)), rgba(10, 15, 26, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.03), 0 24px 64px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.hero-copy h2 {
  margin: 12px 0 16px;
  font-size: clamp(24px, 3.5vw, 48px);
  line-height: 1.1;
  font-weight: 800;
  letter-spacing: -0.01em;
  color: #fff;
  text-shadow: 0 0 32px rgba(255,255,255,0.4);
  display: block;
  max-width: 100%;
  word-break: break-word;
}

.hero-copy p {
  margin: 0;
  max-width: 100%; 
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px; 
  line-height: 1.75;
}

.focus-strip {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-top: 18px;
  padding: 10px 14px;
  border-radius: 999px;
  border: 1px solid rgba(87, 242, 135, 0.2);
  background: rgba(87, 242, 135, 0.08);
  color: var(--text);
  max-width: 100%;
}

.focus-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #57f287;
  box-shadow: 0 0 14px rgba(87, 242, 135, 0.65);
  animation: pulse 1.8s ease-in-out infinite;
  flex: 0 0 auto;
}

.focus-label {
  font-weight: 700;
  color: #eafff6;
  flex: 0 0 auto;
}

.focus-copy {
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hero-kicker {
  color: #57f287;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.core-visual {
  position: relative;
  display: block;
  min-height: 600px;
  border-radius: 28px;
  background:
    radial-gradient(circle at center, rgba(87, 242, 135, 0.16), transparent 30%),
    radial-gradient(circle at center, rgba(125, 211, 252, 0.12), transparent 45%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.04), rgba(255, 255, 255, 0));
  overflow: hidden;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.05), inset 0 0 32px rgba(87, 242, 135, 0.08);
}

.center-map {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}

.core-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.ring-a {
  width: 270px;
  height: 270px;
  animation: spin 20s linear infinite;
  border-color: rgba(87, 242, 135, 0.22);
}

.ring-b {
  width: 330px;
  height: 330px;
  animation: spinReverse 26s linear infinite;
  border-color: rgba(125, 211, 252, 0.16);
}

.ring-c {
  width: 400px;
  height: 400px;
  animation: spin 34s linear infinite;
  border-color: rgba(248, 178, 106, 0.12);
}

.core-pulse {
  position: absolute;
  width: 170px;
  height: 170px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(87, 242, 135, 0.42), rgba(87, 242, 135, 0.08) 52%, transparent 72%);
  animation: pulse 3.4s ease-in-out infinite;
}

.core-particles {
  position: absolute;
  inset: 0;
}

.core-particle {
  position: absolute;
  left: 50%;
  top: 50%;
  border-radius: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(87, 242, 135, 0.65));
  box-shadow: 0 0 18px rgba(87, 242, 135, 0.55);
  animation: orbitFloat 5.8s ease-in-out infinite;
}

.core-center {
  position: relative;
  z-index: 1;
  display: grid;
  place-items: center;
  gap: 6px;
  width: 250px;
  height: 250px;
  border-radius: 50%;
  border: 1px solid rgba(87, 242, 135, 0.2);
  background: radial-gradient(circle, rgba(7, 18, 30, 0.88), rgba(7, 18, 30, 0.52));
  box-shadow: inset 0 0 40px rgba(87, 242, 135, 0.14), 0 0 48px rgba(87, 242, 135, 0.12);
}

.core-value {
  font-size: clamp(24px, 3.5vw, 64px);
  font-weight: 900;
  line-height: 1;
  letter-spacing: -0.02em;
  filter: drop-shadow(0 0 18px rgba(87, 242, 135, 0.5));
  display: block;
  max-width: 100%;
  word-break: break-word;
}

.core-unit {
  color: #57f287;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.core-label {
  color: var(--muted);
  font-size: 13px;
}

.orbit {
  position: absolute;
  z-index: 1;
  padding: 10px 14px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: var(--text);
  font-size: 14px;
  font-weight: 700;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  white-space: nowrap;
  backdrop-filter: blur(12px);
}

.orbit-top {
  top: 26px;
  animation: floatY 5s ease-in-out infinite;
}

.orbit-right {
  right: 22px;
  animation: floatX 5.4s ease-in-out infinite;
}

.orbit-bottom {
  bottom: 30px;
  animation: floatY 6s ease-in-out infinite reverse;
}

.orbit-left {
  left: 22px;
  animation: floatX 5.6s ease-in-out infinite reverse;
}

.hero-metrics {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 8px;
}

.hero-metric {
  padding: 20px 24px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05), rgba(255, 255, 255, 0.01));
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.05);
  display: grid;
  gap: 8px;
}

.hero-metric span,
.hero-metric em {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
}

.recommend-text,
.insight-text,
.activity-time,
.ranking-meta {
  color: rgba(255, 255, 255, 0.75);
}

.hero-metric strong {
  font-size: clamp(20px, 2.5vw, 42px);
  line-height: 1;
  font-weight: 900;
  color: #fff;
  text-shadow: 0 0 24px rgba(255, 255, 255, 0.6);
  display: block;
  max-width: 100%;
  word-break: break-word;
}

.hero-metric em {
  font-style: normal;
  font-size: 12px;
}

.center-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.panel-wide {
  min-height: 360px;
}

.panel-split {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(260px, 0.92fr);
  gap: 14px;
  align-items: stretch;
}

.chart-box {
  width: 100%;
  min-height: 280px;
}

.chart-box.tall {
  min-height: 320px;
}

.center-chart {
  min-height: 320px;
}

.insight-list,
.recommend-list,
.ranking-list,
.activity-list,
.snapshot-grid {
  display: grid;
  gap: 12px;
}

.insight-card,
.recommend-item,
.snapshot-card {
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.insight-card.accent {
  background: linear-gradient(135deg, rgba(87, 242, 135, 0.12), rgba(125, 211, 252, 0.08));
  border-color: rgba(87, 242, 135, 0.22);
}

.insight-title,
.recommend-title,
.snapshot-card span,
.activity-user,
.ranking-name {
  font-weight: 700;
}

.insight-value {
  font-family: 'Bahnschrift', sans-serif;
  margin: 8px 0 6px;
  font-size: 22px;
  font-weight: 800;
}

.recommend-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.recommend-badge {
  font-family: 'Bahnschrift', sans-serif;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(87, 242, 135, 0.14);
  color: #dfffee;
  font-size: 12px;
}

.recommend-text {
  margin-top: 10px;
  line-height: 1.7;
}

.activity-item {
  display: grid;
  grid-template-columns: 64px minmax(0, 1fr);
  gap: 12px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.activity-item.active {
  border-color: rgba(87, 242, 135, 0.28);
  background: rgba(87, 242, 135, 0.08);
  transform: translateX(4px);
}

.activity-time {
  font-size: 12px;
  line-height: 1.6;
}

.activity-main {
  display: grid;
  gap: 6px;
}

.activity-row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
}

.activity-tag {
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 12px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(255, 255, 255, 0.04);
}

.activity-tag.transport {
  color: #7dd3fc;
  border-color: rgba(125, 211, 252, 0.22);
}

.activity-tag.diet {
  color: #f8b26a;
  border-color: rgba(248, 178, 106, 0.22);
}

.activity-tag.electricity {
  color: #c4b5fd;
  border-color: rgba(196, 181, 253, 0.22);
}

.activity-desc {
  color: var(--text);
  line-height: 1.6;
}

.activity-emission {
  color: #57f287;
  font-weight: 700;
  font-size: 13px;
}

.ranking-item {
  display: grid;
  grid-template-columns: 38px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.ranking-item.active {
  border-color: rgba(125, 211, 252, 0.3);
  background: rgba(125, 211, 252, 0.08);
  transform: translateX(4px);
}

.ranking-rank {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.06);
  font-weight: 800;
}

.ranking-rank.rank-1 {
  background: linear-gradient(135deg, rgba(255, 215, 102, 0.28), rgba(255, 215, 102, 0.08));
}

.ranking-rank.rank-2 {
  background: linear-gradient(135deg, rgba(192, 208, 230, 0.22), rgba(192, 208, 230, 0.06));
}

.ranking-rank.rank-3 {
  background: linear-gradient(135deg, rgba(248, 178, 106, 0.24), rgba(248, 178, 106, 0.06));
}

.ranking-main {
  display: grid;
  gap: 4px;
}

.snapshot-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.snapshot-card {
  display: grid;
  gap: 8px;
}

.snapshot-card strong {
  font-size: 22px;
  line-height: 1;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(0.94);
    opacity: 0.72;
  }
  50% {
    transform: scale(1.03);
    opacity: 1;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes spinReverse {
  from {
    transform: rotate(360deg);
  }
  to {
    transform: rotate(0deg);
  }
}

@keyframes floatX {
  0%,
  100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(10px);
  }
}

@keyframes floatY {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes drift {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(0, -14px, 0) scale(1.04);
  }
}

@keyframes orbitFloat {
  0%,
  100% {
    opacity: 0.35;
    transform: translate(var(--x, 0), var(--y, 0)) scale(0.9);
  }
  50% {
    opacity: 1;
    transform: translate(var(--x, 0), var(--y, 0)) scale(1.18);
  }
}

@media (max-width: 1600px) {
  .dashboard-grid {
    grid-template-columns: 340px minmax(0, 1fr) 340px;
  }

  .hero-panel {
    grid-template-columns: 1fr;
  }

  .center-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1280px) {
  .dashboard-screen {
    font-family: 'Noto Sans SC', sans-serif;
  overflow: auto;
  }

  .topbar {
    grid-template-columns: 1fr;
    justify-items: start;
  }

  .clock {
    justify-self: start;
  }

  .status-group,
  .toolbar {
    justify-content: flex-start;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .rail {
    order: 2;
  }

  .center-stage {
    order: 1;
  }
}

@media (max-width: 768px) {
  .dashboard-screen {
    font-family: 'Noto Sans SC', sans-serif;
  padding: 12px;
  }

  .panel,
  .hero-panel {
    padding: 14px;
    border-radius: 18px;
  }

  .hero-copy h2 {
    font-size: 26px;
  }

  .core-visual {
    min-height: 300px;
  }

  .hero-metrics,
  .summary-mini-grid,
  .snapshot-grid,
  .panel-split {
    grid-template-columns: 1fr;
  }

  .activity-item {
    grid-template-columns: 1fr;
  }
}

/* ====================================================
   PREMIUM TOGGLE BUTTON OVERRIDES
   ==================================================== */
.rail-toggle-btn {
  position: absolute !important;
  top: 26px !important;
  z-index: 1000 !important;
  width: 28px !important;
  height: 64px !important;
  background: linear-gradient(180deg, rgba(16, 24, 38, 0.7), rgba(9, 18, 28, 0.95)) !important;
  border: 1px solid rgba(87, 242, 135, 0.25) !important;
  border-radius: 14px !important;
  color: #57f287 !important;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 16px rgba(0, 0, 0, 0.4) !important;
  backdrop-filter: blur(16px) !important;
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1) !important;
}

.rail-toggle-btn:hover {
  background: linear-gradient(135deg, rgba(87, 242, 135, 0.35), rgba(125, 211, 252, 0.15)) !important;
  box-shadow: inset 0 0 0 1px rgba(87, 242, 135, 0.4), 0 12px 32px rgba(0, 0, 0, 0.6), 0 0 20px rgba(87, 242, 135, 0.3) !important;
  border-color: rgba(87, 242, 135, 0.6) !important;
  color: #fff !important;
  transform: scale(1.08) !important;
}

.toggle-left { right: -16px !important; }
.toggle-right { left: -16px !important; }

.rail-toggle-btn .toggle-icon {
  font-size: 14px !important;
  transition: transform 0.3s ease, text-shadow 0.3s ease !important;
}

.rail-toggle-btn:hover .toggle-icon {
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.9) !important;
}

/* 实时动态平滑出入动画 */
.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}
.list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

</style>
