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
        <el-menu :default-active="activeMenu" class="points-menu" @select="handleMenuSelect">
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
      <el-main class="points-main">
        <h2>减碳积分中心</h2>
        
        <!-- 积分概览卡片 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :xs="24" :sm="12" :md="8">
            <el-card class="overview-card points-overview-card">
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
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, Download, Calendar, TrendCharts, CollectionTag } from '@element-plus/icons-vue'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeMenu = ref('/points')

// 加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  loadPointsData()
})

const user = computed(() => carbonStore.user)

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
    // 这里应该调用API获取积分数据
    // 暂时使用模拟数据
    console.log('Loading points data...')
  } catch (error) {
    console.error('Failed to load points data:', error)
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
</style>
