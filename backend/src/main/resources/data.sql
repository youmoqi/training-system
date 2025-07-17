-- Demo users
-- Passwords for all users are '123456'
-- BCrypt hash: $2a$10$N.xNBSf/aA9gFtS9f1y./Oa1k3C1O2yZG59XpGZ08b1oA4IqOI/rq
INSERT INTO users (id, username, password, real_name, gender, id_card, phone, work_unit, training_type, job_category, face_photo_url, payment_amount, role, create_time, update_time) VALUES
(1, 'admin', '$2a$10$N.xNBSf/aA9gFtS9f1y./Oa1k3C1O2yZG59XpGZ08b1oA4IqOI/rq', '超级管理员', '男', '11010119900307881X', '18800000000', '总公司', '管理', '管理', './uploads/faces/default.jpg', 9999.00, 'SUPER_ADMIN', NOW(), NOW()),
(2, 'manager1', '$2a$10$N.xNBSf/aA9gFtS9f1y./Oa1k3C1O2yZG59XpGZ08b1oA4IqOI/rq', '管理员', '男', '11010119900307882X', '18800000001', '分公司', '管理', '管理', './uploads/faces/default.jpg', 9999.00, 'ADMIN', NOW(), NOW()),
(3, 'user1', '$2a$10$N.xNBSf/aA9gFtS9f1y./Oa1k3C1O2yZG59XpGZ08b1oA4IqOI/rq', '李四', '女', '330101199202022345', '18800000002', '二分队', '易制爆', '操作员', './uploads/faces/default.jpg', 500.00, 'EXPLOSIVE_USER', NOW(), NOW()),
(4, 'blast1', '$2a$10$N.xNBSf/aA9gFtS9f1y./Oa1k3C1O2yZG59XpGZ08b1oA4IqOI/rq', '张三', '男', '330101199101011234', '18800000003', '一分队', '爆破作业', '爆破员', './uploads/faces/default.jpg', 0.00, 'BLAST_USER', NOW(), NOW());

-- Demo courses
INSERT INTO courses (id, title, description, cover_image_url, video_url, price, is_online, create_time, update_time) VALUES
(1, '易制爆化学品基础知识', '介绍常见易制爆化学品的性质、危害及管理要求。', './uploads/covers/course1.jpg', './uploads/videos/video1.mp4', 100.00, 1, NOW(), NOW()),
(2, '易制爆化学品安全管理', '讲解易制爆化学品的采购、储存、使用、处置等环节的安全管理规范。', './uploads/covers/course2.jpg', './uploads/videos/video2.mp4', 150.00, 1, NOW(), NOW()),
(3, '易制爆化学品储存与运输', '详细说明易制爆化学品储存仓库的要求、运输过程中的注意事项。', './uploads/covers/course3.jpg', './uploads/videos/video3.mp4', 120.00, 1, NOW(), NOW()),
(4, '爆破作业安全技术', '系统讲授爆破作业中的安全技术和操作规程。', './uploads/covers/course4.jpg', './uploads/videos/video4.mp4', 200.00, 1, NOW(), NOW()),
(5, '爆破器材使用与管理', '介绍各类爆破器材的性能、使用方法及安全管理要求。', './uploads/covers/course5.jpg', './uploads/videos/video5.mp4', 220.00, 1, NOW(), NOW()),
(6, '爆破现场安全管理', '讲解爆破作业现场的组织、警戒、检查等安全管理措施。', './uploads/covers/course6.jpg', './uploads/videos/video6.mp4', 180.00, 1, NOW(), NOW()),
(7, '法律法规与标准规范', '解读与易制爆、爆破作业相关的法律、法规和国家标准。', './uploads/covers/course7.jpg', './uploads/videos/video7.mp4', 80.00, 1, NOW(), NOW()),
(8, '事故案例分析', '通过典型事故案例，分析事故原因，总结经验教训。', './uploads/covers/course8.jpg', './uploads/videos/video8.mp4', 90.00, 0, NOW(), NOW());

-- Demo invitation links
INSERT INTO invitation_links (id, link_code, password, title, description, is_active, expire_time, create_time, update_time) VALUES
(1, 'BLAST2024A', 'blastpwd123', '爆破作业人员第一期培训', '包含爆破作业安全技术和爆破器材使用与管理课程', 1, '2025-01-01 00:00:00', NOW(), NOW()),
(2, 'BLAST2024B', 'blastpwd456', '爆破作业人员第二期培训', '包含爆破现场安全管理和法律法规与标准规范课程', 1, '2025-06-01 00:00:00', NOW(), NOW());

-- Demo invitation_courses mapping
INSERT INTO invitation_courses (invitation_id, course_id) VALUES
(1, 4),
(1, 5),
(2, 6),
(2, 7); 