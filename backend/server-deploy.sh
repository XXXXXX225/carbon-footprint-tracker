#!/bin/bash

# 碳足迹追踪平台服务器部署脚本

set -e

echo "======================================"
echo "  碳足迹追踪平台部署"
echo "======================================"
echo ""

# 清理旧环境
echo "🧹 清理旧容器..."
docker rm -f carbon-footprint-app carbon-footprint-mysql carbon-footprint-redis carbon-footprint-nginx 2>/dev/null || true
docker rm -f $(docker ps -aq) 2>/dev/null || true
docker system prune -f

# 创建网络
echo "🌐 创建Docker网络..."
docker network create carbon-network 2>/dev/null || true

# 启动MySQL
echo "🗄️  启动MySQL容器..."
docker run -d \
  --name carbon-footprint-mysql \
  --network carbon-network \
  -e MYSQL_ROOT_PASSWORD=CarbonFootprint2024! \
  -e MYSQL_DATABASE=carbonfootprint \
  -v mysql_data:/var/lib/mysql \
  -v /root/backend_20260313162617/src/main/resources/db/01-init.sql:/docker-entrypoint-initdb.d/01-init.sql \
  -p 3306:3306 \
  mysql:8.0

# 启动Redis
echo "🔴 启动Redis容器..."
docker run -d \
  --name carbon-footprint-redis \
  --network carbon-network \
  -v redis_data:/data \
  -p 6379:6379 \
  redis:7-alpine

# 等待数据库启动
echo "⏳ 等待MySQL启动..."
sleep 30

# 检查MySQL是否就绪
until docker exec carbon-footprint-mysql mysqladmin ping -h localhost -uroot -pCarbonFootprint2024! &>/dev/null; do
  echo "等待MySQL启动..."
  sleep 5
done
echo "✅ MySQL 已就绪"

# 构建后端应用
echo "🔨 构建后端应用..."
docker build -t carbon-footprint-app .

# 启动后端应用
echo "🚀 启动后端应用..."
docker run -d \
  --name carbon-footprint-app \
  --network carbon-network \
  -p 8080:8080 \
  -e SERVER_PORT=8080 \
  -e MYSQL_URL=jdbc:mysql://mysql:3306/carbonfootprint?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true \
  -e MYSQL_USERNAME=root \
  -e MYSQL_PASSWORD=CarbonFootprint2024! \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET=CarbonFootprint-JWT-Secret-Key-2024-Change-In-Production \
  carbon-footprint-app

# 等待后端启动
echo "⏳ 等待后端应用启动..."
sleep 30

# 启动Nginx
echo "🌐 启动Nginx容器..."
docker run -d \
  --name carbon-footprint-nginx \
  --network carbon-network \
  -p 80:80 \
  -p 443:443 \
  -v /root/backend_20260313162617:/usr/share/nginx/html:ro \
  -v /root/backend_20260313162617/nginx.conf:/etc/nginx/nginx.conf:ro \
  -v /root/backend_20260313162617/ssl:/etc/nginx/ssl:ro \
  nginx:alpine

# 检查容器状态
echo ""
echo "📊 容器状态："
docker ps --filter "name=carbon-footprint"

echo ""
echo "======================================"
echo "  部署完成！"
echo "======================================"
echo ""
echo "📱 访问地址："
echo "   前端: http://111.231.141.121"
echo "   后端API: http://111.231.141.121/api"
echo ""
echo "🔧 默认账号："
echo "   管理员: admin / admin123"
echo "   普通用户: user / user123"
echo ""
echo "📋 查看日志命令："
echo "   docker logs -f carbon-footprint-app"
echo "   docker logs -f carbon-footprint-mysql"
echo "   docker logs -f carbon-footprint-nginx"
echo ""
