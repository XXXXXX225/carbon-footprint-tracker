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
- **框架**：Spring Boot
- **安全**：Spring Security + JWT
- **数据库**：MySQL
- **缓存**：Redis
- **文档**：Swagger

## 📁 项目结构

```
carbon-footprint-tracker/
├── src/                    # 前端源代码
│   ├── views/             # 页面组件
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   ├── store/             # 状态管理
│   ├── api/               # API 接口
│   └── utils/             # 工具函数
├── backend/               # 后端源代码
│   └── src/               # Java 源代码
├── dist/                  # 前端构建输出
└── README.md              # 项目说明
```

## 🚀 快速开始

### 前端开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产环境
npm run build
```

### 后端开发

```bash
# 进入后端目录
cd backend

# 使用 Maven 运行
mvn spring-boot:run

# 或构建 JAR 包
mvn clean package
java -jar target/carbon-footprint-tracker-1.0.0.jar
```

## 🔧 环境配置

### 前端环境变量
创建 `.env.production` 文件：
```
VITE_API_BASE_URL=http://your-api-server/api
```

### 后端配置
修改 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carbon_footprint
    username: your_username
    password: your_password
```

## 📝 API 文档

启动后端服务后，访问 Swagger UI：
```
http://localhost:8080/swagger-ui.html
```

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 👨‍💻 作者

XXXXXX225

---

🌍 让我们一起为地球减负，从追踪碳足迹开始！
