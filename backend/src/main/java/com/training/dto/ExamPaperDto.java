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
    private String examCategory;
    private Boolean allowRetake;
    private Integer maxAttempts;
    private Integer singleChoiceScore;
    private Integer multipleChoiceScore;
    private Integer trueFalseScore;
    private Integer fillBlankScore;
    private Integer shortAnswerScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> visibleCategoryIds;
    private List<ExamPaperQuestionDto> questions;
    private List<ExamPaperAutoRuleDto> autoRules;
} 
