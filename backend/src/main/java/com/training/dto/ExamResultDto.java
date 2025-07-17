package com.training.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamResultDto {
    private Long questionBankId;
    private String questionBankTitle;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private List<QuestionResultDto> questionResults;
    
    @Data
    public static class QuestionResultDto {
        private Long questionId;
        private String content;
        private String type;
        private List<String> userAnswers;
        private List<String> correctAnswers;
        private Boolean isCorrect;
        private String explanation;
    }
} 