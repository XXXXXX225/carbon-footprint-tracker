<template>
  <router-view v-slot="{ Component }">
    <transition name="fade-slide" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
  <!-- 全局挂载移动端导航 -->
  <MobileNav />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCarbonStore } from './store'
import MobileNav from './components/MobileNav.vue'

const carbonStore = useCarbonStore()

// 应用启动时加载用户信息
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  if (carbonStore.user && carbonStore.user.id) {
    carbonStore.fetchAllRecords()
  }
})
</script>


<style>
:root {
  --el-font-family: 'Noto Sans SC', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  --primary-color: #4CAF50;
    --secondary-color: #81C784;
  --accent-color: #2E7D32;
  --background-color: #F0F8F0;
  --text-color: #333333;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans SC', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  color: var(--text-color);
  /* 添加全局流体呼吸纹理背景 (Ambient Mesh Gradient) */
  background: linear-gradient(135deg, #F0F8F0 0%, #E8F5E9 25%, #E0F2F1 50%, #F1F8E9 75%, #F0F8F0 100%);
  background-size: 400% 400%;
  animation: ambientBackground 15s ease infinite;
}

@keyframes ambientBackground {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.el-container {
  min-height: 100vh;
}

.el-header {
  background-color: rgba(76, 175, 80, 0.9) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-aside {
  background-color: rgba(255, 255, 255, 0.7) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.02);
  border-right: 1px solid rgba(255, 255, 255, 0.5);
}

.el-main {
  background-color: transparent !important; /* 让 body 的极光流体背景透出 */
  padding: 20px;
}

.page-shell {
  width: min(1200px, calc(100% - 48px));
  margin: 0 auto;
}

.page-hero,
.page-section {
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.65);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  box-shadow: 0 12px 40px rgba(76, 175, 80, 0.08);
}

.page-hero {
  padding: 28px 30px;
  margin-bottom: 20px;
}

.page-section {
  padding: 28px 30px;
  margin-top: 20px;
}

.page-section:first-child {
  margin-top: 0;
}

.page-section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 20px;
}

.page-kicker {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(76, 175, 80, 0.12);
  color: var(--accent-color);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  margin-bottom: 12px;
}

.page-title {
  font-size: 28px;
  line-height: 1.2;
  color: #1a1a1a;
  margin-bottom: 10px;
}

.page-desc {
  max-width: 720px;
  color: #5f6b60;
  font-size: 15px;
}

.page-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

/* Modern Soft UI Styles (Glassmorphism Upgrade) */
.card, .el-card {
  background-color: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 16px !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  box-shadow: 0 8px 32px rgba(76, 175, 80, 0.05), inset 0 0 0 1px rgba(255, 255, 255, 0.4) !important;
  padding: 20px;
  margin-bottom: 20px;
  transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.25, 1), box-shadow 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.el-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(76, 175, 80, 0.12) !important;
}

.btn-primary, .el-button--primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, #388E3C 100%) !important;
  border: none !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3) !important;
}

.btn-primary:hover, .el-button--primary:hover {
  background: linear-gradient(135deg, #57C75C 0%, var(--accent-color) 100%) !important;
  box-shadow: 0 6px 16px rgba(76, 175, 80, 0.4) !important;
  transform: translateY(-1px);
}

.chart-container {
  width: 100%;
  height: 400px;
}

/* 强调核心 KPI 的字体 */
.overview-value, .metric-value, .el-statistic__content, .point-number {
  font-weight: 800 !important;
  color: #1a1a1a;
  letter-spacing: -0.5px;
}

@media (max-width: 768px) {
  .el-main {
    padding: 10px;
  }

  .page-shell {
    width: calc(100% - 20px);
  }

  .page-hero,
  .page-section {
    padding: 20px 18px;
    border-radius: 20px;
  }

  .page-section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card {
    padding: 15px;
  }
  
  .chart-container {
    height: 300px;
  }

  .page-title {
    font-size: 22px;
  }
}

/* --- 高级动画样式 (Advanced Animations) --- */

/* 1. 页面级路由切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.4s ease, transform 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 2. 卡片瀑布流出场动画 (Staggered Fade-Up) */
.stagger-item {
  opacity: 0;
  animation: fadeUp 0.6s cubic-bezier(0.25, 0.8, 0.25, 1) forwards;
}

@keyframes fadeUp {
  0% { opacity: 0; transform: translateY(40px); }
  100% { opacity: 1; transform: translateY(0); }
}

.delay-0 { animation-delay: 0s; }
.delay-1 { animation-delay: 0.1s; }
.delay-2 { animation-delay: 0.2s; }
.delay-3 { animation-delay: 0.3s; }
.delay-4 { animation-delay: 0.4s; }
.delay-5 { animation-delay: 0.5s; }

/* 3. 微发光玻璃态Hover (Glassmorphism Glow) */
.glow-card:hover {
  box-shadow: 0 0 20px rgba(76, 175, 80, 0.4), inset 0 0 10px rgba(255, 255, 255, 0.5) !important;
  transform: translateY(-4px) scale(1.01);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

/* 4. 游戏化得分跳动 (Gamification Bounce) */
.bounce-animation .points-value {
  color: #FF9800 !important;
  display: inline-block;
  animation: pulseScore 2.5s infinite ease-in-out;
  transform-origin: left bottom;
}

@keyframes pulseScore {
  0%, 100% { transform: scale(1); text-shadow: 0 0 0 rgba(255, 152, 0, 0); }
  50% { transform: scale(1.08); text-shadow: 0 0 12px rgba(255, 152, 0, 0.4); }
}
</style>
<style>
/* ========================================================
   Eco-Light Tech 森林清透科技主题 
   ======================================================== */
:root {
  --page-bg-color: #F4FBF7; /* 极其柔和的薄荷白底色 */
  --card-bg-color: rgba(255, 255, 255, 0.85); /* 霜白毛玻璃 */
  --main-text-color: #064E3B; /* 极深的森林绿（代替纯黑） */
  --sub-text-color: #4B5563; /* 柔和的次要文本 */

  --el-color-primary: #10B981; /* 核心：充满生机的翠绿色 */
  --el-color-primary-light-3: #34D399;
  --el-color-primary-light-5: #6EE7B7;
  --el-color-primary-dark-2: #059669;

  --el-color-success: #10B981;
  --el-color-warning: #F59E0B;
  --el-color-danger: #EF4444;

  --el-border-radius-base: 16px;
  --el-box-shadow-light: 0 8px 32px rgba(16, 185, 129, 0.08); /* 柔和的绿色光晕 */
}

.el-button, .el-card, .el-input__wrapper {
  border-radius: var(--el-border-radius-base) !important;
}
body {
  background-color: var(--page-bg-color);
  color: var(--main-text-color);
}
</style>