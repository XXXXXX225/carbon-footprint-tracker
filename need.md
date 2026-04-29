要实现这种顶级大厂（如 Apple、Vercel 等）爱用的“沉浸式交互景深（Parallax Depth）”，我们完全不需要引入复杂的物理引擎。

核心思路是利用多图层视差（Parallax Layers）：我们把极光、点阵、微尘分别放在不同的层级里。通过捕获全局的鼠标坐标和滚动条高度（Scroll Y），利用 CSS calc() 让不同图层以不同的速率和方向进行位移。

再加上一个跟随鼠标的混合模式探照灯（Spotlight），就能营造出极致的 3D 空间感和交互反馈。

以下是具体的代码修改：

1. 修改 HTML 结构（进行分层）
找到你刚才加的 .aurora-bg，将其替换为包含视差分层的结构：

HTML
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
2. 修改 Script 逻辑（加入滚动监听）
在 <script setup> 中，你已经有了 handleMouseMove 来计算鼠标位置。现在我们只需要加上滚动条监听，把滚动距离也暴露给 CSS 变量。

JavaScript
// === 在 script 顶部区域新增滚动处理逻辑 ===
const handleScroll = () => {
  // 将滚动距离作为单纯的数字传递给全局 CSS 变量
  document.documentElement.style.setProperty('--scroll-y', window.scrollY.toString())
}

// === 在原有的 onMounted 中追加事件绑定 ===
onMounted(() => {
  // ... 其他代码保留 ...
  window.addEventListener('scroll', handleScroll)
})

// === 在原有的 onUnmounted 中移除事件绑定 ===
onUnmounted(() => {
  // ... 其他代码保留 ...
  window.removeEventListener('scroll', handleScroll)
})
注：你的 handleMouseMove 原本就已经输出了 --mouse-x, --mouse-y, --mouse-norm-x, --mouse-norm-y，完美符合我们的计算需求，无需改动。

3. 修改 CSS（添加视差与计算公式）
将这段代码追加或替换到 <style scoped> 中：

CSS
/* ==========================================
   交互式全息背景容器
========================================== */
.aurora-bg {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background-color: #f0fdf4;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

/* 视差层基类：放大 20% 防止移动时边缘露底，并加入丝滑的物理惯性阻尼 */
.parallax-layer {
  position: absolute;
  top: -10%; left: -10%;
  width: 120%; height: 120%;
  will-change: transform;
  transition: transform 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

/* 1. 极光层视差计算 */
.layer-bg {
  transform: translate3d(
    calc(var(--mouse-norm-x, 0) * 30px),
    calc(var(--mouse-norm-y, 0) * 30px - var(--scroll-y, 0) * 0.15px),
    0
  );
}

/* 2. 点阵层视差计算 (反向移动) */
.layer-grid {
  transform: translate3d(
    calc(var(--mouse-norm-x, 0) * -15px),
    calc(var(--mouse-norm-y, 0) * -15px - var(--scroll-y, 0) * 0.05px),
    0
  );
}

/* 3. 微尘层视差计算 (强烈的上下滚动反馈) */
.layer-motes {
  transform: translate3d(
    calc(var(--mouse-norm-x, 0) * 10px),
    calc(var(--mouse-norm-y, 0) * 10px - var(--scroll-y, 0) * 0.35px),
    0
  );
}

/* ==========================================
   鼠标混合探照灯 (高级玻璃光泽)
========================================== */
.interactive-spotlight {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  /* 跟随鼠标的光晕 */
  background: radial-gradient(
    circle 800px at var(--mouse-x, 50vw) var(--mouse-y, 50vh),
    rgba(255, 255, 255, 0.6),
    transparent 70%
  );
  /* 使用 overlay 模式，能够“提亮”底部的颜色和网格，就像手电筒扫过一样 */
  mix-blend-mode: overlay; 
  z-index: 2;
}

/* ==========================================
   内部动画细节（保留你之前的极光和微尘动画样式）
========================================== */
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