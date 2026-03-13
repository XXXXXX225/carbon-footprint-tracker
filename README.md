# 🌍 碳足迹追踪平台 (Carbon Footprint Tracker)

<div align="center">

![Vue 3](https://img.shields.io/badge/Vue-3.5.13-4FC08D?style=flat&logo=vue.js&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.6.2-3178C6?style=flat&logo=typescript&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.5-6DB33F?style=flat&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg)

一个功能完整的个人碳足迹追踪平台，帮助用户记录、分析和减少个人碳排放，为环保事业贡献力量。

[功能特性](#功能特性) • [快速开始](#快速开始) • [技术栈](#技术栈) • [演示](#演示) • [贡献](#贡献)

</div>

## 📋 项目简介

随着全球气候变化问题的日益严重，减少碳排放成为全球共识。本平台旨在帮助用户：
- 📊 **记录**个人碳排放数据（交通、饮食、用电）
- 📈 **分析**排放趋势和模式
- 🎯 **获得**个性化减排建议
- 🏆 **参与**积分激励系统
- 🌱 **培养**低碳生活方式

## ✨ 功能特性

### 核心功能
- 🔐 **用户认证**：安全的JWT认证系统，支持多角色权限管理
- 🚗 **交通排放记录**：记录不同交通方式的碳排放
- 🍽️ **饮食排放记录**：追踪食物消费的碳足迹
- ⚡ **用电排放记录**：监控电器使用的碳排放
- 📊 **数据可视化**：丰富的图表展示排放趋势和分布
- 💡 **个性化建议**：基于用户数据的智能减排推荐
- 🎁 **积分系统**：减排行为获得积分，激励环保行动
- 👑 **管理员功能**：用户管理、数据统计、全局监控

### 技术亮点
- 🎨 **现代化UI**：基于Element Plus的精美界面
- 📱 **响应式设计**：完美适配各种设备
- ⚡ **高性能**：前后端分离，优化加载速度
- 🔒 **安全可靠**：完善的权限控制和数据保护
- 🎯 **智能推荐**：基于机器学习的个性化建议算法

## 🛠️ 技术栈

### 前端
| 技术         | 版本   | 用途                     |
| ------------ | ------ | ------------------------ |
| Vue 3        | 3.5.13 | 渐进式JavaScript框架     |
| TypeScript   | 5.6.2  | 类型安全的JavaScript超集 |
| Vite         | 6.4.1  | 下一代前端构建工具       |
| Element Plus | 2.8.5  | Vue 3 UI组件库           |
| ECharts      | 5.5.1  | 强大的数据可视化库       |
| Pinia        | 2.3.0  | Vue状态管理              |
| Vue Router   | 4.4.5  | 官方路由管理器           |

### 后端
| 技术            | 版本   | 用途               |
| --------------- | ------ | ------------------ |
| Spring Boot     | 3.0.5  | Java应用框架       |
| Spring Security | 6.x    | 安全框架           |
| MySQL           | 8.0    | 关系型数据库       |
| Redis           | 7.x    | 缓存和会话管理     |
| JWT             | 0.11.5 | JSON Web Token认证 |
| JPA             | 3.x    | Java持久化API      |

## 🚀 快速开始

### 环境要求
- Node.js 16+
- JDK 17+
- MySQL 8.0+
- Redis 7.x

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/XXXXXX225/carbon-footprint-tracker.git
cd carbon-footprint-tracker
```

#### 2. 配置数据库
```bash
# 创建MySQL数据库
CREATE DATABASE carbonfootprint CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 配置后端
编辑 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carbonfootprint
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

#### 4. 启动后端服务
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### 5. 启动前端服务
```bash
# 在项目根目录
npm install
npm run dev
```

#### 6. 访问应用
- 前端：http://localhost:5173
- 后端API：http://localhost:8888/api

### 默认账号
| 用户名 | 密码     | 角色     |
| ------ | -------- | -------- |
| admin  | admin123 | 管理员   |
| user   | user123  | 普通用户 |

## 📸 演示

### 仪表盘
![仪表盘](https://via.placeholder.com/800x400/4FC08D/ffffff?text=Dashboard)

### 数据可视化
![数据可视化](https://via.placeholder.com/800x400/3178C6/ffffff?text=Data+Visualization)

### 排放记录
![排放记录](https://via.placeholder.com/800x400/6DB33F/ffffff?text=Emission+Records)

### 减排建议
![减排建议](https://via.placeholder.com/800x400/4479A1/ffffff?text=Recommendations)

## 📁 项目结构

```
carbon-footprint-tracker/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/carbonfootprint/
│   │   │   │       ├── config/      # 配置类
│   │   │   │       ├── controller/  # 控制器
│   │   │   │       ├── dto/         # 数据传输对象
│   │   │   │       ├── entity/      # 实体类
│   │   │   │       ├── repository/  # 数据访问层
│   │   │   │       ├── service/     # 业务逻辑层
│   │   │   │       └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/            # 数据库脚本
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
├── src/                     # 前端项目
│   ├── api/                 # API接口
│   ├── components/           # 组件
│   ├── router/               # 路由配置
│   ├── store/                # 状态管理
│   ├── utils/                # 工具函数
│   ├── views/                # 页面组件
│   ├── App.vue
│   └── main.ts
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## 🔧 配置说明

### 环境变量
创建 `.env` 文件配置环境变量：

```env
# 后端配置
SERVER_PORT=8888
MYSQL_URL=jdbc:mysql://localhost:3306/carbonfootprint
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_password
JWT_SECRET=your-jwt-secret-key

# 前端配置
VITE_API_BASE_URL=http://localhost:8888/api
```

## 🐳 Docker部署

### 使用Docker Compose
```bash
cd backend
docker-compose up -d
```

### 手动构建镜像
```bash
# 构建后端镜像
docker build -t carbon-footprint-backend ./backend

# 构建前端镜像
docker build -t carbon-footprint-frontend .
```

## 📚 API文档

启动后端服务后，访问Swagger文档：
- Swagger UI: http://localhost:8888/swagger-ui.html
- OpenAPI JSON: http://localhost:8888/v3/api-docs

## 🤝 贡献指南

我们欢迎任何形式的贡献！

### 如何贡献
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交Pull Request

### 开发规范
- 遵循现有代码风格
- 添加必要的注释
- 编写单元测试
- 更新相关文档

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 作者

**XXXXXX225**

- GitHub: [@XXXXXX225](https://github.com/XXXXXX225)
- Email: 3484802860@qq.com

## 🙏 致谢

感谢以下开源项目：
- [Vue.js](https://vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Element Plus](https://element-plus.org/)
- [ECharts](https://echarts.apache.org/)

## 📞 联系我们

- 📧 Email: 3484802860@qq.com
- 💬 Issue: [GitHub Issues](https://github.com/XXXXXX225/carbon-footprint-tracker/issues)
- 📖 文档: [项目文档](https://github.com/XXXXXX225/carbon-footprint-tracker/wiki)

---

<div align="center">

**如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！**

Made with ❤️ by XXXXXX225

</div>
