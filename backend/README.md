# 碳足迹追踪平台 - 后端服务

## 环境要求

### 1. MySQL数据库
- 确保MySQL服务正在运行
- 创建数据库：`carbon_footprint`
- 默认用户名：`root`
- 默认密码：`123456`

### 2. Redis（可选）
- 如果需要使用Redis缓存，确保Redis服务正在运行
- 默认端口：6379
- 如果不使用Redis，已在Application.java中排除Redis自动配置

## 启动步骤
### 1. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS carbon_footprint CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 初始化数据库表结构
执行SQL脚本：`src/main/resources/db/schema.sql`

### 3. 启动后端服务
```bash
# 进入后端目录
cd backend

# 使用Maven启动
mvn spring-boot:run

# 或使用java命令运行jar包
java -jar target/carbon-footprint-tracker-1.0.0.jar
```

### 4. 访问API文档
后端服务启动成功后，访问：
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API文档: http://localhost:8080/api/v3/api-docs

## API端点

### 认证相关
- POST /api/auth/login - 用户登录

### 排放记录相关
- POST /api/emission/transport - 记录交通排放
- POST /api/emission/diet - 记录饮食排放
- POST /api/emission/electricity - 记录用电排放
- GET /api/emission/transport - 获取交通排放记录
- GET /api/emission/diet - 获取饮食排放记录
- GET /api/emission/electricity - 获取用电排放记录

### 数据报表相关
- GET /api/report/summary - 获取排放汇总
- GET /api/report/history - 获取碳足迹历史
- GET /api/report/latest - 获取最新碳足迹汇总

### 减排建议相关
- GET /api/recommendations - 获取减排建议
- GET /api/recommendations/personalized - 获取个性化减排建议
- GET /api/recommendations/adopted - 获取已采纳的减排建议
- POST /api/recommendations/adopt/{recommendationId} - 采纳减排建议
- PUT /api/recommendations/update/{recommendationId} - 更新减排建议状态

## 测试用户

系统已预置测试用户：
- 用户名：`admin`，密码：`password`（企业角色）
- 用户名：`user`，密码：`password`（个人角色）

## 注意事项

1. 确保MySQL服务正在运行
2. 确保数据库连接配置正确（application.yml）
3. 确保端口8080未被占用
4. JWT密钥在生产环境中需要修改
5. Redis配置为可选，如不使用可忽略
