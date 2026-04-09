<div align="center">
  
# 🌍 碳足迹追踪平台 (Carbon Footprint Tracker)

<p align="center">
	<img src="https://img.shields.io/badge/Vue-3.5.13-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white" alt="Vue" />
	<img src="https://img.shields.io/badge/TypeScript-5.6.2-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" />
	<img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
	<img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
</p>

**🌱 一个面向个人、企业和管理员的现代化碳足迹追踪与管理平台**

支持 **排放记录**、**趋势分析**、**减排建议**、**🤖 AI 智能分析** 和 **📊 数据大屏**

<p align="center">
	<a href="#-项目亮点">✨ 项目亮点</a> · 
	<a href="#-快速开始">🚀 快速开始</a> · 
	<a href="#-功能地图">🗺️ 功能地图</a> · 
	<a href="#-技术栈">🛠️ 技术栈</a>
</p>

</div>

---

## ✨ 项目亮点

- **🎯 全场景覆盖**：记录交通、饮食、用电三类核心碳排放，形成完整的个人低碳画像。
- **📊 多维度可视化**：提供丰富的本地仪表盘，包含趋势图、统计卡片、排行报表及绿色积分激励。
- **✅ 行动导向**：减排建议支持“一键采纳”、状态跟踪更新与目标跟进，将减排落到实处。
- **🧠 AI 智能赋能**：集成 AI 助手，对用户的碳足迹数据进行深度解读与个性化减排报告生成。
- **🏢 企业与总控视角**：企业端与管理端提供全局数据大屏、区域分布热力视图和高阶分析能力。
- **⚡ 现代化架构**：前后端分离架构，代码结构优雅，内置一键启动脚本，开发与部署极致丝滑。

## 🗺️ 功能地图

### 👤 个人用户 (User)
- 🏠 **首页引导**：快捷浏览、登录/注册、个人主页资料管理
- 📝 **排放记录**：支持精细化的**交通**（自驾/公交/地铁）、**饮食**、**用电**排放录入
- 📈 **数据闭环**：个人仪表盘展示历史趋势、分析报表与积分获取
- 💡 **减排计划**：浏览推荐的减排措施，管理已采纳任务的执行状态
- 🤖 **AI 深度洞察**：根据近期碳足迹生成多维度的 AI 评估与专属建议

### 🏢 企业用户 (Enterprise)
- 👁️ **企业级仪表盘**：全天候监控企业整体碳排放指标
- 📉 **全局大屏与区域分发**：支持重点地区指标的可视化趋势追踪

### 👑 系统管理员 (Admin)
- 👥 **用户与权限体系**：集中的角色及账号生命周期管理
- 🌐 **全局监控**：平台维度的数据看板与系统状态监控

## 🛠️ 技术栈

### 🎨 前端 (Frontend)
- **核心框架**: Vue 3 (Composition API) 🟢
- **语言**: TypeScript 📘
- **构建工具**: Vite ⚡
- **UI 组件库**: Element Plus 💅
- **图表展示**: ECharts 📊
- **状态管理**: Pinia (含本地持久化) 🍍
- **路由管理**: Vue Router 🚥

### ⚙️ 后端 (Backend)
- **核心框架**: Spring Boot 3.3.4 🍃
- **安全认证**: Spring Security + JWT 🛡️
- **持久层**: Spring Data JPA 💾
- **数据库**: MySQL 8.0 🐬
- **缓存**: Redis 🔴
- **接口文档**: Springdoc OpenAPI (Swagger) 📄

---

## 🚀 快速开始

### 1. 环境准备与依赖安装

```bash
# 进入前端目录并安装依赖
cd frontend
npm install
```

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

- 🤖 **AI 服务配置**：如需自定义或更换大模型服务，可以通过配置环境变量 `AI_PROVIDER`、`AI_BASE_URL`、`AI_MODEL` 及 `AI_API_KEY` 来灵活接入（如 OpenAI / 通义千问 / DeepSeek 等）。
- 🔄 **迭代与部署**：如遇前后端同步更新，请确保首先执行完整的 `npm run build` 和 backend `mvn clean package`，再进行产物替换或容器化更新。

<p align="center">
  <br/>
  <i>Made with ❤️ by Carbon Tracker Team</i>
</p>
