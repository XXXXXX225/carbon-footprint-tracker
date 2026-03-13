# 碳足迹追踪平台部署指南

## 项目部署到服务器 111.231.141.121

### 部署架构

本项目采用Docker容器化部署，包含以下服务：

- **前端**: Vue 3应用（通过Nginx提供服务）
- **后端**: Spring Boot应用
- **数据库**: MySQL 8.0
- **缓存**: Redis 7

### 端口配置

| 服务 | 容器端口 | 宿主机端口 | 说明 |
|------|---------|-----------|------|
| Nginx | 80 | 80 | HTTP访问 |
| Nginx | 443 | 443 | HTTPS访问（可选）|
| Spring Boot | 8080 | - | 后端API服务（内部）|
| MySQL | 3306 | 3306 | 数据库服务 |
| Redis | 6379 | 6379 | 缓存服务 |

### 部署步骤

#### 1. 准备环境

确保服务器已安装：
- Docker 20.10+
- Docker Compose 2.0+

#### 2. 构建前端

```bash
cd /path/to/project
npm install
npm run build
```

构建完成后，`dist`目录将包含所有前端静态资源。

#### 3. 配置环境变量

编辑`backend/.env`文件，配置以下参数：

```env
MYSQL_PASSWORD=YourStrongPassword
JWT_SECRET=YourJWTSecretKey
REDIS_PASSWORD=YourRedisPassword
```

#### 4. 启动服务

```bash
cd backend
docker-compose up -d --build
```

#### 5. 查看服务状态

```bash
cd backend
docker-compose ps
docker-compose logs -f
```

### 数据库初始化

数据库会在首次启动时自动初始化，包括：

- 创建数据库和表结构
- 插入初始数据（减排建议）
- 创建默认管理员账号

### 防火墙配置

确保服务器防火墙开放以下端口：

- 80/TCP (HTTP)
- 443/TCP (HTTPS)
- 22/TCP (SSH)
- 3306/TCP (MySQL, 可选用于远程连接)
- 6379/TCP (Redis, 可选用于远程连接)

### 访问应用

部署完成后，通过以下地址访问：

- **前端应用**: http://111.231.141.121
- **后端API**: http://111.231.141.121/api

### 默认账号

系统预设以下测试账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |

⚠️ **重要**: 生产环境部署后请立即修改默认密码！

### 常用命令

#### 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f nginx
```

#### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart app
```

#### 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

#### 进入容器

```bash
# 进入后端容器
docker-compose exec app sh

# 进入MySQL容器
docker-compose exec mysql bash

# 进入Redis容器
docker-compose exec redis sh
```

#### 数据库操作

```bash
# 连接MySQL
docker-compose exec mysql mysql -uroot -p

# 备份数据库
docker-compose exec mysql mysqldump -uroot -p carbonfootprint > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -uroot -p carbonfootprint < backup.sql
```

### 监控和维护

#### 健康检查

```bash
# 检查容器健康状态
docker-compose ps

# 检查服务响应
curl http://111.231.141.121/api/health
```

#### 资源监控

```bash
# 查看容器资源使用情况
docker stats
```

#### 日志管理

```bash
# 清理旧日志
docker system prune -f

# 查看磁盘使用情况
du -sh /var/lib/docker
```

### 更新部署

当需要更新应用时：

```bash
cd backend
# 拉取最新代码
git pull

# 重新构建并启动
docker-compose up -d --build

# 清理未使用的镜像
docker image prune -f
```

### 故障排查

#### 服务无法启动

1. 检查端口占用：
   ```bash
   netstat -tlnp | grep -E ':(80|443|3306|6379)'
   ```

2. 检查日志：
   ```bash
   docker-compose logs app
   ```

3. 检查资源使用：
   ```bash
   free -h
   df -h
   ```

#### 数据库连接失败

1. 检查MySQL容器状态：
   ```bash
   docker-compose ps mysql
   ```

2. 检查数据库连接：
   ```bash
   docker-compose exec mysql mysqladmin ping -h localhost -uroot -p
   ```

#### 前端页面无法访问

1. 检查Nginx配置：
   ```bash
   docker-compose logs nginx
   ```

2. 检查静态文件：
   ```bash
   ls -la dist/
   ```

### 安全建议

1. **修改默认密码**: 生产环境必须修改所有默认密码
2. **启用HTTPS**: 配置SSL证书，使用HTTPS访问
3. **限制数据库访问**: 不要将MySQL和Redis端口暴露到公网
4. **定期更新**: 及时更新依赖和系统补丁
5. **备份数据**: 定期备份数据库数据
6. **监控日志**: 定期检查应用和系统日志
7. **防火墙规则**: 仅开放必要的端口

### 性能优化

1. **启用Gzip压缩**: Nginx已配置Gzip压缩
2. **静态资源缓存**: 配置合理的缓存策略
3. **数据库索引**: 确保关键字段有索引
4. **Redis缓存**: 使用Redis缓存热点数据
5. **负载均衡**: 可配置多个实例实现负载均衡

### 备份策略

建议执行以下备份操作：

1. **数据库备份**: 每日自动备份
2. **应用配置**: 备份环境变量和配置文件
3. **日志归档**: 定期归档和清理日志文件

### 联系支持

如遇问题，请联系技术支持：
- 邮箱: 3484802860@qq.com
