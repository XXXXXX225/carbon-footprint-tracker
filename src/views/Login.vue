<template>
  <div class="login-container">
    <div class="login-box">
      <div class="left-panel">
        <div class="brand-section">
          <div class="logo">
            <svg viewBox="0 0 100 100">
              <circle cx="50" cy="50" r="45" fill="none" stroke="currentColor" stroke-width="3"/>
              <path d="M50 15 L50 85 M15 50 L85 50" stroke="currentColor" stroke-width="2"/>
              <circle cx="50" cy="50" r="15" fill="currentColor"/>
            </svg>
          </div>
          <h1 class="brand-title">碳足迹追踪平台</h1>
          <p class="brand-subtitle">携手共建绿色未来</p>
        </div>

        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M23 6l-9.5 9.5-5-5L1 18"/>
                <path d="M17 6h6v6"/>
              </svg>
            </div>
            <div class="feature-text">
              <h3>智能追踪</h3>
              <p>AI驱动的碳排放分析</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
                <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
                <line x1="12" y1="22.08" x2="12" y2="12"/>
              </svg>
            </div>
            <div class="feature-text">
              <h3>全面监测</h3>
              <p>覆盖生活各个领域</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </div>
            <div class="feature-text">
              <h3>绿色生活</h3>
              <p>助力可持续发展</p>
            </div>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="tab-buttons">
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >
            登录
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >
            注册
          </button>
        </div>

        <form class="login-form" @submit.prevent="handleSubmit">
          <div v-if="activeTab === 'login'">
            <div class="form-group">
              <label>用户名</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
                <input 
                  v-model="loginForm.username" 
                  type="text" 
                  placeholder="请输入用户名"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input 
                  v-model="loginForm.password" 
                  type="password" 
                  placeholder="请输入密码"
                  required
                />
              </div>
            </div>
            <div class="form-options">
              <label class="checkbox">
                <input v-model="rememberMe" type="checkbox" />
                <span>记住我</span>
              </label>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>
            <button type="submit" class="submit-btn" :class="{ loading }" :disabled="loading">
              {{ loading ? '' : '登录' }}
            </button>
          </div>

          <div v-else>
            <div class="form-group">
              <label>用户名</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
                <input 
                  v-model="registerForm.username" 
                  type="text" 
                  placeholder="请输入用户名"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label>姓名</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
                <input 
                  v-model="registerForm.name" 
                  type="text" 
                  placeholder="请输入姓名"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                  <polyline points="22,6 12,13 2,6"/>
                </svg>
                <input 
                  v-model="registerForm.email" 
                  type="email" 
                  placeholder="请输入邮箱"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input 
                  v-model="registerForm.password" 
                  type="password" 
                  placeholder="请输入密码"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label>确认密码</label>
              <div class="input-wrapper">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <input 
                  v-model="registerForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入密码"
                  required
                />
              </div>
            </div>
            <button type="submit" class="submit-btn" :class="{ loading }" :disabled="loading">
              {{ loading ? '' : '注册' }}
            </button>
          </div>
        </form>

        <div class="social-login">
          <div class="divider">
            <span>或使用以下方式</span>
          </div>
          <div class="social-buttons">
            <button class="social-btn google" @click="handleSocialLogin('google')">
              <svg viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
            </button>
            <button class="social-btn wechat" @click="handleSocialLogin('wechat')">
              <svg viewBox="0 0 24 24" fill="#07C160">
                <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
              </svg>
              <span style="position: absolute; bottom: -20px; font-size: 10px; color: #07C160; font-weight: 600; white-space: nowrap;">微信</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'
import { useCarbonStore } from '../store'

const router = useRouter()
const carbonStore = useCarbonStore()
const loading = ref(false)
const activeTab = ref('login')
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})



const handleSubmit = async () => {
  if (activeTab.value === 'login') {
    await handleLogin()
  } else {
    await handleRegister()
  }
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  loading.value = true
  try {
    const response = await authApi.login(loginForm.username, loginForm.password)
    localStorage.setItem('token', response.token)
    const user = {
      id: response.username,
      name: response.name,
      role: response.role
    }
    localStorage.setItem('user', JSON.stringify(user))
    carbonStore.setUser(user)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.name || !registerForm.email || !registerForm.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入密码不一致')
    return
  }
  
  loading.value = true
  try {
    const response = await authApi.register(
      registerForm.username,
      registerForm.password,
      registerForm.name,
      registerForm.email
    )
    const user = {
      id: response.username,
      name: response.name,
      role: response.role
    }
    localStorage.setItem('token', response.token)
    localStorage.setItem('user', JSON.stringify(user))
    carbonStore.setUser(user)
    ElMessage.success('注册成功')
    router.push('/dashboard')
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const handleSocialLogin = (platform: string) => {
  ElMessage.info(`${platform === 'google' ? 'Google' : '微信'}登录功能开发中`)
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 30%, #2E7D32 70%, #1B5E20 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.05) 0%, transparent 70%);
  animation: float 20s ease-in-out infinite;
  pointer-events: none;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.login-box {
  width: 100%;
  max-width: 900px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  display: flex;
  overflow: hidden;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.left-panel {
  flex: 1;
  background: linear-gradient(135deg, #2E7D32 0%, #4CAF50 30%, #66BB6A 60%, #2E7D32 100%);
  padding: 40px 35px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 350px;
  position: relative;
  overflow: hidden;
  box-shadow: inset -5px 0 20px rgba(0, 0, 0, 0.1);
}

.left-panel::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: rotate 25s linear infinite;
  pointer-events: none;
}

.left-panel::after {
  content: '';
  position: absolute;
  top: 20%;
  right: 10%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 60%);
  border-radius: 50%;
  animation: pulse 8s ease-in-out infinite;
  pointer-events: none;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.left-panel::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.1) 0%, transparent 100%);
  pointer-events: none;
}

.brand-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 70px;
  height: 70px;
  margin: 0 auto 20px;
  color: white;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  animation: logoPulse 2s ease-in-out infinite;
  box-shadow: 0 0 30px rgba(255, 255, 255, 0.3);
}

@keyframes logoPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 30px rgba(255, 255, 255, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 40px rgba(255, 255, 255, 0.5);
  }
}

.logo svg {
  width: 40px;
  height: 40px;
  animation: logoRotate 10s linear infinite;
}

@keyframes logoRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.brand-title {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 10px;
  letter-spacing: 1px;
  animation: fadeInUp 0.8s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
  animation-delay: 0.1s;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
  animation: fadeInUp 0.8s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
  animation-delay: 0.3s;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  animation: fadeInLeft 0.6s ease-out forwards;
  opacity: 0;
  transform: translateX(-20px);
}

.feature-item:nth-child(1) { animation-delay: 0.2s; }
.feature-item:nth-child(2) { animation-delay: 0.4s; }
.feature-item:nth-child(3) { animation-delay: 0.6s; }

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(8px) translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  width: 45px;
  height: 45px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-icon svg {
  width: 22px;
  height: 22px;
}

.feature-text h3 {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.feature-text p {
  font-size: 12px;
  opacity: 0.9;
  margin: 0;
}

.right-panel {
  flex: 1;
  padding: 30px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 350px;
  animation: fadeInRight 0.8s ease-out forwards;
  position: relative;
  background: linear-gradient(135deg, #ffffff 0%, #f8fff8 100%);
}

.right-panel::before {
  content: '';
  position: absolute;
  bottom: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.05) 0%, transparent 70%);
  pointer-events: none;
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.tab-buttons, .login-form, .social-login {
  animation: slideUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(15px);
}

.tab-buttons {
  animation-delay: 0.3s;
}

.login-form {
  animation-delay: 0.5s;
}

.social-login {
  animation-delay: 0.7s;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.tab-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  background: #f5f5f5;
  padding: 4px;
  border-radius: 10px;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  font-size: 15px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.tab-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(76, 175, 80, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-btn:hover::before {
  width: 200px;
  height: 200px;
}

.tab-btn.active {
  background: white;
  color: #4CAF50;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.2);
  transform: translateY(-2px);
}

.tab-btn.active::before {
  width: 200px;
  height: 200px;
  background: rgba(76, 175, 80, 0.2);
}

.login-form {
  margin-bottom: 25px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.input-wrapper svg {
  position: absolute;
  left: 12px;
  width: 18px;
  height: 18px;
  color: #999;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}



.form-group input {
  width: 100%;
  padding: 12px 16px 12px 40px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
  box-sizing: border-box;
  position: relative;
  background: white;
}

.form-group input:focus {
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
  transform: translateY(-1px);
}

.form-group input:focus + svg {
  color: #4CAF50;
  transform: scale(1.1) rotate(5deg);
}

.form-group input:hover {
  border-color: #81C784;
  transform: translateY(-1px);
}

.form-group input:hover + svg {
  color: #81C784;
}

.form-group input::placeholder {
  color: #ccc;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.checkbox input {
  width: 14px;
  height: 14px;
  cursor: pointer;
}

.forgot-link {
  font-size: 13px;
  color: #4CAF50;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #2E7D32;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 30%, #388E3C 70%, #2E7D32 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
  transition: all 0.6s ease;
}

.submit-btn::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.3) 50%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 0 0 10px 10px;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #2E7D32 0%, #388E3C 30%, #66BB6A 70%, #4CAF50 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.5);
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.6);
}

.submit-btn:hover:not(:disabled)::before {
  left: 100%;
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.submit-btn.loading {
  position: relative;
  pointer-events: none;
}

.submit-btn.loading::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 20px;
  height: 20px;
  margin: -10px 0 0 -10px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.social-login {
  margin-top: 20px;
}

.divider {
  position: relative;
  text-align: center;
  margin-bottom: 15px;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 40%;
  height: 1px;
  background: #e5e5e5;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  position: relative;
  background: white;
  padding: 0 10px;
  font-size: 11px;
  color: #999;
}

.social-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.social-btn {
  width: 48px;
  height: 48px;
  border: 2px solid #e5e5e5;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fff8 100%);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.social-btn:hover {
  border-color: #4CAF50;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 20px rgba(76, 175, 80, 0.25);
  background: linear-gradient(135deg, #f8fff8 0%, #ffffff 100%);
}

.social-btn:hover svg {
  transform: scale(1.1) rotate(5deg);
  color: #4CAF50;
}

.social-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(76, 175, 80, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.social-btn:hover {
  border-color: #4CAF50;
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.2);
}

.social-btn:hover::before {
  width: 150px;
  height: 150px;
}

.social-btn:active {
  transform: translateY(-2px) scale(0.98);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.2);
}

.social-btn svg {
  width: 24px;
  height: 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
}

.social-btn:hover svg {
  transform: scale(1.1) rotate(5deg);
}

.social-btn.google:hover {
  border-color: #4285F4;
  box-shadow: 0 8px 25px rgba(66, 133, 244, 0.2);
}

.social-btn.google:hover::before {
  background: rgba(66, 133, 244, 0.1);
}

.social-btn.wechat:hover {
  border-color: #07C160;
  box-shadow: 0 8px 25px rgba(7, 193, 96, 0.2);
}

.social-btn.wechat:hover::before {
  background: rgba(7, 193, 96, 0.1);
}

@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    max-width: 450px;
  }
  
  .left-panel {
    padding: 30px 25px;
    min-width: auto;
  }
  
  .right-panel {
    padding: 25px 30px;
    min-width: auto;
  }
  
  .logo {
    width: 60px;
    height: 60px;
  }
  
  .logo svg {
    width: 32px;
    height: 32px;
  }
  
  .brand-title {
    font-size: 22px;
  }
}
</style>
