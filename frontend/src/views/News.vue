<template>
  <div class="news-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <router-link to="/home" class="logo-link">
            <h1>碳足迹追踪平台</h1>
          </router-link>
        </div>
        <div class="navbar-links">
          <template v-if="user && user.id">
            <div class="user-dropdown">
              <el-dropdown @command="handleUserCommand">
                <span class="user-info">
                  <el-avatar :size="32" :icon="UserFilled" />
                  <span class="user-name">{{ user.name }}</span>
                  <el-icon class="el-icon--right">
                    <ArrowDown />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-for="item in topNavItems" :key="item.command" :command="item.command">
                      <el-icon>
                        <component :is="iconMap[item.command]" />
                      </el-icon>
                      {{ item.label }}
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout">
                      <el-icon>
                        <SwitchButton />
                      </el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-primary">登录/注册</router-link>
          </template>
        </div>
      </div>
    </nav>

    <!-- 英雄区域 -->
    <section class="hero-section" @mousemove="handleMouseMove">
      <div class="particles">
        <div class="particle particle-1"></div>
        <div class="particle particle-2"></div>
        <div class="particle particle-3"></div>
        <div class="particle particle-4"></div>
        <div class="particle particle-5"></div>
      </div>
      <div class="hero-container">
        <div class="hero-content">
          <h2>碳足迹资讯中心</h2>
          <p>了解最新的环保资讯、碳减排技术和可持续发展趋势，为地球的未来贡献力量。</p>
        </div>
      </div>
    </section>

    <!-- 资讯列表 -->
    <section class="news-section">
      <div class="news-container">
        <div class="news-grid">
          <div class="news-card fade-in" v-for="(news, index) in newsList" :key="index" @click="openExternalLink(news.link)">
            <div class="news-image">
              <img :src="news.coverImage" :alt="news.title" />
            </div>
            <div class="news-content">
              <div class="news-meta">
                <span class="news-date">{{ news.date }}</span>
                <span class="news-category">{{ news.category }}</span>
                <span class="news-source">{{ news.source }}</span>
              </div>
              <h3 class="news-title">{{ news.title }}</h3>
              <p class="news-excerpt">{{ news.summary }}</p>
              <span class="news-read-more read-more">阅读全文 &rarr;</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门资讯 (全新 Bento Box 布局) -->
    <section class="featured-news-section">
      <div class="featured-news-container">
        <div class="section-header">
          <h3>编辑推荐</h3>
          <p class="section-subtitle">深度解读碳中和浪潮下的行业变革</p>
        </div>

        <div class="bento-grid">
          <div class="bento-card bento-large" @click="goToNewsDetail(featuredNews[0].id)">
            <div class="bento-image">
              <img :src="featuredNews[0].image" :alt="featuredNews[0].title" />
              <div class="bento-overlay"></div>
            </div>
            <div class="bento-content">
              <span class="bento-category">{{ featuredNews[0].category }}</span>
              <h3 class="bento-title">{{ featuredNews[0].title }}</h3>
              <p class="bento-excerpt">{{ featuredNews[0].excerpt }}</p>
            </div>
          </div>

          <div class="bento-card bento-small" v-for="news in featuredNews.slice(1)" :key="news.id" @click="goToNewsDetail(news.id)">
            <div class="bento-image">
              <img :src="news.image" :alt="news.title" />
              <div class="bento-overlay"></div>
            </div>
            <div class="bento-content">
              <span class="bento-category">{{ news.category }}</span>
              <h3 class="bento-title">{{ news.title }}</h3>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-info">
          <h4>碳足迹追踪平台</h4>
          <p>致力于帮助个人和企业了解并减少碳排放，为环保事业贡献力量。</p>
        </div>
        <div class="footer-links">
          <router-link to="/">首页</router-link>
          <router-link to="/dashboard">仪表盘</router-link>
          <router-link to="/news">资讯中心</router-link>
          <router-link to="/action-plan">碳行动计划</router-link>
          <router-link to="/login">登录/注册</router-link>
        </div>
        <div class="footer-contact">
          <p>联系我们: contact@carbonfootprint.com</p>
          <p>© 2026 碳足迹追踪平台. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCarbonStore } from '../store'
import { UserFilled, User, House, CollectionTag, SwitchButton, ArrowDown, Setting, DataLine } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getTopNavItems } from '../utils/access'

const router = useRouter()
const carbonStore = useCarbonStore()

// 用户信息
const user = computed(() => carbonStore.user)
const topNavItems = computed(() => getTopNavItems(user.value.role))

const iconMap: Record<string, any> = {
  dashboard: House,
  'dashboard-screen': DataLine,
  profile: User,
  points: CollectionTag,
  admin: Setting
}

// 处理用户下拉菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'dashboard':
      router.push('/dashboard')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'points':
      router.push('/points')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  carbonStore.user = {
    id: '',
    name: ''
  }
  ElMessage.success('已退出登录')
}

// 跳转到新闻详情页
const goToNewsDetail = (id: number) => {
  router.push(`/news/${id}`)
}

// 鼠标位置跟踪
const mouseX = ref(0)
const mouseY = ref(0)

// 处理鼠标移动
const handleMouseMove = (event: MouseEvent) => {
  mouseX.value = event.clientX
  mouseY.value = event.clientY
}

// 资讯列表 - 使用高质量Unsplash环保主题图片
const newsList = ref([
  {
    id: 1,
    title: '生态环境部：稳步推进全国碳排放权交易市场建设',
    summary: '生态环境部指出，全国碳市场是落实碳达峰碳中和目标的核心政策工具。2024年将持续完善碳排放统计核算、配额分配等关键制度，并逐步扩大行业覆盖范围。',
    date: '2024-02-26',
    category: '政策前沿',
    source: '中华人民共和国生态环境部',
    link: 'https://www.mee.gov.cn/ywdt/hjywnews/',
    coverImage: 'https://images.unsplash.com/photo-1466611653911-95081537e5b7?q=80&w=800&auto=format&fit=crop'
  },
  {
    id: 2,
    title: '国家发改委：加快推动节能降碳改造和用能设备更新',
    summary: '国家发展改革委等部门联合印发行动方案，重点推动工业、建筑、交通等领域的关键设备更新，全面提升重点用能设备能效水平，深入挖掘节能降碳潜力。',
    date: '2024-03-06',
    category: '产业升级',
    source: '国家发展和改革委员会',
    link: 'https://www.ndrc.gov.cn/xwdt/tzgg/',
    coverImage: 'https://images.unsplash.com/photo-1513828583688-c52646db42da?q=80&w=800&auto=format&fit=crop'
  },
  {
    id: 3,
    title: '国家能源局：全国可再生能源装机历史性超过火电',
    summary: '国家能源局发布最新数据，我国可再生能源总装机突破14亿千瓦，在全国发电总装机中的比重超过50%，标志着我国能源结构实现重大历史性转变。',
    date: '2024-01-25',
    category: '能源结构',
    source: '国家能源局',
    link: 'http://www.nea.gov.cn',
    coverImage: 'https://images.unsplash.com/photo-1509391366360-2e959784a276?q=80&w=800&auto=format&fit=crop'
  }
])

const openExternalLink = (url: string) => {
  window.open(url, '_blank', 'noopener,noreferrer');
}

// 热门资讯 - 全新 2026 行业热点数据
const featuredNews = ref([
  {
    id: 4,
    category: '行业白皮书',
    title: '2026全球ESG投资报告：碳足迹追踪系统已成企业合规"标配"',
    excerpt: '随着国际可持续发展准则理事会(ISSB)新规全面落地，全球超过80%的上市企业已将数字化碳追踪接入核心业务流，高碳排企业正面临前所未有的融资壁垒。',
    image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?q=80&w=1200&auto=format&fit=crop'
  },
  {
    id: 5,
    category: '气候科技',
    title: 'AI大模型与气候科技深度融合：预测算法如何精准削减20%工业碳排',
    excerpt: '最新研报显示，引入AI碳推演模型的制造企业，其资源浪费率平均下降了22%。',
    image: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=800&auto=format&fit=crop'
  },
  {
    id: 6,
    category: '碳普惠机制',
    title: '碳积分正式打通多地公共交通，个人减排收益实现"闭环"',
    excerpt: '多城联动推出的"碳普惠2.0"网络，让市民凭借日常的低碳行为积分，直接兑换地铁与公交乘车次卡。',
    image: 'https://images.unsplash.com/photo-1514565131-fce0801e5785?q=80&w=800&auto=format&fit=crop'
  }
])

// 组件挂载时加载用户信息
onMounted(() => {
  // 加载用户信息
  carbonStore.loadUserFromLocalStorage()

  // 滚动动画
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible')
      }
    })
  }, {
    threshold: 0.1
  })

  document.querySelectorAll('.fade-in').forEach(el => {
    observer.observe(el)
  })

  // 添加鼠标移动监听
  window.addEventListener('mousemove', handleMouseMove)
})

// 组件卸载时清除事件监听
onUnmounted(() => {
  // 移除鼠标移动监听
  window.removeEventListener('mousemove', handleMouseMove)
})
</script>

<style scoped>
/* 全局样式 */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans SC', sans-serif;
  line-height: 1.6;
  color: #333;
  overflow-x: hidden;
}

/* 导航栏样式 */
.navbar {
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.2rem 2rem;
}

.navbar-logo .logo-link {
  text-decoration: none;
  color: #2E7D32;
  display: inline-block;
  transition: all 0.3s ease;
  position: relative;
}

.navbar-logo .logo-link:hover {
  color: #1B5E20;
  transform: translateY(-2px);
}

.navbar-logo h1 {
  font-size: 1.8rem;
  margin: 0;
  color: inherit;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.navbar-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  background: rgba(76, 175, 80, 0.1);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(76, 175, 80, 0.2);
}

.user-name {
  font-weight: 500;
  color: #1a1a1a;
  font-size: 14px;
}

/* 英雄区域样式 */
.hero-section {
    background: linear-gradient(135deg, rgba(232, 245, 233, 0.4) 0%, rgba(241, 248, 233, 0.6) 50%, rgba(232, 245, 233, 0.4) 100%);
    background-size: 200% 200%;
    color: #4B5563;
    border-bottom: 1px solid rgba(255, 255, 255, 0.5);
  padding: 4rem 0 3rem;
  position: relative;
  overflow: hidden;
  animation: gradientFlow 8s ease infinite;
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><circle cx="50" cy="50" r="1" fill="rgba(16, 185, 129, 0.2)"/></svg>') repeat;
  opacity: 0.5;
  animation: float 20s infinite linear;
  pointer-events: none;
  z-index: 0;
}

.hero-section::after {
  content: '';
  position: absolute;
  top: 10%;
  left: 5%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 8s ease-in-out infinite;
  pointer-events: none;
}

@keyframes float {
  0% { transform: translate(0, 0); }
  100% { transform: translate(100px, 100px); }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.5;
  }
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 1;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(16, 185, 129, 0.3);
    box-shadow: 0 0 6px rgba(16, 185, 129, 0.2);
  animation: floatAround 8s ease-in-out infinite;
}

@keyframes floatAround {
  0%, 100% {
    transform: translate(0, 0);
    opacity: 0.3;
  }
  25% {
    transform: translate(var(--move-x1), var(--move-y1));
    opacity: 0.5;
  }
  50% {
    transform: translate(var(--move-x2), var(--move-y2));
    opacity: 0.3;
  }
  75% {
    transform: translate(var(--move-x3), var(--move-y3));
    opacity: 0.5;
  }
}

.particle-1 {
  width: 2px;
  height: 2px;
  left: 10%;
  top: 20%;
  --move-x1: 30px;
  --move-y1: -20px;
  --move-x2: -20px;
  --move-y2: 40px;
  --move-x3: 10px;
  --move-y3: -30px;
  animation-delay: 0s;
}

.particle-2 {
  width: 3px;
  height: 3px;
  left: 85%;
  top: 15%;
  --move-x1: -25px;
  --move-y1: 15px;
  --move-x2: 20px;
  --move-y2: -30px;
  --move-x3: -15px;
  --move-y3: 25px;
  animation-delay: 0.3s;
}

.particle-3 {
  width: 2px;
  height: 2px;
  left: 25%;
  top: 75%;
  --move-x1: 20px;
  --move-y1: -25px;
  --move-x2: -30px;
  --move-y2: 15px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 0.6s;
}

.particle-4 {
  width: 4px;
  height: 4px;
  left: 70%;
  top: 80%;
  --move-x1: -20px;
  --move-y1: 20px;
  --move-x2: 25px;
  --move-y2: -15px;
  --move-x3: -30px;
  --move-y3: 25px;
  animation-delay: 0.9s;
}

.particle-5 {
  width: 2px;
  height: 2px;
  left: 45%;
  top: 30%;
  --move-x1: 15px;
  --move-y1: 25px;
  --move-x2: -20px;
  --move-y2: -15px;
  --move-x3: 30px;
  --move-y3: 20px;
  animation-delay: 1.2s;
}

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  z-index: 2;
}

.hero-content h2 {
    font-size: 3.5rem;
    font-weight: 800;
    color: #064E3B;
    line-height: 1.15;
    margin-bottom: 1.5rem;
    letter-spacing: -1px;
    background: linear-gradient(135deg, #10B981, #0ea5e9);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

.hero-content p {
    font-size: 1.15rem;
    color: #4B5563;
    opacity: 0.9;
    max-width: 600px;
    margin: 0 auto;
  }

/* 资讯列表样式 */
.news-section {
  padding: 6rem 0;
  background-color: #f8f9fa;
}

.news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2.5rem;
}

.news-card {
  background-color: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(16, 185, 129, 0.05);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(16, 185, 129, 0.1);
  position: relative;
  opacity: 0;
  transform: translateY(30px);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease, opacity 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.news-card.visible {
  opacity: 1;
  transform: translateY(0);
}

.news-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(16, 185, 129, 0.2);
}

.news-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  aspect-ratio: 16/9;
  transition: transform 0.5s ease;
}

.news-card:hover .news-image img {
  transform: scale(1.1);
}

.news-content {
  padding: 2rem;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.news-date {
  font-weight: 500;
}

.news-source {
  margin-left: 8px;
  color: #888;
  font-size: 0.8rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.news-category {
  background: rgba(46, 125, 50, 0.1);
  color: #2E7D32;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
  display: inline-block;
}

.news-title {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #1B5E20;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-excerpt {
  color: #666;
  line-height: 1.7;
  margin-bottom: 1.5rem;
  font-size: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-read-more {
  display: inline-block;
  color: #2E7D32;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.news-read-more:hover {
  color: #1B5E20;
  transform: translateX(5px);
}

.news-read-more::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #2E7D32;
  transition: width 0.3s ease;
}

.news-read-more:hover::after {
  width: 100%;
}

/* =========================================
   热门资讯 Bento Box 高级网格布局
   ========================================= */
.featured-news-section {
  padding: 6rem 0;
  background-color: #ffffff;
}

.featured-news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.section-header {
  margin-bottom: 3.5rem;
  text-align: center;
}

.section-header h3 {
  font-size: 2.5rem;
  color: #1B5E20;
  font-weight: 800;
  letter-spacing: -0.5px;
  margin-bottom: 0.8rem;
}

.section-subtitle {
  color: #6B7280;
  font-size: 1.15rem;
  letter-spacing: 1px;
}

.bento-grid {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 1.5rem;
  height: 560px;
}

.bento-card {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.4s ease;
}

.bento-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 25px 50px rgba(16, 185, 129, 0.25);
}

.bento-large {
  grid-row: 1 / 3;
}

.bento-image {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
}

.bento-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s ease;
}

.bento-card:hover .bento-image img {
  transform: scale(1.08);
}

.bento-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(to top, rgba(0,0,0,0.85) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0.1) 100%);
  transition: opacity 0.3s ease;
}

.bento-card:hover .bento-overlay {
  background: linear-gradient(to top, rgba(0,0,0,0.9) 0%, rgba(16, 185, 129, 0.4) 50%, transparent 100%);
}

.bento-content {
  position: absolute;
  bottom: 0; left: 0; width: 100%;
  padding: 2.5rem;
  z-index: 2;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.bento-small .bento-content {
  padding: 1.5rem 2rem;
}

.bento-category {
  background: #10B981;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
  margin-bottom: 1.2rem;
  align-self: flex-start;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 4px 10px rgba(16, 185, 129, 0.4);
}

.bento-large .bento-title {
  font-size: 2.2rem;
  font-weight: 800;
  margin-bottom: 1rem;
  line-height: 1.3;
  text-shadow: 0 2px 8px rgba(0,0,0,0.8);
}

.bento-small .bento-title {
  font-size: 1.35rem;
  font-weight: 700;
  line-height: 1.4;
  margin: 0;
  text-shadow: 0 2px 6px rgba(0,0,0,0.8);
}

.bento-excerpt {
  font-size: 1.05rem;
  color: #F3F4F6;
  line-height: 1.6;
  opacity: 0.9;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 992px) {
  .bento-grid {
    grid-template-columns: 1fr;
    grid-template-rows: auto;
    height: auto;
  }
  .bento-large {
    grid-row: auto;
    height: 450px;
  }
  .bento-small {
    height: 280px;
  }
  .bento-large .bento-title {
    font-size: 1.8rem;
  }
}
@media (max-width: 768px) {
  .bento-large { height: 400px; }
  .bento-small { height: 250px; }
  .bento-content { padding: 1.5rem; }
}

/* 页脚样式 */
.footer {
  background-color: #1B5E20;
  color: white;
  padding: 4rem 0;
  margin-top: 4rem;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 3rem;
}

.footer-info h4 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  font-weight: 700;
}

.footer-info p {
  line-height: 1.7;
  opacity: 0.9;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-links a {
  color: white;
  text-decoration: none;
  transition: all 0.3s ease;
  opacity: 0.9;
}

.footer-links a:hover {
  opacity: 1;
  transform: translateX(5px);
  color: #4CAF50;
}

.footer-contact p {
  margin-bottom: 0.5rem;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content h2 {
    font-size: 3.5rem;
    font-weight: 800;
    color: #064E3B;
    line-height: 1.15;
    margin-bottom: 1.5rem;
    letter-spacing: -1px;
    background: linear-gradient(135deg, #10B981, #0ea5e9);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .hero-content p {
    font-size: 1.15rem;
    color: #4B5563;
    opacity: 0.9;
    max-width: 600px;
    margin: 0 auto;
  }

  .news-grid {
    grid-template-columns: 1fr;
  }

  .featured-news-grid {
    grid-template-columns: 1fr;
  }

  .featured-news-card {
    flex-direction: column;
  }

  .featured-news-image {
    flex: 0 0 200px;
  }

  .footer-container {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 按钮样式 */


.btn {
    display: inline-block;
    padding: 14px 28px;
    border-radius: 50px;
    text-decoration: none;
    font-weight: 600;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    cursor: pointer;
    font-size: 1.05rem;
    position: relative;
    overflow: hidden;
    letter-spacing: 1px;
}

.btn::before {
    content: ''; position: absolute; top: 0; left: -100%; width: 100%; height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
    transition: all 0.6s ease;
}
.btn:hover::before { left: 100%; }

.btn-primary {
    background: linear-gradient(135deg, #0ea5e9, #10B981);
    color: #ffffff;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 10px 20px -5px rgba(16, 185, 129, 0.5), inset 0 1px 1px rgba(255, 255, 255, 0.4);
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-primary:hover {
    background: linear-gradient(135deg, #0284c7, #059669);
    transform: translateY(-5px) scale(1.02);
    box-shadow: 0 20px 30px -10px rgba(16, 185, 129, 0.6), inset 0 1px 1px rgba(255, 255, 255, 0.5);
    color: #ffffff;
}

.btn-secondary {
    background: rgba(255, 255, 255, 0.1);
    color: #064E3B;
    border: 1px solid rgba(16, 185, 129, 0.4);
    box-shadow: 0 4px 15px rgba(16, 185, 129, 0.05);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
}

.btn-secondary:hover {
    border-color: #10B981;
    color: #047857;
    background: rgba(16, 185, 129, 0.1);
    transform: translateY(-5px) scale(1.02);
    box-shadow: 0 15px 25px -5px rgba(16, 185, 129, 0.2);
}

.btn-large {
  padding: 16px 32px;
  font-size: 1.1rem;
  border-radius: 12px;
}
</style>
