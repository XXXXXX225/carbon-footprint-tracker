<template>
  <div class="reduction-goal">
    <h3>{{ title }}</h3>
    <el-form :model="goalForm" :rules="goalRules" ref="goalFormRef" label-width="120px">
      <el-form-item label="减排百分比" prop="reductionGoal">
        <el-slider 
          v-model="goalForm.reductionGoal" 
          :min="5" 
          :max="50" 
          :step="5" 
          :format-tooltip="formatTooltip"
        />
        <span class="slider-value">{{ goalForm.reductionGoal }}%</span>
      </el-form-item>
      <el-form-item label="目标期限" prop="deadline">
        <el-date-picker v-model="goalForm.deadline" type="date" placeholder="选择日期" />
      </el-form-item>
      <el-form-item label="目标描述" prop="description">
        <el-input v-model="goalForm.description" placeholder="请输入目标描述" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitGoal">保存目标</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 目标进度 -->
    <div v-if="currentGoal" class="goal-progress">
      <h4>当前目标</h4>
      <div class="progress-content">
        <div class="progress-item">
          <span class="progress-label">减排目标：</span>
          <span class="progress-value">{{ currentGoal.reductionGoal }}%</span>
        </div>
        <div class="progress-item">
          <span class="progress-label">目标期限：</span>
          <span class="progress-value">{{ currentGoal.deadline }}</span>
        </div>
        <div class="progress-item">
          <span class="progress-label">目标描述：</span>
          <span class="progress-value">{{ currentGoal.description }}</span>
        </div>
        <div class="progress-bar">
          <el-progress 
            :percentage="progress" 
            :color="['#4CAF50', '#FFC107', '#F44336']" 
            :format="formatProgress"
          />
        </div>
        <div class="progress-info">
          <span>距离目标还有 {{ remainingDays }} 天</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps({
  title: {
    type: String,
    default: '减排目标设置'
  },
  currentGoal: {
    type: Object,
    default: null
  }
})

const emit = defineEmits<{
  (e: 'save', goal: any): void
}>()

const goalFormRef = ref<FormInstance>()
const goalForm = ref({
  reductionGoal: 20,
  deadline: '',
  description: ''
})

const goalRules = ref<FormRules>({
  reductionGoal: [
    { required: true, message: '请输入减排百分比', trigger: 'blur' }
  ],
  deadline: [
    { required: true, message: '请选择目标期限', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入目标描述', trigger: 'blur' }
  ]
})

const progress = ref(65) // 假设进度为65%
const remainingDays = ref(45) // 假设距离目标还有45天

const formatTooltip = (value: number) => {
  return `${value}%`
}

const formatProgress = (percentage: number) => {
  return `${percentage}%`
}

const submitGoal = async () => {
  if (!goalFormRef.value) return
  
  await goalFormRef.value.validate(async (valid) => {
    if (valid) {
      emit('save', goalForm.value)
    }
  })
}

const resetForm = () => {
  goalFormRef.value?.resetFields()
}
</script>

<style scoped>
.reduction-goal {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.reduction-goal h3 {
  margin-bottom: 20px;
  color: #4CAF50;
}

.slider-value {
  margin-left: 10px;
  font-weight: bold;
  color: #4CAF50;
}

.goal-progress {
  margin-top: 30px;
  padding: 20px;
  background-color: #e8f5e8;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
}

.progress-content {
  margin-top: 10px;
}

.progress-item {
  margin-bottom: 10px;
}

.progress-label {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
  display: inline-block;
}

.progress-value {
  font-size: 14px;
}

.progress-bar {
  margin: 20px 0;
}

.progress-info {
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>