# 碳足迹追踪平台

<p align="center">
	<img src="https://img.shields.io/badge/Vue-3.5.13-4FC08D?style=flat-square&logo=vue.js&logoColor=white" alt="Vue" />
	<img src="https://img.shields.io/badge/TypeScript-5.6.2-3178C6?style=flat-square&logo=typescript&logoColor=white" alt="TypeScript" />
	<img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot" />
	<img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL" />
</p>

<p align="center">
	一个面向个人、企业和管理员的碳足迹追踪平台，支持排放记录、趋势分析、减排建议、AI 分析和数据大屏。
</p>

<p align="center">
	<a href="#项目亮点">项目亮点</a> · <a href="#快速开始">快速开始</a> · <a href="#功能地图">功能地图</a> · <a href="#技术栈">技术栈</a>
</p>

## 项目亮点

- 记录交通、饮食、用电三类碳排放，形成完整的个人低碳画像
- 个人仪表盘提供趋势图、统计卡片、报表导出和积分激励
- 减排建议支持采纳、状态更新和执行跟踪
- AI 分析页可对碳足迹数据做解读与建议生成
- 企业与管理员可查看数据大屏、区域分布和更高阶的分析视图
- 前后端分离，支持本地开发与生产部署

## 功能地图

### 个人用户
- 首页浏览、登录注册、个人资料
- 交通、饮食、用电排放记录
- 仪表盘、趋势图、报表和积分
- 减排建议查看与采纳状态管理
- AI 分析与智能推荐

### 企业用户
- 企业级仪表盘
- 数据大屏与区域统计
- 重点指标和趋势分析

### 管理员
- 用户与角色管理
- 全局数据查看
- 管理端页面与监控能力

## 技术栈

### 前端
- Vue 3
- TypeScript
- Vite
- Element Plus
- ECharts
- Pinia
- Vue Router

### 后端
- Spring Boot 3.3.4
- Spring Security
- JPA
- MySQL
- Redis
- JWT
- Springdoc OpenAPI

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动后端

后端默认配置位于 [backend/src/main/resources/application.yml](backend/src/main/resources/application.yml)。

```bash
cd backend
mvn spring-boot:run
```

### 3. 启动前端

```bash
npm run dev
```

### 4. 访问地址

- 前端：http://localhost:5173
- 后端：http://localhost:8888/api

## 生产构建

```bash
npm run build
```

后端生产配置见 [backend/src/main/resources/application-prod.yml](backend/src/main/resources/application-prod.yml)。

## 默认配置

- 后端端口：`8888`
- 默认数据库：`carbonfootprint`
- 默认账号：`root / 123456`
- 前端开发端口：`5173`

## 目录结构

```text
.
├── src/              前端源码
├── public/           前端静态资源
├── backend/          后端源码与配置
├── package.json      前端依赖与脚本
├── vite.config.ts    前端构建配置
├── 启动项目.bat      一键启动脚本
└── 停止项目.bat      停止脚本
```

## 说明

- 如果需要修改 AI 服务，可通过环境变量配置 `AI_PROVIDER`、`AI_BASE_URL`、`AI_MODEL`、`AI_API_KEY`
- 如果需要重新部署，请先执行构建，再同步生成产物
