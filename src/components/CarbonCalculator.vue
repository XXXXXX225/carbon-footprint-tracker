<template>
  <div class="carbon-calculator">
    <h3>{{ title }}</h3>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <slot></slot>
      <el-form-item>
        <el-button type="primary" @click="calculate">计算排放</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 计算结果 -->
    <div v-if="emission > 0" class="emission-result">
      <h4>计算结果</h4>
      <div class="result-content">
        <span class="result-label">碳排放量：</span>
        <span class="result-value">{{ emission.toFixed(2) }} kg CO₂e</span>
      </div>
      <el-button type="success" @click="$emit('save', emission)" style="margin-top: 10px;">
        保存记录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps({
  title: {
    type: String,
    default: '碳排放计算器'
  }
})

const emit = defineEmits<{
  (e: 'save', emission: number): void
}>()

const formRef = ref<FormInstance>()
const emission = ref(0)

const form = ref({})
const rules = ref<FormRules>({})

const calculate = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 计算逻辑由父组件实现
      emit('calculate')
    }
  })
}

const reset = () => {
  formRef.value?.resetFields()
  emission.value = 0
}

const setEmission = (value: number) => {
  emission.value = value
}

// 暴露方法给父组件
defineExpose({
  calculate,
  reset,
  setEmission,
  formRef
})
</script>

<style scoped>
.carbon-calculator {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.carbon-calculator h3 {
  margin-bottom: 20px;
  color: #4CAF50;
}

.emission-result {
  margin-top: 20px;
  padding: 15px;
  background-color: #e8f5e8;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
}

.result-content {
  margin: 10px 0;
}

.result-value {
  font-size: 20px;
  font-weight: bold;
  color: #4CAF50;
  margin-left: 10px;
}
</style>