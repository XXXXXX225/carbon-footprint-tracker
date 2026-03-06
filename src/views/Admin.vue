<template>
  <div v-if="isAdmin" class="admin-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon total">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon today">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.todayNewUsers }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon week">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.weekNewUsers }}</div>
            <div class="stat-label">本周新增</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon month">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.monthNewUsers }}</div>
            <div class="stat-label">本月新增</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户角色分布</span>
            </div>
          </template>
          <div ref="roleChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近7天注册趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="user-list-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名或姓名"
            style="width: 250px"
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>
      <el-table
        :data="filteredUsers"
        style="width: 100%"
        v-loading="loading"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'INDIVIDUAL' ? 'primary' : scope.row.role === 'ENTERPRISE' ? 'success' : 'warning'">
              {{ scope.row.role === 'INDIVIDUAL' ? '个人用户' : scope.row.role === 'ENTERPRISE' ? '企业用户' : '管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalPoints" label="积分" width="100" />
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)" :disabled="scope.row.username === 'root'">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)" :disabled="scope.row.username === 'root'">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="filteredUsers.length"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
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
import { User, Calendar, TrendCharts, DataLine, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { adminApi } from '../api'
import { useCarbonStore } from '../store'
import { useRouter } from 'vue-router'

const router = useRouter()
const carbonStore = useCarbonStore()

const isAdmin = computed(() => carbonStore.user.role === 'ADMIN')

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
let roleChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

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
    user.name.toLowerCase().includes(query)
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
      left: 'left'
    },
    series: [
      {
        name: '用户角色',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c}'
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
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        data: values,
        type: 'bar',
        itemStyle: {
          color: '#4CAF50'
        },
        label: {
          show: true,
          position: 'top'
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
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  roleChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.admin-container {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 30px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.today {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.week {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.month {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-row {
  margin-top: 20px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.user-list-card {
  margin-top: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
