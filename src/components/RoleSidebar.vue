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
  Van,
  KnifeFork,
  Lightning,
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
  '/transport': Van,
  '/diet': KnifeFork,
  '/electricity': Lightning,
  '/report': DataLine,
  '/ai-analysis': TrendCharts,
  '/recommendations': Star,
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
  padding: 6px 12px 0;
  color: rgba(255, 255, 255, 0.65);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.role-sidebar :deep(.el-menu-item) {
  margin: 0 8px;
  border-radius: 12px;
}
</style>