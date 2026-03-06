<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCarbonStore } from './store'

const carbonStore = useCarbonStore()

// 应用启动时加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
})
</script>

<style>
:root {
  --primary-color: #4CAF50;
  --secondary-color: #81C784;
  --accent-color: #2E7D32;
  --background-color: #F5F5F5;
  --text-color: #333333;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Arial', sans-serif;
  background-color: var(--background-color);
  color: var(--text-color);
}

.el-container {
  min-height: 100vh;
}

.el-header {
  background-color: var(--primary-color);
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-aside {
  background-color: #FFFFFF;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
}

.el-main {
  background-color: var(--background-color);
  padding: 20px;
}

.card {
  background-color: #FFFFFF;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.btn-primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.btn-primary:hover {
  background-color: var(--accent-color);
  border-color: var(--accent-color);
}

.chart-container {
  width: 100%;
  height: 400px;
}

@media (max-width: 768px) {
  .el-main {
    padding: 10px;
  }
  
  .card {
    padding: 15px;
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>