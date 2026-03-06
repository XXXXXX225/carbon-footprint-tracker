# 碳足迹追踪平台 (Carbon Footprint Tracker)

一个基于 Vue3 + Spring Boot 的全栈碳足迹追踪应用，帮助用户记录、分析和减少日常碳排放。

## 🌟 功能特性

- 📊 **智能追踪**：自动记录交通、饮食、用电等日常碳排放活动
- 📈 **数据可视化**：直观的图表展示碳排放趋势和来源分析
- 🤖 **AI 建议**：基于用户数据生成个性化减排建议
- 🏆 **成就系统**：完成环保挑战，获得绿色徽章和积分奖励
- 👤 **用户管理**：支持个人中心和管理员后台

## 🛠️ 技术栈

### 前端
- **框架**：Vue 3 + TypeScript
- **UI 组件库**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **图表**：ECharts
- **构建工具**：Vite

### 后端
- **框架**：Spring Boot 3
- **语言**：Java 17
- **数据库**：MySQL
- **认证**：JWT
- **API 文档**：Swagger

## 📦 安装和运行

### 前端
```bash
# 安装依赖
npm install

# 开发环境运行
npm run dev

# 构建生产版本
npm run build
```

### 后端
```bash
# 编译项目
mvn clean package

# 运行应用
java -jar target/carbon-footprint-tracker-1.0.0.jar
```

## 📄 项目结构

```
├── src/                 # 前端源代码
│   ├── api/            # API 接口
│   ├── components/     # 组件
│   ├── views/          # 页面
│   ├── router/         # 路由
│   ├── store/          # 状态管理
│   └── utils/          # 工具函数
├── backend/            # 后端源代码
│   └── src/            # Java 源码
├── public/             # 静态资源
├── index.html          # 入口 HTML
├── package.json        # 前端依赖
└── vite.config.ts      # Vite 配置
```

## 🌱 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开 Pull Request

## 📞 联系方式

- **作者**：Carbon Footprint Team
- **邮箱**：contact@carbonfootprint.example
- **项目地址**：https://github.com/XXXXXX225/carbon-footprint-tracker

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件
