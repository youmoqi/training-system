package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamPaperDto {
    private Long id;
    private String title;
    private String description;
    private Integer totalScore;
    private Integer passScore;
    private Integer duration;
    private Integer totalQuestions;
    private Boolean isOnline;
    private Boolean isRandom;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> visibleRoles;
    private List<ExamPaperQuestionDto> questions;
} 