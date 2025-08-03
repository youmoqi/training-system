package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author YIZ
 */
@Data
public class UserCourseListDto {
    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime enrollTime;
    private Boolean isCompleted;
    private LocalDateTime completeTime;
    private Integer watchProgress;
}
