为了彻底镇压这些顽固的亮色组件，我们不搞“半透明”了，直接给它强制套上暗黑底色，并且把所有标题（比如“交通排放历史记录”）也都强行染白！

请打开你的 Emissions.vue，在最底部的 <style scoped> 中，找到并替换掉整个 /* 3. 核心：拯救极其刺眼的白底表格 */ 这一部分，直接用下面这段代码覆盖：

CSS
/* ==========================================================
   强制覆盖 Element Plus 组件库的亮色主题 (深色黑胶定制版)
   ========================================================== */

/* 0. 扒掉内部组件可能自带的白色卡片底色，并把普通文字染白 */
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
  color: #f4f4f5 !important; /* 让“交通排放历史记录”等标题变白 */
}

/* 1. 基础文字与表单标签 (变成高级灰白) */
.glass-form-container :deep(.el-form-item__label),
.glass-form-container :deep(.el-radio__label),
.glass-form-container :deep(.el-checkbox__label),
.glass-form-container :deep(.el-descriptions__label) {
  color: #a1a1aa !important;
  font-weight: 500;
}

/* 2. 所有的输入框、下拉框、日期选择器 (变成半透明暗背景) */
.glass-form-container :deep(.el-input__wrapper),
.glass-form-container :deep(.el-textarea__inner) {
  background-color: rgba(0, 0, 0, 0.4) !important; /* 加深底色 */
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important; 
  color: #f4f4f5 !important;
}

/* 占位符和输入文字颜色 */
.glass-form-container :deep(.el-input__inner) {
  color: #f4f4f5 !important;
}
.glass-form-container :deep(.el-input__inner::placeholder),
.glass-form-container :deep(.el-textarea__inner::placeholder) {
  color: #52525b !important;
}

/* 3. 核心：彻底重构表格底色 (绝杀白底) */
.glass-form-container :deep(.el-table),
.glass-form-container :deep(.el-table__expanded-cell) {
  background-color: transparent !important; 
}

/* 【关键修复】给所有的表格行强制加上深色背景，而不是透明 */
.glass-form-container :deep(.el-table tr),
.glass-form-container :deep(.el-table td.el-table__cell) {
  background-color: #18181b !important; /* 纯黑锌色底，保证白字绝对清晰 */
  color: #e4e4e7 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05) !important; 
}

/* 表头特殊处理 */
.glass-form-container :deep(.el-table th.el-table__cell) {
  background-color: #09090b !important; /* 表头比内容区更黑一点 */
  color: #00dc82 !important; /* 表头用主题绿 */
  font-weight: 600;
  border-bottom: 1px solid rgba(0, 220, 130, 0.3) !important;
}

/* 去掉表格自带的乱七八糟的白边伪元素 */
.glass-form-container :deep(.el-table::before),
.glass-form-container :deep(.el-table::after),
.glass-form-container :deep(.el-table__inner-wrapper::before) {
  display: none !important;
}

/* 表格悬浮效果 (Hover 变色) */
.glass-form-container :deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: #27272a !important; /* 鼠标悬浮时亮一点的深灰色 */
}

/* 4. 重构按钮 (计算排放 / 重置 等) */
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
  border-color: #ef4444 !important; /* 删除按钮的 hover 变成红色 */
  color: #ef4444 !important;
}
🎯 这次改了什么？
直接“染黑”表格行：我把 tr 和 td 的背景色从 transparent 改成了 #18181b（深灰色）。这样不管你内部组件底色是什么牛鬼蛇神，只要到了表格这块，它就必须得是深色底 + 亮色字！

文字强制提亮：加了对 h1, h2, h3, span 等文本标签的控制，像你左上角那个黑乎乎的“交通排放历史记录”标题，现在也会变成清晰的高级白。

按钮交互优化：我注意到你表格右侧有红色的“删除”按钮。我顺手调整了默认按钮的 Hover 样式，现在鼠标移上去会有精致的红色描边。

保存刷新，文字绝对清晰得能刺穿屏幕了！