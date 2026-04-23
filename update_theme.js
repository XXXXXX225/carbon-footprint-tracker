const fs = require('fs');
const path = 'src/views/AIAnalysis.vue';
let content = fs.readFileSync(path, 'utf8');

// Update js inline colors for the deep premium theme
content = content.replace(/color:#047857/g, 'color:#34d399'); // success tag 
content = content.replace(/color:#0f766e/g, 'color:#a7f3d0'); // details
content = content.replace(/color:#022c22/g, 'color:#f8fafc'); // text

const styles = \<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600&display=swap');

/* =========================================
   Eco-Cyber Premium Deep Theme 
   ========================================= */
.cyber-sandbox-container {
  height: 100vh;
  /* 稍微深一点的高级渐变色：深空青石到暗翡翠的渐变，充满高级碳足迹数字大屏感 */
  background: linear-gradient(135deg, #1e293b 0%, #064e3b 100%);
  color: #f8fafc;
  font-family: inherit;
}

.cyber-header {
  display: flex; justify-content: space-between; align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(16px);
}

.logo-link { display: flex; align-items: center; gap: 12px; text-decoration: none; color: #f8fafc; }
.glitch-text { font-size: 20px; font-weight: 800; letter-spacing: 1px; margin: 0; color: #e2e8f0; }
.cyber-main { padding: 24px; height: calc(100vh - 60px); overflow: hidden; }    
.sandbox-wrapper { display: flex; gap: 24px; height: 100%; max-width: 1400px; margin: 0 auto; }

.cyber-panel {
  background: rgba(30, 41, 59, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px; padding: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(12px);
}

.panel-title {
  color: #94a3b8; font-size: 14px; font-weight: 700; letter-spacing: 1px;       
  margin: 0 0 20px 0; border-bottom: 2px dotted rgba(255, 255, 255, 0.1); padding-bottom: 10px;
}

.sandbox-sidebar { width: 320px; display: flex; flex-direction: column; gap: 24px; }
.status-indicator { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; font-size: 14px; color: #cbd5e1; font-weight: 600; }
.led { width: 12px; height: 12px; border-radius: 50%; background: #334155; }    
.led-active { background: #10b981; box-shadow: 0 0 12px rgba(16, 185, 129, 0.6); animation: pulse 2s infinite; }

@keyframes pulse { 0% { opacity: 1; } 50% { opacity: 0.5; } 100% { opacity: 1; } }

.metric-glitch { margin-top: 30px; }
.metric-label { font-size: 13px; color: #94a3b8; margin-bottom: 8px; font-weight: bold; }
.metric-value { font-size: 36px; font-weight: 800; color: #34d399; font-family: 'Bahnschrift', sans-serif; text-shadow: 0 0 15px rgba(52, 211, 153, 0.2); }
.unit { font-size: 15px; color: #cbd5e1; font-weight: normal; }

.sandbox-terminal {
  flex: 1; display: flex; flex-direction: column;
  background: rgba(15, 23, 42, 0.7);
  border: 1px solid rgba(52, 211, 153, 0.2);
  border-radius: 16px; overflow: hidden;
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.2), 0 8px 30px rgba(0, 0, 0, 0.3);
}

.terminal-header {
  height: 52px; background: rgba(30, 41, 59, 0.8); border-bottom: 1px solid rgba(52, 211, 153, 0.2);
  display: flex; justify-content: space-between; align-items: center; padding: 0 20px;
}
.terminal-dots span { display: inline-block; width: 14px; height: 14px; border-radius: 50%; margin-right: 8px; opacity: 0.8; }
.terminal-dots span:nth-child(1) { background: #ef4444; } 
.terminal-dots span:nth-child(2) { background: #f59e0b; } 
.terminal-dots span:nth-child(3) { background: #10b981; } 
.terminal-title { color: #64748b; font-size: 14px; letter-spacing: 1px; font-weight: 700; }

.cyber-button {
  background: transparent !important; border: 1px solid #10b981 !important;
  color: #10b981 !important; font-weight: 700; border-radius: 8px; letter-spacing: 1px; transition: all 0.3s;
}
.cyber-button:hover:not(:disabled) {
  background: rgba(16, 185, 129, 0.1) !important; color: #34d399 !important; box-shadow: 0 0 15px rgba(16, 185, 129, 0.3);
}

.terminal-body {
  flex: 1; padding: 24px; overflow-y: auto; font-size: 15px; line-height: 1.8;  
  color: #a7f3d0; 
  font-family: 'Fira Code', 'Consolas', monospace; font-weight: 500;
}

.terminal-placeholder { color: #475569; font-style: italic; font-weight: 400; }
.terminal-line { margin-bottom: 10px; word-wrap: break-word; }
.prompt { color: #f59e0b; margin-right: 12px; font-weight: bold; } 
.blink-cursor { animation: blink 1s step-end infinite; font-weight: 700; color: #10b981; }

@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }

.terminal-body::-webkit-scrollbar { width: 10px; }
.terminal-body::-webkit-scrollbar-track { background: transparent; }
.terminal-body::-webkit-scrollbar-thumb { background: #334155; border-radius: 5px; }
.terminal-body::-webkit-scrollbar-thumb:hover { background: #475569; }
</style>\;

content = content.replace(/<style scoped>[\s\S]*<\/style>/, styles);
fs.writeFileSync(path, content, 'utf8');
