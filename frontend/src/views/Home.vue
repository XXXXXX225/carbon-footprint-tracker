<template>
  <div class="home-container" @mousemove="handleMouseMove">
    <div class="aurora-bg">
      <div class="parallax-layer layer-bg">
        <div class="aurora-blob blob-1"></div>
        <div class="aurora-blob blob-2"></div>
        <div class="aurora-blob blob-3"></div>
      </div>

      <div class="parallax-layer layer-grid">
        <div class="tech-grid-overlay"></div>
      </div>

      <div class="interactive-spotlight"></div>

      <div class="parallax-layer layer-motes">
        <div class="floating-motes">
          <i class="mote mote-1"></i>
          <i class="mote mote-2"></i>
          <i class="mote mote-3"></i>
          <i class="mote mote-4"></i>
          <i class="mote mote-5"></i>
          <i class="mote mote-6"></i>
        </div>
      </div>
    </div>
    <canvas ref="cursorTrailCanvas" class="cursor-trail-layer"></canvas>

    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-logo">
          <a href="/home" class="logo-link">
            <h1>碳足迹追踪平台</h1>
          </a>
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
            <a href="/login" class="nav-login-btn">登录 / 注册</a>
          </template>
        </div>
      </div>
    </nav>

    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <div class="system-status fade-up">
            <span class="pulse-dot"></span> 智能监控节点：在线
          </div>
          <div class="typewriter-container fade-up" style="animation-delay: 0.1s;">
            <div class="typewriter-content">
              <span class="typewriter-text">{{ currentTypewriterText }}</span><span class="cursor">|</span>
            </div>
          </div>
          
          <div class="live-data-screen fade-up" style="animation-delay: 0.2s;" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="data-label">平台累计追踪减排量 (kg CO₂e)</div>
            <div class="data-number" ref="counterEl">0.00</div>
          </div>

          <p class="hero-desc fade-up" style="animation-delay: 0.3s;">融合先进算法与生态模型，精准量化您的每一次低碳行动，让环保效益清晰可见。</p>
          
          <div class="hero-actions fade-up" style="animation-delay: 0.4s;">
              <a :href="user && user.id ? landingRoute : '/login'" class="eco-btn-primary magnetic-item" @mousemove="handleMagneticMove" @mouseleave="handleMagneticLeave">
                <el-icon style="margin-right: 8px;"><User /></el-icon>
                {{ user && user.id ? '立即开始' : '登录 / 注册' }}
              </a>
              <a href="/news" class="eco-btn-secondary magnetic-item" @mousemove="handleMagneticMove" @mouseleave="handleMagneticLeave">
                <el-icon style="margin-right: 8px;"><CollectionTag /></el-icon>
                了解更多
              </a>
            </div>
          </div>
        <div class="hero-visual fade-up" style="animation-delay: 0.5s;" :style="{ transform: carouselTransform }">
          <div class="hologram-frame">
            <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
              <div class="carousel-slide" v-for="(img, idx) in carouselImages" :key="idx">
                <div class="slide-overlay"></div>
                <img :src="img" alt="Eco Display" />
              </div>
            </div>
          </div>
          <div class="eco-indicators">
            <span v-for="(_, idx) in carouselImages" :key="idx"
                  class="eco-dot" :class="{ active: idx === currentSlide }"
                  @click="currentSlide = idx"></span>
          </div>
        </div>
      </div>
    </section>
    <section class="interactive-sandbox-section">
      <div style="max-width: 1200px; margin: 0 auto; padding: 0 2rem; width: 100%;">
        <h3 class="section-title">实时交互体验</h3>
      </div>
      <div class="sandbox-container">
        
        <div class="mini-calc-panel fade-in" data-aos="fade-up" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
          <h3 class="panel-title"><el-icon><DataLine /></el-icon> 实时排放模拟器</h3>
          
          <div class="slider-group">
            <label>今日私家车行驶里程 (公里)</label>
            <el-slider v-model="commuteKm" :max="100" :step="1" show-input />
          </div>
          
          <div class="slider-group">
            <label>今日家庭用电量 (度)</label>
            <el-slider v-model="electricityKwh" :max="50" :step="0.5" show-input />
          </div>

          <div class="calc-result-box">
            <div class="result-label">预计产生碳排放 (kg CO₂e)</div>
            <div class="result-number">{{ calculatedCarbon }}</div>
            <p class="result-feedback" :class="feedbackColor">{{ feedbackText }}</p>
          </div>
        </div>

        <div class="canvas-panel fade-in" data-aos="fade-up" data-aos-delay="100">
          <div ref="threeContainer" class="three-earth-container"></div>
          <div class="canvas-overlay-text">全球监控节点 3D 实时拓扑</div>
        </div>

      </div>
    </section>

    <section class="features-section">
      <div class="features-container">
        <h3 class="section-title">核心功能</h3>
        <div class="features-grid">
          <div class="feature-card reveal-blur" style="--delay: 0s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="15" rx="2" ry="2"></rect><polyline points="17 2 12 7 7 2"></polyline></svg>
            </div>
            <h4>交通排放追踪</h4>
            <p>记录您的日常出行，计算并分析交通方式对环境的长期影响与减排潜力。</p>
          </div>
          <div class="feature-card reveal-blur" style="--delay: 0.15s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8h1a4 4 0 0 1 0 8h-1"></path><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path><line x1="6" y1="1" x2="6" y2="4"></line><line x1="10" y1="1" x2="10" y2="4"></line><line x1="14" y1="1" x2="14" y2="4"></line></svg>
            </div>
            <h4>饮食排放分析</h4>
            <p>深入了解您的饮食习惯产生的碳足迹，获取量身定制的健康环保饮食建议。</p>
          </div>
          <div class="feature-card reveal-blur" style="--delay: 0.3s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon></svg>
            </div>
            <h4>用电排放监测</h4>
            <p>全天候追踪您的家庭用电量，智能分析能源使用效率，发掘潜在的节能机会。</p>
          </div>
          <div class="feature-card reveal-blur" style="--delay: 0.45s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
            </div>
            <h4>碳行动计划</h4>
            <p>协助您设定个人减排目标，动态跟踪进展，生成真正可执行的低碳生活方案。</p>
          </div>
          <div class="feature-card reveal-blur" style="--delay: 0.6s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"></path><path d="M22 12A10 10 0 0 0 12 2v10z"></path></svg>
            </div>
            <h4>排放报告生成</h4>
            <p>一键生成专业详尽的碳排放评估报告，清晰展示您的环保成就与社会价值。</p>
          </div>
          <div class="feature-card reveal-blur" style="--delay: 0.75s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="feature-icon">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=carbon%20action%20plan%20recommendations%20icon%2C%20clean%20modern%20design%2C%20minimalist%2C%20professional&image_size=square" alt="行动方案" />
            </div>
            <h4>AI 行动方案</h4>
            <p>基于大数据与AI模型深度解析您的排放数据，智能推送下一步的精准减排任务。</p>
          </div>
        </div>
      </div>
    </section>

    <section class="why-us-section">
      <div class="why-us-container">
        <h3 class="section-title">为什么选择我们的平台</h3>
        <div class="why-us-grid">
          <div class="why-us-item reveal-blur" style="--delay: 0.1s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
            </div>
            <h4>数据驱动</h4>
            <p>基于权威科学计算模型，提供最精确、可追溯的碳排放数据体系与深度图表分析。</p>
          </div>
          <div class="why-us-item reveal-blur" style="--delay: 0.25s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
            </div>
            <h4>用户友好</h4>
            <p>极简直观的玻璃态 UI 界面设计，剔除复杂概念，让碳排放追踪变得轻松且赏心悦目。</p>
          </div>
          <div class="why-us-item reveal-blur" style="--delay: 0.4s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
            </div>
            <h4>个性化服务</h4>
            <p>算法将根据您独有的生活习惯与出行规律，动态调整并生成专属您的减排挑战计划。</p>
          </div>
          <div class="why-us-item reveal-blur" style="--delay: 0.55s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
            </div>
            <h4>绝对安全</h4>
            <p>采用企业级加密协议，承诺绝不滥用隐私，全面保障您的个人碳排放数据资产安全。</p>
          </div>
          <div class="why-us-item reveal-blur" style="--delay: 0.7s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            </div>
            <h4>生态互联</h4>
            <p>链接全球绿色环保达人，在社区中分享心得与成就，共同构建可持续的零碳生态圈。</p>
          </div>
          <div class="why-us-item reveal-blur" style="--delay: 0.85s" @mousemove="handleCardMouseMove" @mouseleave="handleCardMouseLeave">
            <div class="why-us-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"></polyline><polyline points="1 20 1 14 7 14"></polyline><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path></svg>
            </div>
            <h4>持续演进</h4>
            <p>平台排放因子库与算法模型保持高频更新迭代，始终为您提供最前沿的环保体验。</p>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="cta-container fade-in" data-aos="fade-up">
        <h3>立即开始您的环保之旅</h3>
        <p>加入我们的平台，一起为地球的可持续未来努力。</p>
        <a href="/login" class="eco-btn-primary magnetic-item" @mousemove="handleMagneticMove" @mouseleave="handleMagneticLeave">开始注册</a>
      </div>
    </section>

    <footer class="footer">
      <div class="footer-container">
        <div class="footer-info">
          <h4>碳足迹追踪平台</h4>
          <p>致力于帮助个人和企业了解并减少碳排放，为环保事业贡献力量。</p>
        </div>
        <div class="footer-links">
          <a href="/">首页</a>
          <a href="/dashboard">仪表盘</a>
          <a v-if="user && (user.role === 'ENTERPRISE' || user.role === 'ADMIN')" href="/dashboard-screen">运营视图</a>
          <a v-if="user && user.role === 'ADMIN'" href="/admin">管理员后台</a>
          <a href="/action-plan">碳行动计划</a>
          <a href="/login">登录/注册</a>
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
import slide1 from '../assets/slide1.jpg'
import slide2 from '../assets/slide2.jpg'
import slide3 from '../assets/slide3.jpg'
import { CountUp } from 'countup.js'
import * as THREE from 'three'
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useCarbonStore } from '../store'
import { UserFilled, User, House, CollectionTag, SwitchButton, ArrowDown, Setting, DataLine } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getLandingRoute, getTopNavItems } from '../utils/access'

const carbonStore = useCarbonStore()

const user = computed(() => carbonStore.user)
const landingRoute = computed(() => getLandingRoute(user.value.role))
const topNavItems = computed(() => getTopNavItems(user.value.role))

const iconMap: Record<string, any> = {
  dashboard: House,
  'dashboard-screen': DataLine,
  profile: User,
  points: CollectionTag,
  admin: Setting
}

// === 2. 迷你碳计算器逻辑 ===
const commuteKm = ref(15)
const electricityKwh = ref(8)

const calculatedCarbon = computed(() => {
  const total = (commuteKm.value * 0.22) + (electricityKwh.value * 0.58)
  return total.toFixed(2)
})

const feedbackText = computed(() => {
  const total = parseFloat(calculatedCarbon.value)
  if (total < 5) return '表现极佳！处于低碳生活区间'
  if (total < 15) return '常规水平，还有优化空间'
  return '排放较高，建议采用公共交通'
})

const feedbackColor = computed(() => {
  const total = parseFloat(calculatedCarbon.value)
  return total < 5 ? 'text-green' : (total < 15 ? 'text-yellow' : 'text-red')
})

// ==========================================
// 1. 真正的"数字粒子/火花"鼠标拖尾 (Canvas)
// ==========================================
const cursorTrailCanvas = ref<HTMLCanvasElement | null>(null)
let trailCtx: CanvasRenderingContext2D | null = null
let cursorParticles: any[] = []

const initCursorTrail = () => {
  const canvas = cursorTrailCanvas.value
  if (!canvas) return
  trailCtx = canvas.getContext('2d')
  
  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  window.addEventListener('resize', resize)
  resize()
  animateCursorTrail()
}

const emitParticles = (x: number, y: number) => {
  for (let i = 0; i < 3; i++) {
    cursorParticles.push({
      x: x, y: y,
      vx: (Math.random() - 0.5) * 2,
      vy: (Math.random() - 0.5) * 2,
      life: 1,
      size: Math.random() * 3 + 1
    })
  }
}

window.addEventListener('mousemove', (e) => {
  emitParticles(e.clientX, e.clientY)
})

const animateCursorTrail = () => {
  if (!trailCtx || !cursorTrailCanvas.value) return
  const canvas = cursorTrailCanvas.value
  
  trailCtx.clearRect(0, 0, canvas.width, canvas.height)
  
  for (let i = cursorParticles.length - 1; i >= 0; i--) {
    const p = cursorParticles[i]
    p.x += p.vx
    p.y += p.vy
    p.life -= 0.02
    
    if (p.life <= 0) {
      cursorParticles.splice(i, 1)
    } else {
      trailCtx.beginPath()
      trailCtx.arc(p.x, p.y, p.size, 0, Math.PI * 2)
      trailCtx.fillStyle = `rgba(16, 185, 129, ${p.life})`
      trailCtx.fill()
    }
  }
  requestAnimationFrame(animateCursorTrail)
}


// ==========================================
// 2. Three.js 低多边形 (Low-Poly) 地球与光柱
// ==========================================
const threeContainer = ref<HTMLElement | null>(null)
let scene: THREE.Scene, camera: THREE.PerspectiveCamera, renderer: THREE.WebGLRenderer
let earthMesh: THREE.Mesh
let animationId: number

let isDragging = false
let previousMouseX = 0
let previousMouseY = 0
const autoRotationSpeed = 0.003
let activeDataNodes: any[] = []

const initThreeEarth = () => {
  if (!threeContainer.value) return
  const container = threeContainer.value
  const width = container.clientWidth
  const height = container.clientHeight || 400

  scene = new THREE.Scene()
  camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000)
  camera.position.z = 12
  
  renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(window.devicePixelRatio)
  container.innerHTML = ''
  container.appendChild(renderer.domElement)

  const earthGeo = new THREE.IcosahedronGeometry(4, 2)
  const earthMat = new THREE.MeshBasicMaterial({ 
    color: 0x10b981,         
    wireframe: true,
    transparent: true,
    opacity: 0.15,           
    blending: THREE.AdditiveBlending 
  })
  earthMesh = new THREE.Mesh(earthGeo, earthMat)
  scene.add(earthMesh)

  const solidMat = new THREE.MeshBasicMaterial({ 
    color: 0x064e3b, 
    transparent: true,
    opacity: 0.05,           
    side: THREE.FrontSide    
  })
  const solidEarth = new THREE.Mesh(new THREE.IcosahedronGeometry(3.95, 2), solidMat)
  earthMesh.add(solidEarth)

  const cloudMat = new THREE.PointsMaterial({
    color: 0x10b981,
    size: 0.02,
    transparent: true,
    opacity: 0.2,
    blending: THREE.AdditiveBlending
  })
  const cloudGeo = new THREE.BufferGeometry()
  const cloudPositions = []
  for (let i = 0; i < 200; i++) {
    const phi = Math.random() * Math.PI * 2
    const theta = Math.random() * Math.PI
    const r = 4.5 + Math.random() * 0.3
    cloudPositions.push(
      r * Math.sin(theta) * Math.cos(phi),
      r * Math.cos(theta),
      r * Math.sin(theta) * Math.sin(phi)
    )
  }
  cloudGeo.setAttribute('position', new THREE.Float32BufferAttribute(cloudPositions, 3))
  const dataCloud = new THREE.Points(cloudGeo, cloudMat)
  earthMesh.add(dataCloud)

  const spawnDynamicNode = () => {
    const lat = (Math.random() - 0.5) * 160
    const lon = (Math.random() - 0.5) * 360
    
    const phi = (90 - lat) * (Math.PI / 180)
    const theta = (lon + 180) * (Math.PI / 180)
    const r = 4 
    
    const x = -(r * Math.sin(phi) * Math.cos(theta))
    const z = (r * Math.sin(phi) * Math.sin(theta))
    const y = (r * Math.cos(phi))

    const targetHeight = Math.random() * 2.0 + 0.5
    const lifespan = Math.floor(Math.random() * 120 + 80) 

    const pillarGeo = new THREE.CylinderGeometry(0.04, 0.04, targetHeight, 8)
    pillarGeo.translate(0, targetHeight / 2, 0)
    
    const pillarMat = new THREE.MeshBasicMaterial({ 
      color: 0x34d399,         
      transparent: true, 
      opacity: 0,
      blending: THREE.AdditiveBlending 
    })
    
    const pillar = new THREE.Mesh(pillarGeo, pillarMat)
    pillar.position.set(x, y, z)
    pillar.lookAt(0, 0, 0)
    pillar.rotateX(Math.PI / 2)
    
    earthMesh.add(pillar)

    activeDataNodes.push({
      mesh: pillar,
      material: pillarMat,
      geometry: pillarGeo,
      age: 0,
      lifespan: lifespan
    })
  }

  const addDataArc = () => {
    const getRandomPoint = (radius: number) => {
      const phi = Math.random() * Math.PI * 2
      const theta = Math.random() * Math.PI
      return new THREE.Vector3(
        radius * Math.sin(theta) * Math.cos(phi),
        radius * Math.cos(theta),
        radius * Math.sin(theta) * Math.sin(phi)
      )
    }

    const start = getRandomPoint(4)
    const end = getRandomPoint(4)

    const mid = new THREE.Vector3().addVectors(start, end).multiplyScalar(0.5)
    const midLen = mid.length()
    mid.setLength(midLen * 2)

    const curve = new THREE.QuadraticBezierCurve3(start, mid, end)
    const points = curve.getPoints(50)
    const geometry = new THREE.BufferGeometry().setFromPoints(points)

    const material = new THREE.LineBasicMaterial({
      color: 0x34d399,
      transparent: true,
      opacity: 0.2,
      blending: THREE.AdditiveBlending
    })

    const line = new THREE.Line(geometry, material)
    earthMesh.add(line)

    const lightGeo = new THREE.SphereGeometry(0.02, 8, 8)
    const lightMat = new THREE.MeshBasicMaterial({ color: 0xffffff, blending: THREE.AdditiveBlending })
    const light = new THREE.Mesh(lightGeo, lightMat)
    earthMesh.add(light)

    activeDataNodes.push({
      line,
      light,
      curve,
      progress: 0,
      speed: 0.005 + Math.random() * 0.01,
      age: 0,
      lifespan: 200
    })
  }

  const onMouseDown = (e: MouseEvent) => {
    isDragging = true
    previousMouseX = e.clientX
    previousMouseY = e.clientY
  }

  const onMouseMove = (e: MouseEvent) => {
    if (!isDragging || !earthMesh) return
    const deltaX = e.clientX - previousMouseX
    const deltaY = e.clientY - previousMouseY
    earthMesh.rotation.y += deltaX * 0.005
    earthMesh.rotation.x += deltaY * 0.005
    previousMouseX = e.clientX
    previousMouseY = e.clientY
  }

  const onMouseUp = () => {
    isDragging = false
  }

  container.addEventListener('mousedown', onMouseDown)
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)

  const animate = () => {
    animationId = requestAnimationFrame(animate)

    if (!isDragging) {
      earthMesh.rotation.y += autoRotationSpeed
    }

    if (Math.random() < 0.04) {
      spawnDynamicNode()
    }

    if (Math.random() < 0.01) {
      addDataArc()
    }

    for (let i = activeDataNodes.length - 1; i >= 0; i--) {
      const node = activeDataNodes[i]
      node.age++

      if (node.curve) {
        node.progress += node.speed
        if (node.progress >= 1) {
          earthMesh.remove(node.line)
          earthMesh.remove(node.light)
          node.line.geometry.dispose()
          node.line.material.dispose()
          node.light.geometry.dispose()
          node.light.material.dispose()
          activeDataNodes.splice(i, 1)
        } else {
          const pos = node.curve.getPoint(node.progress)
          node.light.position.copy(pos)
          node.light.material.opacity = Math.sin(node.progress * Math.PI)
        }
      } else {
        const progress = node.age / node.lifespan

        if (progress >= 1) {
          earthMesh.remove(node.mesh)
          node.geometry.dispose()
          node.material.dispose()
          activeDataNodes.splice(i, 1)
        } else {
          const wave = Math.sin(progress * Math.PI)

          node.mesh.scale.y = wave
          node.material.opacity = wave * 0.9
        }
      }
    }

    renderer.render(scene, camera)
  }
  
  animate()

  window.addEventListener('resize', () => {
    if (!threeContainer.value) return
    const w = threeContainer.value.clientWidth
    const h = threeContainer.value.clientHeight || 400
    camera.aspect = w / h
    camera.updateProjectionMatrix()
    renderer.setSize(w, h)
  })
}

// === 1. 打字机逻辑 ===
const scrollingTexts = [
  '全球碳足迹追踪引擎',
  '精确量化低碳环保行动',
  'AI驱动智能减排预测',
  '构建个人零碳全新生活',
  '赋能企业ESG数字化转型',
  '共同守护地球生态资产'
]
const currentTextIndex = ref(0)
const currentTypewriterText = ref('')
let typeInterval: any

const startTypewriter = () => {
  let index = 0
  let isDeleting = false
  
  const type = () => {
    const currentFullText = scrollingTexts[currentTextIndex.value]
    if (isDeleting) {
      currentTypewriterText.value = currentFullText.substring(0, index - 1)
      index--
    } else {
      currentTypewriterText.value = currentFullText.substring(0, index + 1)
      index++
    }
    
    let typeSpeed = isDeleting ? 50 : 100
    if (!isDeleting && index === currentFullText.length) {
      typeSpeed = 2000
      isDeleting = true
    } else if (isDeleting && index === 0) {
      isDeleting = false
      currentTextIndex.value = (currentTextIndex.value + 1) % scrollingTexts.length
      typeSpeed = 500
    }
    typeInterval = setTimeout(type, typeSpeed)
  }
  typeInterval = setTimeout(type, 500)
}

// === 2. 动态数字跳动逻辑 ===
const currentTotal = ref(125848.29)
const counterEl = ref<HTMLElement | null>(null)
let countUpInstance: any = null
let updateInterval: number | null = null

const initCountUp = () => {
  if (counterEl.value) {
    countUpInstance = new CountUp(counterEl.value, currentTotal.value, {
      startVal: 125000.00,
      decimalPlaces: 2,
      duration: 3.5,
      useEasing: true,
    })
    
    if (!countUpInstance.error) {
      countUpInstance.start(() => {
        updateInterval = window.setInterval(() => {
          const randomIncrement = Math.random() * 2.4 + 0.1;
          currentTotal.value += randomIncrement;
          countUpInstance.update(currentTotal.value);
        }, 3000)
      })
    }
  }
}

// === 3. 高级鼠标移动交互 (全局坐标) ===
const mouseX = ref(0)
const mouseY = ref(0)

const handleMouseMove = (event: MouseEvent) => {
  mouseX.value = event.clientX
  mouseY.value = event.clientY
  const normalizedX = (event.clientX / window.innerWidth) * 2 - 1
  const normalizedY = (event.clientY / window.innerHeight) * 2 - 1
  
  document.documentElement.style.setProperty('--mouse-x', `${event.clientX}px`)
  document.documentElement.style.setProperty('--mouse-y', `${event.clientY}px`)
  document.documentElement.style.setProperty('--mouse-norm-x', normalizedX.toString())
  document.documentElement.style.setProperty('--mouse-norm-y', normalizedY.toString())
}

const handleScroll = () => {
  document.documentElement.style.setProperty('--scroll-y', window.scrollY.toString())
}

// === 4. 高级卡片 3D 倾斜 & 动态高光追踪 ===
const handleCardMouseMove = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const rect = card.getBoundingClientRect()
  
  const x = e.clientX - rect.left 
  const y = e.clientY - rect.top  
  
  const centerX = rect.width / 2
  const centerY = rect.height / 2
  
  const rotateX = ((y - centerY) / centerY) * -8 
  const rotateY = ((x - centerX) / centerX) * 8
  
  card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-10px) scale(1.02)`
  card.style.setProperty('--mouse-local-x', `${x}px`)
  card.style.setProperty('--mouse-local-y', `${y}px`)
}

const handleCardMouseLeave = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  card.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg) translateY(0) scale(1)`
}

// === 5. 按钮磁力吸附动画 ===
const handleMagneticMove = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  const rect = el.getBoundingClientRect()
  const x = e.clientX - rect.left - rect.width / 2
  const y = e.clientY - rect.top - rect.height / 2
  el.style.transform = `translate(${x * 0.25}px, ${y * 0.25}px)`
}

const handleMagneticLeave = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  el.style.transform = `translate(0px, 0px)`
}

// === 6. 3D 轮播图 ===
const carouselTransform = computed(() => {
  if (typeof window === 'undefined') return ''
  const x = (mouseX.value / window.innerWidth - 0.5) * 10
  const y = (mouseY.value / window.innerHeight - 0.5) * -10
  return `perspective(1200px) rotateY(${x}deg) rotateX(${y}deg) translateY(-10px) scale(1.02)`
})

const currentSlide = ref(0)
const carouselImages = [ slide1, slide2, slide3 ]
let carouselInterval: number | null = null

const startCarousel = () => {
  carouselInterval = window.setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % carouselImages.length
  }, 5000)
}

// === 7. 彻底解决假死：通过 window.location.href 强制浏览器跳转刷新 ===
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'dashboard': window.location.href = '/dashboard'; break
    case 'dashboard-screen': window.location.href = '/dashboard-screen'; break
    case 'profile': window.location.href = '/profile'; break
    case 'points': window.location.href = '/points'; break
    case 'admin': window.location.href = '/admin'; break
    case 'logout': handleLogout(); break
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  carbonStore.user = { id: '', name: '' }
  ElMessage.success('已退出登录')
  // 退出后自动刷新跳转回首页
  setTimeout(() => { window.location.href = '/home' }, 500)
}

// === 生命周期钩子 ===
onMounted(() => {
  carbonStore.loadUserFromLocalStorage()
  initCountUp()
  startTypewriter()
  startCarousel()
  initCursorTrail()
  initThreeEarth()
  window.addEventListener('scroll', handleScroll)

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible')
      }
    })
  }, { 
    threshold: 0.1, 
    rootMargin: "0px 0px -50px 0px" 
  })

  document.querySelectorAll('.reveal-blur').forEach(el => observer.observe(el))
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  if (renderer) renderer.dispose()
  if (carouselInterval) clearInterval(carouselInterval)
  if (typeInterval) clearTimeout(typeInterval)
  if (updateInterval) clearInterval(updateInterval)
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* =========================================================================
   1. 字体引入
========================================================================== */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&family=Space+Grotesk:wght@700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Noto Sans SC', sans-serif;
  line-height: 1.6;
}

/* =========================================================================
   2. 全局环境与动态光效
========================================================================== */
.home-container {
  position: relative;
  overflow-x: hidden;
  background-color: #e8f5e9;
  color: #064E3B;
  min-height: 100vh;
  width: 100%;
  display: block; 
}

.cursor-trail-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9999;
}

.three-earth-container {
  width: 100%;
  height: 100%;
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(circle at center, rgba(16, 185, 129, 0.1) 0%, transparent 70%);
  cursor: grab;
  box-shadow: inset 0 0 50px rgba(16, 185, 129, 0.1);
  border-radius: 50%;
  mix-blend-mode: plus-lighter;
}

.three-earth-container:active {
  cursor: grabbing;
}

.interactive-sandbox-section {
  width: 100%; padding: 4rem 0; z-index: 10; position: relative;
}

.sandbox-container {
  width: 100%; max-width: 1200px; margin: 0 auto; padding: 0 2rem;
  display: grid; grid-template-columns: 1fr 1.2fr; gap: 3rem; align-items: stretch;
}

.mini-calc-panel {
  background: rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(20px) saturate(200%);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px; padding: 2.5rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05);
}

.panel-title { font-size: 1.4rem; color: #064E3B; margin-bottom: 2rem; display: flex; align-items: center; gap: 8px;}
.slider-group { margin-bottom: 2rem; }
.slider-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #047857; font-size: 0.95rem; }

.calc-result-box {
  background: rgba(16, 185, 129, 0.1); border-radius: 16px; padding: 1.5rem; text-align: center; margin-top: 2rem;
  border: 1px solid rgba(16, 185, 129, 0.2);
}
.result-label { font-size: 0.9rem; color: #064E3B; }
.result-number { font-family: 'Space Grotesk', sans-serif; font-size: 3rem; font-weight: 800; color: #10B981; line-height: 1.2;}
.result-feedback { font-size: 0.9rem; margin-top: 4px; font-weight: 500;}
.text-green { color: #10B981; } .text-yellow { color: #F59E0B; } .text-red { color: #EF4444; }

.canvas-panel {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(8px);
  border-radius: 24px; position: relative; overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.3);
  min-height: 400px;
}
.data-particle-network { width: 100%; height: 100%; display: block; }
.canvas-overlay-text {
  position: absolute; bottom: 20px; left: 24px; color: rgba(6, 78, 59, 0.6); font-weight: 600; font-size: 0.9rem;
  letter-spacing: 1px; pointer-events: none;
}

@media (max-width: 992px) {
  .sandbox-container { grid-template-columns: 1fr; }
  .canvas-panel { min-height: 300px; }
}

.aurora-bg {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background-color: #f0fdf4;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

.parallax-layer {
  position: absolute;
  top: -10%; left: -10%;
  width: 120%; height: 120%;
  will-change: transform;
  transition: transform 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

.layer-bg {
  transform: translate3d(
    calc(var(--mouse-norm-x, 0) * 30px),
    calc(var(--mouse-norm-y, 0) * 30px - var(--scroll-y, 0) * 0.15px),
    0
  );
}

.layer-grid {
  transform: translate3d(
    calc(var(--mouse-norm-x, 0) * -15px),
    calc(var(--mouse-norm-y, 0) * -15px - var(--scroll-y, 0) * 0.05px),
    0
  );
}

.layer-motes {
  transform: translate3d(
    calc(var(--mouse-norm-x, 0) * 10px),
    calc(var(--mouse-norm-y, 0) * 10px - var(--scroll-y, 0) * 0.35px),
    0
  );
}

.interactive-spotlight {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(
    circle 800px at var(--mouse-x, 50vw) var(--mouse-y, 50vh),
    rgba(255, 255, 255, 0.6),
    transparent 70%
  );
  mix-blend-mode: overlay;
  z-index: 2;
}

.aurora-blob { position: absolute; filter: blur(140px); opacity: 0.6; border-radius: 50%; animation: floatBlob 25s infinite alternate ease-in-out;}
.blob-1 { width: 70vw; height: 70vh; background: #34d399; top: -10%; left: -5%; }
.blob-2 { width: 60vw; height: 60vh; background: #0ea5e9; bottom: -10%; right: -5%; animation-delay: -5s; }
.blob-3 { width: 50vw; height: 50vh; background: #10b981; top: 30%; left: 30%; animation-delay: -10s; }
@keyframes floatBlob {
  0% { transform: scale(1) rotate(0deg); }
  33% { transform: scale(1.1) rotate(10deg); }
  66% { transform: scale(0.9) rotate(-10deg); }
  100% { transform: scale(1.05) rotate(5deg); }
}

.tech-grid-overlay { width: 100%; height: 100%; background-image: radial-gradient(rgba(6, 78, 59, 0.1) 1px, transparent 1px); background-size: 24px 24px; }

.floating-motes { width: 100%; height: 100%; position: relative;}
.mote { position: absolute; width: 4px; height: 4px; background: #10B981; border-radius: 50%; box-shadow: 0 0 12px 2px rgba(16, 185, 129, 0.8); opacity: 0; animation: floatUp infinite linear; }
.mote-1 { left: 15%; bottom: -5%; animation-duration: 15s; }
.mote-2 { left: 45%; bottom: -5%; animation-duration: 22s; animation-delay: 4s; width: 6px; height: 6px;}
.mote-3 { left: 75%; bottom: -5%; animation-duration: 18s; animation-delay: 2s; }
.mote-4 { left: 85%; bottom: -5%; animation-duration: 25s; animation-delay: 8s; width: 3px; height: 3px;}
.mote-5 { left: 30%; bottom: -5%; animation-duration: 20s; animation-delay: 6s; }
.mote-6 { left: 60%; bottom: -5%; animation-duration: 16s; animation-delay: 10s; width: 5px; height: 5px;}
@keyframes floatUp { 0% { transform: translateY(0) scale(0.5); opacity: 0; } 20% { opacity: 0.6; } 80% { opacity: 0.6; } 100% { transform: translateY(-100vh) scale(1.2); opacity: 0; } }

/* =========================================================================
   3. 布局骨架强制对齐 (完全修复导航栏未居中的问题)
========================================================================== */
.hero-section, .features-section, .why-us-section, .cta-section {
  width: 100%;
  padding: 6rem 0;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10; 
}

/* 所有大容器共享这个绝对居中的 max-width */
.navbar-container, /* 修复点：加入 navbar-container */
.hero-container, 
.features-container, 
.why-us-container, 
.cta-container, 
.footer-container {
  width: 100% !important;
  max-width: 1200px !important;
  margin: 0 auto !important;
  padding: 0 2rem !important;
}

.hero-container {
  display: grid !important;
  grid-template-columns: 1.1fr 0.9fr !important;
  gap: 4rem;
  align-items: center;
}

.hero-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start; 
  text-align: left;
}

.section-title {
  font-size: 2.8rem; 
  color: #064E3B; 
  font-weight: 800; 
  margin-bottom: 4rem;
  text-align: center !important; 
  width: 100%;
  display: block;
}

.features-grid, .why-us-grid {
  width: 100%;
  display: grid; 
  grid-template-columns: repeat(3, 1fr) !important; 
  gap: 2.5rem;
  align-items: stretch; 
}

/* =========================================================================
   4. 极致透明玻璃态与高级特效
========================================================================== */
.navbar, .feature-card, .live-data-screen, .why-us-item, .system-status {
  background: rgba(255, 255, 255, 0.25) !important; 
  backdrop-filter: blur(16px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(16px) saturate(180%) !important;
  border: 1px solid rgba(255, 255, 255, 0.7) !important;
  border-right-color: rgba(255, 255, 255, 0.2) !important;
  border-bottom-color: rgba(255, 255, 255, 0.2) !important;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.05) !important;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  color: #064E3B; 
  transform-style: preserve-3d;
  will-change: transform, box-shadow;
  transition: transform 0.4s cubic-bezier(0.23, 1, 0.32, 1), box-shadow 0.4s ease, background 0.4s ease;
}

.feature-card::after, .why-us-item::after, .live-data-screen::after {
  content: '';
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(circle 400px at var(--mouse-local-x, 50%) var(--mouse-local-y, 50%), rgba(255,255,255,0.5), transparent 40%);
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
  z-index: 5;
}

.feature-card:hover::after, .why-us-item:hover::after, .live-data-screen:hover::after {
  opacity: 1;
}

/* =========================================================================
   5. 【核心升级】高定版按钮组设计
========================================================================== */

/* --- 导航栏水晶胶囊按钮 --- */
.nav-login-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 28px;
  font-size: 1rem;
  font-weight: 700;
  color: #064E3B;
  background: rgba(255, 255, 255, 0.3); /* 磨砂半透 */
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 50px;
  text-decoration: none;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.nav-login-btn:hover {
  background: #10B981;
  color: white;
  border-color: #10B981;
  box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
  transform: translateY(-3px);
}

/* --- Hero区域极客感大按钮 --- */
.hero-actions { display: flex; gap: 1.5rem; margin-top: 1rem;}

.eco-btn-primary, .eco-btn-secondary {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 16px 36px; font-size: 1.15rem; font-weight: 700; border-radius: 50px;
  text-decoration: none; transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); 
  position: relative; overflow: hidden;
  z-index: 1;
}

/* 翠绿玻璃内发光主按钮 */
.eco-btn-primary { 
  background: rgba(16, 185, 129, 0.85); 
  color: white; 
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 8px 30px rgba(16, 185, 129, 0.3), inset 0 2px 2px rgba(255, 255, 255, 0.5); 
  backdrop-filter: blur(10px);
}
.eco-btn-primary:hover {
  background: rgba(16, 185, 129, 1);
  transform: translateY(-5px); 
  box-shadow: 0 15px 40px rgba(16, 185, 129, 0.5), inset 0 2px 2px rgba(255, 255, 255, 0.6);
  border-color: rgba(255, 255, 255, 0.8);
}

/* 极致清透克制副按钮 */
.eco-btn-secondary { 
  background: rgba(255, 255, 255, 0.15); 
  color: #064E3B; 
  border: 1px solid rgba(255, 255, 255, 0.6); 
  backdrop-filter: blur(12px) saturate(180%);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
}
.eco-btn-secondary:hover {
  background: rgba(255, 255, 255, 0.5); 
  color: #022C22;
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  border-color: white;
}

/* 按钮共用扫光特效 (Shimmer) */
.eco-btn-primary::after, .eco-btn-secondary::after {
  content: '';
  position: absolute;
  top: 0; left: -150%; width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  transform: skewX(-25deg);
  animation: sweepLight 5s infinite;
  pointer-events: none;
  z-index: -1;
}

@keyframes sweepLight {
  0% { left: -150%; }
  20% { left: 200%; }
  100% { left: 200%; }
}

/* =========================================================================
   6. 各区块排版细化
========================================================================== */
.navbar {
  border-radius: 0; padding: 1rem 0; position: sticky; top: 0; z-index: 1000; width: 100%;
}
.navbar-container { display: flex; justify-content: space-between; align-items: center; }
.navbar-logo h1 { font-size: 1.6rem; color: #10B981; font-weight: 800; margin: 0;}
.logo-link { text-decoration: none; }
.user-info {
  display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 6px 12px; border-radius: 20px;
  background: rgba(16, 185, 129, 0.1); color: #064E3B; font-weight: 600;
}

.system-status { display: inline-flex; align-items: center; gap: 8px; padding: 6px 16px; margin-bottom: 20px; font-weight: 600; font-size: 0.9rem;}
.pulse-dot { width: 8px; height: 8px; background: #10B981; border-radius: 50%; animation: pulse 1.5s infinite; }
@keyframes pulse { 0% {box-shadow: 0 0 0 0 rgba(16,185,129,0.4);} 70% {box-shadow: 0 0 0 10px transparent;} 100% {box-shadow: 0 0 0 0 transparent;} }

.typewriter-container { height: 60px; margin-bottom: 1.5rem; display: flex; align-items: center;}
.typewriter-text { font-size: 2.8rem; font-weight: 800; background: linear-gradient(135deg, #059669, #0ea5e9); -webkit-background-clip: text; -webkit-text-fill-color: transparent;}
.cursor { font-size: 2.8rem; color: #10B981; animation: blink 1s step-end infinite; }
@keyframes blink { 50% { opacity: 0; } }

.live-data-screen { display: inline-block; padding: 2rem; margin-bottom: 2rem; width: 100%; max-width: 480px;}
.data-label { font-size: 1.1rem; color: #059669; font-weight: 600; margin-bottom: 8px; }
.data-number { 
  font-family: 'Space Grotesk', sans-serif; 
  font-size: 4.5rem; 
  font-weight: 700; 
  color: #059669; 
  line-height: 1; 
  letter-spacing: -1px; 
  text-shadow: 0 4px 15px rgba(5, 150, 105, 0.2);
  transition: text-shadow 0.3s;
}
.hero-desc { font-size: 1.2rem; color: #047857; margin-bottom: 2.5rem; line-height: 1.8; }

.hero-visual {
  border-radius: 24px; padding: 16px; background: rgba(255, 255, 255, 0.2); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.5); width: 100%;
}
.hologram-frame { width: 100%; aspect-ratio: 16/10; border-radius: 16px; overflow: hidden; position: relative; background: transparent; }
.carousel-track { display: flex; height: 100%; transition: transform 0.8s cubic-bezier(0.25, 1, 0.5, 1); }
.carousel-slide { flex: 0 0 100%; }
.carousel-slide img { width: 100%; height: 100%; object-fit: cover; border-radius: 16px;}
.eco-indicators { display: flex; justify-content: center; gap: 8px; margin-top: 16px; }
.eco-dot { width: 8px; height: 8px; border-radius: 50%; background: rgba(0,0,0,0.1); cursor: pointer; transition: 0.3s;}
.eco-dot.active { background: #10B981; transform: scale(1.5); }

.feature-card, .why-us-item { 
  padding: 2.5rem; text-align: left; display: flex; flex-direction: column; height: 100%; 
  transform-style: preserve-3d;
}
.feature-card p, .why-us-item p { flex-grow: 1; margin-bottom: 0; }
.feature-card:hover, .why-us-item:hover {
  background: rgba(255, 255, 255, 0.45) !important;
  box-shadow: 0 20px 50px rgba(16, 185, 129, 0.15) !important;
}

.feature-icon, .why-us-icon, .feature-card h4, .why-us-item h4 {
  transform: translateZ(30px); 
  transition: transform 0.3s;
}

.feature-icon { margin-bottom: 1.5rem; }
.feature-icon svg, .why-us-icon svg { width: 40px; height: 40px; stroke: #10B981; color: #10B981;}
.feature-icon img { width: 60px; height: 60px; border-radius: 12px; }
.feature-card h4, .why-us-item h4 { font-size: 1.4rem; color: #064E3B; margin-bottom: 1rem; font-weight: 700;}
.feature-card p, .why-us-item p { font-size: 1rem; color: #047857; line-height: 1.6; transform: translateZ(15px);}

/* ==========================================
   高级滚动入场动画：3D 毛玻璃聚焦浮现
========================================== */
.reveal-blur {
  opacity: 0;
  transform: translateY(50px) scale(0.95) perspective(1000px) rotateX(-5deg);
  will-change: transform, opacity;
}

.reveal-blur.visible {
  opacity: 1;
  transform: translateY(0) scale(1) perspective(1000px) rotateX(0deg);
  transition: opacity 0.8s ease-out, transform 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  transition-delay: var(--delay, 0s);
}

.feature-card.visible:hover,
.why-us-item.visible:hover,
.mini-calc-panel.visible:hover {
  transition-delay: 0s;
  transition: transform 0.15s ease-out, box-shadow 0.3s ease;
}

.anti-gravity-float { animation: float 6s ease-in-out infinite; }
@keyframes float { 0%, 100% {transform: translateY(0);} 50% {transform: translateY(-10px);} }

.cta-container { text-align: center; display: flex; flex-direction: column; align-items: center; }
.cta-container p { font-size: 1.2rem; color: #047857; margin-bottom: 2rem; text-align: center;}

.footer { background: rgba(6, 78, 59, 0.85); backdrop-filter: blur(20px); color: white; padding: 4rem 0 2rem; border-top: 1px solid rgba(255,255,255,0.2); width: 100%;}
.footer-container { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 3rem; }
.footer h4 { font-size: 1.4rem; margin-bottom: 1rem; }
.footer p, .footer-links a { color: rgba(255,255,255,0.7); font-size: 1rem; text-decoration: none; transition: 0.3s;}
.footer-links { display: flex; flex-direction: column; gap: 0.8rem; }
.footer-links a:hover { color: white; transform: translateX(5px); }
.footer-contact { margin-top: 2rem; text-align: center; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 2rem; grid-column: 1 / -1;}

@media (max-width: 992px) {
  .hero-container { grid-template-columns: 1fr !important; text-align: center; }
  .hero-content { align-items: center; text-align: center;}
  .system-status { margin: 0 auto 20px auto; }
  .hero-actions { justify-content: center; }
  .typewriter-container { justify-content: center; }
  .features-grid, .why-us-grid { grid-template-columns: repeat(2, 1fr) !important; }
}
@media (max-width: 768px) {
  .features-grid, .why-us-grid { grid-template-columns: 1fr !important; }
}
</style>