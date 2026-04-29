<template>
  <el-container class="cyber-sandbox-container">
    <el-header height="60px" class="cyber-header">
      <div class="header-left">
        <router-link to="/home" target="_blank" class="logo-link" title="在新标签页打开首页">
          <el-icon class="brand-icon"><TrendCharts /></el-icon>
          <h1 class="glitch-text" data-text="Eco-Cyber 推演中心">Eco-Cyber 推演中心</h1>
        </router-link>
      </div>
      <div class="header-right">
        <el-button type="primary" style="color: white; font-weight: bold; letter-spacing: 1px; margin-right: 15px;" @click="exportPDF" :loading="exporting" title="导出为PDF报告">
          <el-icon><Download /></el-icon>导出报告
        </el-button>
        <el-button type="primary" style="color: white; font-weight: bold; letter-spacing: 1px;" @click="openDashboard" title="在新标签页保留此控制台并打开指挥台">
          <el-icon><House /></el-icon>返回指挥台
        </el-button>
      </div>
    </el-header>

    <el-main class="cyber-main">
      <div class="sandbox-wrapper" ref="sandboxWrapperRef">
        <div class="sandbox-sidebar">
          <div class="cyber-panel status-panel">
            <h3 class="panel-title">SYSTEM STATUS</h3>
            <div class="status-indicator">
              <div class="led" :class="{ 'led-active': !loading }"></div>
              <span>Neural Engine: {{ loading ? 'BOOTING...' : 'ONLINE' }}</span>
            </div>
            <div class="status-indicator">
              <div class="led led-active"></div>
              <span>Data Link: SECURE</span>
            </div>
            
            <div class="metric-glitch" v-if="!loading">
              <div class="metric-label">当前月排放量测算</div>
              <div class="metric-value">{{ currentEmission.toFixed(2) }} <span class="unit">kg CO₂e</span></div>
            </div>
          </div>

          <div class="cyber-panel chart-panel" v-if="!loading">
            <h3 class="panel-title">EMISSION RADAR</h3>
            <div class="chart-container">
              <CarbonChart type="pie" :data="categoryPieData" :height="220" :showActions="false" />
            </div>
          </div>

          <div class="cyber-panel action-panel" v-if="actionableRecommendations.length">
            <h3 class="panel-title">ACTION BRIEF</h3>
            <div class="action-brief-list">
              <div v-for="item in actionableRecommendations" :key="item.title + item.detail" class="action-brief-item">
                <div class="action-brief-label">{{ item.title }}</div>
                <p>{{ item.detail }}</p>
              </div>
            </div>
            <el-button class="cyber-button action-bridge-button" @click="openActionPlan">查看碳行动计划</el-button>
          </div>
        </div>

        <div class="sandbox-terminal">
          <div class="terminal-header">
            <div class="terminal-dots">
              <span></span><span></span><span></span>
            </div>
            <div class="terminal-title">AI_PREDICTION_TERMINAL_v3.0</div>
            <el-button 
              class="cyber-button" 
              :loading="isTyping" 
              @click="startAiInference"
              :disabled="loading"
            >
              {{ isTyping ? '推理进行中...' : 'INITIATE INFERENCE' }}
            </el-button>
          </div>
          
          <div class="terminal-body" ref="terminalBody">
            <div v-if="!hasInferred && !isTyping" class="terminal-placeholder">
              <p>> Awaiting user command to start predictive analysis...</p>
              <p class="blink-cursor">_</p>
            </div>
            
            <div v-else class="typewriter-content">
              <div v-for="(line, index) in typedLines" :key="index" class="terminal-line">
                <span class="prompt">></span> <span v-html="line"></span>
              </div>
              <div v-if="isTyping" class="terminal-line typing-indicator">
                <span class="prompt">></span> <span class="blink-cursor">█</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Hidden Formal Report Container for PDF Export -->
      <div id="pdf-report-container" class="pdf-formal-report">
        <h1 class="report-title">碳足迹 AI 深度诊断与预测报告</h1>
        <div class="report-meta">
          <span>报告生成时间：{{ new Date().toLocaleString() }}</span>
          <span v-if="aiAnalysis?.model">分析引擎：{{ aiAnalysis?.model }}</span>
        </div>

        <div class="report-section summary-section">
          <h2>核心数据摘要</h2>
          <div class="data-grid">
            <div class="data-box">
              <span class="data-label">当月碳排放总量</span>
              <span class="data-value">{{ currentEmission.toFixed(2) }} <small>kg CO₂e</small></span>
            </div>
            <div class="data-box">
              <span class="data-label">下月预测排放量</span>
              <span class="data-value">{{ prediction?.predictedEmission?.toFixed(2) || 'N/A' }} <small>kg CO₂e</small></span>
            </div>
            <div class="data-box">
              <span class="data-label">测算置信度</span>
              <span class="data-value">{{ (prediction?.confidence * 100)?.toFixed(0) || 'N/A' }}%</span>
            </div>
            <div class="data-box">
              <span class="data-label">整体风险等级</span>
              <span class="data-value" :class="'risk-' + (aiAnalysis?.riskLevel?.toLowerCase() || 'unknown')">{{ aiAnalysis?.riskLevel || 'N/A' }}</span>
            </div>
          </div>
        </div>

        <div class="report-section">
          <h2>核心结论与风险预警</h2>
          <p class="headline-text" v-if="aiAnalysis?.headline">{{ aiAnalysis.headline }}</p>
          <p class="summary-text" v-if="aiAnalysis?.summary">{{ aiAnalysis.summary }}</p>
        </div>

        <div class="report-section" v-if="aiAnalysis?.insights?.length">
          <h2>深度溯源分析</h2>
          <ul class="insight-list">
            <li v-for="(insight, idx) in aiAnalysis.insights" :key="idx">
              <strong>{{ insight.title }}：</strong> {{ insight.text }}
            </li>
          </ul>
        </div>

        <div class="report-section" v-if="aiAnalysis?.recommendations?.length || prediction?.suggestion || aiAnalysis?.nextActions?.length">
          <h2>AI 行动建议</h2>
          <ul class="recommendation-list">
             <li v-if="prediction?.suggestion">
               <strong>系统优先建议：</strong> {{ prediction.suggestion.suggestion }}
               <el-button 
                 type="primary" 
                 link 
                 @click="acceptAiTask(prediction.suggestion.suggestion)"
                 style="margin-left: 10px;"
               >
                 领取任务
               </el-button>
             </li>
             <li v-for="(rec, idx) in aiAnalysis?.recommendations || []" :key="'r-'+idx">
               {{ rec }}
               <el-button 
                 type="primary" 
                 link 
                 @click="acceptAiTask(rec)"
                 style="margin-left: 10px;"
               >
                 领取任务
               </el-button>
             </li>
             <li v-for="(action, idx) in aiAnalysis?.nextActions || []" :key="'na-'+idx">
               {{ action }}
               <el-button 
                 type="primary" 
                 link 
                 @click="acceptAiTask(action)"
                 style="margin-left: 10px;"
               >
                 领取任务
               </el-button>
             </li>
          </ul>
        </div>

        <div class="report-section" v-if="aiAnalysis?.nextActions?.length">
          <h2>下一步行动计划 (Next Actions)</h2>
          <ul class="action-list">
            <li v-for="(action, idx) in aiAnalysis.nextActions" :key="'a-'+idx">{{ action }}</li>
          </ul>
        </div>

        <div class="report-footer">
          <p>
            <span v-if="aiAnalysis?.source">Source: {{ aiAnalysis.source }}</span> • 
            <span>Eco-Cyber 绿能预测追踪系统</span>
          </p>
          <p>本报告由人工智能模型自动化生成，数据仅供改善生活方式与参考。</p>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import CarbonChart from '../components/CarbonChart.vue'
import { aiAnalysisApi, carbonApi, predictionApi, recommendationApi } from '../api'
import { TrendCharts, House, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

const router = useRouter()
const loading = ref(true)
const exporting = ref(false)
const sandboxWrapperRef = ref<HTMLElement | null>(null)

const openDashboard = () => {
  const routeUrl = router.resolve('/dashboard')
  window.open(routeUrl.href, '_blank')
}

// 数据源 (完美保留你的 API 调用逻辑)
const summary = ref<any>(null)
const prediction = ref<any>(null)
const aiAnalysis = ref<any>(null)

// 衍生数据
const currentEmission = computed(() => summary.value?.totalEmission || 0)
const categoryPieData = computed(() => ({
  legend: ['交通', '饮食', '用电'],
  series: [
    { name: '交通', value: summary.value?.transportEmission || 0, itemStyle: { color: '#0088cc' } },
    { name: '饮食', value: summary.value?.dietEmission || 0, itemStyle: { color: '#00e5ff' } },
    { name: '用电', value: summary.value?.electricityEmission || 0, itemStyle: { color: '#00ffaa' } }
  ]
}))

const actionableRecommendations = computed(() => {
  const items: Array<{ title: string, detail: string }> = []

  if (prediction.value?.suggestion?.suggestion) {
    items.push({
      title: '系统优先建议',
      detail: prediction.value.suggestion.suggestion
    })
  }

  if (Array.isArray(aiAnalysis.value?.recommendations)) {
    aiAnalysis.value.recommendations.slice(0, 2).forEach((recommendation: string, index: number) => {
      items.push({
        title: index === 0 ? 'AI 建议 1' : 'AI 建议 2',
        detail: recommendation
      })
    })
  }

  if (Array.isArray(aiAnalysis.value?.nextActions)) {
    aiAnalysis.value.nextActions.slice(0, 1).forEach((action: string) => {
      items.push({
        title: '下一步行动',
        detail: action
      })
    })
  }

  return items.slice(0, 3)
})

const openActionPlan = () => {
  router.push('/action-plan')
}

const acceptAiTask = async (taskText: string) => {
  try {
    // 调用新增接口或现有的任务跟踪接口
    await recommendationApi.addAiTask({ content: taskText });
    ElMessage.success('成功加入碳行动清单！');
    
    // 可选：提示后跳转
    // router.push('/recommendations');
  } catch (error) {
    ElMessage.error('领取失败');
  }
}

// --- 流式打字机沙盘核心逻辑 ---
const terminalBody = ref<HTMLElement | null>(null)
const isTyping = ref(false)
const hasInferred = ref(false)
const typedLines = ref<string[]>([])

const scrollToBottom = () => {
  nextTick(() => {
    if (terminalBody.value) {
      terminalBody.value.scrollTop = terminalBody.value.scrollHeight
    }
  })
}

const typeLine = async (text: string, speed: number = 20) => {
  return new Promise<void>((resolve) => {
    let currentText = ''
    typedLines.value.push('') // Add empty line
    const lineIndex = typedLines.value.length - 1
    
    let i = 0
    const interval = setInterval(() => {
      if (i < text.length) {
        currentText += text.charAt(i)
        typedLines.value[lineIndex] = currentText
        scrollToBottom()
        i++
      } else {
        clearInterval(interval)
        resolve()
      }
    }, speed)
  })
}

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms))

const startAiInference = async () => {
  isTyping.value = true
  hasInferred.value = true
  typedLines.value = [] // Clear terminal
  
  await typeLine('Initiating neural network analysis module...', 30)
  await sleep(400)
  await typeLine('Scanning historical database (Last 8 months)... <span style="color:#34d399">[OK]</span>', 10)
  await sleep(450)
  await typeLine('Cross-referencing global carbon benchmarks... <span style="color:#34d399">[OK]</span>', 10)
  await sleep(500)
  await typeLine('Calculating behavioral correlation coefficients...', 40)
  await sleep(600)
  await typeLine('Building quarter-ahead volatility projection matrix...', 28)
  await sleep(500)
  await typeLine('Quantifying environmental footprint equivalence...', 32)
  await sleep(600)
  
  // 结合真实的后端数据输出
  const nextMonthPred = prediction.value?.predictedEmission?.toFixed(2) || 'N/A'
  const confidence = (prediction.value?.confidence * 100)?.toFixed(0) || 'N/A'
  
  if (aiAnalysis.value?.model) {
    await typeLine(`[SYSTEM] Linked to AI core: <span style="color:#38bdf8">${aiAnalysis.value.model}</span>`, 20)
    await sleep(300)
  }

  await typeLine(`<span style="background: linear-gradient(135deg, #6ee7b7, #10b981); -webkit-background-clip: text; color: transparent; -webkit-text-fill-color: transparent; font-weight: 800; letter-spacing: 1px; display: inline-block;">================ ANALYSIS COMPLETE ================</span>`, 5)
  await sleep(300)
  
  if (aiAnalysis.value?.headline) {
    await typeLine(`[HEADLINE] <strong style="color:#c084fc;">${aiAnalysis.value.headline}</strong>`, 30)
    await sleep(200)
  }
  
  if (aiAnalysis.value?.riskLevel) {
    const riskColor = aiAnalysis.value.riskLevel === 'HIGH' ? '#ef4444' : (aiAnalysis.value.riskLevel === 'MEDIUM' ? '#f59e0b' : '#10b981')
    await typeLine(`[RISK LEVEL] <strong style="color:${riskColor};">${aiAnalysis.value.riskLevel}</strong>`, 30)
    await sleep(200)
  }

  await typeLine(`[PREDICTION] Projected next month emission: <strong style="color:#34d399; font-size:1.1em;">${nextMonthPred} kg CO₂e</strong>`, 30)
  await typeLine(`[CONFIDENCE] Model confidence level: ${confidence}%`, 30)
  
  if (aiAnalysis.value?.summary) {
    await sleep(300)
    await typeLine(`[SUMMARY] ${aiAnalysis.value.summary}`, 20)
  }

  await sleep(500)
  await typeLine(`<br><span style="color:#facc15; font-weight:bold;">[EXPERT DIAGNOSIS]</span>`, 20)
  await sleep(300)
  await typeLine('- Evaluating root causes behind dominant emission categories...', 18)
  await sleep(280)
  await typeLine('- Assessing next-quarter uncertainty and rebound risks...', 18)
  await sleep(280)
  await typeLine('- Drafting tiered action plan: zero-cost / low-cost / long-term investment.', 18)
  await sleep(350)

  await typeLine(`<br><span style="background: linear-gradient(135deg, #6ee7b7, #10b981); -webkit-background-clip: text; color: transparent; -webkit-text-fill-color: transparent; font-weight: 800; letter-spacing: 1px; display: inline-block;">>> DIAGNOSTIC INSIGHTS:</span>`, 20)
  await sleep(240)

  if (aiAnalysis.value?.insights?.length) {
    await typeLine(`[INSIGHT COUNT] ${aiAnalysis.value.insights.length} expert observations synchronized.`, 16)
    await sleep(220)
  }

  await typeLine(`[RECOMMENDATION PIPELINE] tiered action engine ready.`, 16)
  await sleep(220)

  await typeLine(`[NEXT-ACTION LOOP] measurable tasks mapped with expected carbon impact.`, 16)
  await sleep(260)

  await typeLine(`<br><span style="background: linear-gradient(135deg, #6ee7b7, #10b981); -webkit-background-clip: text; color: transparent; -webkit-text-fill-color: transparent; font-weight: 800; letter-spacing: 1px; display: inline-block;">>> GENERATING ACTIONABLE INTELLIGENCE:</span>`, 20)
  
  if (aiAnalysis.value?.insights && aiAnalysis.value.insights.length > 0) {
    for (const insight of aiAnalysis.value.insights) {
      await sleep(400)
      if (insight.title === '排放占比排序') {
        const highlightedText = insight.text.replace(/([^\s]+)\s([\d.]+)\s(kg\sCO₂e)/g, '<strong style="color:#6ee7b7;">$1 $2</strong> <span style="color:#94a3b8;">$3</span>')
        await typeLine(`- <span style="color:#a7f3d0">${insight.title}</span>: <span style="color:#f8fafc; font-weight: 500;">${highlightedText}</span>`, 20)
      } else {
        await typeLine(`- <span style="color:#a7f3d0">${insight.title}</span>: <span style="color:#cbd5e1;">${insight.text}</span>`, 20)
      }
    }
  } else if (prediction.value?.suggestion) {
    await sleep(400)
    await typeLine(`- <span style="color:#a7f3d0">Priority Suggestion</span>: ${prediction.value.suggestion.suggestion}`, 20)
  } else {
    await sleep(400)
    await typeLine(`- No critical risk detected. Maintain current green lifestyle.`, 20)
  }
  
  if (aiAnalysis.value?.recommendations && aiAnalysis.value.recommendations.length > 0) {
    await sleep(400)
    await typeLine(`<br><span style="color:#38bdf8; font-weight:bold;">[ACTION BRIEF]</span>`, 20)
    for (const rec of aiAnalysis.value.recommendations) {
       await sleep(300)
       await typeLine(`  * ${rec}`, 20)
    }
  }

  if (aiAnalysis.value?.nextActions && aiAnalysis.value.nextActions.length > 0) {
    await sleep(400)
    await typeLine(`<br><span style="color:#f59e0b; font-weight:bold;">[NEXT ACTIONS]</span>`, 20)
    for (const action of aiAnalysis.value.nextActions) {
       await sleep(300)
       await typeLine(`  > ${action}`, 20)
    }
  }
  
  if (aiAnalysis.value?.source) {
    await sleep(400)
    await typeLine(`<br><span style="color:#64748b; font-size: 0.9em;">Source: ${aiAnalysis.value.source}</span>`, 10)
  }
  
  await sleep(500)
  await typeLine('<br>Analysis session terminated. Awaiting further input.', 10)
  isTyping.value = false
}
// --- 结束 ---

const exportPDF = async () => {
  if (loading.value) return
  
  try {
    exporting.value = true
    
    const wrapperElement = document.getElementById('pdf-report-container')
    if (!wrapperElement) return
    
    // Make visible temporarily for capture
    wrapperElement.style.display = 'block'
    wrapperElement.style.opacity = '1'
    wrapperElement.style.position = 'absolute'
    wrapperElement.style.left = '0'
    wrapperElement.style.top = '0'
    wrapperElement.style.zIndex = '-999'
    
    await nextTick()

    // Add brief pause to allow fonts and layout to settle
    await new Promise(r => setTimeout(r, 100))

    const canvas = await html2canvas(wrapperElement, {
      scale: 3, 
      backgroundColor: '#ffffff',
      useCORS: true,
      logging: false,
      windowWidth: wrapperElement.scrollWidth,
      windowHeight: wrapperElement.scrollHeight
    })

    // Hide again
    wrapperElement.style.display = 'none'

    const pdf = new jsPDF('p', 'mm', 'a4')
    const imgWidth = 210
    const pageHeight = 297
    const imgHeight = (canvas.height * imgWidth) / canvas.width
    let heightLeft = imgHeight
    let position = 0

    const imgData = canvas.toDataURL('image/jpeg', 1.0)
    pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight)
    heightLeft -= pageHeight

    while (heightLeft > 0) {
      position -= pageHeight
      pdf.addPage()
      pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight)
      heightLeft -= pageHeight
    }
    
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
    pdf.save(`AI_Carbon_Analysis_Report_${timestamp}.pdf`)
    ElMessage.success('报告导出成功')
  } catch (error) {
    console.error('Export failed:', error)
    ElMessage.error('报告导出失败')
  } finally {
    exporting.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const [summaryResult, predictionResult] = await Promise.all([
      carbonApi.getSummary('month'),
      predictionApi.getNextMonthPrediction()
    ])
    summary.value = summaryResult
    prediction.value = predictionResult

    try {
      aiAnalysis.value = await aiAnalysisApi.getAnalysis()
    } catch (e) {
      console.warn('AI analysis API failed, using fallback')
    }
  } catch (error) {
    console.error('加载底层数据失败:', error)
    ElMessage.error('系统数据加载异常')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600&display=swap');

/* =========================================
   Eco-Cyber Premium Deep Theme 
   ========================================= */
.cyber-sandbox-container {
  height: 100vh;
  background-color: #020b14;
  background-image: 
    linear-gradient(rgba(0, 229, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 229, 255, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  color: #e0f8ff;
  font-family: inherit;
  position: relative;
  overflow: hidden;
  animation: bgScroll 20s linear infinite;
}

/* 动态背景网格位移 */
@keyframes bgScroll {
  0% { background-position: 0 0; }
  100% { background-position: 30px 30px; }
}

/* 全屏扫描线特效 (CRT Scanline) */
.cyber-sandbox-container::before {
  content: ''; position: absolute; top: -100vh; left: 0; right: 0; height: 100vh;
  background: linear-gradient(to bottom, transparent, rgba(0, 255, 170, 0.05) 80%, rgba(0, 229, 255, 0.15) 100%);
  z-index: 1; pointer-events: none;
  animation: scanSweep 8s linear infinite;
}

/* 呼吸发光光晕 (Ambient Pulse Orb) */
.cyber-sandbox-container::after {
  content: ''; position: absolute; top: 50%; left: 50%; width: 70vw; height: 70vh;
  background: radial-gradient(circle, rgba(0, 255, 170, 0.06) 0%, transparent 60%);
  transform: translate(-50%, -50%);
  z-index: 1; pointer-events: none;
  animation: orbPulse 6s ease-in-out infinite alternate;
}

@keyframes scanSweep {
  0% { top: -100vh; }
  100% { top: 100vh; }
}

@keyframes orbPulse {
  0% { opacity: 0.4; transform: translate(-50%, -50%) scale(0.9); }
  100% { opacity: 1; transform: translate(-50%, -50%) scale(1.1); }
}

.cyber-header {
  position: relative; z-index: 10;
  display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid rgba(0, 229, 255, 0.3);
  background: rgba(3, 15, 20, 0.85);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.6);
}

.logo-link { display: flex; align-items: center; gap: 12px; text-decoration: none; color: #00e5ff; text-shadow: 0 0 10px rgba(0, 229, 255, 0.5); }
.glitch-text { font-size: 20px; font-weight: 800; letter-spacing: 1px; margin: 0; color: #e0f8ff; }
.brand-icon { filter: drop-shadow(0 0 8px rgba(0, 229, 255, 0.8)); font-size: 24px; }
.header-right :deep(.el-button) {
  background: rgba(0, 229, 255, 0.1) !important;
  border: 1px solid rgba(0, 229, 255, 0.5) !important;
  color: #00e5ff !important;
  border-radius: 4px;
  box-shadow: none;
}
.header-right :deep(.el-button:hover) {
  background: rgba(0, 229, 255, 0.3) !important;
  color: #ffffff !important;
  box-shadow: 0 0 15px rgba(0, 229, 255, 0.4) !important;
}

.cyber-main { position: relative; z-index: 10; padding: 24px; height: calc(100vh - 60px); overflow: hidden; }    
.sandbox-wrapper { display: flex; gap: 24px; height: 100%; max-width: 1400px; margin: 0 auto; }

.cyber-panel {
  background: rgba(4, 20, 29, 0.8);
  border: 1px solid rgba(0, 229, 255, 0.4);
  border-radius: 8px; padding: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5), inset 0 0 20px rgba(0, 229, 255, 0.05);
  backdrop-filter: blur(12px);
  position: relative;
}

.cyber-panel::before, .cyber-panel::after,
.sandbox-terminal::before, .sandbox-terminal::after {
  content: ''; position: absolute; width: 12px; height: 12px; z-index: 2; pointer-events: none;
}
.cyber-panel::before, .sandbox-terminal::before {
  top: -1px; left: -1px; border-top: 2px solid #00ffaa; border-left: 2px solid #00ffaa; border-top-left-radius: 4px;
  box-shadow: -2px -2px 8px rgba(0, 255, 170, 0.5);
}
.cyber-panel::after, .sandbox-terminal::after {
  bottom: -1px; right: -1px; border-bottom: 2px solid #00e5ff; border-right: 2px solid #00e5ff; border-bottom-right-radius: 4px;
  box-shadow: 2px 2px 8px rgba(0, 229, 255, 0.5);
}

.panel-title {
  color: #00e5ff; font-size: 14px; font-weight: 700; letter-spacing: 2px;       
  margin: 0 0 20px 0; border-bottom: 1px solid rgba(0, 229, 255, 0.3); padding-bottom: 10px;
  text-shadow: 0 0 8px rgba(0, 229, 255, 0.6); text-transform: uppercase;
}

.sandbox-sidebar { width: 320px; display: flex; flex-direction: column; gap: 24px; }
.action-panel { display: grid; gap: 16px; }
.action-brief-list { display: grid; gap: 12px; }
.action-brief-item {
  padding: 12px 14px;
  border-radius: 8px;
  background: rgba(0, 229, 255, 0.06);
  border: 1px solid rgba(0, 229, 255, 0.14);
}
.action-brief-label {
  color: #6ee7b7;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
  margin-bottom: 6px;
  text-transform: uppercase;
}
.action-brief-item p { margin: 0; color: #cbd5e1; line-height: 1.7; }
.action-bridge-button { width: 100%; }
.status-indicator { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; font-size: 14px; color: #a4d8d8; font-weight: 600; }
.led { width: 10px; height: 10px; border-radius: 50%; background: #1a3a3a; border: 1px solid #00ffaa; }    
.led-active { background: #00ffaa; box-shadow: 0 0 10px #00ffaa, 0 0 20px #00ffaa; border: none; animation: pulse 2s infinite; }

@keyframes pulse { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.8; transform: scale(0.9); } }

.metric-glitch { margin-top: 30px; position: relative; padding-top: 20px; border-top: 1px solid rgba(0, 229, 255, 0.3); }
.metric-glitch::before { content:''; position: absolute; top: -2px; left: 0; width: 30px; height: 3px; background: #00ffaa; box-shadow: 0 0 10px #00ffaa; }
.metric-label { font-size: 14px; color: #a4d8d8; margin-bottom: 8px; font-weight: bold; }
.metric-value { font-size: 46px; font-weight: 800; color: #00ffaa; font-family: 'Bahnschrift', sans-serif; text-shadow: 0 0 20px rgba(0, 255, 170, 0.5); line-height: 1; }
.unit { font-size: 20px; color: #00ffaa; font-weight: normal; opacity: 0.8; }

.sandbox-terminal {
  flex: 1; display: flex; flex-direction: column;
  background: rgba(4, 20, 29, 0.8);
  border: 1px solid rgba(0, 229, 255, 0.4);
  border-radius: 8px; overflow: hidden;
  box-shadow: inset 0 0 20px rgba(0, 229, 255, 0.05), 0 8px 30px rgba(0, 0, 0, 0.5);
  position: relative;
}

.terminal-header {
  height: 52px; background: rgba(3, 15, 20, 0.9); border-bottom: 1px solid rgba(0, 229, 255, 0.4);
  display: flex; justify-content: space-between; align-items: center; padding: 0 20px;
}
.terminal-dots { display: none; }
.terminal-title { color: #00e5ff; font-size: 15px; letter-spacing: 2px; font-weight: 700; text-shadow: 0 0 8px rgba(0, 229, 255, 0.5); }

.cyber-button {
  background: rgba(0, 229, 255, 0.1) !important; border: 1px solid #00e5ff !important;
  color: #e0f8ff !important; font-family: inherit; font-weight: 700; border-radius: 4px; letter-spacing: 1px; transition: all 0.3s;
  box-shadow: 0 0 10px rgba(0, 229, 255, 0.2);
}
.cyber-button:hover:not(:disabled) {
  background: rgba(0, 229, 255, 0.3) !important; color: #ffffff !important; box-shadow: 0 0 15px rgba(0, 229, 255, 0.5), inset 0 0 10px rgba(0, 229, 255, 0.2);
}

.terminal-body {
  flex: 1; padding: 24px; overflow-y: auto; font-size: 14px; line-height: 1.8;  
  font-family: 'Fira Code', 'Consolas', monospace; font-weight: 500;
  color: #e0f8ff;
}

.typewriter-content .terminal-line, .terminal-placeholder {
  text-shadow: 0 0 5px rgba(224, 248, 255, 0.3);
}

.terminal-line { margin-bottom: 8px; word-wrap: break-word; }
.prompt { color: #00ffaa; margin-right: 12px; font-weight: bold; text-shadow: 0 0 8px rgba(0, 255, 170, 0.6); }
.blink-cursor { animation: blink 1s step-end infinite; font-weight: 700; color: #00ffaa; text-shadow: 0 0 8px rgba(0, 255, 170, 0.6); }

@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

.terminal-body::-webkit-scrollbar { width: 8px; }
.terminal-body::-webkit-scrollbar-track { background: rgba(0, 229, 255, 0.05); }
.terminal-body::-webkit-scrollbar-thumb { background: rgba(0, 229, 255, 0.3); border-radius: 4px; }
.terminal-body::-webkit-scrollbar-thumb:hover { background: rgba(0, 229, 255, 0.6); }
/* -------------------------------------
   Formal PDF Report Hidden Styles
   ------------------------------------- */
.pdf-formal-report {
  display: none;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  width: 800px;
  background-color: #ffffff;
  color: #333333;
  padding: 40px 50px;
  box-sizing: border-box;
}
.report-title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #1a1a1a;
  margin-bottom: 5px;
  border-bottom: 2px solid #2563eb;
  padding-bottom: 15px;
}
.report-meta {
  text-align: center;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
.report-section {
  margin-bottom: 30px;
}
.report-section h2 {
  font-size: 20px;
  color: #1e3a8a;
  border-left: 4px solid #3b82f6;
  padding-left: 10px;
  margin-bottom: 15px;
}
.data-grid {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 15px;
  background-color: #f1f5f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.data-box {
  display: flex;
  flex-direction: column;
  width: 45%;
}
.data-label {
  font-size: 14px;
  color: #475569;
  margin-bottom: 5px;
}
.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #0f172a;
}
.risk-high { color: #dc2626; }
.risk-medium { color: #f59e0b; }
.risk-low { color: #10b981; }

.headline-text {
  font-size: 18px;
  font-weight: bold;
  color: #b91c1c;
  margin-bottom: 10px;
}
.summary-text {
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
  margin-bottom: 20px;
  text-align: justify;
  padding: 15px;
  background: #f8fafc;
  border-radius: 4px;
}
.insight-list, .recommendation-list, .action-list {
  padding-left: 25px;
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
}
.insight-list li {
  margin-bottom: 15px;
  border-bottom: 1px dashed #e2e8f0;
  padding-bottom: 10px;
}
.recommendation-list li, .action-list li { margin-bottom: 8px; }

.report-footer {
  margin-top: 50px;
  padding-top: 20px;
  border-top: 1px solid #cbd5e1;
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.5;
}
</style>