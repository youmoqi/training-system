# 易制爆与爆破作业人员培训系统 - 后端

## 数据库初始化

### 问题说明
如果你发现每次启动应用时都会重复插入课程和题库数据，这是因为默认配置会执行SQL初始化脚本。

### 解决方案

#### 1. 正常启动（推荐）
使用默认配置启动应用，不会重复插入数据：
```bash
mvn spring-boot:run
```

#### 2. 初始化数据库（仅首次使用）
如果你需要重新初始化数据库（清空所有数据并重新创建），可以使用以下方法：

**Windows:**
```bash
scripts/init-database.bat
```

**Linux/Mac:**
```bash
chmod +x scripts/init-database.sh
./scripts/init-database.sh
```

**手动执行:**
```bash
mvn spring-boot:run -Dspring.profiles.active=init
```

### 配置文件说明

- `application.yml` - 默认配置，不执行SQL初始化
- `application-init.yml` - 初始化配置，会清空数据并重新创建
- `application-prod.yml` - 生产环境配置

### 注意事项

1. **初始化会清空所有数据**，包括用户、课程、题库等
2. **仅在首次部署或需要重置数据时使用初始化**
3. **正常使用时使用默认配置启动**

### 数据库连接信息

- 数据库：training_system
- 用户名：training_user
- 密码：training_password
- 主机：localhost
- 端口：3306 