# 易制爆与爆破作业人员培训系统数据库结构说明

## 概述

本系统数据库采用MySQL 8.0，按功能模块组织表结构，支持用户管理、课程学习、题库练习、试卷考试、邀请管理等核心功能。

## 数据库模块划分

### 1. 用户管理模块

**核心表：**
- `users` - 用户基本信息表
- `user_courses` - 用户课程关系表
- `user_question_banks` - 用户题库关系表
- `user_exam_papers` - 用户试卷关系表

**功能说明：**
- 支持用户注册、登录、权限管理
- 记录用户选课、购买题库、购买试卷等行为
- 跟踪用户学习进度和完成状态

### 2. 课程管理模块

**核心表：**
- `courses` - 课程信息表
- `course_visible_roles` - 课程可见角色表
- `user_courses` - 用户课程关系表

**功能说明：**
- 管理培训课程信息（标题、描述、视频URL等）
- 控制课程对不同角色的可见性
- 记录用户选课和学习进度

### 3. 题库管理模块

**核心表：**
- `question_banks` - 题库信息表
- `questions` - 题目信息表
- `question_options` - 题目选项表
- `question_answers` - 题目答案表
- `question_bank_visible_roles` - 题库可见角色表

**功能说明：**
- 支持多种题型（单选、多选、主观题）
- 管理题目选项和正确答案
- 控制题库对不同角色的可见性

### 4. 考试管理模块

**核心表：**
- `exam_results` - 题库练习考试结果表
- `question_results` - 题目答题结果表
- `question_result_correct_answers` - 答题正确答案表
- `question_result_user_answers` - 答题用户答案表

**功能说明：**
- 记录用户题库练习的考试结果
- 详细记录每道题的答题情况
- 支持考试结果分析和统计

### 5. 题库练习结果模块

**核心表：**
- `question_bank_results` - 题库练习结果表
- `question_bank_question_results` - 题库练习题目答题记录表

**功能说明：**
- 记录用户题库练习的详细结果
- 支持多次练习，无次数限制
- 详细记录每道题的答题情况和正确答案
- 支持练习结果分析和统计

### 6. 试卷管理模块

**核心表：**
- `exam_papers` - 试卷信息表
- `exam_paper_questions` - 试卷题目关联表
- `exam_paper_visible_roles` - 试卷可见角色表
- `exam_paper_results` - 试卷考试记录表
- `exam_paper_question_results` - 试卷题目答题记录表

**功能说明：**
- 支持创建固定试卷，包含多个题目
- 设置试卷总分、及格分、考试时长等参数
- 支持随机出题功能
- 详细记录试卷考试结果

### 7. 邀请管理模块

**核心表：**
- `invitation_links` - 邀请链接表
- `invitation_courses` - 邀请课程关联表
- `invitation_question_banks` - 邀请题库关联表

**功能说明：**
- 支持创建邀请链接，关联课程和题库
- 设置邀请码和密码，控制访问权限
- 支持邀请链接过期时间设置

## 表关系说明

### 用户相关关系
```
users (1) ←→ (N) user_courses ←→ (1) courses
users (1) ←→ (N) user_question_banks ←→ (1) question_banks
users (1) ←→ (N) user_exam_papers ←→ (1) exam_papers
```

### 题库相关关系
```
question_banks (1) ←→ (N) questions (1) ←→ (N) question_options
question_banks (1) ←→ (N) questions (1) ←→ (N) question_answers
```

### 题库练习结果相关关系
```
question_banks (1) ←→ (N) question_bank_results (1) ←→ (N) question_bank_question_results
```

### 考试相关关系
```
exam_results (1) ←→ (N) question_results (1) ←→ (N) question_result_correct_answers
exam_results (1) ←→ (N) question_results (1) ←→ (N) question_result_user_answers
```

### 试卷相关关系
```
exam_papers (1) ←→ (N) exam_paper_questions ←→ (1) questions
exam_papers (1) ←→ (N) exam_paper_results (1) ←→ (N) exam_paper_question_results
```

### 邀请相关关系
```
invitation_links (1) ←→ (N) invitation_courses ←→ (1) courses
invitation_links (1) ←→ (N) invitation_question_banks ←→ (1) question_banks
```

## 角色权限说明

### 用户角色类型
- `SUPER_ADMIN` - 超级管理员
- `ADMIN` - 管理员
- `EXPLOSIVE_USER` - 易制爆用户
- `BLAST_USER` - 爆破用户

### 权限控制机制
- 通过 `*_visible_roles` 表控制资源对不同角色的可见性
- 支持一个资源对多个角色可见
- 管理员可以管理所有资源

## 数据完整性

### 外键约束
- 所有关联表都设置了外键约束
- 支持级联删除，确保数据一致性
- 使用 `ON DELETE CASCADE` 和 `ON UPDATE RESTRICT`

### 唯一索引
- 用户名、身份证号、手机号等关键字段设置唯一索引
- 用户与课程、题库、试卷的关系设置唯一约束
- 防止重复数据产生

### 索引优化
- 为常用查询字段创建索引
- 支持复合索引，提高查询性能
- 为时间字段创建索引，支持时间范围查询

## 扩展性设计

### 模块化设计
- 每个功能模块相对独立
- 支持模块级别的扩展和修改
- 便于功能迭代和维护

### 配置化设计
- 支持角色权限配置
- 支持考试参数配置
- 支持系统参数配置

### 数据迁移支持
- 提供完整的数据库迁移脚本
- 支持版本升级和数据迁移
- 保持向后兼容性

## 性能优化建议

### 查询优化
- 合理使用索引，避免全表扫描
- 优化复杂查询，使用适当的JOIN
- 考虑分页查询，避免大量数据返回

### 存储优化
- 合理设置字段类型和长度
- 使用适当的字符集和排序规则
- 定期清理无用数据

### 缓存策略
- 对热点数据进行缓存
- 使用Redis等缓存系统
- 合理设置缓存过期时间

## 安全考虑

### 数据加密
- 用户密码使用BCrypt加密
- 敏感信息传输使用HTTPS
- 数据库连接使用SSL

### 权限控制
- 基于角色的访问控制
- API接口权限验证
- 数据访问权限控制

### 审计日志
- 记录重要操作日志
- 支持操作追溯
- 异常行为监控

## 维护建议

### 定期维护
- 定期备份数据库
- 监控数据库性能
- 清理过期数据

### 版本管理
- 使用版本控制管理数据库变更
- 记录数据库变更历史
- 支持回滚操作

### 监控告警
- 设置数据库监控指标
- 配置异常告警机制
- 定期检查系统健康状态 