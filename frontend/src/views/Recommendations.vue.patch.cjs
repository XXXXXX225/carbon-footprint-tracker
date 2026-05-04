const fs = require('fs');
let file = 'D:/cfp/frontend/src/views/Recommendations.vue';
let text = fs.readFileSync(file, 'utf8');

text = text.replace(/const COMPLETED_TASKS_KEY = 'action_plan_completed_ids'/, 'const COMPLETED_TASKS_KEY = \'action_plan_completed_ids\'\nconst HISTORY_TASKS_KEY = \'action_plan_history_items\'\nconst historyItems = ref([])\nlet generateCount = parseInt(localStorage.getItem(\'action_plan_gen_count\') || \'0\')');
text = text.replace(/const completedItems = computed\(\(\) => planItems\.value\.filter\(item => item\.completed\)\)/, 'const completedItems = computed(() => historyItems.value)');
text = text.replace(/const pendingItems = computed\(\(\) => planItems\.value\.filter\(item => !item\.completed\)\)/, 'const pendingItems = computed(() => planItems.value)');
text = text.replace(/return Math\.round\(\(completedItems\.value\.length \/ planItems\.value\.length\) \* 100\)/, 'const total = planItems.value.length + historyItems.value.length\n    return total === 0 ? 0 : Math.round((historyItems.value.length / total) * 100)');

text = text.replace(/const loadCompletedIdSet = \(\): Set<number> => \{/, \
const saveHistoryItems = () => { localStorage.setItem(HISTORY_TASKS_KEY, JSON.stringify(historyItems.value)) }
const loadHistoryItems = () => { try { const raw = localStorage.getItem(HISTORY_TASKS_KEY); if (raw) return JSON.parse(raw) } catch (e) {} return [] }
const loadCompletedIdSet = (): Set<number> => {\);

let newBuildPlanItems = \const buildPlanItems = () => {
  const notes = extractNotes()
  const transportEmission = summary.value?.transportEmission || 0
  const dietEmission = summary.value?.dietEmission || 0
  const electricityEmission = summary.value?.electricityEmission || 0

  const transportImpact = Math.max(2, Number(((transportEmission || reductionGap.value * 0.3)).toFixed(1)))
  const dietImpact = Math.max(1, Number(((dietEmission || reductionGap.value * 0.25)).toFixed(1)))
  const electricityImpact = Math.max(1, Number(((electricityEmission || reductionGap.value * 0.2)).toFixed(1)))

  generateCount++
  localStorage.setItem('action_plan_gen_count', generateCount.toString())
  const sid = generateCount * 100

  const baseItems: PlanItem[] = [
    { id: sid + 1, category: '交通', title: '低碳出行目标 #' + generateCount, detail: '用公共交通或步行替代燃油车出行', impact: transportImpact, effort: '低', deadline: '本周', completed: false, source: '系统规则' },
    { id: sid + 2, category: '饮食', title: '健康低碳饮食 #' + generateCount, detail: '减少高碳排放的红肉摄入', impact: dietImpact, effort: '低', deadline: '本周', completed: false, source: '系统规则' },
    { id: sid + 3, category: '用电', title: '节能行动 #' + generateCount, detail: '及时关闭不必要的电源和设备', impact: electricityImpact, effort: '中', deadline: '周末', completed: false, source: '系统规则' }
  ]

  notes.slice(0, 2).forEach((note, index) => {
    baseItems.unshift({
      id: sid + 10 + index, category: '习惯', title: index === 0 ? 'AI 优先建议' : 'AI 补充建议', detail: note,
      impact: Math.max(1, Number((reductionGap.value * 0.15).toFixed(1)) || 1), effort: '低', deadline: '今日', completed: false, source: 'AI 分析'
    })
  })
  planItems.value = baseItems
}\;
text = text.replace(/const buildPlanItems = \(\) => \{[\s\S]*?completedState\.has\(item\.id\)\r?\n\s*\}\)\)\r?\n\}/, newBuildPlanItems);

text = text.replace(/const loadData = async \(\) => \{/, \const loadData = async () => { historyItems.value = loadHistoryItems() \);

const newToggleTask = \const toggleTask = async (id: number) => {
  const index = planItems.value.findIndex(item => item.id === id)
  if (index > -1) {
    const item = planItems.value[index]
    item.completed = true
    historyItems.value.unshift({...item})
    planItems.value.splice(index, 1)
    saveHistoryItems()
    ElMessage.success('任务已完成并归档')
  }

  if (planItems.value.length === 0) {
    ElMessage.success('恭喜！当前所有的行动记录均已完成，正在生成新计划...')
    await regeneratePlan()
  }
}\;
text = text.replace(/const toggleTask = async \(id: number\) => \{[\s\S]*?await regeneratePlan\(\)\r?\n\s*\}/, newToggleTask);

const newConfirmTask = \const confirmReopenTask = (task: PlanItem) => {
  ElMessageBox.confirm(
    '确定要将任务重新移回待办清单吗？', '确认操作', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'}
  ).then(() => {
    const index = historyItems.value.findIndex(item => item.id === task.id)
    if (index > -1) {
      historyItems.value.splice(index, 1)
      saveHistoryItems()
    }
    task.completed = false
    planItems.value.push(task)
    ElMessage.success('任务已重新打开')
  }).catch(() => {})
}\;
text = text.replace(/const confirmReopenTask = \(task: PlanItem\) => \{[\s\S]*?\}\)\.catch\(\(\) => \{\}\)\r?\n\}/, newConfirmTask);

const newResetCompleted = \const resetCompleted = () => {
  historyItems.value = [];
  saveHistoryItems();
  ElMessage.success('已完成记录已清空');
}\;
text = text.replace(/const resetCompleted = \(\) => \{[\s\S]*?宸插畬鎴愯繘搴﹀凡閲嶇疆'\)\r?\n\}/, newResetCompleted);
text = text.replace(/const resetCompleted = \(\) => \{[\s\S]*?已完成进度已重置'\)\r?\n\}/, newResetCompleted);

fs.writeFileSync(file, text, 'utf8');
console.log('Update finished.');
