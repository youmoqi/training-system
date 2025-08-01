package com.training.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamPaperQuestionResultDto {
    private Long id;
    private Long questionId;
    private String questionContent;
    private String questionType;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private Integer score;
    private Integer maxScore;
    private String explanation;
    private List<String> options;
} 