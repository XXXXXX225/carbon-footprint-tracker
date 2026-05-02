<div align="center">
  
# 🌍  AI 驱动的新一代碳足迹追踪平台

<p align="center">
	<img src="https://img.shields.io/badge/Vue-3.5.13-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white" alt="Vue" />
	<img src="https://img.shields.io/badge/TypeScript-5.6.2-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" />
	<img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
	<img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
</p>

**🌱 一个面向个人、企业和管理员的现代化碳足迹追踪与管理平台**

首创 **LLM-Driven Chat-to-Track** 零成本录入，支持 **图像解析**、**动态联网测算**、**趋势分析** 与 **📊 数据大屏**

<p align="center">
	<a href="#-核心创新与亮点">✨ 核心创新</a> · 
	<a href="#-快速开始">🚀 快速开始</a> · 
	<a href="#-功能地图">🗺️ 功能地图</a> · 
	<a href="#-技术架构">🛠️ 技术架构</a>
</p>

</div>

---

## ✨ 核心创新与亮点

- **🧠 首创 Chat-to-Track (对话即追踪)**：彻底抛弃繁琐的传统表单。用户只需输入一句话（如“今天坐了10站地铁”）或拖拽一张电费账单/行程截图，平台即可通过多模态大模型自动提取、分类并记录碳排放数据。
- **🌐 动态联网计算引擎 (Web Search)**：打破传统碳计算器“死板映射”的局限。底层 AI 引擎集成联网搜索能力，支持任意长尾物品（如“一件纯棉T恤”、“一只澳洲龙虾”）的实时碳排放因子检索与动态数学推演。
- **🛡️ 防弹容错架构设计**：后端针对大模型 JSON 幻觉（如 Markdown 标记污染、字段偏移）构建了强鲁棒性的正则清洗与策略容错机制，确保业务流转 100% 稳定。
- **🎨 沉浸式极客视觉体验**：前端采用现代化的绿色拟态玻璃风格，结合 Canvas/WebGL 打造动态碳足迹粒子拖尾特效，提供极佳的交互沉浸感。
- **🎯 全场景闭环管理**：除日常的交通、饮食、用电排放录入外，平台提供丰富的本地仪表盘展示趋势图、统计卡片及绿色积分激励，并支持减排建议的“一键采纳”与状态跟进。

## 🗺️ 功能地图

### 👤 个人用户 (User)
- 🏠 **首页引导**：快捷浏览、登录/注册、个人主页资料管理
- 🎙️ **智能录入 (核心)**：支持纯文本自然语言输入与图片/截图拖拽的多模态碳足迹自动测算。
- 📝 **精细化手录**：作为兜底方案，支持传统交通、饮食、用电的精细化表单录入。
- 📈 **数据闭环**：个人仪表盘展示历史趋势、分析报表与积分获取
- 💡 **减排计划**：浏览推荐的减排措施，管理已采纳任务的执行状态
- 🤖 **AI 深度洞察**：根据近期碳足迹生成多维度的 AI 评估与专属建议

### 🏢 企业端与系统管理 (Enterprise & Admin)
- 👁️ **企业级仪表盘**：全天候监控企业整体碳排放指标
- 📉 **全局大屏与区域分发**：支持重点地区指标的可视化趋势追踪
- 👥 **用户与权限体系**：集中的角色及账号生命周期管理
- 🌐 **全局监控**：平台维度的数据看板与系统状态监控

## 🛠️ 技术架构

### 🎨 前端 (Frontend)
- **核心框架**: Vue 3 (Composition API) 🟢
- **语言**: TypeScript 📘
- **构建工具**: Vite ⚡
- **UI 组件与动效**: Element Plus 💅 / 原生 Canvas 粒子引擎
- **图表展示**: ECharts 📊
- **状态管理**: Pinia (含本地持久化) 🍍
- **路由管理**: Vue Router 🚥

### ⚙️ 后端 (Backend)
- **核心框架**: Spring Boot 3.3.4 🍃
- **安全认证**: Spring Security + JWT 🛡️
- **持久层**: Spring Data JPA 💾 / MyBatis Plus
- **数据库**: MySQL 8.0 🐬
- **高并发缓存**: Redis (精准 Evict 策略保证看板数据一致性) 🔴
- **接口文档**: Springdoc OpenAPI (Swagger) 📄

---

## 🚀 快速开始

### 1. 环境准备与依赖安装
```bash
# 进入前端目录并安装依赖
cd frontend
npm install

### 2. 启动服务

**🔌 启动后端：**

后端默认配置详见: [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml)。

```bash
cd backend
mvn spring-boot:run
```

**💻 启动前端：**

```bash
cd frontend
npm run dev
```

### 3. 访问系统

🎉 服务启动后，可通过以下地址访问：
- **💻 前端界面**：[http://localhost:5173](http://localhost:5173) 
- **🔌 后端接口**：[http://localhost:8888/api](http://localhost:8888/api)

---

## 📦 生产部署

**构建前端产物：**

```bash
cd frontend
npm run build
```
*(后端生产配置详见 [application-prod.yml](backend/src/main/resources/application-prod.yml))*

### 💡 默认配置信息

| 配置项           | 默认值                                  | 说明                                    |
| ---------------- | --------------------------------------- | --------------------------------------- |
| **后端端口**     | `8888`                                  | Spring Boot 默认占用端口                |
| **前端开发端口** | `5173`                                  | Vite 默认占用端口                       |
| **默认数据库**   | `carbonfootprint`                       | MySQL 数据库名称                        |
| **默认管理员**   | `admin` / `123456`<br>`root` / `123456` | 系统默认账号密码 (参考数据库初始化内容) |

---

## 📂 目录结构

```text
.
├── frontend/         # 🎨 前端源码目录 (Vue + TS)
├── public/           # 🌐 前端独立公共资源 (如有)
├── backend/          # ⚙️ 后端源码工程 (Spring Boot)
├── 作品/              # 📦 最终可运行构建产物存放区
├── 启动项目.bat       # 🚀 Windows 本地一键启动脚本
└── 停止项目.bat       # 🛑 Windows 本地一键关闭脚本
```

---

## 📝 进阶说明

🤖 大模型接入：本项目重度依赖 LLM 驱动核心功能。可以通过配置 application.yml 或系统环境变量 AI_PROVIDER、AI_BASE_URL、AI_MODEL 及 AI_API_KEY 来灵活接入外部模型。

🔧 视觉与搜索工具：确保配置的模型支持图片解析，且模型 API 平台开通了 Web Search 权限，以获得最佳的长尾物品计算体验。

🔄 迭代与部署：如遇前后端同步更新，请确保首先执行完整的 npm run build 和 backend mvn clean package，再进行产物替换或容器化更新。

<p align="center">
  <br/>
  <i>Made with ❤️ by Carbon Tracker Team</i>
</p>
