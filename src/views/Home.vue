<template>
  <div class="home-container">
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
                    <el-dropdown-item command="dashboard">
                      <el-icon>
                        <House />
                      </el-icon>
                      仪表盘
                    </el-dropdown-item>
                    <el-dropdown-item command="profile">
                      <el-icon>
                        <User />
                      </el-icon>
                      个人中心
                    </el-dropdown-item>
                    <el-dropdown-item command="points">
                      <el-icon>
                        <CollectionTag />
                      </el-icon>
                      减碳积分
                    </el-dropdown-item>
                    <el-dropdown-item v-if="user.role === 'ADMIN'" command="admin">
                      <el-icon>
                        <Setting />
                      </el-icon>
                      管理员
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
        <div class="particle particle-6"></div>
        <div class="particle particle-7"></div>
        <div class="particle particle-8"></div>
        <div class="particle particle-9"></div>
        <div class="particle particle-10"></div>
        <div class="particle particle-11"></div>
        <div class="particle particle-12"></div>
        <div class="particle particle-13"></div>
        <div class="particle particle-14"></div>
        <div class="particle particle-15"></div>
        <div class="particle particle-16"></div>
        <div class="particle particle-17"></div>
        <div class="particle particle-18"></div>
        <div class="particle particle-19"></div>
        <div class="particle particle-20"></div>
        <div class="particle particle-21"></div>
        <div class="particle particle-22"></div>
        <div class="particle particle-23"></div>
        <div class="particle particle-24"></div>
        <div class="particle particle-25"></div>
        <div class="particle particle-26"></div>
        <div class="particle particle-27"></div>
        <div class="particle particle-28"></div>
        <div class="particle particle-29"></div>
        <div class="particle particle-30"></div>
        <div class="particle particle-31"></div>
        <div class="particle particle-32"></div>
        <div class="particle particle-33"></div>
        <div class="particle particle-34"></div>
        <div class="particle particle-35"></div>
        <div class="particle particle-36"></div>
        <div class="particle particle-37"></div>
        <div class="particle particle-38"></div>
        <div class="particle particle-39"></div>
        <div class="particle particle-40"></div>
        <div class="particle particle-41"></div>
        <div class="particle particle-42"></div>
        <div class="particle particle-43"></div>
        <div class="particle particle-44"></div>
        <div class="particle particle-45"></div>
        <div class="particle particle-46"></div>
        <div class="particle particle-47"></div>
        <div class="particle particle-48"></div>
        <div class="particle particle-49"></div>
        <div class="particle particle-50"></div>
        <div class="particle particle-51"></div>
        <div class="particle particle-52"></div>
        <div class="particle particle-53"></div>
        <div class="particle particle-54"></div>
        <div class="particle particle-55"></div>
        <div class="particle particle-56"></div>
        <div class="particle particle-57"></div>
        <div class="particle particle-58"></div>
        <div class="particle particle-59"></div>
        <div class="particle particle-60"></div>
        <div class="particle particle-61"></div>
        <div class="particle particle-62"></div>
        <div class="particle particle-63"></div>
        <div class="particle particle-64"></div>
        <div class="particle particle-65"></div>
        <div class="particle particle-66"></div>
        <div class="particle particle-67"></div>
        <div class="particle particle-68"></div>
        <div class="particle particle-69"></div>
        <div class="particle particle-70"></div>
        <div class="particle particle-71"></div>
        <div class="particle particle-72"></div>
        <div class="particle particle-73"></div>
        <div class="particle particle-74"></div>
        <div class="particle particle-75"></div>
        <div class="particle particle-76"></div>
        <div class="particle particle-77"></div>
        <div class="particle particle-78"></div>
        <div class="particle particle-79"></div>
        <div class="particle particle-80"></div>
        <div class="particle particle-81"></div>
        <div class="particle particle-82"></div>
        <div class="particle particle-83"></div>
        <div class="particle particle-84"></div>
        <div class="particle particle-85"></div>
        <div class="particle particle-86"></div>
        <div class="particle particle-87"></div>
        <div class="particle particle-88"></div>
        <div class="particle particle-89"></div>
        <div class="particle particle-90"></div>
        <div class="particle particle-91"></div>
        <div class="particle particle-92"></div>
        <div class="particle particle-93"></div>
        <div class="particle particle-94"></div>
        <div class="particle particle-95"></div>
        <div class="particle particle-96"></div>
        <div class="particle particle-97"></div>
        <div class="particle particle-98"></div>
        <div class="particle particle-99"></div>
        <div class="particle particle-100"></div>
      </div>
      <div class="hero-container">
        <div class="hero-content">
          <h2>了解并减少您的碳足迹</h2>
          <p>通过我们的平台，您可以追踪、分析和减少您的日常碳排放，为环保事业贡献力量。</p>
          <div class="hero-buttons">
            <router-link to="/login" class="btn btn-large btn-primary">立即开始</router-link>
            <router-link to="/dashboard" class="btn btn-large btn-secondary">了解更多</router-link>
          </div>
        </div>
        <div class="hero-carousel">
          <div class="carousel-slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div class="carousel-slide">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=carbon%20footprint%20tracking%20dashboard%20with%20charts%20and%20environmental%20icons%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional%2C%20high%20quality%20rendering&image_size=landscape_16_9"
                alt="碳足迹追踪平台" />
            </div>
            <div class="carousel-slide">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=eco%20friendly%20lifestyle%20with%20solar%20panels%20and%20electric%20vehicles%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional%2C%20high%20quality%20rendering&image_size=landscape_16_9"
                alt="环保生活方式" />
            </div>
            <div class="carousel-slide">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sustainable%20transportation%20with%20bicycles%20and%20public%20transit%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional%2C%20high%20quality%20rendering&image_size=landscape_16_9"
                alt="可持续交通" />
            </div>
            <div class="carousel-slide">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=green%20energy%20solutions%20with%20wind%20turbines%20and%20hydroelectric%20power%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional%2C%20high%20quality%20rendering&image_size=landscape_16_9"
                alt="绿色能源解决方案" />
            </div>
          </div>
          <div class="carousel-indicators">
            <button v-for="(slide, index) in slides" :key="index" class="indicator"
              :class="{ active: index === currentSlide }" @click="currentSlide = index"></button>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能介绍 -->
    <section class="features-section">
      <div class="features-container">
        <h3>核心功能</h3>
        <div class="features-grid">
          <div class="feature-card fade-in" data-aos="fade-up">
            <div class="feature-icon">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=transportation%20carbon%20emission%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square"
                alt="交通排放" />
            </div>
            <h4>交通排放追踪</h4>
            <p>记录您的日常出行，计算并分析交通方式对环境的影响。</p>
          </div>
          <div class="feature-card fade-in" data-aos="fade-up" data-aos-delay="100">
            <div class="feature-icon">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=food%20diet%20carbon%20emission%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square"
                alt="饮食排放" />
            </div>
            <h4>饮食排放分析</h4>
            <p>了解您的饮食习惯产生的碳排放，获取健康环保的饮食建议。</p>
          </div>
          <div class="feature-card fade-in" data-aos="fade-up" data-aos-delay="200">
            <div class="feature-icon">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=electricity%20energy%20carbon%20emission%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square"
                alt="用电排放" />
            </div>
            <h4>用电排放监测</h4>
            <p>追踪您的家庭用电量，分析能源使用效率，寻找节能减排机会。</p>
          </div>
          <div class="feature-card fade-in" data-aos="fade-up" data-aos-delay="300">
            <div class="feature-icon">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=reduction%20goal%20carbon%20emission%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square"
                alt="减排目标" />
            </div>
            <h4>减排目标设定</h4>
            <p>设定个人减排目标，跟踪进展，获取个性化的减排建议。</p>
          </div>
          <div class="feature-card fade-in" data-aos="fade-up" data-aos-delay="400">
            <div class="feature-icon">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=report%20analytics%20carbon%20emission%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square"
                alt="排放报告" />
            </div>
            <h4>排放报告生成</h4>
            <p>生成详细的碳排放报告，了解您的环境影响，分享您的环保成就。</p>
          </div>
          <div class="feature-card fade-in" data-aos="fade-up" data-aos-delay="500">
            <div class="feature-icon">
              <img
                src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=personalized%20recommendations%20carbon%20emission%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square"
                alt="个性化建议" />
            </div>
            <h4>个性化减排建议</h4>
            <p>基于您的排放数据，获取量身定制的减排建议，轻松实现环保目标。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 为什么选择我们 -->
    <section class="why-us-section">
      <div class="why-us-container">
        <h3>为什么选择我们的平台</h3>
        <div class="why-us-grid">
          <div class="why-us-item fade-in" data-aos="fade-up">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
            </div>
            <h4>数据驱动</h4>
            <p>基于科学计算模型，提供准确的碳排放数据和分析。</p>
          </div>
          <div class="why-us-item fade-in" data-aos="fade-up" data-aos-delay="100">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
            </div>
            <h4>用户友好</h4>
            <p>直观的界面设计，让碳排放追踪变得简单易行。</p>
          </div>
          <div class="why-us-item fade-in" data-aos="fade-up" data-aos-delay="200">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </div>
            <h4>个性化服务</h4>
            <p>根据您的生活习惯，提供定制化的减排建议。</p>
          </div>
          <div class="why-us-item fade-in" data-aos="fade-up" data-aos-delay="300">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="23 4 23 10 17 10"></polyline>
                <polyline points="1 20 1 14 7 14"></polyline>
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
              </svg>
            </div>
            <h4>持续改进</h4>
            <p>不断优化算法和功能，为您提供更好的用户体验。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 行动召唤 -->
    <section class="cta-section">
      <div class="cta-container">
        <h3>立即开始您的环保之旅</h3>
        <p>加入我们的平台，一起为地球的可持续未来努力。</p>
        <router-link to="/login" class="btn btn-large btn-primary">开始注册</router-link>
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
import { UserFilled, User, House, CollectionTag, SwitchButton, ArrowDown, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const carbonStore = useCarbonStore()

// 用户信息
const user = computed(() => carbonStore.user)

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

// 鼠标位置跟踪
const mouseX = ref(0)
const mouseY = ref(0)

// 处理鼠标移动
const handleMouseMove = (event: MouseEvent) => {
  mouseX.value = event.clientX
  mouseY.value = event.clientY
}

// 轮播功能
const currentSlide = ref(0)
const slides = ref([1, 2, 3, 4]) // 4张幻灯片
let carouselInterval: number | null = null

// 自动轮播
const startCarousel = () => {
  carouselInterval = window.setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.value.length
  }, 5000) // 每5秒切换一次
}

// 组件挂载时启动轮播
onMounted(() => {
  // 加载用户信息
  carbonStore.loadUserFromLocalStorage()

  startCarousel()

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

// 组件卸载时清除轮播定时器
onUnmounted(() => {
  if (carouselInterval) {
    clearInterval(carouselInterval)
  }

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
  padding: 6rem 0 4rem;
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

.particle-6 {
  width: 3px;
  height: 3px;
  left: 55%;
  top: 60%;
  --move-x1: -25px;
  --move-y1: -20px;
  --move-x2: 15px;
  --move-y2: 30px;
  --move-x3: -20px;
  --move-y3: -25px;
  animation-delay: 1.5s;
}

.particle-7 {
  width: 2px;
  height: 2px;
  left: 15%;
  top: 50%;
  --move-x1: 30px;
  --move-y1: -15px;
  --move-x2: -25px;
  --move-y2: 20px;
  --move-x3: 15px;
  --move-y3: -30px;
  animation-delay: 1.8s;
}

.particle-8 {
  width: 3px;
  height: 3px;
  left: 90%;
  top: 45%;
  --move-x1: -20px;
  --move-y1: 25px;
  --move-x2: 30px;
  --move-y2: -20px;
  --move-x3: -25px;
  --move-y3: 15px;
  animation-delay: 2.1s;
}

.particle-9 {
  width: 2px;
  height: 2px;
  left: 35%;
  top: 10%;
  --move-x1: 25px;
  --move-y1: 20px;
  --move-x2: -15px;
  --move-y2: -25px;
  --move-x3: 20px;
  --move-y3: 30px;
  animation-delay: 2.4s;
}

.particle-10 {
  width: 4px;
  height: 4px;
  left: 65%;
  top: 70%;
  --move-x1: -15px;
  --move-y1: -30px;
  --move-x2: 25px;
  --move-y2: 20px;
  --move-x3: -30px;
  --move-y3: -15px;
  animation-delay: 2.7s;
}

.particle-11 {
  width: 2px;
  height: 2px;
  left: 5%;
  top: 85%;
  --move-x1: 20px;
  --move-y1: -25px;
  --move-x2: -30px;
  --move-y2: 15px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 3s;
}

.particle-12 {
  width: 3px;
  height: 3px;
  left: 95%;
  top: 30%;
  --move-x1: -30px;
  --move-y1: 20px;
  --move-x2: 20px;
  --move-y2: -25px;
  --move-x3: -15px;
  --move-y3: 30px;
  animation-delay: 3.3s;
}

.particle-13 {
  width: 2px;
  height: 2px;
  left: 40%;
  top: 85%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 30px;
  --move-x3: 20px;
  --move-y3: -15px;
  animation-delay: 3.6s;
}

.particle-14 {
  width: 3px;
  height: 3px;
  left: 60%;
  top: 15%;
  --move-x1: -25px;
  --move-y1: 30px;
  --move-x2: 15px;
  --move-y2: -20px;
  --move-x3: -30px;
  --move-y3: 25px;
  animation-delay: 3.9s;
}

.particle-15 {
  width: 2px;
  height: 2px;
  left: 20%;
  top: 40%;
  --move-x1: 30px;
  --move-y1: -15px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 15px;
  --move-y3: -30px;
  animation-delay: 4.2s;
}

.particle-16 {
  width: 4px;
  height: 4px;
  left: 80%;
  top: 55%;
  --move-x1: -20px;
  --move-y1: -25px;
  --move-x2: 30px;
  --move-y2: 15px;
  --move-x3: -25px;
  --move-y3: -20px;
  animation-delay: 4.5s;
}

.particle-17 {
  width: 2px;
  height: 2px;
  left: 50%;
  top: 90%;
  --move-x1: 25px;
  --move-y1: 20px;
  --move-x2: -15px;
  --move-y2: -30px;
  --move-x3: 30px;
  --move-y3: 25px;
  animation-delay: 4.8s;
}

.particle-18 {
  width: 3px;
  height: 3px;
  left: 30%;
  top: 5%;
  --move-x1: -15px;
  --move-y1: -25px;
  --move-x2: 25px;
  --move-y2: 20px;
  --move-x3: -20px;
  --move-y3: -15px;
  animation-delay: 5.1s;
}

.particle-19 {
  width: 2px;
  height: 2px;
  left: 75%;
  top: 35%;
  --move-x1: 20px;
  --move-y1: 30px;
  --move-x2: -30px;
  --move-y2: -15px;
  --move-x3: 25px;
  --move-y3: -25px;
  animation-delay: 5.4s;
}

.particle-20 {
  width: 3px;
  height: 3px;
  left: 12%;
  top: 65%;
  --move-x1: -30px;
  --move-y1: 15px;
  --move-x2: 20px;
  --move-y2: -25px;
  --move-x3: -15px;
  --move-y3: 30px;
  animation-delay: 5.7s;
}

.particle-21 {
  width: 2px;
  height: 2px;
  left: 88%;
  top: 65%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 30px;
  --move-x3: 20px;
  --move-y3: -15px;
  animation-delay: 6s;
}

.particle-22 {
  width: 3px;
  height: 3px;
  left: 48%;
  top: 45%;
  --move-x1: -25px;
  --move-y1: 25px;
  --move-x2: 15px;
  --move-y2: -20px;
  --move-x3: -30px;
  --move-y3: -15px;
  animation-delay: 6.3s;
}

.particle-23 {
  width: 2px;
  height: 2px;
  left: 22%;
  top: 25%;
  --move-x1: 30px;
  --move-y1: -15px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 15px;
  --move-y3: -30px;
  animation-delay: 6.6s;
}

.particle-24 {
  width: 4px;
  height: 4px;
  left: 72%;
  top: 90%;
  --move-x1: -20px;
  --move-y1: -25px;
  --move-x2: 30px;
  --move-y2: 15px;
  --move-x3: -25px;
  --move-y3: -20px;
  animation-delay: 6.9s;
}

.particle-25 {
  width: 2px;
  height: 2px;
  left: 8%;
  top: 35%;
  --move-x1: 25px;
  --move-y1: 20px;
  --move-x2: -15px;
  --move-y2: -30px;
  --move-x3: 30px;
  --move-y3: 25px;
  animation-delay: 7.2s;
}

.particle-26 {
  width: 3px;
  height: 3px;
  left: 92%;
  top: 55%;
  --move-x1: -15px;
  --move-y1: -25px;
  --move-x2: 25px;
  --move-y2: 20px;
  --move-x3: -20px;
  --move-y3: -15px;
  animation-delay: 7.5s;
}

.particle-27 {
  width: 2px;
  height: 2px;
  left: 42%;
  top: 75%;
  --move-x1: 20px;
  --move-y1: 30px;
  --move-x2: -30px;
  --move-y2: -15px;
  --move-x3: 25px;
  --move-y3: -25px;
  animation-delay: 7.8s;
}

.particle-28 {
  width: 3px;
  height: 3px;
  left: 58%;
  top: 25%;
  --move-x1: -30px;
  --move-y1: 15px;
  --move-x2: 20px;
  --move-y2: -25px;
  --move-x3: -15px;
  --move-y3: 30px;
  animation-delay: 8.1s;
}

.particle-29 {
  width: 2px;
  height: 2px;
  left: 32%;
  top: 55%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 30px;
  --move-x3: 20px;
  --move-y3: -15px;
  animation-delay: 8.4s;
}

.particle-30 {
  width: 4px;
  height: 4px;
  left: 68%;
  top: 40%;
  --move-x1: -25px;
  --move-y1: 25px;
  --move-x2: 15px;
  --move-y2: -20px;
  --move-x3: -30px;
  --move-y3: -15px;
  animation-delay: 8.7s;
}

.particle-31 {
  width: 2px;
  height: 2px;
  left: 3%;
  top: 12%;
  --move-x1: 20px;
  --move-y1: 15px;
  --move-x2: -15px;
  --move-y2: -25px;
  --move-x3: 25px;
  --move-y3: 20px;
  animation-delay: 9s;
}

.particle-32 {
  width: 3px;
  height: 3px;
  left: 97%;
  top: 88%;
  --move-x1: -20px;
  --move-y1: -15px;
  --move-x2: 25px;
  --move-y2: 20px;
  --move-x3: -15px;
  --move-y3: -25px;
  animation-delay: 9.3s;
}

.particle-33 {
  width: 2px;
  height: 2px;
  left: 18%;
  top: 62%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -25px;
  animation-delay: 9.6s;
}

.particle-34 {
  width: 3px;
  height: 3px;
  left: 82%;
  top: 28%;
  --move-x1: -15px;
  --move-y1: 25px;
  --move-x2: 20px;
  --move-y2: -15px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 9.9s;
}

.particle-35 {
  width: 2px;
  height: 2px;
  left: 38%;
  top: 92%;
  --move-x1: 25px;
  --move-y1: -15px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 15px;
  --move-y3: -20px;
  animation-delay: 10.2s;
}

.particle-36 {
  width: 3px;
  height: 3px;
  left: 62%;
  top: 8%;
  --move-x1: -25px;
  --move-y1: 20px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -20px;
  --move-y3: 15px;
  animation-delay: 10.5s;
}

.particle-37 {
  width: 2px;
  height: 2px;
  left: 7%;
  top: 48%;
  --move-x1: 20px;
  --move-y1: -25px;
  --move-x2: -15px;
  --move-y2: 20px;
  --move-x3: 25px;
  --move-y3: -15px;
  animation-delay: 10.8s;
}

.particle-38 {
  width: 3px;
  height: 3px;
  left: 93%;
  top: 52%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 25px;
  --move-y2: -20px;
  --move-x3: -15px;
  --move-y3: 25px;
  animation-delay: 11.1s;
}

.particle-39 {
  width: 2px;
  height: 2px;
  left: 28%;
  top: 18%;
  --move-x1: 15px;
  --move-y1: 25px;
  --move-x2: -20px;
  --move-y2: -15px;
  --move-x3: 25px;
  --move-y3: 20px;
  animation-delay: 11.4s;
}

.particle-40 {
  width: 4px;
  height: 4px;
  left: 72%;
  top: 82%;
  --move-x1: -25px;
  --move-y1: -20px;
  --move-x2: 20px;
  --move-y2: 25px;
  --move-x3: -15px;
  --move-y3: -25px;
  animation-delay: 11.7s;
}

.particle-41 {
  width: 2px;
  height: 2px;
  left: 13%;
  top: 72%;
  --move-x1: 25px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 25px;
  --move-x3: 20px;
  --move-y3: -20px;
  animation-delay: 12s;
}

.particle-42 {
  width: 3px;
  height: 3px;
  left: 87%;
  top: 28%;
  --move-x1: -15px;
  --move-y1: 20px;
  --move-x2: 25px;
  --move-y2: -15px;
  --move-x3: -20px;
  --move-y3: 25px;
  animation-delay: 12.3s;
}

.particle-43 {
  width: 2px;
  height: 2px;
  left: 33%;
  top: 42%;
  --move-x1: 20px;
  --move-y1: -25px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 15px;
  --move-y3: -20px;
  animation-delay: 12.6s;
}

.particle-44 {
  width: 3px;
  height: 3px;
  left: 67%;
  top: 58%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 12.9s;
}

.particle-45 {
  width: 2px;
  height: 2px;
  left: 2%;
  top: 82%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 25px;
  --move-y3: -15px;
  animation-delay: 13.2s;
}

.particle-46 {
  width: 3px;
  height: 3px;
  left: 98%;
  top: 18%;
  --move-x1: -25px;
  --move-y1: 25px;
  --move-x2: 20px;
  --move-y2: -20px;
  --move-x3: -15px;
  --move-y3: -25px;
  animation-delay: 13.5s;
}

.particle-47 {
  width: 2px;
  height: 2px;
  left: 23%;
  top: 32%;
  --move-x1: 20px;
  --move-y1: 15px;
  --move-x2: -15px;
  --move-y2: -25px;
  --move-x3: 25px;
  --move-y3: 20px;
  animation-delay: 13.8s;
}

.particle-48 {
  width: 4px;
  height: 4px;
  left: 77%;
  top: 68%;
  --move-x1: -15px;
  --move-y1: -20px;
  --move-x2: 25px;
  --move-y2: 15px;
  --move-x3: -20px;
  --move-y3: 25px;
  animation-delay: 14.1s;
}

.particle-49 {
  width: 2px;
  height: 2px;
  left: 43%;
  top: 78%;
  --move-x1: 25px;
  --move-y1: -15px;
  --move-x2: -20px;
  --move-y2: 20px;
  --move-x3: 15px;
  --move-y3: -25px;
  animation-delay: 14.4s;
}

.particle-50 {
  width: 3px;
  height: 3px;
  left: 57%;
  top: 22%;
  --move-x1: -20px;
  --move-y1: 25px;
  --move-x2: 15px;
  --move-y2: -15px;
  --move-x3: -25px;
  --move-y3: -20px;
  animation-delay: 14.7s;
}

.particle-51 {
  width: 2px;
  height: 2px;
  left: 8%;
  top: 58%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -25px;
  animation-delay: 15s;
}

.particle-52 {
  width: 3px;
  height: 3px;
  left: 92%;
  top: 42%;
  --move-x1: -25px;
  --move-y1: 15px;
  --move-x2: 20px;
  --move-y2: -25px;
  --move-x3: -15px;
  --move-y3: 20px;
  animation-delay: 15.3s;
}

.particle-53 {
  width: 2px;
  height: 2px;
  left: 28%;
  top: 88%;
  --move-x1: 20px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 25px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 15.6s;
}

.particle-54 {
  width: 4px;
  height: 4px;
  left: 72%;
  top: 12%;
  --move-x1: -15px;
  --move-y1: 20px;
  --move-x2: 25px;
  --move-y2: -20px;
  --move-x3: -20px;
  --move-y3: 15px;
  animation-delay: 15.9s;
}

.particle-55 {
  width: 2px;
  height: 2px;
  left: 38%;
  top: 52%;
  --move-x1: 25px;
  --move-y1: -25px;
  --move-x2: -20px;
  --move-y2: 15px;
  --move-x3: 15px;
  --move-y3: -20px;
  animation-delay: 16.2s;
}

.particle-56 {
  width: 3px;
  height: 3px;
  left: 62%;
  top: 48%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 16.5s;
}

.particle-57 {
  width: 2px;
  height: 2px;
  left: 5%;
  top: 25%;
  --move-x1: 15px;
  --move-y1: 20px;
  --move-x2: -25px;
  --move-y2: -15px;
  --move-x3: 20px;
  --move-y3: 25px;
  animation-delay: 16.8s;
}

.particle-58 {
  width: 3px;
  height: 3px;
  left: 95%;
  top: 75%;
  --move-x1: -25px;
  --move-y1: -20px;
  --move-x2: 20px;
  --move-y2: 25px;
  --move-x3: -15px;
  --move-y3: -25px;
  animation-delay: 17.1s;
}

.particle-59 {
  width: 2px;
  height: 2px;
  left: 33%;
  top: 15%;
  --move-x1: 20px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 20px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 17.4s;
}

.particle-60 {
  width: 4px;
  height: 4px;
  left: 67%;
  top: 85%;
  --move-x1: -15px;
  --move-y1: 25px;
  --move-x2: 25px;
  --move-y2: -15px;
  --move-x3: -20px;
  --move-y3: -25px;
  animation-delay: 17.7s;
}

.particle-61 {
  width: 2px;
  height: 2px;
  left: 18%;
  top: 38%;
  --move-x1: 25px;
  --move-y1: -20px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 15px;
  --move-y3: -15px;
  animation-delay: 18s;
}

.particle-62 {
  width: 3px;
  height: 3px;
  left: 82%;
  top: 62%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 18.3s;
}

.particle-63 {
  width: 2px;
  height: 2px;
  left: 48%;
  top: 95%;
  --move-x1: 15px;
  --move-y1: -25px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -20px;
  animation-delay: 18.6s;
}

.particle-64 {
  width: 3px;
  height: 3px;
  left: 52%;
  top: 5%;
  --move-x1: -25px;
  --move-y1: 20px;
  --move-x2: 20px;
  --move-y2: -15px;
  --move-x3: -15px;
  --move-y3: 25px;
  animation-delay: 18.9s;
}

.particle-65 {
  width: 2px;
  height: 2px;
  left: 12%;
  top: 68%;
  --move-x1: 20px;
  --move-y1: 15px;
  --move-x2: -15px;
  --move-y2: -25px;
  --move-x3: 25px;
  --move-y3: 20px;
  animation-delay: 19.2s;
}

.particle-66 {
  width: 4px;
  height: 4px;
  left: 88%;
  top: 32%;
  --move-x1: -15px;
  --move-y1: -20px;
  --move-x2: 25px;
  --move-y2: 15px;
  --move-x3: -20px;
  --move-y3: 25px;
  animation-delay: 19.5s;
}

.particle-67 {
  width: 2px;
  height: 2px;
  left: 28%;
  top: 72%;
  --move-x1: 25px;
  --move-y1: -15px;
  --move-x2: -20px;
  --move-y2: 20px;
  --move-x3: 15px;
  --move-y3: -25px;
  animation-delay: 19.8s;
}

.particle-68 {
  width: 3px;
  height: 3px;
  left: 72%;
  top: 28%;
  --move-x1: -20px;
  --move-y1: 25px;
  --move-x2: 15px;
  --move-y2: -15px;
  --move-x3: -25px;
  --move-y3: -20px;
  animation-delay: 20.1s;
}

.particle-69 {
  width: 2px;
  height: 2px;
  left: 42%;
  top: 58%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -25px;
  animation-delay: 20.4s;
}

.particle-70 {
  width: 3px;
  height: 3px;
  left: 58%;
  top: 42%;
  --move-x1: -25px;
  --move-y1: 15px;
  --move-x2: 20px;
  --move-y2: -25px;
  --move-x3: -15px;
  --move-y3: 20px;
  animation-delay: 20.7s;
}

.particle-71 {
  width: 2px;
  height: 2px;
  left: 7%;
  top: 85%;
  --move-x1: 20px;
  --move-y1: -25px;
  --move-x2: -15px;
  --move-y2: 20px;
  --move-x3: 25px;
  --move-y3: -15px;
  animation-delay: 21s;
}

.particle-72 {
  width: 4px;
  height: 4px;
  left: 93%;
  top: 15%;
  --move-x1: -15px;
  --move-y1: 20px;
  --move-x2: 25px;
  --move-y2: -20px;
  --move-x3: -20px;
  --move-y3: 25px;
  animation-delay: 21.3s;
}

.particle-73 {
  width: 2px;
  height: 2px;
  left: 23%;
  top: 45%;
  --move-x1: 25px;
  --move-y1: 15px;
  --move-x2: -20px;
  --move-y2: -15px;
  --move-x3: 15px;
  --move-y3: 25px;
  animation-delay: 21.6s;
}

.particle-74 {
  width: 3px;
  height: 3px;
  left: 77%;
  top: 55%;
  --move-x1: -20px;
  --move-y1: -25px;
  --move-x2: 15px;
  --move-y2: 20px;
  --move-x3: -25px;
  --move-y3: -15px;
  animation-delay: 21.9s;
}

.particle-75 {
  width: 2px;
  height: 2px;
  left: 37%;
  top: 25%;
  --move-x1: 15px;
  --move-y1: -20px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -25px;
  animation-delay: 22.2s;
}

.particle-76 {
  width: 3px;
  height: 3px;
  left: 63%;
  top: 75%;
  --move-x1: -25px;
  --move-y1: 25px;
  --move-x2: 20px;
  --move-y2: -15px;
  --move-x3: -15px;
  --move-y3: -20px;
  animation-delay: 22.5s;
}

.particle-77 {
  width: 2px;
  height: 2px;
  left: 17%;
  top: 92%;
  --move-x1: 20px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 25px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 22.8s;
}

.particle-78 {
  width: 4px;
  height: 4px;
  left: 83%;
  top: 8%;
  --move-x1: -15px;
  --move-y1: 20px;
  --move-x2: 25px;
  --move-y2: -20px;
  --move-x3: -20px;
  --move-y3: 15px;
  animation-delay: 23.1s;
}

.particle-79 {
  width: 2px;
  height: 2px;
  left: 47%;
  top: 35%;
  --move-x1: 25px;
  --move-y1: -25px;
  --move-x2: -20px;
  --move-y2: 15px;
  --move-x3: 15px;
  --move-y3: -20px;
  animation-delay: 23.4s;
}

.particle-80 {
  width: 3px;
  height: 3px;
  left: 53%;
  top: 65%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 23.7s;
}

.particle-81 {
  width: 2px;
  height: 2px;
  left: 3%;
  top: 55%;
  --move-x1: 15px;
  --move-y1: 20px;
  --move-x2: -25px;
  --move-y2: -15px;
  --move-x3: 20px;
  --move-y3: 25px;
  animation-delay: 24s;
}

.particle-82 {
  width: 3px;
  height: 3px;
  left: 97%;
  top: 45%;
  --move-x1: -25px;
  --move-y1: -20px;
  --move-x2: 20px;
  --move-y2: 25px;
  --move-x3: -15px;
  --move-y3: -25px;
  animation-delay: 24.3s;
}

.particle-83 {
  width: 2px;
  height: 2px;
  left: 32%;
  top: 82%;
  --move-x1: 20px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 20px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 24.6s;
}

.particle-84 {
  width: 4px;
  height: 4px;
  left: 68%;
  top: 18%;
  --move-x1: -15px;
  --move-y1: 25px;
  --move-x2: 25px;
  --move-y2: -15px;
  --move-x3: -20px;
  --move-y3: -25px;
  animation-delay: 24.9s;
}

.particle-85 {
  width: 2px;
  height: 2px;
  left: 43%;
  top: 62%;
  --move-x1: 25px;
  --move-y1: -20px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 15px;
  --move-y3: -15px;
  animation-delay: 25.2s;
}

.particle-86 {
  width: 3px;
  height: 3px;
  left: 57%;
  top: 38%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 25.5s;
}

.particle-87 {
  width: 2px;
  height: 2px;
  left: 13%;
  top: 28%;
  --move-x1: 15px;
  --move-y1: -25px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -20px;
  animation-delay: 25.8s;
}

.particle-88 {
  width: 3px;
  height: 3px;
  left: 87%;
  top: 72%;
  --move-x1: -25px;
  --move-y1: 25px;
  --move-x2: 20px;
  --move-y2: -15px;
  --move-x3: -15px;
  --move-y3: -20px;
  animation-delay: 26.1s;
}

.particle-89 {
  width: 2px;
  height: 2px;
  left: 27%;
  top: 48%;
  --move-x1: 20px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 25px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 26.4s;
}

.particle-90 {
  width: 4px;
  height: 4px;
  left: 73%;
  top: 52%;
  --move-x1: -15px;
  --move-y1: 20px;
  --move-x2: 25px;
  --move-y2: -20px;
  --move-x3: -20px;
  --move-y3: 25px;
  animation-delay: 26.7s;
}

.particle-91 {
  width: 2px;
  height: 2px;
  left: 38%;
  top: 88%;
  --move-x1: 25px;
  --move-y1: -25px;
  --move-x2: -20px;
  --move-y2: 15px;
  --move-x3: 15px;
  --move-y3: -20px;
  animation-delay: 27s;
}

.particle-92 {
  width: 3px;
  height: 3px;
  left: 62%;
  top: 12%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 27.3s;
}

.particle-93 {
  width: 2px;
  height: 2px;
  left: 8%;
  top: 65%;
  --move-x1: 15px;
  --move-y1: 20px;
  --move-x2: -25px;
  --move-y2: -15px;
  --move-x3: 20px;
  --move-y3: 25px;
  animation-delay: 27.6s;
}

.particle-94 {
  width: 3px;
  height: 3px;
  left: 92%;
  top: 35%;
  --move-x1: -25px;
  --move-y1: -20px;
  --move-x2: 20px;
  --move-y2: 25px;
  --move-x3: -15px;
  --move-y3: -25px;
  animation-delay: 27.9s;
}

.particle-95 {
  width: 2px;
  height: 2px;
  left: 33%;
  top: 72%;
  --move-x1: 20px;
  --move-y1: -15px;
  --move-x2: -15px;
  --move-y2: 20px;
  --move-x3: 25px;
  --move-y3: -20px;
  animation-delay: 28.2s;
}

.particle-96 {
  width: 4px;
  height: 4px;
  left: 67%;
  top: 28%;
  --move-x1: -15px;
  --move-y1: 25px;
  --move-x2: 25px;
  --move-y2: -15px;
  --move-x3: -20px;
  --move-y3: -25px;
  animation-delay: 28.5s;
}

.particle-97 {
  width: 2px;
  height: 2px;
  left: 48%;
  top: 58%;
  --move-x1: 25px;
  --move-y1: -20px;
  --move-x2: -20px;
  --move-y2: 25px;
  --move-x3: 15px;
  --move-y3: -15px;
  animation-delay: 28.8s;
}

.particle-98 {
  width: 3px;
  height: 3px;
  left: 52%;
  top: 42%;
  --move-x1: -20px;
  --move-y1: 15px;
  --move-x2: 15px;
  --move-y2: -25px;
  --move-x3: -25px;
  --move-y3: 20px;
  animation-delay: 29.1s;
}

.particle-99 {
  width: 2px;
  height: 2px;
  left: 18%;
  top: 85%;
  --move-x1: 15px;
  --move-y1: -25px;
  --move-x2: -25px;
  --move-y2: 15px;
  --move-x3: 20px;
  --move-y3: -20px;
  animation-delay: 29.4s;
}

.particle-100 {
  width: 3px;
  height: 3px;
  left: 82%;
  top: 15%;
  --move-x1: -25px;
  --move-y1: 25px;
  --move-x2: 20px;
  --move-y2: -15px;
  --move-x3: -15px;
  --move-y3: -20px;
  animation-delay: 29.7s;
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

/* 确保按钮可点击 */
.hero-buttons {
  position: relative;
  z-index: 2;
}

.hero-content h2 {
  font-size: 3.2rem;
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

.hero-buttons {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
  opacity: 0;
  animation: fadeInUp 1s ease 0.6s forwards;
}

/* 轮播样式 */
.hero-carousel {
  margin-top: 4rem;
  max-width: 100%;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  width: 100%;
  max-width: 900px;
  opacity: 0;
  animation: fadeInUp 1s ease 0.9s forwards;
}

.carousel-slides {
  display: flex;
  transition: transform 0.6s ease-in-out;
  width: 100%;
}

.carousel-slide {
  flex: 0 0 100%;
  width: 100%;
  position: relative;
}

.carousel-slide img {
  width: 100%;
  height: 450px;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.carousel-slide:hover img {
  transform: scale(1.02);
}

.carousel-indicators {
  position: absolute;
  bottom: 25px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
  background-color: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0.7;
}

.indicator.active {
  background-color: white;
  transform: scale(1.3);
  opacity: 1;
}

.indicator:hover {
  background-color: rgba(255, 255, 255, 0.5);
  opacity: 1;
}

/* 响应式轮播样式 */
@media (max-width: 768px) {
  .hero-carousel {
    max-width: 100%;
  }

  .carousel-indicators {
    bottom: 15px;
  }

  .indicator {
    width: 10px;
    height: 10px;
  }

  .carousel-slide img {
    height: 300px;
  }
}

/* 功能介绍样式 */
.features-section {
  padding: 6rem 0;
  background-color: #f8f9fa;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  text-align: center;
}

.features-container h3 {
  font-size: 2.5rem;
  margin-bottom: 4rem;
  color: #1B5E20;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2.5rem;
}

.feature-card {
  background-color: white;
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease;
}

.feature-card.visible {
  opacity: 1;
  transform: translateY(0);
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #2E7D32, #4CAF50);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-card::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.6s ease;
  pointer-events: none;
}

.feature-card:hover::after {
  width: 200px;
  height: 200px;
}

.feature-icon {
  margin-bottom: 2rem;
  position: relative;
}

.feature-icon img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  padding: 1.5rem;
  transition: all 0.3s ease;
  animation: iconFloat 4s ease-in-out infinite;
}

.feature-card:nth-child(2) .feature-icon img {
  animation-delay: 0.5s;
}

.feature-card:nth-child(3) .feature-icon img {
  animation-delay: 1s;
}

.feature-card:nth-child(4) .feature-icon img {
  animation-delay: 1.5s;
}

.feature-card:nth-child(5) .feature-icon img {
  animation-delay: 2s;
}

.feature-card:nth-child(6) .feature-icon img {
  animation-delay: 2.5s;
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

.feature-card:hover .feature-icon img {
  transform: scale(1.1);
  box-shadow: 0 10px 30px rgba(46, 125, 50, 0.2);
  animation: none;
}

.feature-card h4 {
  font-size: 1.5rem;
  margin-bottom: 1.2rem;
  color: #1B5E20;
  font-weight: 600;
}

.feature-card p {
  color: #666;
  line-height: 1.7;
  font-size: 1.05rem;
}

/* 为什么选择我们样式 */
.why-us-section {
  padding: 6rem 0;
  background-color: white;
}

.why-us-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  text-align: center;
}

.why-us-container h3 {
  font-size: 2.5rem;
  margin-bottom: 4rem;
  color: #1B5E20;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.why-us-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 3rem;
}

.why-us-item {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease;
}

.why-us-item.visible {
  opacity: 1;
  transform: translateY(0);
}

.why-us-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  animation: iconPulse 3s ease-in-out infinite;
}

.why-us-item:nth-child(2) .why-us-icon {
  animation-delay: 0.5s;
}

.why-us-item:nth-child(3) .why-us-icon {
  animation-delay: 1s;
}

.why-us-item:nth-child(4) .why-us-icon {
  animation-delay: 1.5s;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 4px 15px rgba(76, 175, 80, 0.2);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 8px 25px rgba(76, 175, 80, 0.3);
  }
}

.why-us-icon svg {
  width: 40px;
  height: 40px;
  color: #2E7D32;
  position: relative;
  z-index: 1;
}

.why-us-item:hover .why-us-icon {
  transform: scale(1.1);
  box-shadow: 0 12px 35px rgba(76, 175, 80, 0.4);
  animation: none;
}

.why-us-item h4 {
  font-size: 1.3rem;
  margin-bottom: 1.2rem;
  color: #1B5E20;
  font-weight: 600;
}

.why-us-item p {
  color: #666;
  line-height: 1.7;
  font-size: 1.05rem;
}

/* 行动召唤样式 */
.cta-section {
  padding: 6rem 0;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  color: #1B5E20;
  text-align: center;
  position: relative;
  overflow: hidden;
  animation: ctaGradient 10s ease infinite;
}

@keyframes ctaGradient {
  0%, 100% {
    background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  }
  50% {
    background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  }
}

.cta-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.05) 0%, transparent 70%);
  animation: ctaRotate 20s linear infinite;
  pointer-events: none;
}

@keyframes ctaRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.cta-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 60 60"><path d="M10 10L50 10L50 50L10 50Z" fill="none" stroke="rgba(46,125,50,0.1)" stroke-width="1"/></svg>') repeat;
  opacity: 0.5;
}

.cta-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
  position: relative;
  z-index: 1;
}

.cta-container h3 {
  font-size: 2.5rem;
  margin-bottom: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.cta-container p {
  font-size: 1.2rem;
  margin-bottom: 2.5rem;
  color: #388E3C;
  font-weight: 400;
}

/* 页脚样式 */
.footer {
  background-color: #1B5E20;
  color: white;
  padding: 4rem 0 2rem;
  position: relative;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #4CAF50, #81C784);
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
  color: white;
  font-weight: 700;
}

.footer-info p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.7;
  font-size: 1.05rem;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 1.05rem;
  position: relative;
  padding-left: 1.5rem;
}

.footer-links a::before {
  content: '→';
  position: absolute;
  left: 0;
  transition: transform 0.3s ease;
}

.footer-links a:hover {
  color: white;
  transform: translateX(5px);
}

.footer-links a:hover::before {
  transform: translateX(5px);
}

.footer-contact p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.7;
  font-size: 1.05rem;
}

/* 按钮样式 */
.btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(255,255,255,0.1), rgba(255,255,255,0));
  transform: translateX(-100%);
  transition: transform 0.5s ease;
  z-index: -1;
}

.btn:hover::before {
  transform: translateX(100%);
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
  animation: btnPulse 3s ease-in-out infinite;
}

@keyframes btnPulse {
  0%, 100% {
    box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
  }
  50% {
    box-shadow: 0 4px 20px rgba(76, 175, 80, 0.5);
  }
}

.btn-primary:hover {
  background-color: #388E3C;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.4);
  animation: none;
}

.btn-secondary {
  background-color: transparent;
  color: white;
  border: 2px solid white;
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
  animation: btnGlow 4s ease-in-out infinite;
}

@keyframes btnGlow {
  0%, 100% {
    border-color: rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
  }
  50% {
    border-color: rgba(255, 255, 255, 1);
    box-shadow: 0 4px 20px rgba(255, 255, 255, 0.4);
  }
}

.btn-secondary:hover {
  background-color: white;
  color: #2E7D32;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(255, 255, 255, 0.3);
  animation: none;
}

.btn-large {
  padding: 1.2rem 2.5rem;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 0.5px;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 1rem;
  }

  .navbar-logo h1 {
    font-size: 1.5rem;
  }

  .hero-section {
    padding: 4rem 0 3rem;
  }

  .hero-content h2 {
    font-size: 2.2rem;
  }

  .hero-content p {
    font-size: 1.1rem;
  }

  .hero-buttons {
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  .hero-buttons .btn {
    width: 100%;
    max-width: 300px;
    text-align: center;
  }

  .features-section,
  .why-us-section,
  .cta-section {
    padding: 4rem 0;
  }

  .features-container,
  .why-us-container,
  .cta-container {
    padding: 0 1rem;
  }

  .features-container h3,
  .why-us-container h3,
  .cta-container h3 {
    font-size: 2rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .feature-card {
    padding: 2rem;
  }

  .footer-container {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 2rem;
  }

  .footer-links {
    align-items: center;
  }

  .footer-links a {
    padding-left: 0;
  }

  .footer-links a::before {
    display: none;
  }

  .footer-links a:hover {
    transform: none;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #4CAF50;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #388E3C;
}
</style>