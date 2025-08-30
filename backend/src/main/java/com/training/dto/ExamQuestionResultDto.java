package com.training.dto;

import lombok.Data;

import java.util.List;

/**
 * @author YIZ
 */
@Data
public class ExamQuestionResultDto {
    private Long id;
    private Long questionId;
    private String questionContent;
    private String questionType;
    private List<String> options;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private Integer score;
    private Integer maxScore;
    private String explanation;
}
