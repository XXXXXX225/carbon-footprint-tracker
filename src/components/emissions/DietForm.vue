<template>
  <div class="emission-form">

        <!-- 饮食排放计算器 -->
        <el-card class="calculator-card">
          <template #header>
            <div class="card-header">
              <span>今日食物摄入详情</span>
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
                <el-option label="自定义" value="custom" />
              </el-select>
            </el-form-item>

            <template v-if="dietForm.foodType === 'custom'">
              <el-form-item label="自定义食物名称" prop="customName">
                <el-input v-model="dietForm.customName" placeholder="例如：未知混合食物" />
              </el-form-item>
              <el-form-item label="排放因子" prop="customFactor">
                <el-input v-model.number="dietForm.customFactor" placeholder="请输入碳排放因子" />
                <span class="unit">kg CO₂e / 单位</span>
              </el-form-item>
            </template>

            <el-form-item label="具体食物" prop="specificFood" v-if="dietForm.foodType && dietForm.foodType !== 'custom'">
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
              <el-date-picker v-model="dietForm.date" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD" placeholder="选择日期" />
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
            <el-table-column prop="foodType" label="食物类别" width="140">
              <template #default="scope">
                {{ getFoodTypeName(scope.row.foodType, scope.row) }}
              </template>
            </el-table-column>
            <el-table-column prop="specificFood" label="具体食物" width="150">
              <template #default="scope">
                {{ scope.row.foodType === 'custom' ? '-' : getSpecificFoodName(scope.row.foodType, scope.row.specificFood) }}
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
      
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, reactive } from 'vue'
import { useCarbonStore } from '../../store'
import { carbonApi } from '../../api'
import { House, Van, KnifeFork, Lightning, DataLine, Star, ArrowDown, CollectionTag, TrendCharts } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const carbonStore = useCarbonStore()
const dietFormRef = ref<FormInstance>()
const emissionResult = ref(0)

const dietForm = reactive({
  foodType: '',
  specificFood: '',
  amount: 0,
  cookingMethod: 'boil',
  date: new Date().toISOString().split('T')[0],
  description: '',
  customName: '',
  customFactor: null as number | null
})

const dietRules = reactive<FormRules>({
  foodType: [{ required: true, message: '请选择食物类别', trigger: 'change' }],
  specificFood: [{ required: false, message: '请选择具体食物', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入食用量', trigger: 'blur' },
    { type: 'number', message: '必须为数字' }
  ],
  cookingMethod: [{ required: true, message: '请选择烹饪方式', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  customName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  customFactor: [{ required: true, message: '请输入排放因子', trigger: 'blur' }]
})

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

interface DietRecordItem {
  id: string
  date: string
  foodType: string
  specificFood: string
  amount: number
  emission: number
  description: string
}

// remove duplicate dietRules here

const dietRecords = ref<DietRecordItem[]>([])







const getSpecificFoods = (foodType: string) => {
  return specificFoods[foodType as keyof typeof specificFoods] || []
}

const getUnit = (foodType: string) => {
  if (foodType === 'beverages') {
    return '升'
  }
  return '千克'
}

const mapDietFoodTypeToApiCode = (foodType: string, specificFood: string) => {
  if (foodType === 'meat') {
    if (specificFood === 'beef' || specificFood === 'lamb') return 1
    if (specificFood === 'pork') return 2
    if (specificFood === 'chicken') return 3
  }

  return 4
}

const mapDietFoodTypeFromApiCode = (foodType: number) => {
  if (foodType === 1 || foodType === 2 || foodType === 3) {
    return 'meat'
  }
  return 'vegetables'
}

const loadDietRecords = async () => {
  try {
    const apiRecords = await carbonApi.getDietRecords()
    const mapped = apiRecords.map(record => ({
      id: String(record.id),
      date: record.emissionDate,
      foodType: mapDietFoodTypeFromApiCode(record.foodType),
      specificFood: record.specificFood,
      amount: record.amount,
      emission: record.emissionAmount,
      description: record.description || ''
    }))
    
    // 加载自定义数据
    const localRecords = JSON.parse(localStorage.getItem('mock_diet_emissions') || '[]')
    
    dietRecords.value = [...mapped, ...localRecords].sort((left, right) => new Date(right.date).getTime() - new Date(left.date).getTime())
  } catch (error) {
    console.error('加载饮食历史记录失败:', error)
    ElMessage.warning('饮食历史记录加载失败，已显示当前页面数据')
    
    const localRecords = JSON.parse(localStorage.getItem('mock_diet_emissions') || '[]')
    dietRecords.value = localRecords.sort((left: any, right: any) => new Date(right.date).getTime() - new Date(left.date).getTime())
  }
}

const calculateEmission = async () => {
  if (!dietFormRef.value) return
  
  await dietFormRef.value.validate(async (valid) => {
    if (valid) {
      if (dietForm.foodType === 'custom' && dietForm.customFactor !== null) {
        emissionResult.value = dietForm.amount * dietForm.customFactor
        return
      }

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

const saveRecord = async () => {
  try {
    if (dietForm.foodType === 'custom') {
      const localRecords = JSON.parse(localStorage.getItem('mock_diet_emissions') || '[]')
      localRecords.push({
        id: 'local_' + Date.now(),
        date: dietForm.date,
        foodType: 'custom',
        customName: dietForm.customName,
        specificFood: '自定义',
        amount: dietForm.amount,
        emission: Number(emissionResult.value.toFixed(2)),
        description: dietForm.description || ''
      })
      localStorage.setItem('mock_diet_emissions', JSON.stringify(localRecords))
      await loadDietRecords()
      ElMessage.success('自定义饮食记录已保存至本地')
      resetForm()
      return
    }

    await carbonApi.addDietRecord({
      foodType: mapDietFoodTypeToApiCode(dietForm.foodType, dietForm.specificFood),
      specificFood: dietForm.specificFood,
      amount: dietForm.amount,
      cookingMethod: dietForm.cookingMethod,
      emissionDate: dietForm.date,
      description: dietForm.description
    })

    // 全量刷新真实的后端数据，不再向本地 mock array 里 push 数据
    await loadDietRecords()
    await carbonStore.fetchAllRecords()
    
    ElMessage.success('饮食记录已保存')
    resetForm()
  } catch (error) {
    console.error('保存饮食记录失败:', error)
    ElMessage.error('保存饮食记录失败，请稍后重试')
  }
}

const deleteRecord = async (id: string) => {
  try {
    if (id.startsWith('local_')) {
      const localRecords = JSON.parse(localStorage.getItem('mock_diet_emissions') || '[]')
      const newRecords = localRecords.filter((r: any) => r.id !== id)
      localStorage.setItem('mock_diet_emissions', JSON.stringify(newRecords))
      await loadDietRecords()
      ElMessage.success('自定义记录已删除')
      return
    }

    await carbonApi.deleteDietRecord(Number(id))
    await loadDietRecords()
    ElMessage.success('饮食记录已删除')
  } catch (error) {
    console.error('删除饮食记录失败:', error)
    ElMessage.error('删除饮食记录失败，请稍后重试')
  }
}

const clearHistory = async () => {
  try {
    await carbonApi.clearDietRecords()
    localStorage.removeItem('mock_diet_emissions')
    await loadDietRecords()
    ElMessage.success('饮食记录已清空')
  } catch (error) {
    console.error('清空饮食记录失败:', error)
    ElMessage.error('清空饮食记录失败，请稍后重试')
  }
}

const getFoodTypeName = (type: string, row?: any) => {
  if (type === 'custom') return `自定义 (${row?.customName || '未知'})`
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

onMounted(() => {
  loadDietRecords()
})
</script>

<style scoped>
.emission-form { width: 100%; border: none; padding: 10px; }
.calculator-card { margin-bottom: 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
.card-header { font-weight: bold; font-size: 16px; }
.unit { margin-left: 10px; color: #999; }
.emission-result { margin-top: 30px; padding: 20px; background-color: #f5f5f5; border-radius: 8px; border-left: 4px solid #4CAF50; }
.result-value { font-size: 24px; font-weight: bold; color: #4CAF50; }
.history-card { margin-top: 20px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
</style>