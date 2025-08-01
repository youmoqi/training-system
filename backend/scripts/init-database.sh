#!/bin/bash

# 易制爆与爆破作业人员培训系统数据库初始化脚本

echo "=========================================="
echo "易制爆与爆破作业人员培训系统"
echo "MySQL数据库初始化脚本"
echo "=========================================="

# 数据库配置
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="training_system"
DB_USER="training_user"
DB_PASSWORD="training_password"
ROOT_USER="root"
ROOT_PASSWORD=""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 函数：打印带颜色的消息
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查MySQL是否安装
check_mysql() {
    if ! command -v mysql &> /dev/null; then
        print_error "MySQL未安装，请先安装MySQL"
        exit 1
    fi
    print_message "MySQL已安装"
}

# 获取root密码
get_root_password() {
    echo -n "请输入MySQL root用户密码（如果没有密码请直接回车）: "
    read -s ROOT_PASSWORD
    echo
}

# 测试MySQL连接
test_mysql_connection() {
    if [ -z "$ROOT_PASSWORD" ]; then
        mysql -u $ROOT_USER -h $DB_HOST -P $DB_PORT -e "SELECT 1;" > /dev/null 2>&1
    else
        mysql -u $ROOT_USER -p$ROOT_PASSWORD -h $DB_HOST -P $DB_PORT -e "SELECT 1;" > /dev/null 2>&1
    fi
    
    if [ $? -eq 0 ]; then
        print_message "MySQL连接成功"
        return 0
    else
        print_error "MySQL连接失败，请检查用户名和密码"
        return 1
    fi
}

# 创建数据库
create_database() {
    print_message "创建数据库 $DB_NAME..."
    
    if [ -z "$ROOT_PASSWORD" ]; then
        mysql -u $ROOT_USER -h $DB_HOST -P $DB_PORT -e "
            CREATE DATABASE IF NOT EXISTS $DB_NAME 
            CHARACTER SET utf8mb4 
            COLLATE utf8mb4_unicode_ci;
        "
    else
        mysql -u $ROOT_USER -p$ROOT_PASSWORD -h $DB_HOST -P $DB_PORT -e "
            CREATE DATABASE IF NOT EXISTS $DB_NAME 
            CHARACTER SET utf8mb4 
            COLLATE utf8mb4_unicode_ci;
        "
    fi
    
    if [ $? -eq 0 ]; then
        print_message "数据库创建成功"
    else
        print_error "数据库创建失败"
        exit 1
    fi
}

# 创建用户
create_user() {
    print_message "创建数据库用户 $DB_USER..."
    
    if [ -z "$ROOT_PASSWORD" ]; then
        mysql -u $ROOT_USER -h $DB_HOST -P $DB_PORT -e "
            CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';
            GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
            FLUSH PRIVILEGES;
        "
    else
        mysql -u $ROOT_USER -p$ROOT_PASSWORD -h $DB_HOST -P $DB_PORT -e "
            CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASSWORD';
            GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
            FLUSH PRIVILEGES;
        "
    fi
    
    if [ $? -eq 0 ]; then
        print_message "用户创建成功"
    else
        print_error "用户创建失败"
        exit 1
    fi
}

# 执行SQL脚本
execute_sql_script() {
    local script_file=$1
    local description=$2
    
    print_message "执行$description..."
    
    if [ -f "$script_file" ]; then
        mysql -u $DB_USER -p$DB_PASSWORD -h $DB_HOST -P $DB_PORT $DB_NAME < "$script_file"
        
        if [ $? -eq 0 ]; then
            print_message "$description执行成功"
        else
            print_error "$description执行失败"
            exit 1
        fi
    else
        print_error "SQL脚本文件不存在: $script_file"
        exit 1
    fi
}

# 验证数据库初始化
verify_initialization() {
    print_message "验证数据库初始化..."
    
    # 检查表是否存在
    table_count=$(mysql -u $DB_USER -p$DB_PASSWORD -h $DB_HOST -P $DB_PORT $DB_NAME -e "SHOW TABLES;" | wc -l)
    
    if [ $table_count -gt 1 ]; then
        print_message "数据库初始化验证成功，共创建了 $((table_count-1)) 个表"
    else
        print_error "数据库初始化验证失败"
        exit 1
    fi
}

# 显示连接信息
show_connection_info() {
    echo
    echo "=========================================="
    echo "数据库初始化完成！"
    echo "=========================================="
    echo "数据库连接信息："
    echo "  主机: $DB_HOST"
    echo "  端口: $DB_PORT"
    echo "  数据库: $DB_NAME"
    echo "  用户名: $DB_USER"
    echo "  密码: $DB_PASSWORD"
    echo
    echo "应用程序配置："
    echo "  在 application.yml 中使用以下配置："
    echo "  url: jdbc:mysql://$DB_HOST:$DB_PORT/$DB_NAME?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
    echo "  username: $DB_USER"
    echo "  password: $DB_PASSWORD"
    echo
    echo "测试连接命令："
    echo "  mysql -u $DB_USER -p$DB_PASSWORD -h $DB_HOST -P $DB_PORT $DB_NAME"
    echo "=========================================="
}

# 主函数
main() {
    print_message "开始初始化数据库..."
    
    # 检查MySQL
    check_mysql
    
    # 获取root密码
    get_root_password
    
    # 测试连接
    if ! test_mysql_connection; then
        exit 1
    fi
    
    # 创建数据库
    create_database
    
    # 创建用户
    create_user
    
    # 执行建表脚本
    execute_sql_script "database_schema.sql" "建表脚本"
    
    # 执行数据脚本
    execute_sql_script "database_data.sql" "演示数据脚本"
    
    # 验证初始化
    verify_initialization
    
    # 显示连接信息
    show_connection_info
}

# 执行主函数
main "$@"

echo "正在初始化数据库..."
echo "警告：这将清空现有数据并重新创建表结构和示例数据"
echo
read -p "确定要继续吗？(y/N): " confirm

if [[ $confirm =~ ^[Yy]$ ]]; then
    echo "开始初始化数据库..."
    mvn spring-boot:run -Dspring.profiles.active=init
    echo "数据库初始化完成！"
else
    echo "取消初始化操作。"
fi 