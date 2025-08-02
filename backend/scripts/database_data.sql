/*
 Navicat Premium Dump SQL

 Source Server         : LOCAL_MYSQL
 Source Server Type    : MySQL
 Source Server Version : 80023 (8.0.23)
 Source Host           : localhost:3306
 Source Schema         : training_system

 Target Server Type    : MySQL
 Target Server Version : 80023 (8.0.23)
 File Encoding         : 65001

 Date: 03/08/2025 01:10:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for course_visible_roles
-- ----------------------------
DROP TABLE IF EXISTS `course_visible_roles`;
CREATE TABLE `course_visible_roles`  (
                                         `course_id` bigint NOT NULL COMMENT '课程ID',
                                         `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
                                         PRIMARY KEY (`course_id`, `role`) USING BTREE,
                                         INDEX `idx_role`(`role` ASC) USING BTREE,
                                         CONSTRAINT `course_visible_roles_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程可见角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course_visible_roles
-- ----------------------------
INSERT INTO `course_visible_roles` VALUES (4, 'BLAST_USER');
INSERT INTO `course_visible_roles` VALUES (5, 'BLAST_USER');
INSERT INTO `course_visible_roles` VALUES (6, 'BLAST_USER');
INSERT INTO `course_visible_roles` VALUES (7, 'BLAST_USER');
INSERT INTO `course_visible_roles` VALUES (8, 'BLAST_USER');
INSERT INTO `course_visible_roles` VALUES (1, 'EXPLOSIVE_USER');
INSERT INTO `course_visible_roles` VALUES (2, 'EXPLOSIVE_USER');
INSERT INTO `course_visible_roles` VALUES (3, 'EXPLOSIVE_USER');
INSERT INTO `course_visible_roles` VALUES (7, 'EXPLOSIVE_USER');
INSERT INTO `course_visible_roles` VALUES (8, 'EXPLOSIVE_USER');

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
) ENGINE = InnoDB AUTO_INCREMENT = 233 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of courses
-- ----------------------------
INSERT INTO `courses` VALUES (1, '易制爆化学品基础知识', '本课程介绍易制爆化学品的基本概念、分类、特性等基础知识，为后续深入学习打下基础。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 200.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:14', NULL);
INSERT INTO `courses` VALUES (2, '易制爆化学品安全管理', '详细介绍易制爆化学品的安全管理制度、操作规程、应急处置等内容。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 300.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:16', NULL);
INSERT INTO `courses` VALUES (3, '易制爆化学品储存与运输', '学习易制爆化学品的储存条件、运输要求、包装规范等专业知识。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 250.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:18', NULL);
INSERT INTO `courses` VALUES (4, '爆破作业安全技术', '爆破作业的基本原理、安全技术要求、操作规程等内容。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 400.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:20', NULL);
INSERT INTO `courses` VALUES (5, '爆破器材使用与管理', '爆破器材的种类、使用方法、管理制度等专业知识。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 350.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:23', NULL);
INSERT INTO `courses` VALUES (6, '爆破现场安全管理', '爆破现场的安全管理、人员防护、应急预案等内容。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 300.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:25', NULL);
INSERT INTO `courses` VALUES (7, '法律法规与标准规范', '相关法律法规、标准规范的解读和应用。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 150.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:28', NULL);
INSERT INTO `courses` VALUES (8, '事故案例分析', '典型事故案例的分析和教训总结。', 'https://example.com/videos/accident-cases.mp4', 200.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53', NULL);

-- ----------------------------
-- Table structure for exam_categories
-- ----------------------------
DROP TABLE IF EXISTS `exam_categories`;
CREATE TABLE `exam_categories`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                    `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类代码',
                                    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '分类描述',
                                    `parent_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父分类代码',
                                    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
                                    `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `unique_code`(`code` ASC) USING BTREE,
                                    INDEX `idx_parent_code`(`parent_code` ASC) USING BTREE,
                                    INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '考试分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_categories
-- ----------------------------
INSERT INTO `exam_categories` VALUES (1, 'EXPLOSIVE_FIRST', '易制爆用户-首次培训', '易制爆用户首次培训考试', NULL, 1, 1, '2025-08-02 15:34:32', '2025-08-02 15:34:32');
INSERT INTO `exam_categories` VALUES (2, 'EXPLOSIVE_CONTINUE', '易制爆用户-继续教育', '易制爆用户继续教育培训考试', NULL, 2, 1, '2025-08-02 15:34:32', '2025-08-02 15:34:32');
INSERT INTO `exam_categories` VALUES (3, 'BLAST_THREE_FIRST', '爆破三大员-首次培训', '爆破三大员首次培训考试', NULL, 3, 1, '2025-08-02 15:34:32', '2025-08-02 15:34:32');
INSERT INTO `exam_categories` VALUES (4, 'BLAST_THREE_CONTINUE', '爆破三大员-继续教育', '爆破三大员继续教育培训考试', NULL, 4, 1, '2025-08-02 15:34:32', '2025-08-02 15:34:32');
INSERT INTO `exam_categories` VALUES (5, 'BLAST_TECH_FIRST', '爆破工程技术人员-首次培训', '爆破工程技术人员首次培训考试', NULL, 5, 1, '2025-08-02 15:34:32', '2025-08-02 15:34:32');
INSERT INTO `exam_categories` VALUES (6, 'BLAST_TECH_CONTINUE', '爆破工程技术人员-继续教育', '爆破工程技术人员继续教育培训考试', NULL, 6, 1, '2025-08-02 15:34:32', '2025-08-02 15:34:32');

-- ----------------------------
-- Table structure for exam_paper_auto_rules
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_auto_rules`;
CREATE TABLE `exam_paper_auto_rules`  (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
                                          `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
                                          `question_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型',
                                          `question_count` int NOT NULL DEFAULT 0 COMMENT '题目数量',
                                          `question_bank_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '题库ID列表(JSON格式)',
                                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          INDEX `idx_exam_paper_id`(`exam_paper_id` ASC) USING BTREE,
                                          INDEX `idx_question_type`(`question_type` ASC) USING BTREE,
                                          CONSTRAINT `exam_paper_auto_rules_ibfk_1` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷自动组卷规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_paper_auto_rules
-- ----------------------------
INSERT INTO `exam_paper_auto_rules` VALUES (4, 19, 'SINGLE_CHOICE', 1, '[1]', '2025-08-02 21:15:50', '2025-08-02 21:15:50');

-- ----------------------------
-- Table structure for exam_paper_history
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_history`;
CREATE TABLE `exam_paper_history`  (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `attempt_number` int NOT NULL,
                                       `create_time` datetime(6) NOT NULL,
                                       `exam_paper_id` bigint NOT NULL,
                                       `exam_paper_result_id` bigint NOT NULL,
                                       `exam_time` datetime(6) NOT NULL,
                                       `is_passed` bit(1) NOT NULL,
                                       `score` int NOT NULL,
                                       `time_taken` int NULL DEFAULT NULL,
                                       `total_score` int NOT NULL,
                                       `user_id` bigint NOT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       INDEX `FKiw6r5etco59xnewyjwe2rv04h`(`exam_paper_id` ASC) USING BTREE,
                                       INDEX `FKemp2n27xkwmfbwvvbr2alknsm`(`exam_paper_result_id` ASC) USING BTREE,
                                       INDEX `FK4v8w6tfnqbxu8ok0nwux653hx`(`user_id` ASC) USING BTREE,
                                       CONSTRAINT `FK4v8w6tfnqbxu8ok0nwux653hx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `FKemp2n27xkwmfbwvvbr2alknsm` FOREIGN KEY (`exam_paper_result_id`) REFERENCES `exam_paper_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `FKiw6r5etco59xnewyjwe2rv04h` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_paper_history
-- ----------------------------

-- ----------------------------
-- Table structure for exam_paper_question_results
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_question_results`;
CREATE TABLE `exam_paper_question_results`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷题目答题记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_paper_question_results
-- ----------------------------
INSERT INTO `exam_paper_question_results` VALUES (7, 3, 1, 'B', 'A', b'0', 0, 2, '硝酸铵是典型的易制爆化学品，广泛用于制造炸药fadsfadsf。');
INSERT INTO `exam_paper_question_results` VALUES (8, 3, 2, 'eee', '30', b'0', 0, 5, '易制爆化学品对温度敏感，一般储存温度不超过30℃。');
INSERT INTO `exam_paper_question_results` VALUES (9, 3, 3, 'A,B', 'B', b'0', 0, 4, '易制爆化学品具有爆炸性、氧化性、毒性等多种危险特性。');
INSERT INTO `exam_paper_question_results` VALUES (10, 3, 8, 'B,C', 'B', b'0', 0, 4, '包括设计、审批、施工、检查、起爆等步骤。');
INSERT INTO `exam_paper_question_results` VALUES (11, 4, 1, 'A', 'A', b'1', 2, 2, '硝酸铵是典型的易制爆化学品，广泛用于制造炸药fadsfadsf。');
INSERT INTO `exam_paper_question_results` VALUES (12, 4, 2, '', '30', b'0', 0, 5, '易制爆化学品对温度敏感，一般储存温度不超过30℃。');
INSERT INTO `exam_paper_question_results` VALUES (13, 4, 3, '', 'B', b'0', 0, 4, '易制爆化学品具有爆炸性、氧化性、毒性等多种危险特性。');
INSERT INTO `exam_paper_question_results` VALUES (14, 4, 8, '', 'B', b'0', 0, 4, '包括设计、审批、施工、检查、起爆等步骤。');
INSERT INTO `exam_paper_question_results` VALUES (15, 5, 380, 'B', 'B', b'1', 2, 2, '234');

-- ----------------------------
-- Table structure for exam_paper_questions
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_questions`;
CREATE TABLE `exam_paper_questions`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷题目关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_paper_questions
-- ----------------------------
INSERT INTO `exam_paper_questions` VALUES (14, 1, 1, 1, 2, '2025-08-02 19:48:29');
INSERT INTO `exam_paper_questions` VALUES (16, 1, 3, 3, 4, '2025-08-02 19:48:29');
INSERT INTO `exam_paper_questions` VALUES (17, 1, 2, 2, 5, '2025-08-02 19:48:49');
INSERT INTO `exam_paper_questions` VALUES (18, 1, 8, 4, 4, '2025-08-02 19:50:02');
INSERT INTO `exam_paper_questions` VALUES (19, 19, 380, 1, 2, '2025-08-02 21:15:53');

-- ----------------------------
-- Table structure for exam_paper_results
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_results`;
CREATE TABLE `exam_paper_results`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷考试记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_paper_results
-- ----------------------------
INSERT INTO `exam_paper_results` VALUES (3, 164, 1, 0, 100, 0, 4, 8, '2025-08-02 19:50:22.666940', 0, '2025-08-02 19:50:23');
INSERT INTO `exam_paper_results` VALUES (4, 164, 1, 2, 100, 1, 4, 2, '2025-08-02 19:55:02.367637', 0, '2025-08-02 19:55:02');
INSERT INTO `exam_paper_results` VALUES (5, 164, 19, 2, 100, 1, 1, 2, '2025-08-02 21:16:27.200526', 0, '2025-08-02 21:16:27');

-- ----------------------------
-- Table structure for exam_paper_visible_roles
-- ----------------------------
DROP TABLE IF EXISTS `exam_paper_visible_roles`;
CREATE TABLE `exam_paper_visible_roles`  (
                                             `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
                                             `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
                                             PRIMARY KEY (`exam_paper_id`, `role`) USING BTREE,
                                             INDEX `idx_role`(`role` ASC) USING BTREE,
                                             CONSTRAINT `exam_paper_visible_roles_ibfk_1` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '试卷可见角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_paper_visible_roles
-- ----------------------------
INSERT INTO `exam_paper_visible_roles` VALUES (2, 'BLAST_USER');
INSERT INTO `exam_paper_visible_roles` VALUES (1, 'EXPLOSIVE_USER');
INSERT INTO `exam_paper_visible_roles` VALUES (19, 'EXPLOSIVE_USER');

-- ----------------------------
-- Table structure for exam_papers
-- ----------------------------
DROP TABLE IF EXISTS `exam_papers`;
CREATE TABLE `exam_papers`  (
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
-- Records of exam_papers
-- ----------------------------
INSERT INTO `exam_papers` VALUES (1, '易制爆化学品基础知识考试', '易制爆化学品基础知识综合考试，包含选择题、多选题和判断题', 100, 60, 90, 4, 1, 'GENERAL', 1, 4, 2, 4, 2, 3, 5, '2025-01-07 10:00:00', '2025-08-03 01:06:48');
INSERT INTO `exam_papers` VALUES (2, '爆破作业安全技术考试', '爆破作业安全技术综合考试，包含理论知识和实践操作', 100, 60, 120, 2, 1, 'GENERAL', 1, 3, 2, 4, 2, 3, 5, '2025-01-07 10:00:00', '2025-08-02 17:25:59');
INSERT INTO `exam_papers` VALUES (19, '11', '111', 100, 60, 120, 1, 1, 'GENERAL', 1, 3, 2, 4, 2, 3, 5, '2025-08-02 21:15:37', '2025-08-02 21:15:52');

-- ----------------------------
-- Table structure for exam_results
-- ----------------------------
DROP TABLE IF EXISTS `exam_results`;
CREATE TABLE `exam_results`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试结果ID',
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `question_bank_id` bigint NOT NULL COMMENT '题库ID',
                                 `score` int NOT NULL COMMENT '得分',
                                 `total_questions` int NOT NULL COMMENT '总题目数',
                                 `correct_answers` int NOT NULL COMMENT '正确答题数',
                                 `time_taken` int NULL DEFAULT NULL COMMENT '用时(秒)',
                                 `exam_time` datetime(6) NOT NULL COMMENT '考试时间',
                                 `submit_time` datetime(6) NOT NULL,
                                 `is_passed` bit(1) NOT NULL,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `FKmwbdklnv7ohk7wpi7qngmsjbb`(`question_bank_id` ASC) USING BTREE,
                                 INDEX `FKt2jcn29o332cpiv7s7h3o877e`(`user_id` ASC) USING BTREE,
                                 CONSTRAINT `FKmwbdklnv7ohk7wpi7qngmsjbb` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                 CONSTRAINT `FKt2jcn29o332cpiv7s7h3o877e` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '考试结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of exam_results
-- ----------------------------

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
-- Records of invitation_courses
-- ----------------------------
INSERT INTO `invitation_courses` VALUES (85, 1);
INSERT INTO `invitation_courses` VALUES (88, 3);
INSERT INTO `invitation_courses` VALUES (1, 4);
INSERT INTO `invitation_courses` VALUES (85, 4);
INSERT INTO `invitation_courses` VALUES (87, 4);
INSERT INTO `invitation_courses` VALUES (1, 5);
INSERT INTO `invitation_courses` VALUES (87, 5);
INSERT INTO `invitation_courses` VALUES (88, 5);
INSERT INTO `invitation_courses` VALUES (2, 6);
INSERT INTO `invitation_courses` VALUES (87, 6);
INSERT INTO `invitation_courses` VALUES (2, 7);
INSERT INTO `invitation_courses` VALUES (88, 7);
INSERT INTO `invitation_courses` VALUES (85, 8);

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
) ENGINE = InnoDB AUTO_INCREMENT = 89 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邀请链接表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of invitation_links
-- ----------------------------
INSERT INTO `invitation_links` VALUES (1, 'BLAST2024A', 'blastpwd123', '爆破作业人员第一期培训', '包含爆破作业安全技术和爆破器材使用与管理课程', 1, '2025-01-01 00:00:00', '2025-06-30 21:01:15', '2025-06-30 21:01:15');
INSERT INTO `invitation_links` VALUES (2, 'BLAST2024B', 'blastpwd456', '爆破作业人员第二期培训', '包含爆破现场安全管理和法律法规与标准规范课程', 1, '2025-06-01 00:00:00', '2025-06-30 21:01:15', '2025-06-30 21:01:15');
INSERT INTO `invitation_links` VALUES (85, 'INV1751289272316A16767', '222222', '22', '22', 1, '2025-10-17 16:00:00', '2025-06-30 21:14:32', '2025-06-30 21:14:32');
INSERT INTO `invitation_links` VALUES (87, 'INV-1B5C25A4', '333333', '333', '33', 1, '2025-08-08 16:00:00', '2025-06-30 21:42:18', '2025-06-30 21:42:18');
INSERT INTO `invitation_links` VALUES (88, 'INV-9C1A024F', '111111', 'tret', 'wret', 1, '2025-09-11 16:00:00', '2025-07-01 13:29:57', '2025-07-01 13:29:57');

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
-- Records of invitation_question_banks
-- ----------------------------

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
-- Records of question_answers
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库练习题目答题记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_bank_question_results
-- ----------------------------
INSERT INTO `question_bank_question_results` VALUES (13, 3, 1, 'B', 'A', b'0', 0, 5, '硝酸铵是典型的易制爆化学品，广泛用于制造炸药fadsfadsf。');
INSERT INTO `question_bank_question_results` VALUES (14, 3, 2, '', '', b'0', 0, 5, '易制爆化学品对温度敏感，一般储存温度不超过30℃。');
INSERT INTO `question_bank_question_results` VALUES (15, 3, 3, '', '', b'0', 0, 5, '易制爆化学品具有爆炸性、氧化性、毒性等多种危险特性。');
INSERT INTO `question_bank_question_results` VALUES (16, 3, 4, '', '', b'0', 0, 5, '易制爆化学品按用途可分为氧化剂、还原剂、起爆药等类别。');
INSERT INTO `question_bank_question_results` VALUES (17, 3, 380, '', '', b'0', 0, 5, '234');
INSERT INTO `question_bank_question_results` VALUES (18, 3, 382, '', '', b'0', 0, 5, '');
INSERT INTO `question_bank_question_results` VALUES (19, 4, 1, 'A', 'A', b'1', 5, 5, '硝酸铵是典型的易制爆化学品，广泛用于制造炸药fadsfadsf。');
INSERT INTO `question_bank_question_results` VALUES (20, 4, 2, 'test', '30', b'0', 0, 5, '易制爆化学品对温度敏感，一般储存温度不超过30℃。');
INSERT INTO `question_bank_question_results` VALUES (21, 4, 3, 'A,B', 'B', b'0', 0, 5, '易制爆化学品具有爆炸性、氧化性、毒性等多种危险特性。');
INSERT INTO `question_bank_question_results` VALUES (22, 4, 4, '', '222', b'0', 0, 5, '易制爆化学品按用途可分为氧化剂、还原剂、起爆药等类别。');
INSERT INTO `question_bank_question_results` VALUES (23, 4, 380, '', 'B', b'0', 0, 5, '234');
INSERT INTO `question_bank_question_results` VALUES (24, 4, 382, '', 'A', b'0', 0, 5, '');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库练习结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_bank_results
-- ----------------------------
INSERT INTO `question_bank_results` VALUES (3, 164, 1, 0, 30, 0, 6, 3, '2025-08-02 20:05:40.608123', 0, '2025-08-02 20:05:41');
INSERT INTO `question_bank_results` VALUES (4, 164, 1, 5, 30, 1, 6, 37, '2025-08-02 20:16:58.516073', 0, '2025-08-02 20:16:59');

-- ----------------------------
-- Table structure for question_bank_visible_roles
-- ----------------------------
DROP TABLE IF EXISTS `question_bank_visible_roles`;
CREATE TABLE `question_bank_visible_roles`  (
                                                `question_bank_id` bigint NOT NULL COMMENT '题库ID',
                                                `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
                                                PRIMARY KEY (`question_bank_id`, `role`) USING BTREE,
                                                INDEX `idx_role`(`role` ASC) USING BTREE,
                                                CONSTRAINT `question_bank_visible_roles_ibfk_1` FOREIGN KEY (`question_bank_id`) REFERENCES `question_banks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库可见角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_bank_visible_roles
-- ----------------------------
INSERT INTO `question_bank_visible_roles` VALUES (3, 'BLAST_USER');
INSERT INTO `question_bank_visible_roles` VALUES (4, 'BLAST_USER');
INSERT INTO `question_bank_visible_roles` VALUES (118, 'BLAST_USER');
INSERT INTO `question_bank_visible_roles` VALUES (1, 'EXPLOSIVE_USER');
INSERT INTO `question_bank_visible_roles` VALUES (2, 'EXPLOSIVE_USER');
INSERT INTO `question_bank_visible_roles` VALUES (4, 'EXPLOSIVE_USER');

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
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '题库表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_banks
-- ----------------------------
INSERT INTO `question_banks` VALUES (1, '易制爆化学品基础知识题库', '包含易制爆化学品基础知识的选择题和判断题，共100题。', 100.00, 1, '2025-06-21 10:51:53', '2025-06-30 22:02:41');
INSERT INTO `question_banks` VALUES (2, '易制爆化学品安全管理题库', '易制爆化学品安全管理相关的题目，包含案例分析题。', 150.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53');
INSERT INTO `question_banks` VALUES (3, '爆破作业安全技术题库', '爆破作业安全技术相关的题目，包含计算题和案例分析。', 200.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53');
INSERT INTO `question_banks` VALUES (4, '法律法规综合题库', '相关法律法规的综合测试题库。', 80.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53');
INSERT INTO `question_banks` VALUES (118, 'test1', 'test', 0.00, 1, '2025-08-02 20:52:23', '2025-08-02 21:05:37');

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
-- Records of question_options
-- ----------------------------
INSERT INTO `question_options` VALUES (5, 3, 'A', '氧化性', 0);
INSERT INTO `question_options` VALUES (6, 3, 'B', '氧化纳', 1);
INSERT INTO `question_options` VALUES (7, 3, 'C', 'Test', 2);
INSERT INTO `question_options` VALUES (8, 5, 'A', '灭火器', 0);
INSERT INTO `question_options` VALUES (9, 5, 'B', '通风设备', 1);
INSERT INTO `question_options` VALUES (10, 5, 'C', '防爆电器', 2);
INSERT INTO `question_options` VALUES (11, 5, 'D', '监控设备', 3);
INSERT INTO `question_options` VALUES (12, 8, 'A', '设计', 0);
INSERT INTO `question_options` VALUES (13, 8, 'B', '审批', 1);
INSERT INTO `question_options` VALUES (14, 8, 'C', '施工', 2);
INSERT INTO `question_options` VALUES (15, 8, 'D', '检查', 3);
INSERT INTO `question_options` VALUES (16, 10, 'A', '危险化学品安全管理条例', 0);
INSERT INTO `question_options` VALUES (17, 10, 'B', '民用爆炸物品安全管理条例', 1);
INSERT INTO `question_options` VALUES (18, 10, 'C', '安全生产法', 2);
INSERT INTO `question_options` VALUES (19, 10, 'D', '环境保护法', 3);
INSERT INTO `question_options` VALUES (20, 11, 'A', '爆破作业单位许可证', 0);
INSERT INTO `question_options` VALUES (21, 11, 'B', '爆破作业人员许可证', 1);
INSERT INTO `question_options` VALUES (22, 11, 'C', '环境影响评价批复', 2);
INSERT INTO `question_options` VALUES (23, 11, 'D', '安全生产许可证', 3);
INSERT INTO `question_options` VALUES (24, 380, 'A', '234', 0);
INSERT INTO `question_options` VALUES (25, 380, 'B', '2345', 1);
INSERT INTO `question_options` VALUES (121, 1, 'A', '111', 0);
INSERT INTO `question_options` VALUES (122, 1, 'B', '222', 1);
INSERT INTO `question_options` VALUES (125, 382, 'A', '正确', 0);
INSERT INTO `question_options` VALUES (126, 382, 'B', '错误', 1);
INSERT INTO `question_options` VALUES (129, 383, 'A', 'e', 0);
INSERT INTO `question_options` VALUES (130, 383, 'B', 'ttttt', 1);
INSERT INTO `question_options` VALUES (131, 384, 'A', '具有爆炸性的物品', 0);
INSERT INTO `question_options` VALUES (132, 384, 'B', '具有燃烧性的物品', 1);
INSERT INTO `question_options` VALUES (133, 384, 'C', '具有毒性的物品', 2);
INSERT INTO `question_options` VALUES (134, 384, 'D', '具有腐蚀性的物品', 3);
INSERT INTO `question_options` VALUES (135, 385, 'A', '硝酸铵', 0);
INSERT INTO `question_options` VALUES (136, 385, 'B', '氯酸钾', 1);
INSERT INTO `question_options` VALUES (137, 385, 'C', '高锰酸钾', 2);
INSERT INTO `question_options` VALUES (138, 385, 'D', '硫磺', 3);
INSERT INTO `question_options` VALUES (139, 386, 'A', '正确', 0);
INSERT INTO `question_options` VALUES (140, 386, 'B', '错误', 1);

-- ----------------------------
-- Table structure for question_result_answers
-- ----------------------------
DROP TABLE IF EXISTS `question_result_answers`;
CREATE TABLE `question_result_answers`  (
                                            `question_result_id` bigint NOT NULL,
                                            `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                            INDEX `FKlgck2a5ja0pfcoo2hqvw95kll`(`question_result_id` ASC) USING BTREE,
                                            CONSTRAINT `FKlgck2a5ja0pfcoo2hqvw95kll` FOREIGN KEY (`question_result_id`) REFERENCES `question_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_result_answers
-- ----------------------------

-- ----------------------------
-- Table structure for question_result_correct_answers
-- ----------------------------
DROP TABLE IF EXISTS `question_result_correct_answers`;
CREATE TABLE `question_result_correct_answers`  (
                                                    `question_result_id` bigint NOT NULL,
                                                    `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                                    INDEX `FKn3xwnluf13wqnjhlsjwv9x96q`(`question_result_id` ASC) USING BTREE,
                                                    CONSTRAINT `FKn3xwnluf13wqnjhlsjwv9x96q` FOREIGN KEY (`question_result_id`) REFERENCES `question_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_result_correct_answers
-- ----------------------------

-- ----------------------------
-- Table structure for question_result_user_answers
-- ----------------------------
DROP TABLE IF EXISTS `question_result_user_answers`;
CREATE TABLE `question_result_user_answers`  (
                                                 `question_result_id` bigint NOT NULL,
                                                 `user_answer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                                 INDEX `FK1vbf3kg73t0vnte7sp8o2klq7`(`question_result_id` ASC) USING BTREE,
                                                 CONSTRAINT `FK1vbf3kg73t0vnte7sp8o2klq7` FOREIGN KEY (`question_result_id`) REFERENCES `question_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_result_user_answers
-- ----------------------------

-- ----------------------------
-- Table structure for question_results
-- ----------------------------
DROP TABLE IF EXISTS `question_results`;
CREATE TABLE `question_results`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `correct` bit(1) NOT NULL,
                                     `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                     `exam_result_id` bigint NOT NULL,
                                     `question_id` bigint NOT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `FKdfnvqtu823qpi32575hx2g6dv`(`exam_result_id` ASC) USING BTREE,
                                     INDEX `FKklak3v8y8oniosgx9cj8yqq2`(`question_id` ASC) USING BTREE,
                                     CONSTRAINT `FKdfnvqtu823qpi32575hx2g6dv` FOREIGN KEY (`exam_result_id`) REFERENCES `exam_results` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                     CONSTRAINT `FKklak3v8y8oniosgx9cj8yqq2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of question_results
-- ----------------------------

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
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (1, 1, 'fasdfasdfadsfa', 'SINGLE_CHOICE', '硝酸铵是典型的易制爆化学品，广泛用于制造炸药fadsfadsf。', 'A');
INSERT INTO `questions` VALUES (2, 1, '易制爆化学品的储存温度一般不超过多少度？', 'SHORT_ANSWER', '易制爆化学品对温度敏感，一般储存温度不超过30℃。', '30');
INSERT INTO `questions` VALUES (3, 1, '易制爆化学品的主要危险特性包括哪些？', 'MULTIPLE_CHOICE', '易制爆化学品具有爆炸性、氧化性、毒性等多种危险特性。', 'B');
INSERT INTO `questions` VALUES (4, 1, '简述易制爆化学品的基本分类。', 'SHORT_ANSWER', '易制爆化学品按用途可分为氧化剂、还原剂、起爆药等类别。', '222');
INSERT INTO `questions` VALUES (5, 2, '易制爆化学品储存库房应配备哪些安全设施？', 'MULTIPLE_CHOICE', '储存库房需要配备防火、防爆、通风等多种安全设施。', 'A,B');
INSERT INTO `questions` VALUES (6, 2, '发生易制爆化学品泄漏事故时，应如何处置？', 'SHORT_ANSWER', '应立即疏散人员，切断火源，使用专用吸附材料处理。', 'test');
INSERT INTO `questions` VALUES (7, 2, '易制爆化学品的安全管理制度包括哪些内容？', 'SHORT_ANSWER', '包括采购、储存、使用、运输、废弃等全流程管理制度。', '111');
INSERT INTO `questions` VALUES (8, 3, '爆破作业的基本流程包括哪些步骤？', 'MULTIPLE_CHOICE', '包括设计、审批、施工、检查、起爆等步骤。', 'B');
INSERT INTO `questions` VALUES (9, 3, '计算题：某爆破工程需要炸药500kg，安全距离应如何计算？', 'SHORT_ANSWER', '根据炸药量和爆破方式，按相关公式计算安全距离。', '111');
INSERT INTO `questions` VALUES (10, 3, '爆破作业现场的安全管理要点有哪些？', 'SINGLE_CHOICE', '包括人员管理、设备检查、环境监测、应急预案等。', 'B');
INSERT INTO `questions` VALUES (11, 4, '《危险化学品安全管理条例》规定，易制爆化学品的管理适用哪些规定？', 'SINGLE_CHOICE', '易制爆化学品的管理适用《危险化学品安全管理条例》的相关规定。', 'B');
INSERT INTO `questions` VALUES (380, 1, '324', 'SINGLE_CHOICE', '234', 'B');
INSERT INTO `questions` VALUES (382, 1, 'tttt', 'TRUE_FALSE', '', 'A');
INSERT INTO `questions` VALUES (383, 118, 'test', 'SINGLE_CHOICE', 'ttt', 'A');
INSERT INTO `questions` VALUES (384, 1, '易制爆物品是指哪些物品？', 'SINGLE_CHOICE', '易制爆物品是指具有爆炸性的物品，包括炸药、雷管等。', 'A');
INSERT INTO `questions` VALUES (385, 1, '以下哪些属于易制爆物品？', 'MULTIPLE_CHOICE', '这些都是常见的易制爆物品。', 'A,B,C,D');
INSERT INTO `questions` VALUES (386, 1, '易制爆物品可以随意购买和使用。', 'TRUE_FALSE', '易制爆物品需要特殊许可才能购买和使用。', 'B');
INSERT INTO `questions` VALUES (387, 1, '易制爆物品的储存要求是什么？', 'FILL_BLANK', '易制爆物品具有爆炸性，必须严格管理。', '必须储存在专用仓库中，远离火源和热源');
INSERT INTO `questions` VALUES (388, 1, '简述易制爆物品的安全管理措施。', 'SHORT_ANSWER', '易制爆物品管理需要多方面的安全措施。', '1. 建立完善的管理制度；2. 指定专人负责；3. 定期检查；4. 建立应急预案');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培训证明表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of training_certificates
-- ----------------------------
INSERT INTO `training_certificates` VALUES (1, 4, 2, 'CERT-20250802-EX-2-4', '2025-08-02 23:19:40', '2025-08-02 23:18:48', 1, 'EXPLOSIVE_USER', '2025-08-02 23:19:40', '2025-08-02 23:19:40');

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
) ENGINE = InnoDB AUTO_INCREMENT = 250 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户课程关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_courses
-- ----------------------------
INSERT INTO `user_courses` VALUES (1, 4, 1, '2025-06-21 10:51:53', 0, NULL, 30);
INSERT INTO `user_courses` VALUES (2, 4, 2, '2025-06-21 10:51:53', 1, '2025-08-02 23:18:48', 100);
INSERT INTO `user_courses` VALUES (3, 5, 1, '2025-06-21 10:51:53', 1, '2025-08-02 23:35:58', 100);
INSERT INTO `user_courses` VALUES (4, 5, 3, '2025-06-21 10:51:53', 0, NULL, 60);
INSERT INTO `user_courses` VALUES (5, 6, 2, '2025-06-21 10:51:53', 0, NULL, 15);
INSERT INTO `user_courses` VALUES (7, 8, 5, '2025-06-21 10:51:53', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (8, 9, 6, '2025-06-21 10:51:53', 0, NULL, 20);
INSERT INTO `user_courses` VALUES (139, 164, 3, '2025-06-21 17:19:29', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (164, 164, 5, '2025-06-21 17:37:03', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (222, 4, 5, '2025-06-22 14:08:44', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (231, 4, 8, '2025-06-22 15:03:24', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (232, 4, 4, '2025-06-22 15:03:37', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (244, 7, 4, '2025-06-30 22:02:13', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (245, 7, 5, '2025-06-30 22:02:13', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (246, 7, 6, '2025-06-30 22:02:13', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (247, 7, 3, '2025-07-01 13:30:27', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (248, 7, 7, '2025-07-01 13:30:27', 0, NULL, 0);
INSERT INTO `user_courses` VALUES (249, 164, 4, '2025-08-02 20:36:12', 0, NULL, 0);

-- ----------------------------
-- Table structure for user_exam_papers
-- ----------------------------
DROP TABLE IF EXISTS `user_exam_papers`;
CREATE TABLE `user_exam_papers`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
                                     `user_id` bigint NOT NULL COMMENT '用户ID',
                                     `exam_paper_id` bigint NOT NULL COMMENT '试卷ID',
                                     `purchase_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `unique_user_exam_paper`(`user_id` ASC, `exam_paper_id` ASC) USING BTREE,
                                     INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
                                     INDEX `idx_exam_paper_id`(`exam_paper_id` ASC) USING BTREE,
                                     CONSTRAINT `user_exam_papers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
                                     CONSTRAINT `user_exam_papers_ibfk_2` FOREIGN KEY (`exam_paper_id`) REFERENCES `exam_papers` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户试卷关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_exam_papers
-- ----------------------------
INSERT INTO `user_exam_papers` VALUES (3, 164, 1, '2025-08-02 17:43:33');
INSERT INTO `user_exam_papers` VALUES (4, 164, 19, '2025-08-02 21:16:19');

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
-- Records of user_question_banks
-- ----------------------------
INSERT INTO `user_question_banks` VALUES (1, 4, 1, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (2, 4, 2, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (3, 5, 1, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (4, 6, 2, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (5, 7, 3, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (6, 8, 3, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (7, 9, 4, '2025-06-21 10:51:53');
INSERT INTO `user_question_banks` VALUES (141, 164, 1, '2025-06-21 17:33:30');
INSERT INTO `user_question_banks` VALUES (142, 164, 2, '2025-06-21 17:33:30');
INSERT INTO `user_question_banks` VALUES (200, 4, 4, '2025-06-22 15:04:52');
INSERT INTO `user_question_banks` VALUES (201, 164, 4, '2025-06-29 09:56:17');
INSERT INTO `user_question_banks` VALUES (202, 164, 118, '2025-08-02 20:55:11');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 257 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', '男', '110101199001011234', '13800138001', '系统管理部', '系统管理', '管理', '/uploads/faces/admin.jpg', 999999.00, 'SUPER_ADMIN', '2025-06-21 10:51:53', '2025-06-21 11:58:38');
INSERT INTO `users` VALUES (2, 'manager1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '张经理', '男', '110101199002021234', '13800138002', '培训管理部', '培训管理', '管理', '/uploads/faces/manager1.jpg', 50000.00, 'ADMIN', '2025-06-21 10:51:53', '2025-06-21 11:58:40');
INSERT INTO `users` VALUES (3, 'manager2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '李经理', '女', '110101199003031234', '13800138003', '培训管理部', '培训管理', '管理', '/uploads/faces/manager2.jpg', 50000.00, 'ADMIN', '2025-06-21 10:51:53', '2025-06-21 11:58:42');
INSERT INTO `users` VALUES (4, 'user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '王小明', '男', '110101199101011234', '13800138004', '化工厂A', '易制爆', '生产', '/uploads/faces/user1.jpg', 3970.00, 'EXPLOSIVE_USER', '2025-06-21 10:51:53', '2025-06-22 15:04:52');
INSERT INTO `users` VALUES (5, 'user2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '刘小红', '女', '110101199202021234', '13800138005', '化工厂B', '易制爆', '质检', '/uploads/faces/user2.jpg', 3000.00, 'EXPLOSIVE_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:46');
INSERT INTO `users` VALUES (6, 'user3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '陈大力', '男', '110101199303031234', '13800138006', '化工厂C', '易制爆', '仓储', '/uploads/faces/user3.jpg', 4000.00, 'EXPLOSIVE_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:48');
INSERT INTO `users` VALUES (7, '222', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '赵爆破', '男', '110101199404041234', '13800138007', '爆破公司A', '爆破作业', '爆破', '/uploads/faces/blast1.jpg', 2000.00, 'BLAST_USER', '2025-06-21 10:51:53', '2025-06-30 20:01:23');
INSERT INTO `users` VALUES (8, 'blast2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '孙安全', '男', '110101199505051234', '13800138008', '爆破公司B', '爆破作业', '安全', '/uploads/faces/blast2.jpg', 2500.00, 'BLAST_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:52');
INSERT INTO `users` VALUES (9, 'blast3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '周技术', '男', '110101199606061234', '13800138009', '爆破公司C', '爆破作业', '技术', '/uploads/faces/blast3.jpg', 3000.00, 'BLAST_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:55');
INSERT INTO `users` VALUES (164, '111', '$2a$10$sKcgeBqiEfE3uWkPsfGig.ZF.YxVPa5AO/vrX2zkWnp5FvlxFmpRy', '111', '男', '411422199903140919', '13940241359', '111', '易制爆', '爆破', 'uploads/faces/c6ea1ee3-c781-4120-8958-80bfc65f7663_证件照.jpg', 997970.00, 'EXPLOSIVE_USER', '2025-06-21 17:18:28', '2025-08-02 20:36:12');

SET FOREIGN_KEY_CHECKS = 1;
