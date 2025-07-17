#!/bin/bash

echo "正在启动易制爆与爆破作业人员培训系统..."
echo

echo "1. 安装依赖..."
npm install
if [ $? -ne 0 ]; then
    echo "依赖安装失败，请检查Node.js环境"
    exit 1
fi

echo
echo "2. 安装前端依赖..."
cd frontend
npm install
if [ $? -ne 0 ]; then
    echo "前端依赖安装失败"
    exit 1
fi
cd ..

echo
echo "3. 启动后端服务..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

echo
echo "4. 等待后端启动完成..."
sleep 10

echo
echo "5. 启动前端服务..."
cd frontend
npm run serve &
FRONTEND_PID=$!
cd ..

echo
echo "系统启动完成！"
echo "前端地址: http://localhost:3000"
echo "后端API: http://localhost:8080"
echo "H2数据库控制台: http://localhost:8080/h2-console"
echo
echo "按 Ctrl+C 停止服务..."

# 等待用户中断
trap "echo '正在停止服务...'; kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait 