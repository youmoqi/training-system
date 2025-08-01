package com.training.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamPaperQuestionDto {
    private Long id;
    private Long questionId;
    private String questionContent;
    private String questionType;
    private Integer questionOrder;
    private Integer score;
    private String explanation;
    private List<String> options;
    private List<String> answers;
} 