<template>
  <!-- 移动端侧边抽屉导航 -->
  <div class="mobile-nav-wrapper" v-if="shouldShowNav">
    <el-button 
      class="mobile-menu-btn" 
      type="primary" 
      circle 
      @click="drawerVisible = true"
    >
      <el-icon><Menu /></el-icon>
    </el-button>
    
    <el-drawer
      v-model="drawerVisible"
      title="碳足迹追踪平台"
      direction="ltr"
      size="240px"
      class="mobile-drawer"
    >
      <RoleSidebar />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Menu } from '@element-plus/icons-vue'
import RoleSidebar from './RoleSidebar.vue'

const route = useRoute()
const drawerVisible = ref(false)

// 只有在需要登录的页面（后台）才显示导航
const shouldShowNav = computed(() => {
  return route.meta && route.meta.requiresAuth
})

watch(
  () => route.path,
  () => {
    drawerVisible.value = false
  }
)

</script>

<style scoped>
.mobile-nav-wrapper {
  display: none;
}

@media (max-width: 768px) {
  .mobile-nav-wrapper {
    display: block;
  }
  
  .mobile-menu-btn {
    position: fixed;
    bottom: 30px;
    right: 20px;
    z-index: 9999;
    box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
    width: 60px;
    height: 60px;
    font-size: 24px;
  }
}
</style>
