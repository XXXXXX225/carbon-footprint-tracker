<template>
  <el-container class="points-container">
    <el-header height="60px" class="points-header">
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
      <el-aside width="200px" class="points-aside">
        <RoleSidebar />
      </el-aside>
      <el-main class="points-main">
        <h2>减碳积分中心</h2>
        
        <!-- 积分概览卡片 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card points-overview-card bounce-animation">
              <div class="overview-item">
                <div class="overview-label">总积分</div>
                <div class="overview-value points-value">{{ totalPoints }}</div>
                <div class="overview-change points-change">
                  <el-icon><Star /></el-icon>
                  <span>当前等级: {{ currentLevel }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card points-overview-card">
              <div class="overview-item">
                <div class="overview-label">累计减碳</div>
                <div class="overview-value points-value">{{ totalEmissionReduced.toFixed(2) }} kg</div>
                <div class="overview-change points-change">
                  <el-icon><TrendCharts /></el-icon>
                  <span>相当于种植 {{ treesPlanted }} 棵树</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card points-overview-card">
              <div class="overview-item">
                <div class="overview-label">本月获得</div>
                <div class="overview-value points-value">{{ monthlyPoints }} 积分</div>
                <div class="overview-change points-change">
                  <el-icon><Calendar /></el-icon>
                  <span>减碳 {{ monthlyEmissionReduced.toFixed(2) }} kg</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 绿色兑换商城 -->
        <el-card class="mall-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header mall-header">
              <span><el-icon><ShoppingCart /></el-icon> 绿色兑换商城</span>
              <el-tag type="success" effect="dark" round>您当前拥有 {{ totalPoints }} 积分</el-tag>
            </div>
          </template>
          <div class="mall-content">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" v-for="(item, index) in storeItems" :key="index">
                <div class="mall-item">
                  <div class="mall-icon">{{ item.icon }}</div>
                  <div class="mall-info">
                    <h4>{{ item.name }}</h4>
                    <p class="mall-cost">{{ item.cost }} 积分</p>
                    <el-button type="success" plain size="small" @click="exchangeItem(item)">
                      立即兑换
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>

        <!-- AI闯关任务 -->
        <el-card class="quest-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header quest-header">
              <span><el-icon><CollectionTag /></el-icon> AI 闯关任务</span>
              <el-tag type="warning" effect="dark" round>完成可解锁额外积分奖励</el-tag>
            </div>
          </template>
          <div class="quest-content">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12" :md="8" v-for="quest in challengeCards" :key="quest.title">
                <div class="quest-item" :class="quest.styleClass">
                  <div class="quest-topline">
                    <span class="quest-tier">{{ quest.tier }}</span>
                    <span class="quest-reward">+{{ quest.reward }} 分</span>
                  </div>
                  <h4>{{ quest.title }}</h4>
                  <p>{{ quest.description }}</p>
                  <div class="quest-meta">{{ quest.hint }}</div>
                  <el-button type="warning" plain size="small" @click="startQuest(quest)">
                    领取挑战
                  </el-button>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>

        <!-- 积分规则说明 -->
        <el-card class="rules-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>积分规则说明</span>
            </div>
          </template>
          <div class="rules-content">
            <el-row :gutter="20">
              <el-col :xs="24" :md="8">
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><Van /></el-icon>
                  </div>
                  <div class="rule-info">
                    <h4>交通出行</h4>
                    <p>每减少1kg碳排放获得10积分</p>
                    <p class="rule-desc">与步行/骑行相比，使用公共交通、电动车等低碳出行方式可获得积分</p>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :md="8">
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><KnifeFork /></el-icon>
                  </div>
                  <div class="rule-info">
                    <h4>饮食消费</h4>
                    <p>每减少1kg碳排放获得10积分</p>
                    <p class="rule-desc">选择植物性食物、减少肉类消费可获得积分</p>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :md="8">
                <div class="rule-item">
                  <div class="rule-icon">
                    <el-icon><Lightning /></el-icon>
                  </div>
                  <div class="rule-info">
                    <h4>用电节能</h4>
                    <p>每减少1kg碳排放获得10积分</p>
                    <p class="rule-desc">使用节能电器、减少待机时间可获得积分</p>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
        
        <!-- 积分历史记录 -->
        <el-card class="history-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>积分历史记录</span>
              <el-button type="primary" size="small" @click="exportPointsHistory">
                <el-icon><Download /></el-icon>
                导出记录
              </el-button>
            </div>
          </template>
          <div class="history-content">
            <el-table :data="pointsHistory" style="width: 100%">
              <el-table-column prop="date" label="日期" width="180" />
              <el-table-column prop="reason" label="获得原因" />
              <el-table-column prop="emissionReduced" label="减碳量(kg)" width="120">
                <template #default="scope">
                  {{ scope.row.emissionReduced.toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="pointsChange" label="获得积分" width="100" />
              <el-table-column prop="totalPoints" label="积分余额" width="100" />
            </el-table>
            <div class="pagination-container" style="margin-top: 20px;">
              <el-pagination
                :current-page="currentPage"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="totalHistory"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </el-card>
        
        <!-- 积分等级说明 -->
        <el-card class="levels-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>积分等级说明</span>
            </div>
          </template>
          <div class="levels-content">
            <el-row :gutter="20">
              <el-col :xs="12" :sm="8" :md="4" v-for="level in levels" :key="level.id" class="level-item" :class="{ active: level.id === currentLevelId }">
                <div class="level-icon">{{ level.icon }}</div>
                <div class="level-name">{{ level.name }}</div>
                <div class="level-requirement">{{ level.requirement }}</div>
                <div class="level-desc">{{ level.description }}</div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import RoleSidebar from '../components/RoleSidebar.vue'
import { pointsApi } from '../api'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, Download, Calendar, TrendCharts, CollectionTag, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/points')

// 加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  loadPointsData()
})

const user = computed(() => carbonStore.user)

// 积分商城数据 (升级为真实的商业闭环羊毛)
const storeItems = ref([
  { id: 1, name: '滴滴打车 5元环保立减券', icon: '🚕', cost: 100, badge: '高频', description: '把减碳直接转成出行优惠' },
  { id: 2, name: '瑞幸咖啡 免费兑换券', icon: '☕', cost: 300, badge: '羊毛', description: '需使用个人自带杯前往门店核销兑换' },
  { id: 3, name: '蚂蚁森林 500g能量球', icon: '🌲', cost: 50, badge: '社交', description: '立即同步到支付宝，保护能量不被偷' }
])

const exchangeItem = async (item: any) => {
  if (totalPoints.value < item.cost) {
    ElMessage.warning(`积分不足！还需要 ${item.cost - totalPoints.value} 积分。`)
    return
  }

  try {
    await ElMessageBox.confirm(`确认消耗 ${item.cost} 积分兑换【${item.name}】吗？`, '积分兑换', {
      confirmButtonText: '确定兑换',
      cancelButtonText: '暂不兑换',
      type: 'success'
    })

    const result = await pointsApi.redeemPoints({
      pointsSpent: item.cost,
      reason: `兑换商城商品: ${item.name}`
    })

    totalPoints.value = result.remainingPoints
    refreshLevel(totalPoints.value)
    await loadPointsData()
    ElMessage.success(`恭喜您成功兑换【${item.name}】！`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('兑换失败，请稍后重试')
    }
  }
}

// 积分相关数据
const totalPoints = ref(1250)
const totalEmissionReduced = ref(125.5)
const monthlyPoints = ref(150)
const monthlyEmissionReduced = ref(15.5)
const treesPlanted = ref(4)
const currentLevel = ref('环保先锋')
const currentLevelId = ref(3)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const totalHistory = ref(50)

// 积分历史记录
const pointsHistory = ref([
  { date: '2026-02-08', reason: '交通出行减碳', emissionReduced: 2.5, pointsChange: 25, totalPoints: 1250 },
  { date: '2026-02-07', reason: '饮食减碳', emissionReduced: 1.8, pointsChange: 18, totalPoints: 1225 },
  { date: '2026-02-06', reason: '用电减碳', emissionReduced: 1.2, pointsChange: 12, totalPoints: 1207 },
  { date: '2026-02-05', reason: '交通出行减碳', emissionReduced: 3.0, pointsChange: 30, totalPoints: 1195 },
  { date: '2026-02-04', reason: '饮食减碳', emissionReduced: 2.2, pointsChange: 22, totalPoints: 1165 },
  { date: '2026-02-03', reason: '用电减碳', emissionReduced: 1.5, pointsChange: 15, totalPoints: 1143 },
  { date: '2026-02-02', reason: '交通出行减碳', emissionReduced: 2.8, pointsChange: 28, totalPoints: 1128 },
  { date: '2026-02-01', reason: '饮食减碳', emissionReduced: 1.9, pointsChange: 19, totalPoints: 1100 },
  { date: '2026-01-31', reason: '用电减碳', emissionReduced: 1.3, pointsChange: 13, totalPoints: 1081 },
  { date: '2026-01-30', reason: '交通出行减碳', emissionReduced: 2.6, pointsChange: 26, totalPoints: 1068 }
])

type ChallengeCard = {
  title: string
  description: string
  hint: string
  tier: string
  reward: number
  styleClass: string
}

const challengeCards = computed<ChallengeCard[]>(() => {
  const primaryTask = totalPoints.value >= 1000
    ? '将 AI 诊断中的最高风险项转成一次真实兑换或减排行动'
    : '先完成 AI 诊断后解锁定制任务'
  const secondaryTask = Array.isArray(pointsHistory.value) && pointsHistory.value.length > 0
    ? `最近 ${Math.min(pointsHistory.value.length, 5)} 条积分记录已接入挑战系统`
    : '完成一次减碳行为后即可刷新任务奖励'

  return [
    {
      title: 'AI 诊断首通',
      description: primaryTask,
      hint: '完成后可在 AI 分析页领取对应任务并累计积分',
      tier: '新手关',
      reward: 120,
      styleClass: 'quest-primary'
    },
    {
      title: '连续减碳挑战',
      description: '连续完成 3 次减碳记录，解锁加速奖励',
      hint: secondaryTask,
      tier: '进阶关',
      reward: 180,
      styleClass: 'quest-secondary'
    },
    {
      title: '兑换满额加成',
      description: '累计兑换任意 2 次绿色商品，解锁神秘徽章',
      hint: '适合把积分转成生活方式奖励的用户',
      tier: '隐藏关',
      reward: 260,
      styleClass: 'quest-elite'
    }
  ]
})

const refreshLevel = (points: number) => {
  if (points >= 5000) {
    currentLevel.value = '碳中和大使'
    currentLevelId.value = 6
  } else if (points >= 3000) {
    currentLevel.value = '环保大师'
    currentLevelId.value = 5
  } else if (points >= 1500) {
    currentLevel.value = '环保领袖'
    currentLevelId.value = 4
  } else if (points >= 500) {
    currentLevel.value = '环保先锋'
    currentLevelId.value = 3
  } else if (points >= 100) {
    currentLevel.value = '环保爱好者'
    currentLevelId.value = 2
  } else {
    currentLevel.value = '环保新手'
    currentLevelId.value = 1
  }
}

const refreshStatsFromHistory = () => {
  const records = pointsHistory.value
  totalEmissionReduced.value = Number(records.reduce((sum, record) => sum + (record.emissionReduced || 0), 0).toFixed(2))
  monthlyPoints.value = records
    .filter(record => record.date.startsWith(new Date().toISOString().slice(0, 7)))
    .reduce((sum, record) => sum + (record.pointsChange || 0), 0)
  monthlyEmissionReduced.value = Number(records
    .filter(record => record.date.startsWith(new Date().toISOString().slice(0, 7)))
    .reduce((sum, record) => sum + (record.emissionReduced || 0), 0)
    .toFixed(2))
  treesPlanted.value = Math.max(1, Math.round(totalEmissionReduced.value / 30))
}

const startQuest = (quest: ChallengeCard) => {
  if (quest.title === 'AI 诊断首通') {
    router.push('/ai-analysis')
    return
  }

  ElMessage.success(`${quest.title} 已领取，建议先完成对应减碳动作再回来兑换奖励。`)
}

const mapHistoryRecord = (record: any) => ({
  date: record.createdAt ? new Date(record.createdAt).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
  reason: record.reason,
  emissionReduced: Number(record.emissionReduced || 0),
  pointsChange: Number(record.pointsChange || 0),
  totalPoints: Number(record.totalPoints || 0)
})

// 积分等级
const levels = ref([
  {
    id: 1,
    name: '环保新手',
    icon: '🌱',
    requirement: '0-100积分',
    description: '开始您的环保之旅'
  },
  {
    id: 2,
    name: '环保爱好者',
    icon: '🌿',
    requirement: '100-500积分',
    description: '积极参与环保活动'
  },
  {
    id: 3,
    name: '环保先锋',
    icon: '🌳',
    requirement: '500-1500积分',
    description: '为环保事业做出贡献'
  },
  {
    id: 4,
    name: '环保领袖',
    icon: '🏆',
    requirement: '1500-3000积分',
    description: '引领环保潮流'
  },
  {
    id: 5,
    name: '环保大师',
    icon: '🌟',
    requirement: '3000+积分',
    description: '环保领域的专家'
  },
  {
    id: 6,
    name: '碳中和大使',
    icon: '🌍',
    requirement: '5000+积分',
    description: '为碳中和目标不懈努力'
  }
])

// 加载积分数据
const loadPointsData = async () => {
  try {
    const [total, records] = await Promise.all([
      pointsApi.getTotalPoints(),
      pointsApi.getPointsRecords()
    ])

    totalPoints.value = total ?? 0
    pointsHistory.value = (records || []).map(mapHistoryRecord).sort((a, b) => b.date.localeCompare(a.date))
    totalHistory.value = pointsHistory.value.length
    refreshLevel(totalPoints.value)
    refreshStatsFromHistory()
  } catch (error) {
    console.error('Failed to load points data:', error)
    totalHistory.value = pointsHistory.value.length
    refreshLevel(totalPoints.value)
    refreshStatsFromHistory()
  }
}

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

const exportPointsHistory = () => {
  // 导出积分历史记录的逻辑
  console.log('Exporting points history...')
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  // 重新加载数据
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  // 重新加载数据
}
</script>

<style scoped>
.points-container {
  min-height: 100vh;
}

.points-header {
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

.points-aside {
  background-color: #fff;
}

.points-menu {
  height: 100%;
  border-right: none;
}

.points-main {
  padding: 20px;
}

/* 积分概览卡片 */
.points-overview-card {
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

/* 规则卡片 */
.rules-card {
  margin-top: 20px;
}

.rule-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 15px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.rule-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.rule-icon {
  font-size: 24px;
  margin-right: 15px;
  color: #4CAF50;
}

.rule-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.rule-info p {
  margin: 0 0 5px 0;
  color: #666;
}

.rule-desc {
  font-size: 12px;
  color: #999;
}

/* 历史记录 */
.history-card {
  margin-top: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

/* 等级卡片 */
.levels-card {
  margin-top: 20px;
}

.level-item {
  text-align: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.level-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.level-item.active {
  background-color: #e6f7ff;
  border: 2px solid #4CAF50;
  box-shadow: 0 8px 16px rgba(76, 175, 80, 0.2);
}

.level-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.level-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.level-requirement {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.level-desc {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .points-aside {
    display: none;
  }
  
  .points-main {
    padding: 15px 10px;
  }
  
  .points-header h1 {
    font-size: 16px;
  }
  
  .overview-card {
    margin-bottom: 15px;
  }
  
  .level-item {
    margin-bottom: 15px;
  }
}

.bounce-animation .points-value {
  color: #FF9800 !important;
  display: inline-block;
  animation: pulse 2.5s infinite ease-in-out;
  transform-origin: left bottom;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); text-shadow: 0 0 0 rgba(255, 152, 0, 0); }
  50% { transform: scale(1.08); text-shadow: 0 0 12px rgba(255, 152, 0, 0.4); }
}
.mall-card {
  border-radius: 12px;
  background: linear-gradient(180deg, #f9fff6 0%, #fff 100%);
  border: 1px solid #dcdfe6;
}

.mall-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mall-header span {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: bold;
}

.mall-item {
  display: flex;
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  gap: 15px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
  transition: all 0.3s;
}

.mall-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
  border-color: #67c23a;
}

.mall-icon {
  font-size: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  background: #f0f9eb;
  border-radius: 50%;
}

.mall-info h4 {
  margin: 0;
  font-size: 15px;
  color: #303133;
}

.mall-cost {
  color: #e6a23c;
  font-weight: bold;
  margin: 8px 0;
  font-size: 14px;
}
</style>
