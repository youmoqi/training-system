package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamPaperResultDto {
    private Long id;
    private Integer score;
    private Integer totalScore;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer timeTaken;
    private LocalDateTime examTime;
    private Boolean isPassed;
} 