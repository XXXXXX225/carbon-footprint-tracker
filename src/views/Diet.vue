<template>
  <el-container class="diet-container">
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
      <el-main class="diet-main">
        <h2>饮食排放计算</h2>
        
        <!-- 饮食排放计算器 -->
        <el-card class="calculator-card">
          <template #header>
            <div class="card-header">
              <span>饮食排放计算器</span>
            </div>
          </template>
          <el-form :model="dietForm" :rules="dietRules" ref="dietFormRef" label-width="120px">
            <el-form-item label="食物类别" prop="foodType">
              <el-select v-model="dietForm.foodType" placeholder="请选择食物类别">
                <el-option label="谷物类" value="grains" />
                <el-option label="蔬菜类" value="vegetables" />
                <el-option label="水果类" value="fruits" />
                <el-option label="豆类" value="legumes" />
                <el-option label="坚果类" value="nuts" />
                <el-option label="乳制品" value="dairy" />
                <el-option label="蛋类" value="eggs" />
                <el-option label="肉类" value="meat">
                  <template #default>
                    <span>肉类</span>
                  </template>
                </el-option>
                <el-option label="鱼类" value="fish" />
                <el-option label="饮料" value="beverages" />
              </el-select>
            </el-form-item>
            <el-form-item label="具体食物" prop="specificFood" v-if="dietForm.foodType">
              <el-select v-model="dietForm.specificFood" placeholder="请选择具体食物">
                <el-option 
                  v-for="food in getSpecificFoods(dietForm.foodType)" 
                  :key="food.value" 
                  :label="food.label" 
                  :value="food.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="食用量" prop="amount">
              <el-input v-model.number="dietForm.amount" placeholder="请输入食用量" />
              <span class="unit">{{ getUnit(dietForm.foodType) }}</span>
            </el-form-item>
            <el-form-item label="烹饪方式" prop="cookingMethod" v-if="dietForm.foodType">
              <el-select v-model="dietForm.cookingMethod" placeholder="请选择烹饪方式">
                <el-option label="生食" value="raw" />
                <el-option label="煮" value="boil" />
                <el-option label="蒸" value="steam" />
                <el-option label="炒" value="fry" />
                <el-option label="烤" value="bake" />
                <el-option label="煎" value="pan" />
              </el-select>
            </el-form-item>
            <el-form-item label="食用日期" prop="date">
              <el-date-picker v-model="dietForm.date" type="date" placeholder="选择日期" />
            </el-form-item>
            <el-form-item label="备注" prop="description">
              <el-input v-model="dietForm.description" placeholder="请输入备注信息" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="calculateEmission">计算排放</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
          
          <!-- 计算结果 -->
          <div v-if="emissionResult > 0" class="emission-result">
            <h3>计算结果</h3>
            <div class="result-content">
              <div class="result-item">
                <span class="result-label">碳排放量：</span>
                <span class="result-value">{{ emissionResult.toFixed(2) }} kg CO₂e</span>
              </div>
              <el-button type="success" @click="saveRecord" style="margin-top: 20px;">保存记录</el-button>
            </div>
          </div>
        </el-card>
        
        <!-- 历史记录 -->
        <el-card class="history-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>饮食排放历史记录</span>
              <el-button type="danger" size="small" @click="clearHistory" v-if="dietRecords.length > 0">清空记录</el-button>
            </div>
          </template>
          <el-table :data="dietRecords" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="foodType" label="食物类别" width="120">
              <template #default="scope">
                {{ getFoodTypeName(scope.row.foodType) }}
              </template>
            </el-table-column>
            <el-table-column prop="specificFood" label="具体食物" width="150">
              <template #default="scope">
                {{ getSpecificFoodName(scope.row.foodType, scope.row.specificFood) }}
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="食用量" width="100" />
            <el-table-column prop="emission" label="排放量（kg CO₂e）" width="150" />
            <el-table-column prop="description" label="备注" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteRecord(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination 
            v-if="dietRecords.length > 0" 
            layout="prev, pager, next" 
            :total="dietRecords.length" 
            :page-size="10" 
            style="margin-top: 20px; text-align: right;"
          />
        </el-card>
      </el-main>
    </el-container>
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
const activeMenu = ref('/diet')
const dietFormRef = ref<FormInstance>()
const emissionResult = ref(0)

const user = computed(() => carbonStore.user)

// 食物排放因子（kg CO₂e/千克）
const foodEmissionFactors = {
  grains: {
    rice: 2.7,
    wheat: 2.5,
    corn: 2.0
  },
  vegetables: {
    tomatoes: 2.0,
    potatoes: 1.1,
    lettuce: 2.5,
    carrots: 1.1
  },
  fruits: {
    apples: 0.7,
    bananas: 0.8,
    oranges: 0.6,
    grapes: 2.0
  },
  legumes: {
    beans: 2.0,
    lentils: 1.5,
    chickpeas: 2.0
  },
  nuts: {
    almonds: 3.5,
    walnuts: 2.5,
    peanuts: 2.0
  },
  dairy: {
    milk: 1.0,
    cheese: 13.5,
    yogurt: 2.0
  },
  eggs: {
    eggs: 2.0
  },
  meat: {
    beef: 27.0,
    pork: 12.1,
    chicken: 6.9,
    lamb: 39.2
  },
  fish: {
    salmon: 11.0,
    tuna: 6.0,
    cod: 5.0
  },
  beverages: {
    coffee: 0.5,
    tea: 0.2,
    soft_drinks: 0.5
  }
}

// 具体食物列表
const specificFoods = {
  grains: [
    { label: '大米', value: 'rice' },
    { label: '小麦', value: 'wheat' },
    { label: '玉米', value: 'corn' }
  ],
  vegetables: [
    { label: '西红柿', value: 'tomatoes' },
    { label: '土豆', value: 'potatoes' },
    { label: '生菜', value: 'lettuce' },
    { label: '胡萝卜', value: 'carrots' }
  ],
  fruits: [
    { label: '苹果', value: 'apples' },
    { label: '香蕉', value: 'bananas' },
    { label: '橙子', value: 'oranges' },
    { label: '葡萄', value: 'grapes' }
  ],
  legumes: [
    { label: '豆类', value: 'beans' },
    { label: '扁豆', value: 'lentils' },
    { label: '鹰嘴豆', value: 'chickpeas' }
  ],
  nuts: [
    { label: '杏仁', value: 'almonds' },
    { label: '核桃', value: 'walnuts' },
    { label: '花生', value: 'peanuts' }
  ],
  dairy: [
    { label: '牛奶', value: 'milk' },
    { label: '奶酪', value: 'cheese' },
    { label: '酸奶', value: 'yogurt' }
  ],
  eggs: [
    { label: '鸡蛋', value: 'eggs' }
  ],
  meat: [
    { label: '牛肉', value: 'beef' },
    { label: '猪肉', value: 'pork' },
    { label: '鸡肉', value: 'chicken' },
    { label: '羊肉', value: 'lamb' }
  ],
  fish: [
    { label: '三文鱼', value: 'salmon' },
    { label: '金枪鱼', value: 'tuna' },
    { label: '鳕鱼', value: 'cod' }
  ],
  beverages: [
    { label: '咖啡', value: 'coffee' },
    { label: '茶', value: 'tea' },
    { label: '软饮料', value: 'soft_drinks' }
  ]
}

const dietForm = reactive({
  foodType: '',
  specificFood: '',
  amount: 0,
  cookingMethod: 'raw',
  date: new Date().toISOString().split('T')[0],
  description: ''
})

const dietRules = reactive<FormRules>({
  foodType: [
    { required: true, message: '请选择食物类别', trigger: 'blur' }
  ],
  specificFood: [
    { required: true, message: '请选择具体食物', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入食用量', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '食用量必须大于0', trigger: 'blur' }
  ],
  cookingMethod: [
    { required: true, message: '请选择烹饪方式', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'blur' }
  ]
})

const dietRecords = ref([
  {
    id: '1',
    date: '2026-01-30',
    foodType: 'meat',
    specificFood: 'chicken',
    amount: 0.2,
    emission: 1.38,
    description: '晚餐'
  },
  {
    id: '2',
    date: '2026-01-30',
    foodType: 'vegetables',
    specificFood: 'tomatoes',
    amount: 0.3,
    emission: 0.6,
    description: '沙拉'
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

const getSpecificFoods = (foodType: string) => {
  return specificFoods[foodType as keyof typeof specificFoods] || []
}

const getUnit = (foodType: string) => {
  if (foodType === 'beverages') {
    return '升'
  }
  return '千克'
}

const calculateEmission = async () => {
  if (!dietFormRef.value) return
  
  await dietFormRef.value.validate(async (valid) => {
    if (valid) {
      const foodType = dietForm.foodType as keyof typeof foodEmissionFactors
      const specificFood = dietForm.specificFood as keyof typeof foodEmissionFactors[typeof foodType]
      
      if (foodEmissionFactors[foodType] && foodEmissionFactors[foodType][specificFood]) {
        let emissionFactor = foodEmissionFactors[foodType][specificFood]
        
        // 烹饪方式对排放的影响
        const cookingFactor = {
          raw: 1.0,
          boil: 1.1,
          steam: 1.1,
          fry: 1.3,
          bake: 1.2,
          pan: 1.2
        }
        
        emissionFactor *= cookingFactor[dietForm.cookingMethod as keyof typeof cookingFactor]
        
        emissionResult.value = dietForm.amount * emissionFactor
      }
    }
  })
}

const resetForm = () => {
  dietFormRef.value?.resetFields()
  emissionResult.value = 0
}

const saveRecord = () => {
  const record = {
    type: 'diet' as const,
    value: emissionResult.value,
    date: dietForm.date,
    description: dietForm.description
  }
  
  carbonStore.addRecord(record)
  
  // 添加到本地记录
  dietRecords.value.unshift({
    id: Date.now().toString(),
    date: dietForm.date,
    foodType: dietForm.foodType,
    specificFood: dietForm.specificFood,
    amount: dietForm.amount,
    emission: emissionResult.value,
    description: dietForm.description
  })
  
  // 重置表单
  resetForm()
}

const deleteRecord = (id: string) => {
  dietRecords.value = dietRecords.value.filter(record => record.id !== id)
}

const clearHistory = () => {
  dietRecords.value = []
}

const getFoodTypeName = (type: string) => {
  const typeMap = {
    grains: '谷物类',
    vegetables: '蔬菜类',
    fruits: '水果类',
    legumes: '豆类',
    nuts: '坚果类',
    dairy: '乳制品',
    eggs: '蛋类',
    meat: '肉类',
    fish: '鱼类',
    beverages: '饮料'
  }
  return typeMap[type as keyof typeof typeMap] || type
}

const getSpecificFoodName = (foodType: string, specificFood: string) => {
  const foods = specificFoods[foodType as keyof typeof specificFoods] || []
  const food = foods.find(f => f.value === specificFood)
  return food?.label || specificFood
}
</script>

<style scoped>
.diet-container {
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

.diet-main {
  padding: 20px;
}

.calculator-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.unit {
  margin-left: 10px;
  color: #999;
}

.emission-result {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
}

.result-content {
  margin-top: 10px;
}

.result-item {
  margin-bottom: 10px;
}

.result-value {
  font-size: 24px;
  font-weight: bold;
  color: #4CAF50;
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
  
  .el-form-item {
    margin-bottom: 15px;
  }
}
</style>