@echo off
echo 正在启动易制爆与爆破作业人员培训系统...
echo.

echo 1. 安装依赖...
call npm install
if %errorlevel% neq 0 (
    echo 依赖安装失败，请检查Node.js环境
    pause
    exit /b 1
)

echo.
echo 2. 安装前端依赖...
cd frontend
call npm install
if %errorlevel% neq 0 (
    echo 前端依赖安装失败
    pause
    exit /b 1
)
cd ..

echo.
echo 3. 启动后端服务...
start "Spring Boot Backend" cmd /k "cd backend && mvn spring-boot:run"

echo.
echo 4. 等待后端启动完成...
timeout /t 10 /nobreak > nul

echo.
echo 5. 启动前端服务...
start "Vue Frontend" cmd /k "cd frontend && npm run serve"

echo.
echo 系统启动完成！
echo 前端地址: http://localhost:3000
echo 后端API: http://localhost:8080
echo H2数据库控制台: http://localhost:8080/h2-console
echo.
echo 按任意键退出...
pause > nul 