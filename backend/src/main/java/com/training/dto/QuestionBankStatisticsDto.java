package com.training.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionBankStatisticsDto {
    private Long questionBankId;
    private String questionBankTitle;
    private Integer totalQuestions;
    private Integer totalUsers;
    private Integer completedUsers;
    private Double averageScore;
    private Double completionRate;
    private List<ScoreDistributionDto> scoreDistribution;
    private List<QuestionStatisticsDto> questionStatistics;
    
    @Data
    public static class ScoreDistributionDto {
        private String range; // 0-60, 60-70, 70-80, 80-90, 90-100
        private Integer count;
        private Double percentage;
    }
    
    @Data
    public static class QuestionStatisticsDto {
        private Long questionId;
        private String content;
        private String type;
        private Integer totalAttempts;
        private Integer correctAttempts;
        private Double correctRate;
    }
} 