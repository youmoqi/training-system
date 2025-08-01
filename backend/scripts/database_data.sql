-- course_visible_roles 表数据
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

-- courses 表数据
INSERT INTO `courses` VALUES (1, '易制爆化学品基础知识', '本课程介绍易制爆化学品的基本概念、分类、特性等基础知识，为后续深入学习打下基础。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 200.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:14', NULL);
INSERT INTO `courses` VALUES (2, '易制爆化学品安全管理', '详细介绍易制爆化学品的安全管理制度、操作规程、应急处置等内容。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 300.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:16', NULL);
INSERT INTO `courses` VALUES (3, '易制爆化学品储存与运输', '学习易制爆化学品的储存条件、运输要求、包装规范等专业知识。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 250.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:18', NULL);
INSERT INTO `courses` VALUES (4, '爆破作业安全技术', '爆破作业的基本原理、安全技术要求、操作规程等内容。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 400.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:20', NULL);
INSERT INTO `courses` VALUES (5, '爆破器材使用与管理', '爆破器材的种类、使用方法、管理制度等专业知识。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 350.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:23', NULL);
INSERT INTO `courses` VALUES (6, '爆破现场安全管理', '爆破现场的安全管理、人员防护、应急预案等内容。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 300.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:25', NULL);
INSERT INTO `courses` VALUES (7, '法律法规与标准规范', '相关法律法规、标准规范的解读和应用。', 'https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4', 150.00, 1, '2025-06-21 10:51:53', '2025-06-22 14:16:28', NULL);
INSERT INTO `courses` VALUES (8, '事故案例分析', '典型事故案例的分析和教训总结。', 'https://example.com/videos/accident-cases.mp4', 200.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53', NULL);

-- invitation_courses 表数据
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

-- invitation_links 表数据
INSERT INTO `invitation_links` VALUES (1, 'BLAST2024A', 'blastpwd123', '爆破作业人员第一期培训', '包含爆破作业安全技术和爆破器材使用与管理课程', 1, '2025-01-01 00:00:00', '2025-06-30 21:01:15', '2025-06-30 21:01:15');
INSERT INTO `invitation_links` VALUES (2, 'BLAST2024B', 'blastpwd456', '爆破作业人员第二期培训', '包含爆破现场安全管理和法律法规与标准规范课程', 1, '2025-06-01 00:00:00', '2025-06-30 21:01:15', '2025-06-30 21:01:15');
INSERT INTO `invitation_links` VALUES (85, 'INV1751289272316A16767', '222222', '22', '22', 1, '2025-10-17 16:00:00', '2025-06-30 21:14:32', '2025-06-30 21:14:32');
INSERT INTO `invitation_links` VALUES (87, 'INV-1B5C25A4', '333333', '333', '33', 1, '2025-08-08 16:00:00', '2025-06-30 21:42:18', '2025-06-30 21:42:18');
INSERT INTO `invitation_links` VALUES (88, 'INV-9C1A024F', '111111', 'tret', 'wret', 1, '2025-09-11 16:00:00', '2025-07-01 13:29:57', '2025-07-01 13:29:57');

-- question_answers 表数据
INSERT INTO `question_answers` VALUES (2, '30');
INSERT INTO `question_answers` VALUES (3, 'B. 氧化性');
INSERT INTO `question_answers` VALUES (3, 'A. 氧化性');
INSERT INTO `question_answers` VALUES (5, 'B. 通风设备');
INSERT INTO `question_answers` VALUES (8, 'B. 审批');
INSERT INTO `question_answers` VALUES (10, 'B. 民用爆炸物品安全管理条例');
INSERT INTO `question_answers` VALUES (11, 'B. 爆破作业人员许可证');
INSERT INTO `question_answers` VALUES (1, 'B. 硝酸铵fasd');
INSERT INTO `question_answers` VALUES (4, '22222');
INSERT INTO `question_answers` VALUES (380, '234');

-- question_bank_visible_roles 表数据
INSERT INTO `question_bank_visible_roles` VALUES (3, 'BLAST_USER');
INSERT INTO `question_bank_visible_roles` VALUES (4, 'BLAST_USER');
INSERT INTO `question_bank_visible_roles` VALUES (1, 'EXPLOSIVE_USER');
INSERT INTO `question_bank_visible_roles` VALUES (2, 'EXPLOSIVE_USER');
INSERT INTO `question_bank_visible_roles` VALUES (4, 'EXPLOSIVE_USER');

-- question_banks 表数据
INSERT INTO `question_banks` VALUES (1, '易制爆化学品基础知识题库', '包含易制爆化学品基础知识的选择题和判断题，共100题。', 100.00, 1, '2025-06-21 10:51:53', '2025-06-30 22:02:41');
INSERT INTO `question_banks` VALUES (2, '易制爆化学品安全管理题库', '易制爆化学品安全管理相关的题目，包含案例分析题。', 150.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53');
INSERT INTO `question_banks` VALUES (3, '爆破作业安全技术题库', '爆破作业安全技术相关的题目，包含计算题和案例分析。', 200.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53');
INSERT INTO `question_banks` VALUES (4, '法律法规综合题库', '相关法律法规的综合测试题库。', 80.00, 1, '2025-06-21 10:51:53', '2025-06-21 10:51:53');

-- question_options 表数据
INSERT INTO `question_options` VALUES (3, 'A. 氧化性');
INSERT INTO `question_options` VALUES (3, 'B.氧化纳');
INSERT INTO `question_options` VALUES (3, 'C.Test');
INSERT INTO `question_options` VALUES (5, 'A. 灭火器');
INSERT INTO `question_options` VALUES (5, 'B. 通风设备');
INSERT INTO `question_options` VALUES (5, 'C. 防爆电器');
INSERT INTO `question_options` VALUES (5, 'D. 监控设备');
INSERT INTO `question_options` VALUES (8, 'A. 设计');
INSERT INTO `question_options` VALUES (8, 'B. 审批');
INSERT INTO `question_options` VALUES (8, 'C. 施工');
INSERT INTO `question_options` VALUES (8, 'D. 检查');
INSERT INTO `question_options` VALUES (10, 'A. 危险化学品安全管理条例');
INSERT INTO `question_options` VALUES (10, 'B. 民用爆炸物品安全管理条例');
INSERT INTO `question_options` VALUES (10, 'C. 安全生产法');
INSERT INTO `question_options` VALUES (10, 'D. 环境保护法');
INSERT INTO `question_options` VALUES (11, 'A. 爆破作业单位许可证');
INSERT INTO `question_options` VALUES (11, 'B. 爆破作业人员许可证');
INSERT INTO `question_options` VALUES (11, 'C. 环境影响评价批复');
INSERT INTO `question_options` VALUES (11, 'D. 安全生产许可证');
INSERT INTO `question_options` VALUES (1, 'A. 硫酸');
INSERT INTO `question_options` VALUES (1, 'B. 硝酸铵fasd');
INSERT INTO `question_options` VALUES (1, 'C. 氯化钠');
INSERT INTO `question_options` VALUES (1, 'D. 氢氧化钠fadsf');
INSERT INTO `question_options` VALUES (380, '234');
INSERT INTO `question_options` VALUES (380, '234');

-- questions 表数据
INSERT INTO `questions` VALUES (1, 1, 'fasdfasdfadsfa', 'SINGLE_CHOICE', '硝酸铵是典型的易制爆化学品，广泛用于制造炸药fadsfadsf。');
INSERT INTO `questions` VALUES (2, 1, '易制爆化学品的储存温度一般不超过多少度？', 'SUBJECTIVE', '易制爆化学品对温度敏感，一般储存温度不超过30℃。');
INSERT INTO `questions` VALUES (3, 1, '易制爆化学品的主要危险特性包括哪些？', 'MULTIPLE_CHOICE', '易制爆化学品具有爆炸性、氧化性、毒性等多种危险特性。');
INSERT INTO `questions` VALUES (4, 1, '简述易制爆化学品的基本分类。', 'SUBJECTIVE', '易制爆化学品按用途可分为氧化剂、还原剂、起爆药等类别。');
INSERT INTO `questions` VALUES (5, 2, '易制爆化学品储存库房应配备哪些安全设施？', 'MULTIPLE_CHOICE', '储存库房需要配备防火、防爆、通风等多种安全设施。');
INSERT INTO `questions` VALUES (6, 2, '发生易制爆化学品泄漏事故时，应如何处置？', 'SUBJECTIVE', '应立即疏散人员，切断火源，使用专用吸附材料处理。');
INSERT INTO `questions` VALUES (7, 2, '易制爆化学品的安全管理制度包括哪些内容？', 'SUBJECTIVE', '包括采购、储存、使用、运输、废弃等全流程管理制度。');
INSERT INTO `questions` VALUES (8, 3, '爆破作业的基本流程包括哪些步骤？', 'MULTIPLE_CHOICE', '包括设计、审批、施工、检查、起爆等步骤。');
INSERT INTO `questions` VALUES (9, 3, '计算题：某爆破工程需要炸药500kg，安全距离应如何计算？', 'SUBJECTIVE', '根据炸药量和爆破方式，按相关公式计算安全距离。');
INSERT INTO `questions` VALUES (10, 3, '爆破作业现场的安全管理要点有哪些？', 'SINGLE_CHOICE', '包括人员管理、设备检查、环境监测、应急预案等。');
INSERT INTO `questions` VALUES (11, 4, '《危险化学品安全管理条例》规定，易制爆化学品的管理适用哪些规定？', 'SINGLE_CHOICE', '易制爆化学品的管理适用《危险化学品安全管理条例》的相关规定。');
INSERT INTO `questions` VALUES (380, 1, '324', 'SINGLE_CHOICE', '234');

-- user_courses 表数据
INSERT INTO `user_courses` VALUES (1, 4, 1, '2025-06-21 10:51:53', 0, NULL, 30);
INSERT INTO `user_courses` VALUES (2, 4, 2, '2025-06-21 10:51:53', 1, NULL, 100);
INSERT INTO `user_courses` VALUES (3, 5, 1, '2025-06-21 10:51:53', 1, NULL, 100);
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

-- user_question_banks 表数据
INSERT INTO `user_question_banks` VALUES (1, 4, 1, '2025-06-21 10:51:53', 1, NULL, 85);
INSERT INTO `user_question_banks` VALUES (2, 4, 2, '2025-06-21 10:51:53', 0, NULL, NULL);
INSERT INTO `user_question_banks` VALUES (3, 5, 1, '2025-06-21 10:51:53', 1, NULL, 92);
INSERT INTO `user_question_banks` VALUES (4, 6, 2, '2025-06-21 10:51:53', 0, NULL, NULL);
INSERT INTO `user_question_banks` VALUES (5, 7, 3, '2025-06-21 10:51:53', 1, '2025-06-30 21:53:41', 66);
INSERT INTO `user_question_banks` VALUES (6, 8, 3, '2025-06-21 10:51:53', 0, NULL, NULL);
INSERT INTO `user_question_banks` VALUES (7, 9, 4, '2025-06-21 10:51:53', 1, NULL, 88);
INSERT INTO `user_question_banks` VALUES (141, 164, 1, '2025-06-21 17:33:30', 1, '2025-07-01 13:35:56', 20);
INSERT INTO `user_question_banks` VALUES (142, 164, 2, '2025-06-21 17:33:30', 1, '2025-06-29 11:05:21', 0);
INSERT INTO `user_question_banks` VALUES (200, 4, 4, '2025-06-22 15:04:52', 0, NULL, NULL);
INSERT INTO `user_question_banks` VALUES (201, 164, 4, '2025-06-29 09:56:17', 0, NULL, NULL);

-- users 表数据
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', '男', '110101199001011234', '13800138001', '系统管理部', '系统管理', '管理', '/uploads/faces/admin.jpg', 999999.00, 'SUPER_ADMIN', '2025-06-21 10:51:53', '2025-06-21 11:58:38');
INSERT INTO `users` VALUES (2, 'manager1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '张经理', '男', '110101199002021234', '13800138002', '培训管理部', '培训管理', '管理', '/uploads/faces/manager1.jpg', 50000.00, 'ADMIN', '2025-06-21 10:51:53', '2025-06-21 11:58:40');
INSERT INTO `users` VALUES (3, 'manager2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '李经理', '女', '110101199003031234', '13800138003', '培训管理部', '培训管理', '管理', '/uploads/faces/manager2.jpg', 50000.00, 'ADMIN', '2025-06-21 10:51:53', '2025-06-21 11:58:42');
INSERT INTO `users` VALUES (4, 'user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '王小明', '男', '110101199101011234', '13800138004', '化工厂A', '易制爆', '生产', '/uploads/faces/user1.jpg', 3970.00, 'EXPLOSIVE_USER', '2025-06-21 10:51:53', '2025-06-22 15:04:52');
INSERT INTO `users` VALUES (5, 'user2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '刘小红', '女', '110101199202021234', '13800138005', '化工厂B', '易制爆', '质检', '/uploads/faces/user2.jpg', 3000.00, 'EXPLOSIVE_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:46');
INSERT INTO `users` VALUES (6, 'user3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '陈大力', '男', '110101199303031234', '13800138006', '化工厂C', '易制爆', '仓储', '/uploads/faces/user3.jpg', 4000.00, 'EXPLOSIVE_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:48');
INSERT INTO `users` VALUES (7, '222', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '赵爆破', '男', '110101199404041234', '13800138007', '爆破公司A', '爆破作业', '爆破', '/uploads/faces/blast1.jpg', 2000.00, 'BLAST_USER', '2025-06-21 10:51:53', '2025-06-30 20:01:23');
INSERT INTO `users` VALUES (8, 'blast2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '孙安全', '男', '110101199505051234', '13800138008', '爆破公司B', '爆破作业', '安全', '/uploads/faces/blast2.jpg', 2500.00, 'BLAST_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:52');
INSERT INTO `users` VALUES (9, 'blast3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '周技术', '男', '110101199606061234', '13800138009', '爆破公司C', '爆破作业', '技术', '/uploads/faces/blast3.jpg', 3000.00, 'BLAST_USER', '2025-06-21 10:51:53', '2025-06-21 11:58:55');
INSERT INTO `users` VALUES (164, '111', '$2a$10$sKcgeBqiEfE3uWkPsfGig.ZF.YxVPa5AO/vrX2zkWnp5FvlxFmpRy', '111', '男', '411422199903140919', '13940241359', '111', '易制爆', '爆破', 'uploads/faces/c6ea1ee3-c781-4120-8958-80bfc65f7663_证件照.jpg', 998370.00, 'EXPLOSIVE_USER', '2025-06-21 17:18:28', '2025-06-30 21:45:16');

-- exam_papers 表数据
INSERT INTO `exam_papers` VALUES (1, '易制爆化学品基础知识考试', '易制爆化学品基础知识综合考试，包含选择题、多选题和判断题', 100, 60, 90, 0, 1, 0, '2025-01-07 10:00:00', '2025-01-07 10:00:00');
INSERT INTO `exam_papers` VALUES (2, '爆破作业安全技术考试', '爆破作业安全技术综合考试，包含理论知识和实践操作', 100, 70, 120, 0, 1, 0, '2025-01-07 10:00:00', '2025-01-07 10:00:00');
