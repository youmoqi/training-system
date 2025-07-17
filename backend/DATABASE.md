# 数据库设计说明

## 数据库概述

本系统使用MySQL数据库作为主要数据库，支持完整的生产环境部署。

## 数据库配置

### 开发环境配置
- 数据库类型：MySQL 8.0+
- 数据库名：training_system
- 用户名：training_user
- 密码：training_password
- 字符集：utf8mb4
- 排序规则：utf8mb4_unicode_ci

### 生产环境配置
- 使用 `application-prod.yml` 配置文件
- 建议通过环境变量设置数据库连接信息
- 关闭SQL日志输出
- 使用绝对路径存储文件

## 数据库初始化

### 1. 创建数据库和用户
```sql
-- 创建数据库
CREATE DATABASE training_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'training_user'@'localhost' IDENTIFIED BY 'training_password';
GRANT ALL PRIVILEGES ON training_system.* TO 'training_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 自动初始化
系统启动时会自动执行以下操作：
1. 创建数据库表结构（schema.sql）
2. 插入演示数据（data.sql）

### 3. 手动初始化
如需手动执行，可以使用提供的迁移脚本：
```bash
mysql -u root -p < backend/scripts/migrate-to-mysql.sql
```

## 表结构设计

### 1. 用户表 (users)
存储系统用户信息，包括不同角色的用户。

**字段说明：**
- `id`: 主键
- `username`: 用户名（唯一）
- `password`: 密码（BCrypt加密）
- `real_name`: 真实姓名
- `gender`: 性别
- `id_card`: 身份证号（唯一）
- `phone`: 手机号（唯一）
- `work_unit`: 工作单位
- `training_type`: 培训类型
- `job_category`: 工种类别
- `face_photo_url`: 人脸照片URL
- `payment_amount`: 缴费金额
- `role`: 用户角色（SUPER_ADMIN/ADMIN/EXPLOSIVE_USER/BLAST_USER）
- `create_time`: 创建时间
- `update_time`: 更新时间

**索引：**
- `idx_username`: 用户名索引
- `idx_id_card`: 身份证号索引
- `idx_phone`: 手机号索引
- `idx_role`: 角色索引

### 2. 课程表 (courses)
存储培训课程信息。

**字段说明：**
- `id`: 主键
- `title`: 课程标题
- `description`: 课程描述
- `video_url`: 视频URL
- `price`: 课程价格
- `is_online`: 是否上线
- `create_time`: 创建时间
- `update_time`: 更新时间

**索引：**
- `idx_title`: 标题索引
- `idx_is_online`: 上线状态索引

### 3. 课程可见角色表 (course_visible_roles)
控制不同角色用户可见的课程。

**字段说明：**
- `course_id`: 课程ID（外键）
- `role`: 角色名称

**索引：**
- `idx_role`: 角色索引

### 4. 题库表 (question_banks)
存储题库信息。

**字段说明：**
- `id`: 主键
- `title`: 题库标题
- `description`: 题库描述
- `price`: 题库价格
- `is_online`: 是否上线
- `create_time`: 创建时间
- `update_time`: 更新时间

**索引：**
- `idx_title`: 标题索引
- `idx_is_online`: 上线状态索引

### 5. 题库可见角色表 (question_bank_visible_roles)
控制不同角色用户可见的题库。

### 6. 题目表 (questions)
存储题库中的具体题目。

**字段说明：**
- `id`: 主键
- `question_bank_id`: 题库ID（外键）
- `content`: 题目内容
- `type`: 题目类型（SINGLE_CHOICE/MULTIPLE_CHOICE/SUBJECTIVE）
- `explanation`: 题目解析

**索引：**
- `idx_question_bank_id`: 题库ID索引
- `idx_type`: 题目类型索引

### 7. 题目选项表 (question_options)
存储选择题的选项。

**索引：**
- `idx_question_id`: 题目ID索引

### 8. 题目答案表 (question_answers)
存储题目的正确答案。

**索引：**
- `idx_question_id`: 题目ID索引

### 9. 邀请链接表 (invitation_links)
存储培训邀请链接信息。

**字段说明：**
- `id`: 主键
- `link_code`: 链接代码（唯一）
- `password`: 访问密码
- `title`: 邀请标题
- `description`: 邀请描述
- `is_active`: 是否激活
- `expire_time`: 过期时间
- `create_time`: 创建时间
- `update_time`: 更新时间

**索引：**
- `idx_link_code`: 链接代码索引
- `idx_is_active`: 激活状态索引
- `idx_expire_time`: 过期时间索引

### 10. 邀请链接课程关联表 (invitation_courses)
存储邀请链接包含的课程。

### 11. 邀请链接题库关联表 (invitation_question_banks)
存储邀请链接包含的题库。

### 12. 用户课程关联表 (user_courses)
记录用户选课和学习进度。

**字段说明：**
- `id`: 主键
- `user_id`: 用户ID（外键）
- `course_id`: 课程ID（外键）
- `enroll_time`: 选课时间
- `is_completed`: 是否完成
- `complete_time`: 完成时间
- `watch_progress`: 观看进度（百分比）

**索引：**
- `idx_user_id`: 用户ID索引
- `idx_course_id`: 课程ID索引
- `idx_is_completed`: 完成状态索引

### 13. 用户题库关联表 (user_question_banks)
记录用户购买题库和考试成绩。

**字段说明：**
- `id`: 主键
- `user_id`: 用户ID（外键）
- `question_bank_id`: 题库ID（外键）
- `purchase_time`: 购买时间
- `is_completed`: 是否完成
- `complete_time`: 完成时间
- `score`: 考试成绩

**索引：**
- `idx_user_id`: 用户ID索引
- `idx_question_bank_id`: 题库ID索引
- `idx_is_completed`: 完成状态索引

## 演示数据说明

### 用户数据
- **超级管理员**: admin (密码: 123456)
- **管理员**: manager1, manager2 (密码: 123456)
- **易制爆人员**: user1, user2, user3 (密码: 123456)
- **爆破三大员**: blast1, blast2, blast3 (密码: 123456)

### 课程数据
包含8门课程，涵盖易制爆和爆破作业的各个方面：
1. 易制爆化学品基础知识
2. 易制爆化学品安全管理
3. 易制爆化学品储存与运输
4. 爆破作业安全技术
5. 爆破器材使用与管理
6. 爆破现场安全管理
7. 法律法规与标准规范
8. 事故案例分析

### 题库数据
包含4个题库：
1. 易制爆化学品基础知识题库
2. 易制爆化学品安全管理题库
3. 爆破作业安全技术题库
4. 法律法规综合题库

### 邀请链接数据
包含3个邀请链接：
1. 易制爆人员培训第一期 (INV2024001)
2. 爆破作业人员培训第一期 (INV2024002)
3. 综合安全培训 (INV2024003)

## 性能优化

### 索引策略
- 为常用查询字段创建索引
- 为外键字段创建索引
- 为状态字段创建索引
- 为时间字段创建索引

### 查询优化
- 使用适当的索引
- 避免全表扫描
- 合理使用分页查询
- 优化JOIN查询

## 备份策略

### 自动备份
建议设置定时任务进行数据库备份：
```bash
# 每日备份
mysqldump -u training_user -p training_system > backup_$(date +%Y%m%d).sql

# 压缩备份文件
gzip backup_$(date +%Y%m%d).sql
```

### 恢复数据
```bash
# 恢复数据库
mysql -u training_user -p training_system < backup_20240101.sql
```

## 注意事项

1. **字符集**: 使用utf8mb4字符集以支持完整的Unicode字符
2. **时区**: 设置正确的时区，建议使用Asia/Shanghai
3. **连接池**: 生产环境建议配置连接池参数
4. **密码**: 演示数据中所有用户密码都是`123456`（BCrypt加密后的值）
5. **权限**: 确保数据库用户有足够的权限
6. **备份**: 定期备份数据库，避免数据丢失
7. **监控**: 监控数据库性能，及时优化 