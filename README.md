# 易制爆与爆破作业人员培训系统

## 项目简介

本系统是一个基于Vue.js + Spring Boot的在线培训管理系统，专门为易制爆危险品从业人员与爆破作业三大员提供培训服务。系统支持多角色权限管理、课程观看、题库练习、选课管理等功能。

## 技术栈

### 后端
- Java 8
- Spring Boot 2.7.14
- Spring Security
- Spring Data JPA
- MySQL 8.0+
- JWT认证
- Maven

### 前端
- Vue 3
- Vue Router 4
- Vuex 4
- Element Plus
- Axios
- Video.js

## 功能特性

### 用户角色
- **超级管理员**: 系统最高权限，可管理所有功能
- **管理员**: 负责课程管理、题库管理、用户管理等
- **易制爆人员**: 可自主选课，受缴费额度限制
- **爆破三大员**: 通过邀请链接获取课程权限

### 核心功能
1. **用户管理**
   - 用户注册（含人脸照片上传）
   - 用户登录认证
   - 角色权限控制

2. **课程管理**
   - 课程上传与编辑
   - 课程上下线控制
   - 课程观看进度跟踪
   - 视频播放支持

3. **选课系统**
   - 易制爆人员自主选课
   - 爆破三大员链接选课
   - 缴费额度控制

4. **题库系统**
   - 选择题、简答题支持
   - 题库购买与答题
   - 成绩统计

5. **邀请链接系统**
   - 生成带密码的邀请链接
   - 批量课程分配

## 快速开始

### 环境要求
- Java 8+
- Node.js 14+
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/YIZ-ops/zitong.git
cd zitong
```

2. **安装依赖**
```bash
# 安装前端依赖
cd frontend
npm install

# 返回根目录
cd ..
```

3. **初始化数据库**
```bash
# Linux/Mac
cd backend/scripts
chmod +x init-database.sh
./init-database.sh

# Windows
cd backend\scripts
init-database.bat
```

4. **启动后端服务**
```bash
# 进入backend目录
cd backend
mvn spring-boot:run
```

5. **启动前端服务**
```bash
# 进入frontend目录
cd frontend
npm run serve
```

6. **访问系统**
- 前端地址: http://localhost:9789
- 后端API: http://localhost:8989
- http://8.130.78.1:9789

### 数据库配置
- 数据库类型: MySQL 8.0+
- 数据库名: training_system
- 用户名: training_user
- 密码: training_password
- 字符集: utf8mb4
- 排序规则: utf8mb4_unicode_ci

### 演示账户
- **超级管理员**: admin / 111
- **管理员**: manger1 / 111
- **易制爆人员**: 111 / 111
- **爆破三大员**: 222 / 111

## 项目结构

```
training-system/
├── backend/                 # Spring Boot后端
│   ├── src/main/java/
│   │   └── com/training/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── repository/  # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── config/      # 配置类
│   │       └── util/        # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml  # 应用配置
│   │   ├── schema.sql       # 建表语句
│   │   └── data.sql         # 演示数据
│   ├── scripts/             # 数据库脚本
│   │   ├── init-database.sh # Linux/Mac初始化脚本
│   │   ├── init-database.bat # Windows初始化脚本
│   │   └── migrate-to-mysql.sql # MySQL迁移脚本
│   └── pom.xml             # Maven配置
├── frontend/               # Vue.js前端
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 通用组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── api/            # API接口
│   │   └── assets/         # 静态资源
│   ├── package.json        # 前端依赖
│   └── vue.config.js       # Vue配置
├── package.json            # 根目录配置
└── README.md              # 项目说明
```

## API接口文档

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 课程接口
- `GET /api/courses` - 获取课程列表
- `GET /api/courses/{id}` - 获取课程详情
- `POST /api/courses` - 创建课程
- `PUT /api/courses/{id}` - 更新课程
- `DELETE /api/courses/{id}` - 删除课程
- `POST /api/courses/{id}/enroll` - 选课
- `GET /api/courses/user/{userId}` - 获取用户课程

### 题库接口
- `GET /api/question-banks` - 获取题库列表
- `POST /api/question-banks` - 创建题库
- `POST /api/question-banks/{id}/purchase` - 购买题库

## 部署说明

### 生产环境部署
1. 使用 `application-prod.yml` 配置文件
2. 修改数据库连接信息
3. 配置文件上传路径
4. 设置JWT密钥
5. 配置CORS策略
6. 构建前端项目: `npm run build`
7. 部署到服务器

### Docker部署
```bash
# 构建镜像
docker build -t training-system .

# 运行容器
docker run -p 8080:8080 training-system
```

## 数据库管理

### 备份数据库
```bash
mysqldump -u training_user -p training_system > backup_$(date +%Y%m%d).sql
```

### 恢复数据库
```bash
mysql -u training_user -p training_system < backup_20240101.sql
```

### 性能优化
- 已为常用查询字段创建索引
- 建议定期分析查询性能
- 监控数据库连接数

## 开发说明

### 代码规范
- 后端遵循阿里巴巴Java开发手册
- 前端使用ESLint + Prettier
- 提交信息使用Conventional Commits规范

### 测试
```bash
# 后端测试
cd backend
mvn test

# 前端测试
cd frontend
npm run test
```

## 常见问题

### 数据库连接问题
1. 确保MySQL服务已启动
2. 检查数据库用户名和密码
3. 确认数据库已创建
4. 检查防火墙设置

### 权限问题
1. 确保数据库用户有足够权限
2. 检查文件上传目录权限
3. 确认JWT密钥配置正确

## 技术支持

如有问题，请查看：
- [数据库说明文档](backend/DATABASE.md)
- [API接口文档](docs/api.md)
- [部署指南](docs/deployment.md) 
