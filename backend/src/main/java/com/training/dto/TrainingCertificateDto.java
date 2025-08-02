package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrainingCertificateDto {
    private Long id;
    private Long userId;
    private String username;
    private String realName;
    private String userRole;
    private Long courseId;
    private String courseTitle;
    private String certificateNumber;
    private LocalDateTime issueDate;
    private LocalDateTime completeDate;
    private Boolean isPaid;
    private String certificateType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 