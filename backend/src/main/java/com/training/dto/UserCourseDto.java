package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCourseDto {
    private Long userId;
    private String username;
    private String realName;
    private Long courseId;
    private String courseTitle;
    private Boolean isCompleted;
    private LocalDateTime completedTime;
}
