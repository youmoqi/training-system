# 数据库迁移指南：从H2到MySQL

## 概述

本指南详细说明了如何将易制爆与爆破作业人员培训系统从H2数据库迁移到MySQL数据库。

## 已完成的修改

### 1. 配置文件修改

#### application.yml
- 数据库连接URL改为MySQL格式
- 用户名改为 `training_user`
- 密码改为 `training_password`
- JPA方言改为 `MySQL8Dialect`
- 删除了H2控制台配置

#### pom.xml
- 移除了H2数据库依赖
- 保留了MySQL驱动依赖

### 2. 数据库脚本更新

#### schema.sql
- 更新为MySQL兼容语法
- 添加了索引优化
- 使用MySQL的TIMESTAMP默认值语法
- 添加了外键约束

#### data.sql
- 更新为MySQL兼容语法
- 保持了所有演示数据

### 3. 新增文件

#### 数据库初始化脚本
- `backend/scripts/init-database.sh` (Linux/Mac)
- `backend/scripts/init-database.bat` (Windows)
- `backend/scripts/migrate-to-mysql.sql` (MySQL迁移脚本)

#### 生产环境配置
- `backend/src/main/resources/application-prod.yml`

#### 文档更新
- `backend/DATABASE.md` - 详细的数据库说明
- `README.md` - 更新了安装和使用说明

## 迁移步骤

### 1. 安装MySQL
确保您的系统已安装MySQL 8.0或更高版本。

### 2. 初始化数据库

#### Windows用户
```cmd
cd backend\scripts
init-database.bat
```

#### Linux/Mac用户
```bash
cd backend/scripts
chmod +x init-database.sh
./init-database.sh
```

### 3. 启动应用
```bash
# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm run serve
```

## 数据库配置详情

### 连接信息
- **主机**: localhost
- **端口**: 3306
- **数据库名**: training_system
- **用户名**: training_user
- **密码**: training_password
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci

### 连接URL
```
jdbc:mysql://localhost:3306/training_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
```

## 性能优化

### 索引策略
系统已为以下字段创建了索引：
- 用户名、身份证号、手机号
- 课程标题、上线状态
- 题库标题、上线状态
- 题目类型、题库ID
- 邀请链接代码、激活状态、过期时间
- 用户课程关联、完成状态
- 用户题库关联、完成状态

### 查询优化建议
1. 使用适当的索引
2. 避免全表扫描
3. 合理使用分页查询
4. 优化JOIN查询

## 备份和恢复

### 备份数据库
```bash
mysqldump -u training_user -p training_system > backup_$(date +%Y%m%d).sql
```

### 恢复数据库
```bash
mysql -u training_user -p training_system < backup_20240101.sql
```

## 生产环境部署

### 1. 使用生产配置
```bash
java -jar training-system.jar --spring.profiles.active=prod
```

### 2. 环境变量配置
建议通过环境变量设置敏感信息：
```bash
export DB_URL=jdbc:mysql://your-db-host:3306/training_system
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret
```

### 3. 数据库连接池配置
在生产环境中，建议配置连接池参数：
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## 常见问题

### 1. 连接失败
- 检查MySQL服务是否启动
- 确认用户名和密码正确
- 检查防火墙设置
- 确认数据库已创建

### 2. 字符集问题
- 确保使用utf8mb4字符集
- 检查连接URL中的字符集参数

### 3. 时区问题
- 在连接URL中设置正确的时区
- 建议使用Asia/Shanghai时区

### 4. 权限问题
- 确保数据库用户有足够权限
- 检查文件上传目录权限

## 验证迁移

### 1. 检查表结构
```sql
USE training_system;
SHOW TABLES;
DESCRIBE users;
DESCRIBE courses;
DESCRIBE question_banks;
```

### 2. 检查数据
```sql
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM courses;
SELECT COUNT(*) FROM question_banks;
```

### 3. 测试应用功能
- 用户登录
- 课程浏览
- 题库访问
- 管理员功能

## 回滚方案

如果需要回滚到H2数据库：

1. 恢复原始的 `application.yml` 配置
2. 在 `pom.xml` 中恢复H2依赖
3. 删除MySQL相关配置
4. 重新启动应用

## 技术支持

如有问题，请查看：
- [数据库说明文档](DATABASE.md)
- [README.md](../README.md)
- MySQL官方文档

## 总结

迁移到MySQL数据库后，系统将具有：
- 更好的性能和稳定性
- 更强的数据持久性
- 更丰富的功能支持
- 更好的生产环境适应性

所有功能保持不变，用户体验无影响。 
