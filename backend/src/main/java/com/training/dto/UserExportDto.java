package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据导出DTO
 * @author YIZ
 */
@Data
public class UserExportDto {
    private Long id;
    private String username;
    private String realName;
    private String gender;
    private String idCard;
    private String phone;
    private String workUnit;
    private String trainingType;
    private String jobCategory;
    private String role;
    private LocalDateTime createTime;

    // 学习记录
    private int totalCourses;
    private int completedCourses;
    private int averageProgress;

    // 考试成绩
    private int totalExams;
    private int passedExams;
    private int averageScore;
}
