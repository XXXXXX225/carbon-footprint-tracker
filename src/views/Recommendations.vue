<template>
  <el-container class="recommendations-container">
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
      <el-main class="recommendations-main">
        <h2>减排建议</h2>
        
        <!-- 减排概览 -->
        <el-card class="overview-card">
          <template #header>
            <div class="card-header">
              <span>减排概览</span>
            </div>
          </template>
          <div class="overview-content">
            <div class="overview-item">
              <span class="overview-label">当前碳排放水平：</span>
              <span class="overview-value">{{ currentEmission }} kg CO₂e/月</span>
            </div>
            <div class="overview-item">
              <span class="overview-label">目标碳排放水平：</span>
              <span class="overview-value">{{ targetEmission }} kg CO₂e/月</span>
            </div>
            <div class="overview-item">
              <span class="overview-label">需要减少：</span>
              <span class="overview-value highlight">{{ reductionNeeded }} kg CO₂e/月</span>
            </div>
            <div class="overview-item">
              <span class="overview-label">减排进度：</span>
              <el-progress 
                :percentage="reductionProgress" 
                :color="['#4CAF50', '#FFC107', '#F44336']" 
                :format="formatProgress"
                style="width: 200px; margin-left: 10px;"
              />
            </div>
          </div>
        </el-card>
        
        <!-- 个性化减排建议 -->
        <el-card class="recommendations-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>个性化减排建议</span>
              <el-button type="primary" size="small" @click="generateRecommendations">生成新建议</el-button>
            </div>
          </template>
          
          <!-- 交通建议 -->
          <div class="recommendation-section">
            <h3><el-icon><Van /></el-icon> 交通出行</h3>
            <el-divider />
            <el-collapse v-model="activeTransportRecommendations">
              <el-collapse-item v-for="(recommendation, index) in transportRecommendations" :key="index" :title="recommendation.title">
                <div class="recommendation-content">
                  <p>{{ recommendation.description }}</p>
                  <div class="recommendation-impact">
                    <span class="impact-label">预计减排：</span>
                    <span class="impact-value">{{ recommendation.impact }} kg CO₂e/月</span>
                  </div>
                  <div class="recommendation-actions">
                    <el-button type="success" size="small" @click="adoptRecommendation(recommendation)">
                      采纳建议
                    </el-button>
                    <el-button type="info" size="small" @click="viewDetails(recommendation)">
                      查看详情
                    </el-button>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
          
          <!-- 饮食建议 -->
          <div class="recommendation-section" style="margin-top: 20px;">
            <h3><el-icon><KnifeFork /></el-icon> 饮食消费</h3>
            <el-divider />
            <el-collapse v-model="activeDietRecommendations">
              <el-collapse-item v-for="(recommendation, index) in dietRecommendations" :key="index" :title="recommendation.title">
                <div class="recommendation-content">
                  <p>{{ recommendation.description }}</p>
                  <div class="recommendation-impact">
                    <span class="impact-label">预计减排：</span>
                    <span class="impact-value">{{ recommendation.impact }} kg CO₂e/月</span>
                  </div>
                  <div class="recommendation-actions">
                    <el-button type="success" size="small" @click="adoptRecommendation(recommendation)">
                      采纳建议
                    </el-button>
                    <el-button type="info" size="small" @click="viewDetails(recommendation)">
                      查看详情
                    </el-button>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
          
          <!-- 用电建议 -->
          <div class="recommendation-section" style="margin-top: 20px;">
            <h3><el-icon><Lightning /></el-icon> 能源使用</h3>
            <el-divider />
            <el-collapse v-model="activeElectricityRecommendations">
              <el-collapse-item v-for="(recommendation, index) in electricityRecommendations" :key="index" :title="recommendation.title">
                <div class="recommendation-content">
                  <p>{{ recommendation.description }}</p>
                  <div class="recommendation-impact">
                    <span class="impact-label">预计减排：</span>
                    <span class="impact-value">{{ recommendation.impact }} kg CO₂e/月</span>
                  </div>
                  <div class="recommendation-actions">
                    <el-button type="success" size="small" @click="adoptRecommendation(recommendation)">
                      采纳建议
                    </el-button>
                    <el-button type="info" size="small" @click="viewDetails(recommendation)">
                      查看详情
                    </el-button>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>
        
        <!-- 已采纳的建议 -->
        <el-card class="adopted-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>已采纳的建议</span>
            </div>
          </template>
          <el-table :data="adoptedRecommendations" style="width: 100%">
            <el-table-column prop="category" label="类别" width="100" />
            <el-table-column prop="title" label="建议标题" />
            <el-table-column prop="adoptedDate" label="采纳日期" width="120" />
            <el-table-column prop="impact" label="预计减排（kg CO₂e/月）" width="150" />
            <el-table-column prop="status" label="执行状态" width="100">
              <template #default="scope">
                <el-tag :type="getTagType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button size="small" type="primary" @click="updateStatus(scope.row)">
                  更新状态
                </el-button>
                <el-button size="small" type="danger" @click="removeRecommendation(scope.row.id)">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>
    
    <!-- 建议详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="建议详情">
      <div v-if="selectedRecommendation" class="recommendation-detail">
        <h4>{{ selectedRecommendation.title }}</h4>
        <p>{{ selectedRecommendation.description }}</p>
        <div class="detail-section">
          <h5>预计影响</h5>
          <p>减排量：{{ selectedRecommendation.impact }} kg CO₂e/月</p>
          <p>实施难度：{{ selectedRecommendation.difficulty }}</p>
          <p>成本：{{ selectedRecommendation.cost }}</p>
        </div>
        <div class="detail-section">
          <h5>实施步骤</h5>
          <ul>
            <li v-for="(step, index) in selectedRecommendation.steps" :key="index">
              {{ step }}
            </li>
          </ul>
        </div>
      </div>
    </el-dialog>
    
    <!-- 状态更新对话框 -->
    <el-dialog v-model="statusDialogVisible" title="更新执行状态">
      <el-form :model="statusForm" :rules="statusRules" ref="statusFormRef">
        <el-form-item label="执行状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择执行状态">
            <el-option label="计划中" value="计划中" />
            <el-option label="执行中" value="执行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行进度" prop="progress" v-if="statusForm.status !== '已完成'">
          <el-input-number v-model="statusForm.progress" :min="0" :max="100" :step="10" />
          <span class="unit">%</span>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="statusForm.notes" type="textarea" :rows="2" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatusUpdate">确定</el-button>
        </span>
      </template>
    </el-dialog>
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
const activeMenu = ref('/recommendations')

const user = computed(() => carbonStore.user)

// 模拟数据
const currentEmission = ref(250)
const targetEmission = ref(200)
const reductionNeeded = computed(() => currentEmission.value - targetEmission.value)
const reductionProgress = ref(35)

const activeTransportRecommendations = ref([0])
const activeDietRecommendations = ref([0])
const activeElectricityRecommendations = ref([0])

const detailDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const selectedRecommendation = ref(null)
const statusFormRef = ref<FormInstance>()

const statusForm = reactive({
  status: '计划中',
  progress: 0,
  notes: ''
})

const statusRules = reactive<FormRules>({
  status: [
    { required: true, message: '请选择执行状态', trigger: 'blur' }
  ],
  progress: [
    { required: true, message: '请输入执行进度', trigger: 'blur' }
  ]
})

// 交通减排建议
const transportRecommendations = ref([
  {
    title: '使用公共交通工具',
    description: '将私家车出行替换为公共汽车或地铁，每周至少3天',
    impact: 25,
    difficulty: '中等',
    cost: '低',
    steps: [
      '查询附近的公共交通路线',
      '购买公交卡或地铁卡',
      '调整出行时间以适应公交时刻表',
      '逐步增加公共交通使用频率'
    ]
  },
  {
    title: '骑行或步行短途出行',
    description: '对于3公里以内的短途出行，选择骑行或步行',
    impact: 15,
    difficulty: '低',
    cost: '低',
    steps: [
      '购买或租用一辆自行车',
      '规划安全的骑行路线',
      '准备必要的骑行装备',
      '开始尝试短途骑行'
    ]
  },
  {
    title: '拼车出行',
    description: '与同事或邻居拼车上班，减少单独驾车次数',
    impact: 20,
    difficulty: '中等',
    cost: '低',
    steps: [
      '寻找合适的拼车伙伴',
      '制定拼车时间表',
      '分摊油费和过路费',
      '建立拼车规则'
    ]
  }
])

// 饮食减排建议
const dietRecommendations = ref([
  {
    title: '减少肉类消费',
    description: '每周至少有2天选择素食，减少牛肉和羊肉的摄入',
    impact: 30,
    difficulty: '中等',
    cost: '低',
    steps: [
      '了解素食食谱',
      '尝试植物性蛋白质来源',
      '逐渐减少肉类分量',
      '发现喜欢的素食菜品'
    ]
  },
  {
    title: '选择当地季节性食材',
    description: '优先购买当地生产的季节性蔬菜水果，减少运输碳排放',
    impact: 10,
    difficulty: '低',
    cost: '中等',
    steps: [
      '了解当地当季食材',
      '寻找农贸市场',
      '规划季节性菜单',
      '尝试种植简单的蔬菜'
    ]
  },
  {
    title: '减少食物浪费',
    description: '合理规划饮食，存储剩余食物，减少食物浪费',
    impact: 15,
    difficulty: '低',
    cost: '低',
    steps: [
      '制定购物清单',
      '合理存储食物',
      '创意利用剩余食材',
      '堆肥厨余垃圾'
    ]
  }
])

// 用电减排建议
const electricityRecommendations = ref([
  {
    title: '更换LED灯泡',
    description: '将所有传统灯泡更换为LED节能灯泡',
    impact: 8,
    difficulty: '低',
    cost: '中等',
    steps: [
      '统计需要更换的灯泡数量',
      '购买LED灯泡',
      '逐步更换所有灯泡',
      '回收旧灯泡'
    ]
  },
  {
    title: '使用智能插座',
    description: '安装智能插座，监控和控制家电用电',
    impact: 12,
    difficulty: '中等',
    cost: '中等',
    steps: [
      '选择适合的智能插座',
      '安装并连接到手机APP',
      '设置用电计划',
      '监控用电数据'
    ]
  },
  {
    title: '优化空调使用',
    description: '夏季将空调温度设置为26℃，冬季设置为20℃',
    impact: 25,
    difficulty: '低',
    cost: '低',
    steps: [
      '调整空调温度设置',
      '定期清洁空调滤网',
      '使用窗帘减少热量进入',
      '考虑使用风扇辅助降温'
    ]
  }
])

// 已采纳的建议
const adoptedRecommendations = ref([
  {
    id: 1,
    category: '交通',
    title: '使用公共交通工具',
    adoptedDate: '2026-01-15',
    impact: 25,
    status: '执行中'
  },
  {
    id: 2,
    category: '饮食',
    title: '减少肉类消费',
    adoptedDate: '2026-01-10',
    impact: 30,
    status: '计划中'
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

const formatProgress = (percentage: number) => {
  return `${percentage}%`
}

const generateRecommendations = () => {
  // 模拟生成新建议
  alert('已生成新的个性化减排建议')
}

const adoptRecommendation = (recommendation: any) => {
  const newRecommendation = {
    id: Date.now(),
    category: getCategory(recommendation),
    title: recommendation.title,
    adoptedDate: new Date().toISOString().split('T')[0],
    impact: recommendation.impact,
    status: '计划中'
  }
  
  adoptedRecommendations.value.push(newRecommendation)
}

const viewDetails = (recommendation: any) => {
  selectedRecommendation.value = recommendation
  detailDialogVisible.value = true
}

const updateStatus = (recommendation: any) => {
  statusForm.status = recommendation.status
  statusForm.progress = 0
  statusForm.notes = ''
  statusDialogVisible.value = true
}

const submitStatusUpdate = async () => {
  if (!statusFormRef.value) return
  
  await statusFormRef.value.validate(async (valid) => {
    if (valid) {
      // 更新状态逻辑
      statusDialogVisible.value = false
    }
  })
}

const removeRecommendation = (id: number) => {
  adoptedRecommendations.value = adoptedRecommendations.value.filter(item => item.id !== id)
}

const getCategory = (recommendation: any) => {
  if (transportRecommendations.value.includes(recommendation)) {
    return '交通'
  } else if (dietRecommendations.value.includes(recommendation)) {
    return '饮食'
  } else if (electricityRecommendations.value.includes(recommendation)) {
    return '用电'
  }
  return '其他'
}

const getTagType = (status: string) => {
  switch (status) {
    case '计划中':
      return 'info'
    case '执行中':
      return 'warning'
    case '已完成':
      return 'success'
    default:
      return 'default'
  }
}
</script>

<style scoped>
.recommendations-container {
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

.recommendations-main {
  padding: 20px;
}

.overview-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.overview-item {
  display: flex;
  align-items: center;
}

.overview-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 120px;
}

.overview-value {
  font-size: 16px;
}

.overview-value.highlight {
  color: #4CAF50;
  font-weight: bold;
}

.recommendation-section {
  margin-bottom: 30px;
}

.recommendation-section h3 {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.recommendation-section h3 el-icon {
  margin-right: 8px;
}

.recommendation-content {
  padding: 10px;
}

.recommendation-impact {
  margin: 10px 0;
  font-weight: bold;
}

.impact-value {
  color: #4CAF50;
  margin-left: 5px;
}

.recommendation-actions {
  margin-top: 15px;
}

.recommendation-detail {
  padding: 10px;
}

.detail-section {
  margin-top: 20px;
}

.detail-section h5 {
  margin-bottom: 10px;
  color: #4CAF50;
}

.unit {
  margin-left: 10px;
  color: #999;
}

@media (max-width: 768px) {
  .dashboard-aside {
    display: none;
  }
  
  .el-main {
    padding: 10px;
  }
  
  .overview-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .overview-label {
    margin-bottom: 5px;
  }
}
</style>