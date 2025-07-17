@echo off
echo 正在初始化数据库...
echo 警告：这将清空现有数据并重新创建表结构和示例数据
echo.
set /p confirm="确定要继续吗？(y/N): "
if /i "%confirm%"=="y" (
    echo 开始初始化数据库...
    mvn spring-boot:run -Dspring.profiles.active=init
    echo 数据库初始化完成！
) else (
    echo 取消初始化操作。
)
pause 