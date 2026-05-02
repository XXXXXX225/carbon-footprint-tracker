<template>
  <el-container v-if="isAdmin" class="advanced-admin-wrapper" direction="vertical">
    <canvas ref="bgCanvas" class="admin-bg-canvas"></canvas>

    <el-header height="60px" class="dashboard-header">
      <div class="header-left">
        <a href="/home" class="logo-link">
          <h1>碳足迹追踪平台</h1>
        </a>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            {{ carbonStore.user.name || '超级管理员' }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goHome">返回首页</el-dropdown-item>
              <el-dropdown-item @click="router.push('/dashboard')">返回仪表盘</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <div class="admin-layout">
      <aside class="admin-sidebar glass-panel">
        <div class="sidebar-logo" @click="goHome">
          <div class="logo-orb"></div>
          <h1>CARBON<span>ADMIN</span></h1>
        </div>
        
        <div class="menu-sections">
          <div 
            v-for="tab in ['dashboard', 'users', 'logs']" 
            :key="tab"
            :class="['menu-item', { active: activeTab === tab }]"
            @click="activeTab = tab"
          >
            <el-icon><component :is="tabIconMap[tab]" /></el-icon>
            <span>{{ tabLabelMap[tab] }}</span>
          </div>
        </div>
      </aside>

      <main class="admin-main">
        <header class="main-header">
          <div class="breadcrumb">控制台 / {{ tabLabelMap[activeTab] }}</div>
          <div class="admin-user-info">
            <el-tag effect="dark" type="success" class="status-tag">系统加密链路：已建立</el-tag>
          </div>
        </header>

        <div class="content-viewport">
          <transition name="fade-transform" mode="out-in">
            <div :key="activeTab" class="tab-content">
              
              <div v-if="activeTab === 'dashboard'" class="dashboard-grid">
                <div class="stats-row">
                  <div 
                    v-for="(stat, idx) in statCards" 
                    :key="idx"
                    class="advanced-stat-card glass-panel magnetic-item"
                    @mousemove="handleMagneticMove"
                    @mouseleave="handleMagneticLeave"
                  >
                    <div class="stat-inner">
                      <div class="stat-info">
                        <p class="label">{{ stat.title }}</p>
                        <h2 class="value">{{ stat.value }}</h2>
                      </div>
                      <div class="stat-chart-mini">
                        <div class="trend-indicator" :class="stat.trend > 0 ? 'up' : 'down'">
                          {{ stat.trend > 0 ? '↑' : '↓' }} {{ Math.abs(stat.trend) }}%
                        </div>
                      </div>
                    </div>
                    <div class="glow-border"></div>
                  </div>
                </div>

                <div class="charts-row">
                  <div class="chart-box glass-panel anti-gravity-float">
                    <div class="box-header">用户角色矩阵</div>
                    <div ref="roleChartRef" class="echart-container"></div>
                  </div>
                  <div class="chart-box glass-panel anti-gravity-float" style="animation-delay: -2s;">
                    <div class="box-header">注册增长曲线</div>
                    <div ref="trendChartRef" class="echart-container"></div>
                  </div>
                </div>

                <div class="monitoring-row">
                  <div class="monitor-box glass-panel anti-gravity-float" style="animation-delay: -1s;">
                    <div class="box-header text-danger">
                      <el-icon><WarningFilled /></el-icon> 异常高排放预警 (本周)
                    </div>
                    <el-table :data="abnormalUsers" class="glass-table">
                      <el-table-column prop="username" label="用户名" />
                      <el-table-column prop="emission" label="异常排放量">
                        <template #default="scope">
                          <span class="danger-text">{{ scope.row.emission }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="reason" label="预警原因" show-overflow-tooltip />
                      <el-table-column label="操作" width="100" align="center">
                        <template #default>
                          <el-button type="danger" plain size="small" round>警告</el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>

                  <div class="monitor-box glass-panel anti-gravity-float" style="animation-delay: -3s;">
                    <div class="box-header text-warning">
                      <el-icon><TrophyBase /></el-icon> 平台减排榜单 TOP 5
                    </div>
                    <el-table :data="leaderboardUsers" class="glass-table">
                      <el-table-column label="排名" width="80" align="center">
                        <template #default="scope">
                          <div class="rank-badge" :class="'rank-' + (scope.$index + 1)">
                            {{ scope.$index + 1 }}
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column prop="username" label="环保先锋" />
                      <el-table-column prop="totalReduction" label="累计减排量" />
                      <el-table-column prop="points" label="贡献积分" align="right">
                        <template #default="scope">
                          <el-tag type="success" effect="plain" round>{{ scope.row.points }} 绿叶</el-tag>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
              </div>

              <div v-else-if="activeTab === 'users'" class="table-view">
                <div class="table-container glass-panel">
                  <div class="table-header">
                    <el-input v-model="searchQuery" placeholder="检索全库用户..." class="glass-input">
                      <template #prefix><el-icon><Search /></el-icon></template>
                    </el-input>
                    <el-button type="success" :icon="Download">导出报表</el-button>
                  </div>
                  <el-table :data="paginatedUsers" class="glass-table" v-loading="loading">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="username" label="用户名" width="150" />
                    <el-table-column prop="name" label="姓名" width="150" />
                    <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
                    <el-table-column prop="role" label="角色" width="120" align="center">
                      <template #default="scope">
                        <el-tag 
                          :type="scope.row.role === 'ADMIN' ? 'danger' : (scope.row.role === 'ENTERPRISE' ? 'warning' : 'success')"
                          effect="light"
                          round
                        >
                          {{ scope.row.role === 'INDIVIDUAL' ? '个人用户' : scope.row.role === 'ENTERPRISE' ? '企业用户' : '管理员' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="totalPoints" label="碳积分" width="100" align="center" />
                    <el-table-column label="操作" width="160" fixed="right" align="center">
                      <template #default="scope">
                        <el-button-group>
                          <el-button type="primary" link @click="handleEdit(scope.row)" :disabled="scope.row.username === 'root'">编辑</el-button>
                          <el-divider direction="vertical" />
                          <el-button type="danger" link @click="handleDelete(scope.row)" :disabled="scope.row.username === 'root'">删除</el-button>
                        </el-button-group>
                      </template>
                    </el-table-column>
                  </el-table>
                  <div class="pagination-wrapper">
                    <el-pagination
                      background
                      v-model:current-page="currentPage"
                      v-model:page-size="pageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      :total="filteredUsers.length"
                      layout="total, sizes, prev, pager, next, jumper"
                      @size-change="handleSizeChange"
                      @current-change="handleCurrentChange"
                    />
                  </div>
                </div>
              </div>

              <div v-else-if="activeTab === 'logs'" class="logs-view">
                <div class="table-container glass-panel">
                  <div class="table-header">
                    <h3>系统操作日志</h3>
                  </div>
                  <el-table :data="logsData" class="glass-table">
                    <el-table-column prop="time" label="时间" width="180" />
                    <el-table-column prop="user" label="操作用户" width="150" />
                    <el-table-column prop="action" label="操作" />
                    <el-table-column prop="ip" label="IP地址" width="150" />
                  </el-table>
                </div>
              </div>

            </div>
          </transition>
        </div>
      </main>
    </div>
  </el-container>
  <div v-else class="access-denied">
    <el-result icon="error" title="访问被拒绝" sub-title="您没有权限访问管理员页面">
      <template #extra>
        <el-button type="primary" @click="router.push('/dashboard')">返回仪表盘</el-button>
      </template>
    </el-result>
  </div>

  <!-- 编辑用户对话框 -->
  <el-dialog
    v-model="editDialogVisible"
    title="编辑用户"
    width="500px"
  >
    <el-form :model="editForm" label-width="100px">
      <el-form-item label="用户名" disabled>
        <el-input v-model="editForm.username" disabled />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="editForm.name" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="editForm.email" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="editForm.role" placeholder="选择角色">
          <el-option label="个人用户" value="INDIVIDUAL" />
          <el-option label="企业用户" value="ENTERPRISE" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
      </el-form-item>
      <el-form-item label="积分">
        <el-input-number v-model="editForm.totalPoints" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 删除确认对话框 -->
  <el-dialog
    v-model="deleteDialogVisible"
    title="删除用户"
    width="300px"
  >
    <p>确定要删除用户 <span style="color: red;">{{ deleteUser?.username }}</span> 吗？</p>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitDelete">删除</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { User, Calendar, TrendCharts, DataLine, Search, WarningFilled, TrophyBase, ArrowLeft, Top, Bottom, Download, Document, Monitor, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { adminApi } from '../api'

const abnormalUsers = ref([
  { username: 'test_user1', emission: '1240.2 kg', reason: '用电量连续3天超标' },
  { username: 'enterprise8', emission: '9820.0 kg', reason: '交通排放激增200%' }
])

const leaderboardUsers = ref([
  { username: 'green_master', totalReduction: '452.1 kg', points: 4520 },
  { username: 'leo_chen', totalReduction: '380.5 kg', points: 3804 },
  { username: 'anna99', totalReduction: '345.2 kg', points: 3452 },
  { username: 'eco_hero', totalReduction: '290.0 kg', points: 2900 },
  { username: 'bike_lover', totalReduction: '210.8 kg', points: 2108 }
])

const logsData = ref([
  { time: '2026-04-25 14:30:22', user: 'admin', action: '删除用户 test_user1', ip: '192.168.1.100' },
  { time: '2026-04-25 13:15:45', user: 'admin', action: '编辑用户 leo_chen', ip: '192.168.1.100' },
  { time: '2026-04-25 10:05:12', user: 'admin', action: '导出用户报表', ip: '192.168.1.100' },
  { time: '2026-04-24 16:45:33', user: 'admin', action: '添加新用户 anna99', ip: '192.168.1.100' }
])

import { useCarbonStore } from '../store'
import { useRouter } from 'vue-router'

const router = useRouter()
const carbonStore = useCarbonStore()

const isAdmin = computed(() => carbonStore.user.role === 'ADMIN')

const activeTab = ref('dashboard')

const tabIconMap = {
  dashboard: Monitor,
  users: User,
  logs: Clock
}

const tabLabelMap = {
  dashboard: '数据总览',
  users: '用户管理',
  logs: '系统日志'
}

const statCards = computed(() => [
  { title: '总用户数', value: stats.value.totalUsers, icon: User, theme: 'theme-blue', trend: 5.2 },
  { title: '今日新增', value: stats.value.todayNewUsers, icon: Calendar, theme: 'theme-green', trend: 12.5 },
  { title: '本周新增', value: stats.value.weekNewUsers, icon: TrendCharts, theme: 'theme-cyan', trend: -2.4 },
  { title: '本月新增', value: stats.value.monthNewUsers, icon: DataLine, theme: 'theme-purple', trend: 8.9 }
])

// 磁性吸附效果
const handleMagneticMove = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  const rect = el.getBoundingClientRect()
  const x = e.clientX - rect.left - rect.width / 2
  const y = e.clientY - rect.top - rect.height / 2
  el.style.transform = `translate(${x * 0.1}px, ${y * 0.1}px)`
}

const handleMagneticLeave = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  el.style.transform = `translate(0px, 0px)`
}

if (!isAdmin.value) {
  ElMessage.error('无权访问管理员页面')
  router.push('/dashboard')
}

interface UserInfo {
  id: number
  username: string
  name: string
  email: string
  role: string
  totalPoints: number
  createdAt: string
  updatedAt: string
}

interface UserStats {
  totalUsers: number
  todayNewUsers: number
  weekNewUsers: number
  monthNewUsers: number
  roleDistribution: Record<string, number>
  dailyRegistrationTrend: Record<string, number>
}

const loading = ref(false)
const users = ref<UserInfo[]>([])
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const roleChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
const bgCanvas = ref<HTMLCanvasElement | null>(null)
let roleChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null
let animationFrameId: number | null = null

// 编辑用户相关
const editDialogVisible = ref(false)
const editForm = ref({
  id: 0,
  username: '',
  name: '',
  email: '',
  role: '',
  totalPoints: 0
})

// 删除用户相关
const deleteDialogVisible = ref(false)
const deleteUser = ref<UserInfo | null>(null)

const stats = ref<UserStats>({
  totalUsers: 0,
  todayNewUsers: 0,
  weekNewUsers: 0,
  monthNewUsers: 0,
  roleDistribution: {},
  dailyRegistrationTrend: {}
})

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user =>
    user.username.toLowerCase().includes(query) ||
    user.name.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query)
  )
})

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
})

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 初始化背景画布
const initBackgroundCanvas = () => {
  const canvas = bgCanvas.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  const particles: any[] = []
  
  class Particle {
    x: number
    y: number
    size: number
    speedX: number
    speedY: number
    color: string
    
    constructor() {
      this.x = Math.random() * canvas.width
      this.y = Math.random() * canvas.height
      this.size = Math.random() * 2 + 1
      this.speedX = (Math.random() - 0.5) * 0.5
      this.speedY = (Math.random() - 0.5) * 0.5
      this.color = `rgba(16, 185, 129, ${Math.random() * 0.3})`
    }
    
    update() {
      this.x += this.speedX
      this.y += this.speedY
      
      if (this.x < 0 || this.x > canvas.width) this.speedX *= -1
      if (this.y < 0 || this.y > canvas.height) this.speedY *= -1
    }
    
    draw() {
      ctx.beginPath()
      ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
      ctx.fillStyle = this.color
      ctx.fill()
    }
  }
  
  for (let i = 0; i < 50; i++) {
    particles.push(new Particle())
  }
  
  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    
    for (let i = 0; i < particles.length; i++) {
      particles[i].update()
      particles[i].draw()
      
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x
        const dy = particles[i].y - particles[j].y
        const distance = Math.sqrt(dx * dx + dy * dy)
        
        if (distance < 100) {
          ctx.beginPath()
          ctx.strokeStyle = `rgba(16, 185, 129, ${0.2 * (1 - distance / 100)})`
          ctx.lineWidth = 0.5
          ctx.moveTo(particles[i].x, particles[i].y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.stroke()
        }
      }
    }
    
    animationFrameId = requestAnimationFrame(animate)
  }
  
  animate()
}

const initRoleChart = () => {
  if (!roleChartRef.value) return
  roleChart = echarts.init(roleChartRef.value)
  const data = Object.entries(stats.value.roleDistribution).map(([name, value]) => ({
    name: name === 'INDIVIDUAL' ? '个人用户' : name === 'ENTERPRISE' ? '企业用户' : '管理员',
    value
  }))
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: {
        color: '#e0e0e0'
      }
    },
    series: [
      {
        name: '用户角色',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: 'rgba(255, 255, 255, 0.1)',
          borderWidth: 2,
          color: function(params: any) {
            const colors = ['#10B981', '#3B82F6', '#F59E0B']
            return colors[params.dataIndex]
          }
        },
        label: {
          show: true,
          formatter: '{b}: {c}',
          color: '#e0e0e0'
        },
        data
      }
    ]
  }
  roleChart.setOption(option)
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  const dates = Object.keys(stats.value.dailyRegistrationTrend)
  const values = Object.values(stats.value.dailyRegistrationTrend)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        color: '#94a3b8'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: {
        color: '#94a3b8'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.05)'
        }
      }
    },
    series: [
      {
        data: values,
        type: 'bar',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.8)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0.1)' }
          ])
        },
        label: {
          show: true,
          position: 'top',
          color: '#e0e0e0'
        }
      }
    ]
  }
  trendChart.setOption(option)
}

const fetchData = async () => {
  loading.value = true
  try {
    const [usersData, statsData] = await Promise.all([
      adminApi.getAllUsers(),
      adminApi.getUserStats()
    ])
    users.value = usersData
    stats.value = statsData
    await nextTick()
    initRoleChart()
    initTrendChart()
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
}

const handleResize = () => {
  roleChart?.resize()
  trendChart?.resize()
  
  const canvas = bgCanvas.value
  if (canvas) {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
}

const goHome = () => {
  window.location.href = '/home'
}

// 编辑用户
const handleEdit = (user: UserInfo) => {
  editForm.value = {
    id: user.id,
    username: user.username,
    name: user.name,
    email: user.email,
    role: user.role,
    totalPoints: user.totalPoints
  }
  editDialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    await adminApi.updateUser(editForm.value.id, {
      name: editForm.value.name,
      email: editForm.value.email,
      role: editForm.value.role,
      totalPoints: editForm.value.totalPoints
    })
    ElMessage.success('用户信息更新成功')
    editDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('更新失败：' + (error as Error).message)
  }
}

// 删除用户
const handleDelete = (user: UserInfo) => {
  deleteUser.value = user
  deleteDialogVisible.value = true
}

// 提交删除
const submitDelete = async () => {
  if (!deleteUser.value) return
  try {
    await adminApi.deleteUser(deleteUser.value.id)
    ElMessage.success('用户删除成功')
    deleteDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('删除失败：' + (error as Error).message)
  }
}

onMounted(() => {
  fetchData()
  initBackgroundCanvas()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  roleChart?.dispose()
  trendChart?.dispose()
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
})
</script>

<style scoped>
:root {
  --admin-glass: rgba(255, 255, 255, 0.05);
  --admin-accent: #10B981;
  --admin-bg: #0a0f12;
}

.advanced-admin-wrapper {
  background: radial-gradient(circle at top right, #0d1a15, #050a09);
  min-height: 100vh;
  color: #e0e0e0;
  overflow: hidden;
  position: relative;
}

.admin-bg-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.admin-layout {
  display: flex;
  position: relative;
  z-index: 1;
  min-height: 100vh;
}

.admin-sidebar {
  width: 260px;
  height: calc(100vh - 40px);
  margin: 20px;
  padding: 30px 0;
  display: flex;
  flex-direction: column;
}

.sidebar-logo {
  padding: 0 30px;
  margin-bottom: 50px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.sidebar-logo:hover {
  opacity: 0.8;
}

.logo-orb {
  width: 12px;
  height: 12px;
  background: var(--admin-accent);
  border-radius: 50%;
  box-shadow: 0 0 15px var(--admin-accent);
}

.sidebar-logo h1 {
  font-size: 18px;
  letter-spacing: 2px;
  font-weight: 800;
  margin: 0;
  color: #e0e0e0;
}

.sidebar-logo span {
  color: var(--admin-accent);
  opacity: 0.7;
}

.menu-sections {
  flex: 1;
  padding: 0 20px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
  color: #94a3b8;
}

.menu-item:hover {
  background: rgba(16, 185, 129, 0.1);
  color: #e0e0e0;
}

.menu-item.active {
  background: rgba(16, 185, 129, 0.2);
  color: var(--admin-accent);
  font-weight: 600;
}

.admin-main {
  flex: 1;
  margin: 20px 20px 20px 0;
  display: flex;
  flex-direction: column;
}

.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.breadcrumb {
  font-size: 14px;
  color: #94a3b8;
}

.status-tag {
  font-size: 12px;
}

.content-viewport {
  flex: 1;
  overflow-y: auto;
}

.tab-content {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.advanced-stat-card {
  flex: 1;
  padding: 24px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.advanced-stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(16, 185, 129, 0.2);
}

.stat-inner {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  position: relative;
  z-index: 1;
}

.stat-info .label {
  font-size: 12px;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 1px;
  margin: 0 0 8px 0;
}

.stat-info .value {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(to right, #fff, #94a3b8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.trend-indicator {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
}

.trend-indicator.up {
  color: #10B981;
}

.trend-indicator.down {
  color: #EF4444;
}

.glow-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 20px;
  padding: 1px;
  background: linear-gradient(45deg, var(--admin-accent), transparent, var(--admin-accent));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.advanced-stat-card:hover .glow-border {
  opacity: 1;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.monitoring-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-box, .monitor-box {
  padding: 24px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.box-header {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #e0e0e0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.echart-container {
  width: 100%;
  height: 300px;
}

.table-view {
  padding: 0 20px;
}

.table-container {
  padding: 24px;
  border-radius: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.logs-view {
  padding: 0 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.glass-panel {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
}

.magnetic-item {
  transition: transform 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.anti-gravity-float {
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(0.5deg); }
}

.glass-table {
  background: transparent !important;
  color: #cbd5e1 !important;
}

:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255, 255, 255, 0.05);
  --el-table-border-color: rgba(255, 255, 255, 0.05);
  color: #cbd5e1;
}

:deep(.el-table th) {
  color: #94a3b8 !important;
}

:deep(.el-table__row:hover > td) {
  background-color: rgba(16, 185, 129, 0.1) !important;
}

:deep(.el-table__empty-text) {
  color: #94a3b8;
}

.glass-input :deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border-radius: 12px;
  color: #e0e0e0;
}

.glass-input :deep(.el-input__placeholder) {
  color: #94a3b8;
}

:deep(.el-pagination) {
  color: #94a3b8;
}

:deep(.el-pagination__item:hover) {
  color: var(--admin-accent);
}

:deep(.el-pagination__item.is-active) {
  background-color: var(--admin-accent);
  border-color: var(--admin-accent);
}

.rank-badge {
  width: 28px;
  height: 28px;
  line-height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: #94a3b8;
  font-weight: bold;
  margin: 0 auto;
  text-align: center;
}

.rank-1 {
  background: rgba(254, 243, 199, 0.2);
  color: #D97706;
}

.rank-2 {
  background: rgba(243, 244, 246, 0.2);
  color: #6B7280;
}

.rank-3 {
  background: rgba(255, 237, 213, 0.2);
  color: #C2410C;
}

.text-danger {
  color: #EF4444;
}

.danger-text {
  color: #EF4444;
  font-weight: 600;
}

.text-warning {
  color: #F59E0B;
}

.access-denied {
  background: radial-gradient(circle at top right, #0d1a15, #050a09);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e0e0e0;
}

:deep(.el-result__title) {
  color: #e0e0e0;
}

:deep(.el-result__sub-title) {
  color: #94a3b8;
}

@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }
  
  .admin-sidebar {
    width: calc(100% - 40px);
    height: auto;
    flex-direction: row;
    padding: 20px;
  }
  
  .sidebar-logo {
    margin-bottom: 0;
  }
  
  .menu-sections {
    display: flex;
    gap: 10px;
  }
  
  .menu-item {
    margin-bottom: 0;
    padding: 10px 15px;
  }
  
  .charts-row, .monitoring-row {
    grid-template-columns: 1fr;
  }
  
  .stats-row {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
