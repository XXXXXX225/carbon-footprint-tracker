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
          <div class="news-card fade-in" v-for="(news, index) in newsList" :key="index" @click="goToNewsDetail(news.id)">
            <div class="news-image">
              <img :src="news.image" :alt="news.title" />
            </div>
            <div class="news-content">
              <div class="news-meta">
                <span class="news-date">{{ news.date }}</span>
                <span class="news-category">{{ news.category }}</span>
              </div>
              <h3 class="news-title">{{ news.title }}</h3>
              <p class="news-excerpt">{{ news.excerpt }}</p>
              <span class="news-read-more">阅读全文 →</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门资讯 -->
    <section class="featured-news-section">
      <div class="featured-news-container">
        <h3>热门资讯</h3>
        <div class="featured-news-grid">
          <div class="featured-news-card" v-for="(news, index) in featuredNews" :key="index" @click="goToNewsDetail(news.id)">
            <div class="featured-news-image">
              <img :src="news.image" :alt="news.title" />
            </div>
            <div class="featured-news-content">
              <h3 class="featured-news-title">{{ news.title }}</h3>
              <p class="featured-news-excerpt">{{ news.excerpt }}</p>
              <span class="featured-news-read-more">阅读全文 →</span>
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
          <router-link to="/recommendations">减排建议</router-link>
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
    title: '全球碳排放创新低，环保政策成效显著',
    excerpt: '根据最新数据，全球碳排放在过去一年创新低，这主要归功于各国政府采取的积极环保政策和可再生能源的广泛应用。',
    date: '2026-03-05',
    category: '全球趋势',
    image: 'https://images.unsplash.com/photo-1466611653911-95081537e5b7?w=800&q=80'
  },
  {
    id: 2,
    title: '新能源汽车销量突破历史新高',
    excerpt: '2025年全球新能源汽车销量突破1000万辆，同比增长35%，成为减少交通领域碳排放的重要力量。',
    date: '2026-03-04',
    category: '交通减排',
    image: 'https://images.unsplash.com/photo-1593941707882-a5bba14938c7?w=800&q=80'
  },
  {
    id: 3,
    title: '新型碳捕捉技术取得重大突破',
    excerpt: '科学家开发出一种高效碳捕捉技术，能够从空气中直接捕捉二氧化碳，转化率达到90%以上。',
    date: '2026-03-03',
    category: '技术创新',
    image: 'https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=800&q=80'
  },
  {
    id: 4,
    title: '可持续饮食成为全球新趋势',
    excerpt: '越来越多的人选择植物性饮食，减少肉类消费，这一趋势正在显著降低全球农业碳排放。',
    date: '2026-03-02',
    category: '饮食减排',
    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800&q=80'
  },
  {
    id: 5,
    title: '企业碳足迹报告成为投资者关注焦点',
    excerpt: '越来越多的投资者将企业碳足迹报告作为投资决策的重要依据，推动企业更加重视减排。',
    date: '2026-03-01',
    category: '企业责任',
    image: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&q=80'
  },
  {
    id: 6,
    title: '政府推出碳税政策，促进绿色转型',
    excerpt: '多个国家开始实施碳税政策，通过经济手段鼓励企业和个人减少碳排放，推动绿色经济转型。',
    date: '2026-02-28',
    category: '政策法规',
    image: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800&q=80'
  }
])

// 热门资讯 - 使用高质量Unsplash大图
const featuredNews = ref([
  {
    id: 1,
    title: '全球100家企业承诺2030年实现碳中和',
    excerpt: '包括科技、金融、能源等多个行业的100家全球企业联合承诺，到2030年实现碳中和目标，这将对全球减排产生重大影响。',
    image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=1200&q=80'
  },
  {
    id: 2,
    title: '城市绿化计划减少城市热岛效应',
    excerpt: '全球多个城市实施大规模绿化计划，通过增加城市植被覆盖，不仅减少了碳排放，还显著降低了城市热岛效应。',
    image: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1200&q=80'
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
  background: linear-gradient(270deg, #2E7D32, #1B5E20, #388E3C, #2E7D32);
  background-size: 400% 400%;
  color: white;
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
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.15)"/></svg>') repeat;
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
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
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
  background: rgba(255, 255, 255, 0.3);
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.2);
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
  font-size: 3rem;
  margin-bottom: 1.5rem;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: -1px;
  opacity: 0;
  animation: fadeInUp 1s ease forwards;
}

.hero-content p {
  font-size: 1.3rem;
  margin-bottom: 2.5rem;
  max-width: 800px;
  font-weight: 300;
  opacity: 0;
  animation: fadeInUp 1s ease 0.3s forwards;
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease;
  cursor: pointer;
}

.news-card.visible {
  opacity: 1;
  transform: translateY(0);
}

.news-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
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

.news-category {
  background: rgba(46, 125, 50, 0.1);
  color: #2E7D32;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.news-title {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #1B5E20;
  font-weight: 600;
  line-height: 1.4;
}

.news-excerpt {
  color: #666;
  line-height: 1.7;
  margin-bottom: 1.5rem;
  font-size: 1rem;
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

/* 热门资讯样式 */
.featured-news-section {
  padding: 6rem 0;
  background-color: white;
}

.featured-news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.featured-news-container h3 {
  font-size: 2.5rem;
  margin-bottom: 4rem;
  color: #1B5E20;
  font-weight: 700;
  letter-spacing: -0.5px;
  text-align: center;
}

.featured-news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 3rem;
}

.featured-news-card {
  display: flex;
  background-color: #f8f9fa;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.featured-news-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}

.featured-news-image {
  flex: 0 0 40%;
  height: 250px;
  overflow: hidden;
}

.featured-news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.featured-news-card:hover .featured-news-image img {
  transform: scale(1.1);
}

.featured-news-content {
  flex: 1;
  padding: 2.5rem;
}

.featured-news-title {
  font-size: 1.5rem;
  margin-bottom: 1.2rem;
  color: #1B5E20;
  font-weight: 600;
  line-height: 1.4;
}

.featured-news-excerpt {
  color: #666;
  line-height: 1.7;
  margin-bottom: 1.5rem;
  font-size: 1rem;
}

.featured-news-read-more {
  display: inline-block;
  color: #2E7D32;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.featured-news-read-more:hover {
  color: #1B5E20;
  transform: translateX(5px);
}

.featured-news-read-more::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #2E7D32;
  transition: width 0.3s ease;
}

.featured-news-read-more:hover::after {
  width: 100%;
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
    font-size: 2.5rem;
  }

  .hero-content p {
    font-size: 1.1rem;
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
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  position: relative;
  overflow: hidden;
}

.btn-primary {
  background: linear-gradient(135deg, #2E7D32, #4CAF50);
  color: white;
  box-shadow: 0 4px 15px rgba(46, 125, 50, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(46, 125, 50, 0.4);
}

.btn-secondary {
  background: white;
  color: #2E7D32;
  border: 2px solid #2E7D32;
  box-shadow: 0 4px 15px rgba(46, 125, 50, 0.1);
}

.btn-secondary:hover {
  background: #2E7D32;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(46, 125, 50, 0.3);
}

.btn-large {
  padding: 16px 32px;
  font-size: 1.1rem;
  border-radius: 12px;
}
</style>
