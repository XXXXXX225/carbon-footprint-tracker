<template>
  <el-container class="emissions-container">
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
      <el-main class="emissions-main">
        <div class="page-title-box">
          <div class="title-header">
            <div>
              <h2>碳排放计算与记录</h2>
              <p class="subtitle">记录您的日常碳排放，共同为地球减负。</p>
            </div>
            <div class="today-progress" v-if="user.role !== 'admin'">
              <div class="progress-title">今日碳限额 ({{ dailyQuota }}kg) <span>{{ todayEmissions.toFixed(1) }} kg</span></div>
              <el-progress 
                :percentage="todayProgress" 
                :status="progressStatus"
                :stroke-width="10"
                style="width: 250px"
                :show-text="false"
              />
              <div class="progress-hint" :class="todayProgress >= 100 ? 'danger-text' : ''">
                {{ progressHint }}
              </div>
            </div>
          </div>
        </div>

        <el-card class="quick-checkin-card" shadow="never" v-if="user.role !== 'admin'">
          <div class="quick-header" style="display: flex; justify-content: space-between; align-items: center;">
            <div><el-icon><Location /></el-icon> <span>常用情景一键打卡</span></div>
            <el-button type="primary" size="small" round @click="showCustomScenarioDialog = true"><el-icon><Plus /></el-icon> 添加自定义</el-button>
          </div>
          <div class="quick-actions">
            <el-button class="eco-btn eco-btn-transport" round @click="quickAdd('transport', 1.2, '坐地铁通勤 (中等距离)')">🚇 地铁通勤</el-button>
            <el-button class="eco-btn eco-btn-diet" round @click="quickAdd('diet', 0.5, '纯素食一餐')">🥗 素食一餐</el-button>
            <el-button class="eco-btn eco-btn-electric" round @click="quickAdd('electricity', 0.3, '随手关灯/拔掉插头')">💡 节电小事</el-button>
            <el-button class="eco-btn eco-btn-bike" round @click="quickAdd('transport', 0.0, '骑行/步行 3公里')">🚲 绿色出行</el-button>
            <el-button 
              v-for="(scenario, index) in customScenarios" 
              :key="index"
              class="eco-btn" 
              :class="scenario.category === 'electricity' ? 'eco-btn-electric' : `eco-btn-${scenario.category}`"
              round 
              @click="quickAdd(scenario.category, scenario.value, scenario.name)">
              {{ scenario.category === 'transport' ? '🚌' : scenario.category === 'diet' ? '🥗' : scenario.category === 'electricity' ? '💡' : '💧' }} {{ scenario.name }}
            </el-button>
          </div>
        </el-card>

        <el-dialog v-model="showCustomScenarioDialog" title="添加自定义快捷打卡" width="400px">
          <el-form :model="newScenario" label-width="100px">
            <el-form-item label="情景名称">
              <el-input v-model="newScenario.name" placeholder="例如：喝了一杯奶茶" />
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="newScenario.category" style="width: 100%" @change="newScenario.subCategory = ''; newScenario.amount = 0">
                <el-option label="交通出行" value="transport" />
                <el-option label="饮食记录" value="diet" />
                <el-option label="家庭用电" value="electricity" />
                <el-option label="水资源" value="water" />
              </el-select>
            </el-form-item>
            <el-form-item label="项目" v-if="newScenario.category">
              <el-select v-model="newScenario.subCategory" style="width: 100%">
                <template v-if="newScenario.category === 'transport'">
                  <el-option label="公交车" value="bus" />
                  <el-option label="地铁" value="subway" />
                  <el-option label="出租车" value="taxi" />
                  <el-option label="网约车" value="rideshare" />
                  <el-option label="私家车" value="private_car" />
                  <el-option label="自行车" value="bike" />
                  <el-option label="步行" value="walk" />
                </template>
                <template v-else-if="newScenario.category === 'diet'">
                  <el-option label="牛肉" value="beef" />
                  <el-option label="猪肉" value="pork" />
                  <el-option label="鸡肉" value="chicken" />
                  <el-option label="鱼肉" value="fish" />
                  <el-option label="鸡蛋" value="eggs" />
                  <el-option label="牛奶" value="milk" />
                  <el-option label="奶酪" value="cheese" />
                  <el-option label="米饭" value="rice" />
                  <el-option label="面食" value="wheat" />
                  <el-option label="蔬菜" value="vegetables" />
                  <el-option label="水果" value="fruits" />
                  <el-option label="纯素" value="pure_veg" />
                </template>
                <template v-else-if="newScenario.category === 'electricity'">
                  <el-option label="台式电脑" value="desktop" />
                  <el-option label="笔记本电脑" value="laptop" />
                  <el-option label="显示器" value="monitor" />
                  <el-option label="打印机/复印机" value="printer" />
                  <el-option label="电视" value="tv" />
                  <el-option label="空调" value="ac" />
                  <el-option label="电暖器" value="heater" />
                  <el-option label="洗衣机" value="washing_machine" />
                  <el-option label="冰箱" value="fridge" />
                  <el-option label="LED灯" value="light_led" />
                  <el-option label="白炽灯" value="light_incandescent" />
                </template>
                <template v-else-if="newScenario.category === 'water'">
                  <el-option label="自来水" value="tap" />
                  <el-option label="桶装水" value="bottled" />
                  <el-option label="热水" value="hot" />
                </template>
              </el-select>
            </el-form-item>
            <el-form-item :label="amountLabel" v-if="newScenario.category && newScenario.subCategory">
              <el-input-number v-model="newScenario.amount" :min="0" :step="0.1" style="width: 100%" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="dialog-footer" style="display: flex; justify-content: center; gap: 15px; margin-top: 10px;">
              <el-button round @click="showCustomScenarioDialog = false" style="min-width: 120px;">取消</el-button>
              <el-button type="primary" round style="min-width: 120px; background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); border: none; font-weight: bold; color: #111; display:flex; align-items:center; justify-content:center; gap:5px;" @click="saveCustomScenario">
                <el-icon><Check /></el-icon> 确认保存
              </el-button>
            </div>
          </template>
        </el-dialog>

        <el-alert
          :title="currentTip.title"
          :description="currentTip.desc"
          :type="currentTip.type as any"
          show-icon
          class="dynamic-tip"
          :closable="false"
          v-if="user.role !== 'admin'"
        />

        <div class="emissions-grid">
          <el-tabs type="border-card" class="emissions-tabs" v-model="activeTab">
            <el-tab-pane name="transport">
              <template #label>
                <div class="tab-label"><el-icon><Van /></el-icon> 交通出行</div>
              </template>
              <div class="tab-content-wrapper">
                <TransportForm />
              </div>
            </el-tab-pane>
            <el-tab-pane name="diet">
              <template #label>
                <div class="tab-label"><el-icon><KnifeFork /></el-icon> 饮食记录</div>
              </template>
              <div class="tab-content-wrapper">
                <DietForm />
              </div>
            </el-tab-pane>
            <el-tab-pane name="electricity">
              <template #label>
                <div class="tab-label"><el-icon><Lightning /></el-icon> 家庭用电</div>
              </template>
              <div class="tab-content-wrapper">
                <ElectricityForm />
              </div>
            </el-tab-pane>
            <el-tab-pane name="water">
              <template #label>
                <div class="tab-label"><el-icon><Odometer /></el-icon> 水资源</div>
              </template>
              <div class="tab-content-wrapper">
                <WaterForm />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import RoleSidebar from '../components/RoleSidebar.vue'
import { Van, KnifeFork, Lightning, ArrowDown, Location, Odometer, Plus, Check } from '@element-plus/icons-vue'  
import { ElMessage } from 'element-plus'
import { carbonApi } from '../api'

import TransportForm from '../components/emissions/TransportForm.vue'
import DietForm from '../components/emissions/DietForm.vue'
import ElectricityForm from '../components/emissions/ElectricityForm.vue'       
import WaterForm from '../components/emissions/WaterForm.vue'

const router = useRouter()
const carbonStore = useCarbonStore()
const activeTab = ref('transport')

const user = computed(() => carbonStore.user)

// --- 方案A：今日碳限额进度 ---
const dailyQuota = 15 // 每日提示限额 15kg
const todayEmissions = computed(() => {
  const today = new Date().toISOString().split('T')[0]
  return carbonStore.records
    .filter(r => r.date.startsWith(today))
    .reduce((sum, r) => sum + r.value, 0)
})
const todayProgress = computed(() => {
  return Math.min((todayEmissions.value / dailyQuota) * 100, 100)
})
const progressStatus = computed(() => {
  if (todayProgress.value < 60) return 'success'
  if (todayProgress.value < 100) return 'warning'
  return 'exception'
})
const progressHint = computed(() => {
  if (todayProgress.value >= 100) return '⚠️ 哎呀，今天超标啦，请注意减排！'
  if (todayProgress.value >= 60) return '注意控制接下来的排放哦～'
  return '低碳达人，继续保持！'
})

// --- 方案B：快捷打卡 ---
const customScenarios = ref<{category: string, name: string, value: number}[]>([])
const showCustomScenarioDialog = ref(false)
const newScenario = ref({
  category: 'transport',
  subCategory: '',
  amount: 0,
  name: ''
})

const amountLabel = computed(() => {
  switch (newScenario.value.category) {
    case 'transport': return '距离(公里)'
    case 'diet': return '重量(kg)'
    case 'electricity': return '用电量(度)'
    case 'water': return '水量(吨)'
    default: return '数量'
  }
})

const emissionFactors: Record<string, Record<string, number>> = {
  transport: { bus: 0.068, subway: 0.046, taxi: 0.23, rideshare: 0.15, private_car: 0.27, bike: 0, walk: 0 },
  diet: { beef: 27.0, pork: 12.1, chicken: 6.9, fish: 6.1, eggs: 4.8, milk: 3.2, cheese: 21.2, rice: 4.0, wheat: 1.5, vegetables: 2.0, fruits: 0.9, pure_veg: 1.5 },
  electricity: { desktop: 0.15, laptop: 0.05, monitor: 0.03, printer: 0.02, tv: 0.1, ac: 0.8, heater: 1.5, washing_machine: 0.5, fridge: 0.05, light_led: 0.01, light_incandescent: 0.06 },
  water: { tap: 0.25, bottled: 2.5, hot: 12.5 }
}

onMounted(() => {
  const stored = localStorage.getItem('custom_scenarios')
  if (stored) {
    try {
      customScenarios.value = JSON.parse(stored)
    } catch (e) {}
  }
})

const saveCustomScenario = () => {
  if (!newScenario.value.name) {
    ElMessage.warning('请输入情景名称')
    return
  }
  if (!newScenario.value.subCategory) {
    ElMessage.warning('请选择项目')
    return
  }
  if (newScenario.value.amount <= 0 && newScenario.value.subCategory !== 'bike' && newScenario.value.subCategory !== 'walk') {
    ElMessage.warning('请输入有效数量')
    return
  }
  
  const factor = emissionFactors[newScenario.value.category]?.[newScenario.value.subCategory] || 0
  const value = factor * newScenario.value.amount

  const scenarioToSave = {
    category: newScenario.value.category,
    name: newScenario.value.name,
    value: parseFloat(value.toFixed(2))
  }

  customScenarios.value.push(scenarioToSave)
  localStorage.setItem('custom_scenarios', JSON.stringify(customScenarios.value))
  showCustomScenarioDialog.value = false
  ElMessage.success('已添加自定义场景')
}

const quickAdd = async (type: string, value: number, desc: string) => {
  try {
    const dStr = new Date().toISOString()
    // 模拟将自定义固定排放量换算为接口参数
    if (type === 'transport') {
      let tCode = 0 // 默认步行
      if (desc.includes('地铁')) tCode = 3
      // 这里传距离时稍微模拟一下计算结果反推，实际上后端可能有固定因子，这里只是示例
      await carbonApi.addTransportRecord({ transportType: tCode, distance: value > 0 ? value * 5 : 10, emissionDate: dStr, description: `[快捷打卡] ${desc}` })
    } else if (type === 'diet') {
      await carbonApi.addDietRecord({ foodType: desc.includes('素食') ? 2 : 0, specificFood: '快捷打卡', amount: value > 0 ? value : 1, cookingMethod: '标准', emissionDate: dStr, description: `[快捷打卡] ${desc}` })
    } else if (type === 'electricity') {
      await carbonApi.addElectricityRecord({ deviceType: '综合用电', power: 100, usageTime: value > 0 ? value * 10 : 1, usageDays: 1, emissionDate: dStr, description: `[快捷打卡] ${desc}` })
    } else if (type === 'water') {
      const records = JSON.parse(localStorage.getItem('mock_water_emissions') || '[]')
      records.unshift({
        id: Date.now(),
        waterType: '自定义',
        amount: value,
        unit: '自定义',
        date: dStr.split('T')[0],
        description: `[快捷打卡] ${desc}`,
        emission: value
      })
      localStorage.setItem('mock_water_emissions', JSON.stringify(records))
    }
    
    await carbonStore.fetchAllRecords()
    activeTab.value = type

    ElMessage({
      message: `打卡成功！已快捷记录：${desc} (+${value}kg)`,
      type: 'success',
      plain: true
    })
  } catch (err) {
    ElMessage.error(`打卡失败`)
  }
}

// --- 方案C：动态小贴士 ---
const currentTip = computed(() => {
  switch (activeTab.value) {
    case 'transport':
      return { title: '绿色出行贴士 🚲', desc: '尽量选择公共交通哦！轮胎气压不足会增加汽车5%的碳排放。', type: 'info' }
    case 'diet':
      return { title: '低碳饮食贴士 🥗', desc: '生产1kg牛肉的碳排放是1kg猪肉的4倍。多吃素食，为地球减负！', type: 'success' }
    case 'electricity':
      return { title: '家庭节能贴士 💡', desc: '待机状态下的电器依然会消耗电量，拔掉不用的插头也能减碳！', type: 'warning' }
    case 'water':
      return { title: '节水贴士 💧', desc: '洗澡时间缩短1分钟，就能节约不少水资源和加热消耗的能源哦！', type: 'info' }
    default:
      return { title: '小贴士', desc: '多采取低碳行动，保护我们的地球。', type: 'info' }
  }
})

const handleLogout = () => {
  router.push('/login')
}

const navigateToProfile = () => {
  router.push('/profile')
}
</script>

<style scoped>
.emissions-container {
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
  border-right: 1px solid #e6e6e6;
}

.emissions-main {
  padding: 24px 30px;
  background-color: #f7f9fa;
}

.page-title-box {
  margin-bottom: 24px;
}

.title-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.page-title-box h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.today-progress {
  text-align: right;
  background: white;
  padding: 10px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.progress-title {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-title span {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.progress-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}

.danger-text {
  color: #f56c6c;
  font-weight: bold;
}

.quick-checkin-card {
  margin-bottom: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
  border: 1px solid #dcdfe6;
}

.quick-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #4CAF50;
  margin-bottom: 12px;
  font-size: 15px;
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.eco-btn {
  background-color: rgba(255, 255, 255, 0.9) !important;
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02) !important;
  font-weight: 600 !important;
  transition: all 0.3s ease !important;
}

.eco-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1) !important;
}

/* Sub-themes for Eco Buttons */
.eco-btn-transport { color: #009688 !important; border: 1px solid #80cbc4 !important; }
.eco-btn-transport:hover { background-color: #009688 !important; color: #fff !important; border-color: #009688 !important; }

.eco-btn-diet { color: #4CAF50 !important; border: 1px solid #a5d6a7 !important; }
.eco-btn-diet:hover { background-color: #4CAF50 !important; color: #fff !important; border-color: #4CAF50 !important; }

.eco-btn-electric { color: #e6a23c !important; border: 1px solid #f3d19e !important; }
.eco-btn-electric:hover { background-color: #e6a23c !important; color: #fff !important; border-color: #e6a23c !important; }

.eco-btn-bike { color: #67c23a !important; border: 1px solid #b3e19d !important; }
.eco-btn-bike:hover { background-color: #67c23a !important; color: #fff !important; border-color: #67c23a !important; }

.eco-btn-water { color: #409EFF !important; border: 1px solid #c6e2ff !important; }
.eco-btn-water:hover { background-color: #409EFF !important; color: #fff !important; border-color: #409EFF !important; }

.dynamic-tip {
  margin-bottom: 20px;
  border-radius: 8px;
}

.emissions-grid {
  margin-top: 5px;
}

.emissions-tabs {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0,0,0,0.04);
  border: none;
}

.emissions-tabs :deep(.el-tabs__header) {
  background-color: #f7f9fa;
  border-bottom: 1px solid #ebeef5;
}

.emissions-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  color: #606266;
  height: 54px;
  line-height: 54px;
  padding: 0 30px;
  transition: all 0.3s;
}

.emissions-tabs :deep(.el-tabs__item.is-active) {
  color: #4CAF50;
  background-color: #fff;
  border-right-color: #ebeef5;
  border-left-color: #ebeef5;
  font-weight: bold;
}

.emissions-tabs :deep(.el-tabs__item.is-active::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #4CAF50;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-label .el-icon {
  font-size: 20px;
}

.tab-content-wrapper {
  padding: 10px;
}

/* 统一内部表单和表格样式 */
.tab-content-wrapper :deep(.el-card) {
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.02);
  border-radius: 12px;
  margin-bottom: 20px;
}

.tab-content-wrapper :deep(.el-card__header) {
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #f0f2f5;
  padding: 18px 24px;
  background: white;
}

.tab-content-wrapper :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.tab-content-wrapper :deep(.el-input__wrapper),
.tab-content-wrapper :deep(.el-select) {
  border-radius: 6px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

.tab-content-wrapper :deep(.el-button) {
  border-radius: 6px;
}

@media (max-width: 768px) {
  .dashboard-aside {
    display: none;
  }
  .emissions-main {
    padding: 15px;
  }
  .emissions-tabs :deep(.el-tabs__item) {
    padding: 0 15px;
    font-size: 14px;
  }
}
</style>
