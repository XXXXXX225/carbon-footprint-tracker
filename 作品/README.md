 碳足迹追踪平台 - 作品说明

 文件夹结构


作品/
├── frontend/               前端打包后的静态资源
│   ├── server.py           Python代理服务器（支持POST/PUT/DELETE）
│   ├── index.html          入口页面
│   └── assets/             静态资源文件
├── backend/                后端JAR文件
│   └── carbon-footprint-tracker.jar
├── config/                 应用配置文件
│   └── application.yml
├── start.bat               一键启动脚本
└── README.md               使用说明文档


 环境要求

 必需环境
- JDK 17+ - 用于运行后端服务
- MySQL 8.0+ - 数据库服务
- Python 3.x - 用于运行前端代理服务器

 可选环境
- Maven 3.8+（如需重新编译后端）
- Node.js 16+（如需重新编译前端）

 安装步骤

 1. 数据库配置

1. 打开MySQL，创建数据库：
sql
CREATE DATABASE carbon_footprint CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


2. 导入数据库脚本（位于源代码的 `backend/src/main/resources/db/` 目录）：
sql
-- 执行 schema.sql 创建表结构
-- 执行 init_data.sql 初始化数据


3. 修改配置文件 `config/application.yml` 中的数据库连接信息：
yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carbon_footprint
    username: your_username
    password: your_password


 2. 启动项目

方法一：使用启动脚本（推荐）

双击 `start.bat` 文件，系统会自动启动前端和后端服务。

方法二：手动启动

启动后端：
bash
cd backend
java -jar carbon-footprint-tracker.jar


启动前端（在另一个终端）：
bash
cd frontend
python server.py


 访问地址

- 前端页面：http://localhost:8080/
- 后端API：http://localhost:8888/api

 默认账号

| 角色     | 用户名 | 密码   |
| -------- | ------ | ------ |
| 管理员   | root   | 123456 |
| 普通用户 | user   | 123456 |

 功能说明

 前端功能
- 用户注册/登录
- 碳排放数据记录（交通、饮食、用电）
- 数据可视化分析（图表展示）
- 减排建议查看
- 积分系统
- 数据大屏（管理员专用）

 后端功能
- RESTful API接口
- JWT身份认证
- 角色权限控制
- 数据持久化
- 碳排放计算

 注意事项

1. 数据库配置：确保MySQL服务已启动，并且数据库连接信息正确
2. 端口占用：确保8080和8888端口未被占用
3. 启动顺序：必须先启动后端，再启动前端
4. Python版本：前端代理服务器需要Python 3.x版本
5. 防火墙设置：如果无法访问，请检查防火墙设置

 故障排查

 后端启动失败
- 检查数据库连接配置是否正确
- 检查MySQL服务是否运行
- 查看控制台错误信息
- 确认JDK版本是否为17+

 前端无法访问
- 检查Python是否正确安装（3.x版本）
- 检查8080端口是否被占用
- 确保后端服务正常运行
- 检查前端代理服务器是否启动成功

 登录失败（Unsupported method）
- 确保使用的是 `server.py` 启动前端，而不是简单的 `python -m http.server`
- 检查后端API地址是否正确
- 查看浏览器开发者工具的网络请求

 管理员页面获取数据失败
**问题原因**：admin用户的角色可能不是'ADMIN'，导致无法访问管理员接口。

**解决方案**：
1. 连接到MySQL数据库
2. 执行以下SQL命令：
```sql
-- 修复admin用户角色为ADMIN
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';

-- 确保root用户也是ADMIN角色
UPDATE users SET role = 'ADMIN' WHERE username = 'root';

-- 查看修复结果
SELECT username, role FROM users WHERE username IN ('admin', 'root');
```

或者直接执行配置文件中的修复脚本：
```bash
mysql -u root -p carbonfootprint < config/fix_admin_role.sql
```

3. 重启后端服务
4. 重新登录管理员账号

 其他问题
- 查看控制台日志输出
- 检查系统资源使用情况
- 确认所有依赖环境已正确安装

 技术栈

- 前端：Vue 3 + TypeScript + Element Plus + ECharts
- 后端：Spring Boot + Spring Security + JWT + MySQL
- 构建工具：Vite（前端）+ Maven（后端）
- 代理服务器：Python 3 + http.server

 项目特点

1. 前后端分离：采用现代前后端分离架构
2. 响应式设计：适配不同屏幕尺寸
3. 数据可视化：丰富的图表展示
4. 权限控制：基于角色的访问控制
5. 环保主题：绿色系UI设计，体现环保理念

 技术支持

如遇到问题，请检查：
1. 环境要求是否满足
2. 配置文件是否正确
3. 端口是否被占用
4. 日志输出信息

---

祝您使用愉快！
