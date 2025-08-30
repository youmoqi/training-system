/*
 Navicat Premium Dump SQL

 Source Server         : 8.130.78.1
 Source Server Type    : MySQL
 Source Server Version : 80403 (8.4.3)
 Source Host           : 8.130.78.1:3306
 Source Schema         : training_system

 Target Server Type    : MySQL
 Target Server Version : 80403 (8.4.3)
 File Encoding         : 65001

 Date: 30/08/2025 17:19:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for course_visible_roles
-- ----------------------------
DROP TABLE IF EXISTS `course_visible_roles`;
CREATE TABLE `course_visible_roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `visibility_role_id` bigint NULL DEFAULT NULL COMMENT '可见角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_qb_vis_cat`(`visibility_role_id` ASC) USING BTREE,
  INDEX `fk_qb_cou_ban`(`course_id` ASC) USING BTREE,
  CONSTRAINT `fk_qb_cou_ban` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_qb_cou_cat` FOREIGN KEY (`visibility_role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程可见角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 235 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_auto_rules
-- ----------------------------
DROP TABLE IF EXISTS `exam_auto_rules`;
CREATE TABLE `exam_auto_rules`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `question_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型',
  `question_count` int NOT NULL DEFAULT 0 COMMENT '题目数量',
  `question_bank_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题库ID列表(JSON格式)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_paper_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_type`(`question_type` ASC) USING BTREE,
  CONSTRAINT `exam_auto_rules_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷自动组卷规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_question_results
-- ----------------------------
DROP TABLE IF EXISTS `exam_question_results`;
CREATE TABLE `exam_question_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `exam_result_id` bigint NOT NULL COMMENT '试卷考试记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户答案',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正确答案',
  `is_correct` bit(1) NOT NULL COMMENT '是否正确',
  `score` int NOT NULL DEFAULT 0 COMMENT '得分',
  `max_score` int NOT NULL DEFAULT 0 COMMENT '满分',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '解析',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_paper_result_id`(`exam_result_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `exam_question_results_ibfk_1` FOREIGN KEY (`exam_result_id`) REFERENCES `exam_results` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_question_results_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷题目答题记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_questions
-- ----------------------------
DROP TABLE IF EXISTS `exam_questions`;
CREATE TABLE `exam_questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NOT NULL DEFAULT 0 COMMENT '题目顺序',
  `score` int NOT NULL DEFAULT 0 COMMENT '题目分值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_paper_question`(`exam_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_exam_paper_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `exam_questions_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷题目关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_results
-- ----------------------------
DROP TABLE IF EXISTS `exam_results`;
CREATE TABLE `exam_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
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
  INDEX `idx_exam_paper_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_exam_time`(`exam_time` ASC) USING BTREE,
  CONSTRAINT `exam_results_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `exam_results_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷考试记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exam_visible_roles
-- ----------------------------
DROP TABLE IF EXISTS `exam_visible_roles`;
CREATE TABLE `exam_visible_roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `visibility_role_id` bigint NULL DEFAULT NULL COMMENT '可见角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_qb_vis_cat`(`visibility_role_id` ASC) USING BTREE,
  INDEX `fk_qb_exa_ban`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `fk_qb_exa_ban` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `FKdg174pqdgwcby6xmxbns94ey5` FOREIGN KEY (`visibility_role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '考试可见角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for exams
-- ----------------------------
DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '试卷标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '试卷描述',
  `total_score` int NOT NULL DEFAULT 100 COMMENT '总分',
  `pass_score` int NOT NULL DEFAULT 60 COMMENT '及格分数',
  `duration` int NOT NULL DEFAULT 120 COMMENT '考试时长（分钟）',
  `total_questions` int NOT NULL DEFAULT 0 COMMENT '题目总数',
  `is_online` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否上线',
  `exam_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'GENERAL' COMMENT '考试分类',
  `allow_retake` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许重复考试',
  `max_attempts` int NOT NULL DEFAULT 3 COMMENT '最大考试次数',
  `single_choice_score` int NOT NULL DEFAULT 2 COMMENT '单选题分值',
  `multiple_choice_score` int NOT NULL DEFAULT 4 COMMENT '多选题分值',
  `true_false_score` int NOT NULL DEFAULT 2 COMMENT '判断题分值',
  `fill_blank_score` int NOT NULL DEFAULT 3 COMMENT '填空题分值',
  `short_answer_score` int NOT NULL DEFAULT 5 COMMENT '主观题分值',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_is_online`(`is_online` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for invitation_courses
-- ----------------------------
DROP TABLE IF EXISTS `invitation_courses`;
CREATE TABLE `invitation_courses`  (
  `invitation_id` bigint NOT NULL COMMENT '邀请链接ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  PRIMARY KEY (`invitation_id`, `course_id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  CONSTRAINT `invitation_courses_ibfk_1` FOREIGN KEY (`invitation_id`) REFERENCES `invitation_links` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `invitation_courses_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邀请课程关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for invitation_links
-- ----------------------------
DROP TABLE IF EXISTS `invitation_links`;
CREATE TABLE `invitation_links`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邀请链接表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for invitation_question_banks
-- ----------------------------
DROP TABLE IF EXISTS `invitation_question_banks`;
CREATE TABLE `invitation_question_banks`  (
  `invitation_id` bigint NOT NULL COMMENT '邀请链接ID',
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  PRIMARY KEY (`invitation_id`, `question_bank_id`) USING BTREE,
  INDEX `question_bank_id`(`question_bank_id` ASC) USING BTREE,
  CONSTRAINT `invitation_question_banks_ibfk_1` FOREIGN KEY (`invitation_id`) REFERENCES `invitation_links` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `invitation_question_banks_ibfk_2` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邀请题库关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for job_categories
-- ----------------------------
DROP TABLE IF EXISTS `job_categories`;
CREATE TABLE `job_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answers
-- ----------------------------
DROP TABLE IF EXISTS `question_answers`;
CREATE TABLE `question_answers`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `answer_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `answer_order` int NULL DEFAULT NULL,
  `answer_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `question_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKrms3u35c10orgjqyw03ajd7x7`(`question_id` ASC) USING BTREE,
  CONSTRAINT `FKrms3u35c10orgjqyw03ajd7x7` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_bank_question_results
-- ----------------------------
DROP TABLE IF EXISTS `question_bank_question_results`;
CREATE TABLE `question_bank_question_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `question_bank_result_id` bigint NOT NULL COMMENT '题库练习记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户答案',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '正确答案',
  `is_correct` bit(1) NOT NULL COMMENT '是否正确',
  `score` int NOT NULL DEFAULT 0 COMMENT '得分',
  `max_score` int NOT NULL DEFAULT 0 COMMENT '满分',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '解析',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_bank_result_id`(`question_bank_result_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `question_bank_question_results_ibfk_1` FOREIGN KEY (`question_bank_result_id`) REFERENCES `question_bank_results` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `question_bank_question_results_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库练习题目答题记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_bank_results
-- ----------------------------
DROP TABLE IF EXISTS `question_bank_results`;
CREATE TABLE `question_bank_results`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '练习记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  `score` int NOT NULL COMMENT '得分',
  `total_score` int NOT NULL COMMENT '总分',
  `correct_answers` int NOT NULL COMMENT '正确答题数',
  `total_questions` int NOT NULL COMMENT '总题目数',
  `time_taken` int NULL DEFAULT NULL COMMENT '用时（秒）',
  `submit_time` datetime(6) NOT NULL COMMENT '提交时间',
  `is_passed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否通过',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_question_bank_id`(`question_bank_id` ASC) USING BTREE,
  INDEX `idx_submit_time`(`submit_time` ASC) USING BTREE,
  CONSTRAINT `question_bank_results_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `question_bank_results_ibfk_2` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库练习结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_bank_visible_roles
-- ----------------------------
DROP TABLE IF EXISTS `question_bank_visible_roles`;
CREATE TABLE `question_bank_visible_roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  `visibility_role_id` bigint NULL DEFAULT NULL COMMENT '可见角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_qb_vis_cat`(`visibility_role_id` ASC) USING BTREE,
  INDEX `fk_qb_que_ban`(`question_bank_id` ASC) USING BTREE,
  CONSTRAINT `fk_qb_que_ban` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `FKpw74h0evvxsbnk24smi70jgx` FOREIGN KEY (`visibility_role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库可见角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_banks
-- ----------------------------
DROP TABLE IF EXISTS `question_banks`;
CREATE TABLE `question_banks`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 121 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_options
-- ----------------------------
DROP TABLE IF EXISTS `question_options`;
CREATE TABLE `question_options`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '选项ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `option_label` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项标签(A,B,C,D...)',
  `option_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项内容',
  `option_order` int NOT NULL COMMENT '选项顺序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_option_label`(`question_id` ASC, `option_label` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE,
  CONSTRAINT `question_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 141 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题目选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_bank_id` bigint NOT NULL COMMENT '所属题库ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题目解析',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题目答案',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_bank_id`(`question_bank_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 389 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for registration_config
-- ----------------------------
DROP TABLE IF EXISTS `registration_config`;
CREATE TABLE `registration_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fields_config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段配置JSON',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '注册信息配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for training_certificates
-- ----------------------------
DROP TABLE IF EXISTS `training_certificates`;
CREATE TABLE `training_certificates`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '证书ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `certificate_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证书编号',
  `issue_date` timestamp NOT NULL COMMENT '颁发日期',
  `complete_date` timestamp NOT NULL COMMENT '完成日期',
  `is_paid` tinyint(1) NOT NULL COMMENT '是否收费',
  `certificate_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证书类型',
  `job_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作业类别',
  `engineer_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工程技术人员等级 A/B/C/D',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `certificate_number`(`certificate_number` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_certificate_type`(`certificate_type` ASC) USING BTREE,
  INDEX `idx_is_paid`(`is_paid` ASC) USING BTREE,
  CONSTRAINT `training_certificates_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `training_certificates_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培训证明表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_courses
-- ----------------------------
DROP TABLE IF EXISTS `user_courses`;
CREATE TABLE `user_courses`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 255 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户课程关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_exams
-- ----------------------------
DROP TABLE IF EXISTS `user_exams`;
CREATE TABLE `user_exams`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `exam_id` bigint NOT NULL COMMENT '试卷ID',
  `purchase_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_exam_paper`(`user_id` ASC, `exam_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_exam_paper_id`(`exam_id` ASC) USING BTREE,
  CONSTRAINT `user_exams_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_exams_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户试卷关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_question_banks
-- ----------------------------
DROP TABLE IF EXISTS `user_question_banks`;
CREATE TABLE `user_question_banks`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_bank_id` bigint NOT NULL COMMENT '题库ID',
  `purchase_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_question_bank`(`user_id` ASC, `question_bank_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_question_bank_id`(`question_bank_id` ASC) USING BTREE,
  CONSTRAINT `user_question_banks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_question_banks_ibfk_2` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 203 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户题库关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(加密)',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `work_unit` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作单位',
  `training_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '培训类型',
  `job_category_id` bigint NULL DEFAULT NULL COMMENT '作业类别',
  `face_photo_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人脸照片URL',
  `payment_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '缴费额度',
  `can_learn` tinyint(1) NULL DEFAULT 0 COMMENT '学习权限',
  `can_exam` tinyint(1) NULL DEFAULT 0 COMMENT '考试权限',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色分类ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `id_card`(`id_card` ASC) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `FKardt8pfj2l4r0kjn04q1jcxkn`(`role_id` ASC) USING BTREE,
  INDEX `FKsvdx48thf3vfak6tvd4an22en`(`job_category_id` ASC) USING BTREE,
  CONSTRAINT `FKardt8pfj2l4r0kjn04q1jcxkn` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKsvdx48thf3vfak6tvd4an22en` FOREIGN KEY (`job_category_id`) REFERENCES `job_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 259 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
