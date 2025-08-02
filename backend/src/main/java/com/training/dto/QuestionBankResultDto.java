package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionBankResultDto {
    private Long id;
    private Long questionBankId;
    private String questionBankTitle;
    private Long userId;
    private String userName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Integer totalScore;
    private Integer timeTaken;
    private Boolean isPassed;
    private LocalDateTime submitTime;
    private LocalDateTime createTime;
    private Integer attemptNumber; // 练习次数
    private List<QuestionResult> questionResults;
    
    @Data
    public static class QuestionResult {
        private Long questionId;
        private String questionContent;
        private String questionType;
        private String userAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Integer score;
        private Integer maxScore;
        private String explanation;
        private List<String> options; // 题目选项
    }
} 