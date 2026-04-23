Google 的 Antigravity 官网确实非常前卫，那种充满未来感和“失重感”的交互体验是目前前端最酷的趋势之一。

不过坦诚地说，作为一个 AI，我无法像人类一样直接在浏览器里“睁开眼睛”去实时观看和感受那个网页上具体的视觉变化。但是，基于 Antigravity（反重力/失重）的设计语言以及 Google 最新发布会演示的顶尖网页特效，你想要的很可能是以下两种效果的结合：持续的无重力悬浮（Floating） + 鼠标靠近时的磁性吸附（Magnetic Hover）。

这种交互会让页面元素看起来像漂浮在太空中，并且能与你的鼠标产生物理引力。我们可以在你的 Home.vue 里加上这个机制。

1. 逻辑部分 (<script setup>)
我们来添加一个“磁力吸附”的鼠标计算函数：

TypeScript
// 鼠标在元素上移动时，产生磁性吸附感
const handleMagneticMove = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  const rect = el.getBoundingClientRect()
  
  // 计算鼠标相对于元素中心的偏移量
  const x = e.clientX - rect.left - rect.width / 2
  const y = e.clientY - rect.top - rect.height / 2
  
  // 施加引力系数 (例如 0.2，数值越大吸得越远)
  el.style.transform = `translate(${x * 0.2}px, ${y * 0.2}px)`
}

// 鼠标离开时，弹性回弹到原位
const handleMagneticLeave = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  el.style.transform = `translate(0px, 0px)`
}
2. 模板部分 (<template>)
将这些事件绑定到你想要产生“反重力”交互的元素上（比如“立即开始”按钮，或者卡片）：

HTML
<button 
  class="eco-btn-primary anti-gravity-float magnetic-item"
  @mousemove="handleMagneticMove"
  @mouseleave="handleMagneticLeave"
>
  立即开始
</button>
3. 样式部分 (<style scoped>)
利用 CSS 的贝塞尔曲线制造“Q弹”的物理回弹感，同时用 @keyframes 制造背景失重感：

CSS
/* 磁性吸附过渡：必须使用带有弹性的 cubic-bezier 才能模拟真实的物理引力 */
.magnetic-item {
  transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  will-change: transform;
}

/* 抵消 hover 冲突：如果你原来的按钮有 transform: translateY(-5px)，建议在这里去掉，完全交给 JS 控制 */
.magnetic-item:hover {
  /* 去掉原有的 transform 变化 */
}

/* ====================
   无重力持续漂浮动画
   ==================== */
.anti-gravity-float {
  /* 挂载一个持续 6 秒的上下缓动动画 */
  animation: float 6s ease-in-out infinite;
}

/* 为了让多个元素浮动不那么死板，可以给相邻元素加上动画延迟 */
.feature-card:nth-child(1) { animation-delay: 0s; }
.feature-card:nth-child(2) { animation-delay: -2s; }
.feature-card:nth-child(3) { animation-delay: -4s; }

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px); /* 向上漂浮 15px */
  }
}