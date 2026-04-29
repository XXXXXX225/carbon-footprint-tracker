<template>
  <el-menu :default-active="route.path" class="role-sidebar" @select="handleSelect">
    <div v-for="section in sections" :key="section.title" class="role-menu-group">
      <div class="role-menu-group-title">{{ section.title }}</div>
      <el-menu-item v-for="item in section.items" :key="item.path" :index="item.path">
        <el-icon>
          <component :is="iconMap[item.path]" />
        </el-icon>
        <span>{{ item.label }}</span>
      </el-menu-item>
    </div>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  House,
  Compass,
  DataLine,
  TrendCharts,
  Star,
  CollectionTag,
  Monitor,
  Setting
} from '@element-plus/icons-vue'
import { useCarbonStore } from '../store'
import { getSidebarSections } from '../utils/access'

const route = useRoute()
const router = useRouter()
const carbonStore = useCarbonStore()

const sections = computed(() => getSidebarSections(carbonStore.user.role))

const iconMap: Record<string, any> = {
  '/dashboard': House,
  '/emissions': Compass,
  '/report': DataLine,
  '/ai-analysis': TrendCharts,
  '/action-plan': Star,
  '/points': CollectionTag,
  '/dashboard-screen': Monitor,
  '/admin': Setting
}

const handleSelect = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.role-sidebar {
  height: 100%;
  border-right: none;
  background-color: transparent !important;
}

.role-menu-group {
  display: grid;
  gap: 8px;
  margin-bottom: 14px;
}

.role-menu-group-title {
  padding: 0 12px;
  margin: 12px 0 8px 8px;
  color: #606266;
  font-size: 13px;
  font-weight: bold;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
}

.role-menu-group-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 14px;
  background: linear-gradient(180deg, #67c23a 0%, #a0cfff 100%); /* 环保绿到天空蓝的渐变 */
  border-radius: 4px;
  margin-right: 8px;
}

.role-sidebar :deep(.el-menu-item) {
  margin: 0 8px;
  border-radius: 12px;
}
</style>