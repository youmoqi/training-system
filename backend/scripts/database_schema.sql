/*
 * 易制爆与爆破作业人员培训系统数据库结构
 * 版本: 1.0
 * 创建时间: 2025-01-07
 * 说明: 本文件包含完整的数据库表结构，按功能模块组织
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 模块一：用户管理模块
-- 功能：用户注册、登录、权限管理
-- =====================================================

-- 用户表 - 存储系统所有用户信息
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(加密)',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '性别',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `work_unit` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工作单位',
  `training_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '培训类型',
  `job_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作业类别',
  `face_photo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '人脸照片URL',
  `payment_amount` decimal(10, 2) NOT NULL COMMENT '缴费额度',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户角色',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 257 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '用户表';

-- =====================================================
-- 模块二：课程管理模块
-- 功能：课程信息管理、用户选课、学习进度
-- =====================================================

-- 课程表 - 存储培训课程信息
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频URL',
  `price` decimal(10, 2) NOT NULL COMMENT '课程价格',
  `is_online` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否上线',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `cover_image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_is_online`(`is_online` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 233 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '课程表';

-- 课程可见角色表 - 控制课程对不同角色的可见性
DROP TABLE IF EXISTS `course_visible_roles`;
CREATE TABLE `course_visible_roles` (
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
  PRIMARY KEY (`course_id`, `role`) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  CONSTRAINT `course_visible_roles_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '课程可见角色表';

-- 用户课程关系表 - 记录用户选课和学习进度
DROP TABLE IF EXISTS `user_courses`;
CREATE TABLE `user_courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `enroll_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `is_completed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否完成',
  `complete_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `watch_progress` int NULL DEFAULT 0 COMMENT '观看进度(百分比)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_course`(`user_id` ASC, `course_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_is_completed`(`is_completed` ASC) USING BTREE,
  CONSTRAINT `user_courses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_courses_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 249 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '用户课程关系表';

-- =====================================================
-- 模块三：题库管理模块
-- 功能：题目管理、题库分类、题目选项和答案
-- =====================================================

-- 题库表 - 存储题目集合信息
DROP TABLE IF EXISTS `question_banks`;
CREATE TABLE `question_banks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题库ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题库标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题库描述',
  `price` decimal(10, 2) NOT NULL COMMENT '题库价格',
  `is_online` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否上线',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_is_online`(`is_online` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '题库表';

-- 题库可见角色表 - 控制题库对不同角色的可见性
DROP TABLE IF EXISTS `question_bank_visible_roles`;
CREATE TABLE `question_bank_visible_roles` (
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
  PRIMARY KEY (`question_bank_id`, `role`) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  CONSTRAINT `question_bank_visible_roles_ibfk_1` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '题库可见角色表';

-- 题目表 - 存储具体题目信息
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_bank_id` bigint NOT NULL COMMENT '所属题库ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题目解析',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_bank_id`(`question_bank_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 381 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '题目表';

-- 题目选项表 - 存储选择题的选项
DROP TABLE IF EXISTS `question_options`;
CREATE TABLE `question_options` (
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `option_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项内容',
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `question_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '题目选项表';

-- 题目答案表 - 存储题目的正确答案
DROP TABLE IF EXISTS `question_answers`;
CREATE TABLE `question_answers` (
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `answer` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '答案内容',
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `question_answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '题目答案表';

-- 用户题库关系表 - 记录用户购买的题库
DROP TABLE IF EXISTS `user_question_banks`;
CREATE TABLE `user_question_banks` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  `purchase_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `is_completed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否完成',
  `complete_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `score` int NULL DEFAULT NULL COMMENT '最高得分',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_question_bank`(`user_id` ASC, `question_bank_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_question_bank_id`(`question_bank_id` ASC) USING BTREE,
  INDEX `idx_is_completed`(`is_completed` ASC) USING BTREE,
  CONSTRAINT `user_question_banks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_question_banks_ibfk_2` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 202 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '用户题库关系表';

-- =====================================================
-- 模块四：考试管理模块
-- 功能：题库练习考试、考试结果记录
-- =====================================================

-- 考试结果表 - 记录用户题库练习的考试结果
DROP TABLE IF EXISTS `exam_results`;
CREATE TABLE `exam_results` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试结果ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  `score` int NOT NULL COMMENT '得分',
  `total_questions` int NOT NULL COMMENT '总题目数',
  `correct_answers` int NOT NULL COMMENT '正确答题数',
  `time_taken` int NULL DEFAULT NULL COMMENT '用时(秒)',
  `exam_time` datetime(6) NOT NULL COMMENT '考试时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKmwbdklnv7ohk7wpi7qngmsjbb`(`question_bank_id` ASC) USING BTREE,
  INDEX `FKt2jcn29o332cpiv7s7h3o877e`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKmwbdklnv7ohk7wpi7qngmsjbb` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKt2jcn29o332cpiv7s7h3o877e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '考试结果表';

-- 题目答题结果表 - 记录每道题的答题情况
DROP TABLE IF EXISTS `question_results`;
CREATE TABLE `question_results` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题结果ID',
  `exam_result_id` bigint NOT NULL COMMENT '考试结果ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `is_correct` bit(1) NOT NULL COMMENT '是否正确',
  `explanation` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '解析',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKdfnvqtu823qpi32575hx2g6dv`(`exam_result_id` ASC) USING BTREE,
  INDEX `FKklak3v8y8oniosgx9cj8yqq2`(`question_id` ASC) USING BTREE,
  CONSTRAINT `FKdfnvqtu823qpi32575hx2g6dv` FOREIGN KEY (`exam_result_id`) REFERENCES `exam_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKklak3v8y8oniosgx9cj8yqq2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '题目答题结果表';

-- 题目答题结果-正确答案表 - 存储答题时的正确答案
DROP TABLE IF EXISTS `question_result_correct_answers`;
CREATE TABLE `question_result_correct_answers` (
  `question_result_id` bigint NOT NULL COMMENT '答题结果ID',
  `correct_answer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '正确答案',
  INDEX `FKn3xwnluf13wqnjhlsjwv9x96q`(`question_result_id` ASC) USING BTREE,
  CONSTRAINT `FKn3xwnluf13wqnjhlsjwv9x96q` FOREIGN KEY (`question_result_id`) REFERENCES `question_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '题目答题结果-正确答案表';

-- 题目答题结果-用户答案表 - 存储用户的答题答案
DROP TABLE IF EXISTS `question_result_user_answers`;
CREATE TABLE `question_result_user_answers` (
  `question_result_id` bigint NOT NULL COMMENT '答题结果ID',
  `user_answer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户答案',
  INDEX `FK1vbf3kg73t0vnte7sp8o2klq7`(`question_result_id` ASC) USING BTREE,
  CONSTRAINT `FK1vbf3kg73t0vnte7sp8o2klq7` FOREIGN KEY (`question_result_id`) REFERENCES `question_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC COMMENT = '题目答题结果-用户答案表';

-- =====================================================
-- 模块五：试卷管理模块
-- 功能：试卷创建、题目管理、考试记录
-- =====================================================

-- 试卷表 - 存储试卷基本信息
DROP TABLE IF EXISTS `exam_papers`;
CREATE TABLE `exam_papers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '试卷标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '试卷描述',
  `total_score` int NOT NULL DEFAULT 100 COMMENT '总分',
  `pass_score` int NOT NULL DEFAULT 60 COMMENT '及格分数',
  `duration` int NOT NULL DEFAULT 120 COMMENT '考试时长（分钟）',
  `total_questions` int NOT NULL DEFAULT 0 COMMENT '题目总数',
  `is_online` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否上线',
  `is_random` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否随机出题',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_is_online`(`is_online` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '试卷表';

-- 试卷题目关联表 - 记录试卷包含的题目及分值
DROP TABLE IF EXISTS `exam_paper_questions`;
CREATE TABLE `exam_paper_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NOT NULL DEFAULT 0 COMMENT '题目顺序',
  `score` int NOT NULL DEFAULT 0 COMMENT '题目分值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_paper_question`(`exam_paper_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_paper_id`(`exam_paper_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `exam_paper_questions_ibfk_1` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_paper_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '试卷题目关联表';

-- 试卷可见角色表 - 控制试卷对不同角色的可见性
DROP TABLE IF EXISTS `exam_paper_visible_roles`;
CREATE TABLE `exam_paper_visible_roles` (
  `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
  PRIMARY KEY (`exam_paper_id`, `role`) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  CONSTRAINT `exam_paper_visible_roles_ibfk_1` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '试卷可见角色表';

-- 用户试卷关系表 - 记录用户购买的试卷
DROP TABLE IF EXISTS `user_exam_papers`;
CREATE TABLE `user_exam_papers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
  `purchase_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `is_completed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否完成',
  `complete_time` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `attempt_count` int NOT NULL DEFAULT 0 COMMENT '考试次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_exam_paper`(`user_id` ASC, `exam_paper_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_exam_paper_id`(`exam_paper_id` ASC) USING BTREE,
  INDEX `idx_is_completed`(`is_completed` ASC) USING BTREE,
  CONSTRAINT `user_exam_papers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_exam_papers_ibfk_2` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '用户试卷关系表';

-- 试卷考试记录表 - 记录用户试卷考试结果
DROP TABLE IF EXISTS `exam_paper_results`;
CREATE TABLE `exam_paper_results` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
  `score` int NOT NULL COMMENT '得分',
  `total_score` int NOT NULL COMMENT '总分',
  `correct_answers` int NOT NULL COMMENT '正确答题数',
  `total_questions` int NOT NULL COMMENT '总题目数',
  `time_taken` int NULL DEFAULT NULL COMMENT '用时（秒）',
  `exam_time` datetime(6) NOT NULL COMMENT '考试时间',
  `is_passed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否通过',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_exam_paper_id`(`exam_paper_id` ASC) USING BTREE,
  INDEX `idx_exam_time`(`exam_time` ASC) USING BTREE,
  CONSTRAINT `exam_paper_results_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_paper_results_ibfk_2` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '试卷考试记录表';

-- 试卷题目答题记录表 - 记录试卷中每道题的答题情况
DROP TABLE IF EXISTS `exam_paper_question_results`;
CREATE TABLE `exam_paper_question_results` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `exam_paper_result_id` bigint NOT NULL COMMENT '试卷考试记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户答案',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正确答案',
  `is_correct` bit(1) NOT NULL COMMENT '是否正确',
  `score` int NOT NULL DEFAULT 0 COMMENT '得分',
  `max_score` int NOT NULL DEFAULT 0 COMMENT '满分',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '解析',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_paper_result_id`(`exam_paper_result_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `exam_paper_question_results_ibfk_1` FOREIGN KEY (`exam_paper_result_id`) REFERENCES `exam_paper_results` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_paper_question_results_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '试卷题目答题记录表';

-- =====================================================
-- 模块六：邀请管理模块
-- 功能：邀请链接管理、课程和题库关联
-- =====================================================

-- 邀请链接表 - 存储邀请链接信息
DROP TABLE IF EXISTS `invitation_links`;
CREATE TABLE `invitation_links` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '邀请链接ID',
  `link_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邀请码',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否激活',
  `expire_time` timestamp NOT NULL COMMENT '过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `link_code`(`link_code` ASC) USING BTREE,
  INDEX `idx_link_code`(`link_code` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_expire_time`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 89 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '邀请链接表';

-- 邀请课程关联表 - 记录邀请链接关联的课程
DROP TABLE IF EXISTS `invitation_courses`;
CREATE TABLE `invitation_courses` (
  `invitation_id` bigint NOT NULL COMMENT '邀请链接ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  PRIMARY KEY (`invitation_id`, `course_id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `invitation_courses_ibfk_1` FOREIGN KEY (`invitation_id`) REFERENCES `invitation_links` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `invitation_courses_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '邀请课程关联表';

-- 邀请题库关联表 - 记录邀请链接关联的题库
DROP TABLE IF EXISTS `invitation_question_banks`;
CREATE TABLE `invitation_question_banks` (
  `invitation_id` bigint NOT NULL COMMENT '邀请链接ID',
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  PRIMARY KEY (`invitation_id`, `question_bank_id`) USING BTREE,
  INDEX `question_bank_id`(`question_bank_id` ASC) USING BTREE,
  CONSTRAINT `invitation_question_banks_ibfk_1` FOREIGN KEY (`invitation_id`) REFERENCES `invitation_links` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `invitation_question_banks_ibfk_2` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC COMMENT = '邀请题库关联表';

SET FOREIGN_KEY_CHECKS = 1;
