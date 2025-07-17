-- MySQL数据库迁移脚本
-- 用于将H2数据库迁移到MySQL数据库

-- 创建数据库
CREATE DATABASE IF NOT EXISTS training_system 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE training_system;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(100) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    phone VARCHAR(11) NOT NULL UNIQUE,
    work_unit VARCHAR(200) NOT NULL,
    training_type VARCHAR(50) NOT NULL,
    job_category VARCHAR(50) NOT NULL,
    face_photo_url VARCHAR(500) NOT NULL,
    payment_amount DECIMAL(10,2) NOT NULL,
    role VARCHAR(20) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_id_card (id_card),
    INDEX idx_phone (phone),
    INDEX idx_role (role)
);

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(500),
    video_url VARCHAR(500) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    is_online BOOLEAN NOT NULL DEFAULT TRUE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_is_online (is_online)
);

-- 创建课程可见角色表
CREATE TABLE IF NOT EXISTS course_visible_roles (
    course_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (course_id, role),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_role (role)
);

-- 创建题库表
CREATE TABLE IF NOT EXISTS question_banks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    is_online BOOLEAN NOT NULL DEFAULT TRUE,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_is_online (is_online)
);

-- 创建题库可见角色表
CREATE TABLE IF NOT EXISTS question_bank_visible_roles (
    question_bank_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (question_bank_id, role),
    FOREIGN KEY (question_bank_id) REFERENCES question_banks(id) ON DELETE CASCADE,
    INDEX idx_role (role)
);

-- 创建题目表
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_bank_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    explanation TEXT,
    FOREIGN KEY (question_bank_id) REFERENCES question_banks(id) ON DELETE CASCADE,
    INDEX idx_question_bank_id (question_bank_id),
    INDEX idx_type (type)
);

-- 创建题目选项表
CREATE TABLE IF NOT EXISTS question_options (
    question_id BIGINT NOT NULL,
    option_content VARCHAR(500) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id)
);

-- 创建题目答案表
CREATE TABLE IF NOT EXISTS question_answers (
    question_id BIGINT NOT NULL,
    answer VARCHAR(500) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id)
);

-- 创建邀请链接表
CREATE TABLE IF NOT EXISTS invitation_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link_code VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    expire_time TIMESTAMP NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_link_code (link_code),
    INDEX idx_is_active (is_active),
    INDEX idx_expire_time (expire_time)
);

-- 创建邀请链接课程关联表
CREATE TABLE IF NOT EXISTS invitation_courses (
    invitation_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (invitation_id, course_id),
    FOREIGN KEY (invitation_id) REFERENCES invitation_links(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 创建用户课程关联表
CREATE TABLE IF NOT EXISTS user_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enroll_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    complete_time TIMESTAMP NULL,
    watch_progress INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_course (user_id, course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_is_completed (is_completed)
);

-- 创建用户题库关联表
CREATE TABLE IF NOT EXISTS user_question_banks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_bank_id BIGINT NOT NULL,
    purchase_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    complete_time TIMESTAMP NULL,
    score INT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_bank_id) REFERENCES question_banks(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_question_bank (user_id, question_bank_id),
    INDEX idx_user_id (user_id),
    INDEX idx_question_bank_id (question_bank_id),
    INDEX idx_is_completed (is_completed)
);

-- 创建MySQL用户（可选）
-- CREATE USER 'training_user'@'localhost' IDENTIFIED BY 'training_password';
-- GRANT ALL PRIVILEGES ON training_system.* TO 'training_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 显示创建的表
SHOW TABLES;

-- 显示表结构
DESCRIBE users;
DESCRIBE courses;
DESCRIBE question_banks;
DESCRIBE questions;
DESCRIBE invitation_links;
DESCRIBE user_courses;
DESCRIBE user_question_banks; 