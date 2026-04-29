<template>
  <el-container class="action-plan-container">
    <el-header height="60px" class="dashboard-header">
      <div class="header-left">
        <router-link to="/home" class="logo-link">
          <h1>碳足迹追踪平台</h1>
        </router-link>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            {{ user.name }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateToProfile">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <el-aside width="200px" class="dashboard-aside">
        <RoleSidebar />
      </el-aside>

      <el-main class="action-plan-main">
        <div class="page-shell">
          <section class="page-hero action-hero">
            <div>
              <div class="page-kicker">AI 执行中心</div>
              <h2 class="page-title">碳行动计划</h2>
              <p class="page-desc">
                把 AI 分析结果拆成每周可完成的任务，自动给出优先级、预计减排和执行进度，方便你真正把建议做成结果。
              </p>
            </div>
            <div class="page-actions">
              <el-button type="primary" @click="regeneratePlan" :loading="loading">同步 AI 建议</el-button>
              <el-button @click="resetCompleted">重置进度</el-button>
              <el-button @click="openAiAnalysis">查看 AI 分析</el-button>
            </div>
          </section>

          <section class="page-section">
            <div class="page-section-header">
              <div>
                <div class="page-kicker">行动概览</div>
                <h3 class="section-title">本月减碳执行概览</h3>
              </div>
              <span class="section-meta">{{ completedItems.length }} / {{ planItems.length }} 项已完成</span>
            </div>

            <div class="overview-grid">
              <article class="overview-card emphasis-card">
                <span class="overview-label">当前月排放</span>
                <strong class="overview-value">{{ Number(currentEmission).toFixed(2) }}</strong>
                <span class="overview-unit">kg CO₂e</span>
              </article>
              <article class="overview-card">
                <span class="overview-label">建议目标</span>
                <strong class="overview-value">{{ Number(targetEmission).toFixed(2) }}</strong>
                <span class="overview-unit">kg CO₂e</span>
              </article>
              <article class="overview-card">
                <span class="overview-label">待缩减缺口</span>
                <strong class="overview-value">{{ Number(reductionGap).toFixed(2) }}</strong>
                <span class="overview-unit">kg CO₂e</span>
              </article>
              <article class="overview-card">
                <span class="overview-label">行动完成率</span>
                <strong class="overview-value">{{ completionRate }}%</strong>
                <el-progress :percentage="completionRate" :stroke-width="12" status="success" />
              </article>
            </div>
          </section>

          <section class="page-section">
            <div class="page-section-header">
              <div>
                <div class="page-kicker">本周任务</div>
                <h3 class="section-title">最值得先做的 5 件事</h3>
              </div>
              <span class="section-meta">优先执行：{{ nextPriority }}</span>
            </div>

            <div class="plan-grid">
              <article v-for="item in pendingItems" :key="item.id" class="plan-card" :class="{ done: item.completed }">
                <div class="plan-card-head">
                  <el-tag :type="tagTypeMap[item.category] || 'info'"> {{ item.category }} </el-tag>
                  <span class="plan-impact"> 预计减排 {{ Number(item.impact).toFixed(1) }} kg CO₂e </span>
                </div>
                <h4> {{ item.title }} </h4>
                <p> {{ item.detail }} </p>
                <div class="plan-meta">
                  <span> 截止：{{ item.deadline }} </span>
                  <span> 难度：{{ item.effort }} </span>
                </div>
                <div class="plan-actions-row">
                  <el-button size="small" type="primary" @click="toggleTask(item.id)">
                    {{ item.completed ? '取消完成' : '标记完成' }}
                  </el-button>
                  <el-button size="small" text @click="showTaskDetail(item)"> 查看详情 </el-button>
                </div>
              </article>
            </div>
          </section>

          <section class="page-section completed-section">
            <div class="page-section-header">
              <div>
                <div class="page-kicker">执行记录</div>
                <h3 class="section-title">已完成动作</h3>
              </div>
              <span class="section-meta">记录会随计划刷新自动保留</span>
            </div>

            <el-table :data="completedItems" style="width: 100%">
              <el-table-column prop="category" label="类别" width="120" />
              <el-table-column prop="title" label="动作" />
              <el-table-column prop="impact" label="预计减排（kg CO₂e）" width="180">
                <template #default="scope">{{ Number(scope.row.impact).toFixed(1) }}</template>
              </el-table-column>
              <el-table-column prop="deadline" label="截止" width="120" />
              <el-table-column label="状态" width="100">
                <template #default>
                  <el-tag type="success">已完成</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button size="small" type="warning" @click="confirmReopenTask(scope.row)">重新打开</el-button>
                </template>
              </el-table-column>
            </el-table>
          </section>
        </div>
      </el-main>
    </el-container>

    <el-dialog v-model="detailDialogVisible" title="任务详情" width="520px">
      <div v-if="selectedItem" class="detail-card">
        <h4>{{ selectedItem.title }}</h4>
        <p>{{ selectedItem.detail }}</p>
        <div class="detail-grid">
          <div>
            <span class="detail-label">类别</span>
            <strong>{{ selectedItem.category }}</strong>
          </div>
          <div>
            <span class="detail-label">预计减排</span>
            <strong>{{ selectedItem.impact.toFixed(1) }} kg CO₂e</strong>
          </div>
          <div>
            <span class="detail-label">难度</span>
            <strong>{{ selectedItem.effort }}</strong>
          </div>
          <div>
            <span class="detail-label">截止时间</span>
            <strong>{{ selectedItem.deadline }}</strong>
          </div>
        </div>
        <p class="detail-source">来源：{{ selectedItem.source }}</p>
      </div>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import RoleSidebar from '../components/RoleSidebar.vue'
import { aiAnalysisApi, carbonApi, recommendationApi } from '../api'
import { useCarbonStore } from '../store'

interface PlanItem {
  id: number
  category: string
  title: string
  detail: string
  impact: number
  effort: string
  deadline: string
  completed: boolean
  source: string
}

const router = useRouter()
const carbonStore = useCarbonStore()
const loading = ref(false)
const summary = ref<any>(null)
const aiAnalysis = ref<any>(null)
const planItems = ref<PlanItem[]>([])
const detailDialogVisible = ref(false)
const selectedItem = ref<PlanItem | null>(null)
const COMPLETED_TASKS_KEY = 'action_plan_completed_ids'

const user = computed(() => carbonStore.user)

const currentEmission = computed(() => summary.value?.totalEmission || 0)
const targetEmission = computed(() => Math.max(180, Math.round(currentEmission.value * 0.85)))
const reductionGap = computed(() => Math.max(currentEmission.value - targetEmission.value, 0))
const completedItems = computed(() => planItems.value.filter(item => item.completed))
const pendingItems = computed(() => planItems.value.filter(item => !item.completed))
const completionRate = computed(() => {
  if (planItems.value.length === 0) {
    return 0
  }

  return Math.round((completedItems.value.length / planItems.value.length) * 100)
})
const nextPriority = computed(() => planItems.value.find(item => !item.completed)?.title || '暂无未完成任务')

const tagTypeMap: Record<string, 'success' | 'warning' | 'info' | 'danger'> = {
  交通: 'success',
  饮食: 'warning',
  用电: 'info',
  习惯: 'danger'
}

const loadCompletedIdSet = (): Set<number> => {
  try {
    const raw = localStorage.getItem(COMPLETED_TASKS_KEY)
    if (!raw) {
      return new Set<number>()
    }
    const parsed = JSON.parse(raw)
    if (!Array.isArray(parsed)) {
      return new Set<number>()
    }
    return new Set(parsed.map((id: unknown) => Number(id)).filter(id => Number.isFinite(id)))
  } catch {
    return new Set<number>()
  }
}

const saveCompletedIdSet = (ids: Set<number>) => {
  localStorage.setItem(COMPLETED_TASKS_KEY, JSON.stringify(Array.from(ids)))
}

const extractNotes = () => {
  const notes: string[] = []

  if (aiAnalysis.value?.suggestion?.suggestion) {
    notes.push(aiAnalysis.value.suggestion.suggestion)
  }

  if (Array.isArray(aiAnalysis.value?.recommendations)) {
    notes.push(...aiAnalysis.value.recommendations)
  }

  if (Array.isArray(aiAnalysis.value?.nextActions)) {
    notes.push(...aiAnalysis.value.nextActions)
  }

  return notes.filter(Boolean)
}

const buildPlanItems = () => {
  const notes = extractNotes()
  const transportEmission = summary.value?.transportEmission || 0
  const dietEmission = summary.value?.dietEmission || 0
  const electricityEmission = summary.value?.electricityEmission || 0
  
  const transportImpact = Math.max(2, Number(((transportEmission || reductionGap.value * 0.3)).toFixed(1)))
  const dietImpact = Math.max(1, Number(((dietEmission || reductionGap.value * 0.25)).toFixed(1)))
  const electricityImpact = Math.max(1, Number(((electricityEmission || reductionGap.value * 0.2)).toFixed(1)))

  const baseItems: PlanItem[] = [
    {
      id: 1,
      category: '交通',
      title: '把 2 次短途通勤换成低碳出行',
      detail: '优先把最容易替代的短途出行改成步行、骑行或公共交通，先拿到第一波减排收益。',
      impact: transportImpact,
      effort: '低',
      deadline: '周三',
      completed: false,
      source: '系统规则'
    },
    {
      id: 2,
      category: '饮食',
      title: '安排 1 个低碳工作日',
      detail: '将一次高碳外卖或红肉餐替换为本地、当季、少加工餐食，降低饮食端排放。',
      impact: dietImpact,
      effort: '低',
      deadline: '周四',
      completed: false,
      source: '系统规则'
    },
    {
      id: 3,
      category: '用电',
      title: '开启夜间节电检查',
      detail: '调整空调温度、清理待机设备、减少不必要的夜间充电，先消掉无效耗电。',
      impact: electricityImpact,
      effort: '中',
      deadline: '周末',
      completed: false,
      source: '系统规则'
    }
  ]

  notes.slice(0, 2).forEach((note, index) => {
    baseItems.unshift({
      id: 100 + index,
      category: '习惯',
      title: index === 0 ? 'AI 优先建议' : 'AI 补充建议',
      detail: note,
      impact: Math.max(1, Number((reductionGap.value * 0.15).toFixed(1)) || 1),
      effort: '低',
      deadline: '今日',
      completed: false,
      source: 'AI 分析'
    })
  })

  const completedState = loadCompletedIdSet()

  planItems.value = baseItems.slice(0, 5).map(item => ({
    ...item,
    completed: completedState.has(item.id)
  }))
}

const loadData = async () => {
  loading.value = true

  try {
    const [summaryResult, analysisResult] = await Promise.allSettled([
      carbonApi.getSummary('month'),
      aiAnalysisApi.getAnalysis()
    ])

    if (summaryResult.status === 'fulfilled') {
      summary.value = summaryResult.value
    }

    if (analysisResult.status === 'fulfilled') {
      aiAnalysis.value = analysisResult.value
    }

    buildPlanItems()
  } catch (error) {
    console.error('加载行动计划失败:', error)
    ElMessage.error('行动计划加载失败')
  } finally {
    loading.value = false
  }
}

const regeneratePlan = async () => {
  await loadData()
  ElMessage.success('行动计划已更新')
}

const resetCompleted = () => {
  planItems.value = planItems.value.map(item => ({ ...item, completed: false }))
  saveCompletedIdSet(new Set<number>())
  ElMessage.success('已完成进度已重置')
}

const toggleTask = (id: number) => {
  planItems.value = planItems.value.map(item => {
    if (item.id !== id) {
      return item
    }
    return {
      ...item,
      completed: !item.completed
    }
  })

  const completedIds = new Set(
    planItems.value
      .filter(item => item.completed)
      .map(item => item.id)
  )
  saveCompletedIdSet(completedIds)
  ElMessage.success('任务状态已保存')
}

const showTaskDetail = (item: PlanItem) => {
  selectedItem.value = item
  detailDialogVisible.value = true
}

const openAiAnalysis = () => {
  router.push('/ai-analysis')
}

const confirmReopenTask = (task: PlanItem) => {
  ElMessageBox.confirm(
    `确定要将任务 "${task.title}" 重新打开吗？`,
    '确认操作',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      console.log('开始重新打开任务:', task.id, task.title)
      
      // 调用 API 更新状态
      // 暂时注释掉 API 调用，因为可能是 API 调用失败导致的问题
      // await recommendationApi.updateUserRecommendation({
      //   id: task.id,
      //   status: 'PENDING'
      // })
      
      // 更新本地状态
      planItems.value = planItems.value.map(item => {
        if (item.id !== task.id) {
          return item
        }
        return {
          ...item,
          completed: false
        }
      })
      
      // 更新本地存储
      const completedIds = new Set(
        planItems.value
          .filter(item => item.completed)
          .map(item => item.id)
      )
      console.log('更新本地存储，已完成任务ID:', Array.from(completedIds))
      saveCompletedIdSet(completedIds)
      
      ElMessage.success('任务已重新打开')
    } catch (error) {
      console.error('重新打开任务失败:', error)
      ElMessage.error('操作失败：' + (error instanceof Error ? error.message : '未知错误'))
    }
  }).catch(() => {
    // 取消操作
  })
}

const navigateToProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  carbonStore.setUser({ id: '', name: '', role: '' })
  router.push('/login')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.action-plan-container {
  min-height: 100vh;
  background: transparent !important;
}

.action-plan-main {
  padding: 24px;
}

.action-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  align-items: flex-start;
}

.section-title {
  font-size: 22px;
  color: #1a1a1a;
  margin: 0;
}

.section-meta {
  color: #5f6b60;
  font-size: 13px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.overview-card {
  padding: 18px 20px;
  border-radius: 18px;
  border: 1px solid rgba(76, 175, 80, 0.12);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(248, 252, 248, 0.88));
  box-shadow: 0 10px 28px rgba(76, 175, 80, 0.06);
  display: grid;
  gap: 10px;
}

.emphasis-card {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.12), rgba(129, 199, 132, 0.08));
}

.overview-label {
  color: #5f6b60;
  font-size: 13px;
}

.overview-value {
  font-size: 30px;
  line-height: 1;
  color: #1a1a1a;
  font-family: 'Orbitron', 'Space Grotesk', monospace;
}

.overview-unit {
  color: #60705f;
  font-size: 13px;
  font-family: 'Orbitron', 'Space Grotesk', monospace;
}

.plan-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.plan-card {
  padding: 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 28px rgba(76, 175, 80, 0.08);
  display: grid;
  gap: 14px;
}

.plan-card.done {
  opacity: 0.72;
}

.plan-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.plan-card h4 {
  font-size: 18px;
  line-height: 1.4;
  margin: 0;
  color: #1b1b1b;
}

.plan-card p {
  margin: 0;
  color: #5f6b60;
  line-height: 1.7;
}

.plan-impact {
  color: #2e7d32;
  font-weight: 700;
  font-size: 13px;
  white-space: nowrap;
  font-family: 'Orbitron', 'Space Grotesk', monospace;
}

.plan-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: #6b6b6b;
  font-size: 13px;
}

.plan-actions-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.completed-section :deep(.el-table) {
  border-radius: 16px;
  overflow: hidden;
}

/* 表格中的数字字体 */
.completed-section :deep(.el-table .cell) {
  font-family: 'Orbitron', 'Space Grotesk', monospace;
}

.detail-card {
  display: grid;
  gap: 14px;
}

.detail-card h4 {
  margin: 0;
  font-size: 18px;
}

.detail-card p {
  margin: 0;
  color: #5f6b60;
  line-height: 1.7;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-grid > div {
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(76, 175, 80, 0.06);
}

.detail-label {
  display: block;
  color: #6b6b6b;
  font-size: 12px;
  margin-bottom: 6px;
}

.detail-source {
  color: #7a8477;
  font-size: 12px;
}

@media (max-width: 1024px) {
  .overview-grid,
  .plan-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .action-hero {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .action-plan-main {
    padding: 12px;
  }

  .overview-grid,
  .plan-grid,
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .plan-meta {
    flex-direction: column;
  }
}
</style>
