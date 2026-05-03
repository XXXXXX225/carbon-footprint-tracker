<template>
  <div class="emissions-premium-page" @mousemove="handleMouseMove">
    <!-- 1. 鼠标跟随探照光晕 -->
    <div 
      class="mouse-glow" 
      :style="{ left: `${mousePixelX}px`, top: `${mousePixelY}px` }"
    ></div>

    <!-- 2. 极客风抗重力视差背景 -->
    <div class="antigravity-bg">
      <!-- 为每个球增加独立的视差计算层，互不干扰 -->
      <div class="orb-layer" :style="{ transform: `translate(${mouseX * -40}px, ${mouseY * -40}px)` }">
        <div class="orb orb-1"></div>
      </div>
      <div class="orb-layer" :style="{ transform: `translate(${mouseX * 50}px, ${mouseY * 50}px)` }">
        <div class="orb orb-2"></div>
      </div>
      <div class="orb-layer" :style="{ transform: `translate(${mouseX * -20}px, ${mouseY * -20}px)` }">
        <div class="orb orb-3"></div>
      </div>
      <div class="noise-overlay"></div>
    </div>

    <!-- 3. 玻璃态顶部导航栏 -->
    <nav class="glass-navbar">
      <div class="nav-left">
        <button @click="router.push('/dashboard')" class="nav-icon-btn">
          <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
          <span>控制台</span>
        </button>
      </div>
      <div class="nav-right">
        <button @click="router.push('/ai-analysis')" class="nav-glow-btn">
          <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none"><circle cx="12" cy="12" r="10"></circle><path d="M12 16v-4"></path><path d="M12 8h.01"></path></svg>
          深度 AI 分析
        </button>
      </div>
    </nav>

    <!-- 4. 主体内容区 -->
    <main class="content-wrapper">
      <header class="page-header">
        <h2 class="magic-title">Chat to track AI</h2>
        <p class="magic-subtitle">What did you do for the earth today?</p>
      </header>

      <!-- AI 核心超级输入框 (Omnibox) -->
      <section 
        class="ai-omnibox-container"
        @dragover.prevent="isDragging = true"
        @dragleave.prevent="isDragging = false"
        @drop.prevent="handleDrop"
      >
        <transition name="fade-slide">
          <div v-if="previewImage" class="image-preview-badge">
            <img :src="previewImage" alt="Preview" />
            <button @click="clearImage" class="btn-close-img">
              <svg viewBox="0 0 24 24" width="16" height="16" stroke="currentColor" stroke-width="2" fill="none"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>
        </transition>

        <div class="omnibox" :class="{ 'is-focused': isFocused, 'is-dragging': isDragging }">
          <input 
            v-model="inputText" 
            type="text" 
            placeholder="试试输入：“今天骑共享单车 5 公里” 或 拖拽截图至此..." 
            @keyup.enter="submitAiData"
            @focus="isFocused = true"
            @blur="isFocused = false"
            :disabled="isLoading"
            class="magic-input"
          />
          
          <div class="omnibox-actions">
            <button @click="triggerFileInput" class="btn-icon" title="上传账单/行程截图" :disabled="isLoading">
              <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
            </button>
            
            <button @click="submitAiData" class="btn-submit" :disabled="isLoading || (!inputText && !base64Image)">
              <span v-if="!isLoading">
                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2" fill="none"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>
              </span>
              <span v-else class="loader"></span>
            </button>
          </div>
        </div>
        
        <input type="file" ref="fileInput" @change="handleFileUpload" accept="image/*" style="display: none" />
        
        <div v-if="isDragging" class="drag-glass-overlay">
          <div class="drag-content">
            <svg viewBox="0 0 24 24" width="48" height="48" stroke="#00dc82" stroke-width="1.5" fill="none"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
            <h3>松开鼠标，让 AI 提取数据</h3>
          </div>
        </div>
      </section>

      <div class="premium-divider">
        <span>OR MANUAL ENTRY</span>
      </div>

      <section class="manual-entry-section">
        <div class="glass-tabs">
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'transport' }" 
            @click="activeTab = 'transport'"
          >
            🚗 交通出行
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'diet' }" 
            @click="activeTab = 'diet'"
          >
            🍔 饮食消费
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'electricity' }" 
            @click="activeTab = 'electricity'"
          >
            ⚡ 能源用电
          </button>
        </div>
        
        <div class="glass-form-container">
          <transition name="fade-slide" mode="out-in">
            <TransportForm v-if="activeTab === 'transport'" />
            <DietForm v-else-if="activeTab === 'diet'" />
            <ElectricityForm v-else-if="activeTab === 'electricity'" />
          </transition>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

import TransportForm from '@/components/emissions/TransportForm.vue'
import DietForm from '@/components/emissions/DietForm.vue'
import ElectricityForm from '@/components/emissions/ElectricityForm.vue'

const activeTab = ref('transport')

const router = useRouter()

const inputText = ref('')
const previewImage = ref(null)
const base64Image = ref('')
const isDragging = ref(false)
const isLoading = ref(false)
const isFocused = ref(false)
const fileInput = ref(null)

// --- 鼠标交互动效逻辑 ---
const mouseX = ref(0)
const mouseY = ref(0)
const mousePixelX = ref(window.innerWidth / 2) // 初始在屏幕中间
const mousePixelY = ref(window.innerHeight / 2)

const handleMouseMove = (e) => {
  // 记录真实的像素坐标，给光晕用
  mousePixelX.value = e.clientX
  mousePixelY.value = e.clientY
  
  // 归一化坐标 (-1 到 1)，给视差背景用
  mouseX.value = (e.clientX / window.innerWidth) * 2 - 1
  mouseY.value = (e.clientY / window.innerHeight) * 2 - 1
}

// 确保组件销毁时清理事件（虽然绑在div上一般不会漏，但好习惯）
onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove)
})
onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
})
// ----------------------

const triggerFileInput = () => {
  fileInput.value.click()
}

const processFile = (file) => {
  if (!file || !file.type.startsWith('image/')) return
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
    base64Image.value = e.target.result 
  }
  reader.readAsDataURL(file)
}

const handleFileUpload = (e) => {
  processFile(e.target.files[0])
}

const handleDrop = (e) => {
  isDragging.value = false
  processFile(e.dataTransfer.files[0])
}

const clearImage = () => {
  previewImage.value = null
  base64Image.value = ''
  if (fileInput.value) fileInput.value.value = ''
}

const submitAiData = async () => {
  if (!inputText.value && !base64Image.value) return
  isLoading.value = true

  try {
    let token = localStorage.getItem('token')
    if (!token) console.warn("未获取到 Token，请求可能会被后端拦截！")
    else token = token.replace(/^"(.*)"$/, '$1')

    const authHeader = token ? (token.startsWith('Bearer') ? token : `Bearer ${token}`) : ''

    const response = await fetch('/api/ai/chat-to-track', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authHeader
      },
      body: JSON.stringify({
        text: inputText.value,
        image: base64Image.value
      })
    })

    if (!response.ok) {
      alert('❌ 录入失败：大模型响应异常，请检查后端日志！')
      isLoading.value = false
      return
    }
    
    const resData = await response.json()
    
    if (resData.code && resData.code !== 200) {
       alert('录入失败: ' + resData.message)
       return
    }

    console.log('AI 解析成功:', resData)
    inputText.value = ''
    clearImage()
    router.push('/dashboard')
    
  } catch (error) {
    console.error('录入失败', error)
    alert('网络请求失败，请稍后重试！')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.emissions-premium-page {
  position: relative;
  min-height: 100vh;
  background-color: #09090b; 
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* --- 极客鼠标跟随光晕 --- */
.mouse-glow {
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(0, 220, 130, 0.08) 0%, rgba(0, 0, 0, 0) 60%);
  border-radius: 50%;
  pointer-events: none; /* 绝对不能挡住点击事件 */
  transform: translate(-50%, -50%);
  z-index: 5;
  transition: opacity 0.3s ease;
  will-change: left, top;
}

/* --- 动态抗重力背景 (加入了视差层) --- */
.antigravity-bg {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 0;
  pointer-events: none; 
  overflow: hidden;
}

.orb-layer {
  position: absolute;
  width: 100%; height: 100%;
  transition: transform 0.6s cubic-bezier(0.2, 0.8, 0.2, 1);
  will-change: transform;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.5;
  animation: float 20s infinite ease-in-out alternate;
}

.orb-1 {
  width: 500px; height: 500px;
  background: #00dc82; 
  top: -150px; left: -150px;
}

.orb-2 {
  width: 400px; height: 400px;
  background: #0047e1; 
  bottom: -100px; right: -100px;
  animation-delay: -5s;
  animation-duration: 25s;
}

.orb-3 {
  width: 300px; height: 300px;
  background: #36e4da; 
  top: 40%; left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -10s;
  animation-duration: 30s;
}

.noise-overlay {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='0.05'/%3E%3C/svg%3E");
  z-index: 1;
}

@keyframes float {
  0% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(50px, 80px) scale(1.1); }
  100% { transform: translate(-50px, 40px) scale(0.9); }
}

/* --- 顶部毛玻璃导航栏 --- */
.glass-navbar {
  position: relative;
  z-index: 50;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(9, 9, 11, 0.4);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.nav-icon-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  color: #a1a1aa;
  border: none;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-icon-btn:hover {
  color: #f4f4f5;
}

.nav-glow-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(0, 220, 130, 0.1);
  color: #00dc82;
  border: 1px solid rgba(0, 220, 130, 0.2);
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 0 15px rgba(0, 220, 130, 0);
}

.nav-glow-btn:hover {
  background: rgba(0, 220, 130, 0.2);
  border-color: rgba(0, 220, 130, 0.5);
  box-shadow: 0 0 20px rgba(0, 220, 130, 0.3);
  transform: translateY(-1px);
}

/* --- 主体内容区 --- */
.content-wrapper {
  position: relative;
  z-index: 10;
  padding: 4rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 4rem;
}

.magic-title {
  font-size: 3.5rem;
  font-weight: 800;
  letter-spacing: -0.05em;
  background: linear-gradient(135deg, #00dc82, #36e4da, #f4f4f5);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
  animation: shine 6s linear infinite;
}

@keyframes shine {
  to { background-position: 200% center; }
}

.magic-subtitle {
  font-size: 1.2rem;
  color: #a1a1aa; 
  font-weight: 500;
}

/* 核心输入框容器 */
.ai-omnibox-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.omnibox {
  position: relative;
  width: 100%;
  background: rgba(24, 24, 27, 0.6); 
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 10px 10px 10px 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 2;
}

.omnibox.is-focused {
  border-color: #00dc82;
  background: rgba(24, 24, 27, 0.8);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.5), 0 0 0 1px #00dc82, 0 0 30px rgba(0, 220, 130, 0.2);
  transform: translateY(-2px);
}

.omnibox.is-dragging {
  border-color: #36e4da;
  transform: scale(1.02);
}

.magic-input {
  flex: 1;
  background: transparent;
  border: none;
  color: #ffffff;
  font-size: 1.15rem;
  line-height: 1.5;
  outline: none;
}

.magic-input::placeholder {
  color: #71717a; 
}

.omnibox-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 按钮样式 */
.btn-icon {
  background: transparent;
  border: none;
  color: #a1a1aa;
  padding: 10px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover:not(:disabled) {
  color: #00dc82;
  background: rgba(0, 220, 130, 0.15);
}

.btn-submit {
  background: #f4f4f5; 
  color: #09090b;
  border: none;
  border-radius: 20px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.omnibox.is-focused .btn-submit {
  background: linear-gradient(135deg, #00dc82, #10b981);
  color: white;
  box-shadow: 0 4px 14px rgba(0, 220, 130, 0.4);
}

.btn-submit:hover:not(:disabled) {
  transform: scale(1.05);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 加载动画 */
.loader {
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 3px solid currentColor;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* 图片预览徽章 */
.image-preview-badge {
  align-self: flex-start;
  margin-left: 24px;
  margin-bottom: -16px;
  position: relative;
  z-index: 1;
  padding: 4px;
  background: rgba(24, 24, 27, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
  border-bottom: none;
  padding-bottom: 20px;
}

.image-preview-badge img {
  height: 60px;
  border-radius: 8px;
  object-fit: cover;
}

.btn-close-img {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0,0,0,0.4);
}

/* 拖拽蒙层 */
.drag-glass-overlay {
  position: absolute;
  top: -20px; left: -20px; right: -20px; bottom: -20px;
  background: rgba(9, 9, 11, 0.7);
  backdrop-filter: blur(12px);
  border: 2px dashed #00dc82;
  border-radius: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.drag-content {
  text-align: center;
  color: #00dc82;
}

.drag-content h3 {
  margin-top: 1rem;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 分割线与底部 */
.premium-divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 4rem 0;
  color: rgba(255,255,255,0.2);
}

.premium-divider::before, .premium-divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.premium-divider span {
  padding: 0 1.5rem;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 2px;
}

.manual-placeholder {
  text-align: center;
  padding: 3rem;
  border: 1px dashed rgba(255,255,255,0.1);
  background: rgba(255, 255, 255, 0.02);
  border-radius: 16px;
  color: #71717a;
}

.manual-entry-section {
  width: 100%;
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  z-index: 2;
  position: relative;
}

.glass-tabs {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  background: rgba(24, 24, 27, 0.4);
  backdrop-filter: blur(12px);
  padding: 6px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  width: fit-content;
  margin: 0 auto;
}

.tab-btn {
  background: transparent;
  color: #a1a1aa;
  border: none;
  padding: 8px 20px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-btn:hover {
  color: #f4f4f5;
  background: rgba(255, 255, 255, 0.05);
}

.tab-btn.active {
  background: rgba(0, 220, 130, 0.15);
  color: #00dc82;
  box-shadow: inset 0 0 0 1px rgba(0, 220, 130, 0.3);
}

.glass-form-container {
  background: rgba(24, 24, 27, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
  min-height: 300px;
}

.glass-form-container :deep(.el-card),
.glass-form-container :deep(.box-card) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}
.glass-form-container :deep(h1),
.glass-form-container :deep(h2),
.glass-form-container :deep(h3),
.glass-form-container :deep(h4),
.glass-form-container :deep(span),
.glass-form-container :deep(p) {
  color: #f4f4f5 !important;
}

.glass-form-container :deep(.el-form-item__label),
.glass-form-container :deep(.el-radio__label),
.glass-form-container :deep(.el-checkbox__label),
.glass-form-container :deep(.el-descriptions__label) {
  color: #a1a1aa !important;
  font-weight: 500;
}

.glass-form-container :deep(.el-input__wrapper),
.glass-form-container :deep(.el-textarea__inner) {
  background-color: rgba(0, 0, 0, 0.4) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important;
  color: #f4f4f5 !important;
}

.glass-form-container :deep(.el-input__inner) {
  color: #f4f4f5 !important;
}
.glass-form-container :deep(.el-input__inner::placeholder),
.glass-form-container :deep(.el-textarea__inner::placeholder) {
  color: #52525b !important;
}

.glass-form-container :deep(.el-table),
.glass-form-container :deep(.el-table__expanded-cell) {
  background-color: transparent !important;
}

.glass-form-container :deep(.el-table tr),
.glass-form-container :deep(.el-table td.el-table__cell) {
  background-color: #18181b !important;
  color: #e4e4e7 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important;
}

.glass-form-container :deep(.el-table th.el-table__cell) {
  background-color: #09090b !important;
  color: #00dc82 !important;
  font-weight: 600;
  border-bottom: 1px solid rgba(0, 220, 130, 0.3) !important;
}

.glass-form-container :deep(.el-table::before),
.glass-form-container :deep(.el-table::after),
.glass-form-container :deep(.el-table__inner-wrapper::before) {
  display: none !important;
}

.glass-form-container :deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: #27272a !important;
}

.glass-form-container :deep(.el-button--primary),
.glass-form-container :deep(.el-button--success) {
  background: linear-gradient(135deg, #00dc82, #10b981) !important;
  border: none !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(0, 220, 130, 0.3) !important;
}
.glass-form-container :deep(.el-button--primary:hover),
.glass-form-container :deep(.el-button--success:hover) {
  box-shadow: 0 6px 20px rgba(0, 220, 130, 0.5) !important;
  transform: translateY(-1px);
}

.glass-form-container :deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: #a1a1aa !important;
}
.glass-form-container :deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: #ef4444 !important;
  color: #ef4444 !important;
}

/* 动画过渡 */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>