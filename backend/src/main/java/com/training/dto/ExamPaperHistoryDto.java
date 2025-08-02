package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamPaperHistoryDto {
    private Long id;
    private Long userId;
    private Long examPaperId;
    private Long examPaperResultId;
    private Integer attemptNumber;
    private Integer score;
    private Integer totalScore;
    private Integer timeTaken;
    private LocalDateTime examTime;
    private Boolean isPassed;
    private LocalDateTime createTime;
    
    // 关联信息
    private String examPaperTitle;
    private String userName;
} 