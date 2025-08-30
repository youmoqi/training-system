package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 14798
 */
@Data
public class ExamDto {
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
    private List<Long> visibleRoleIds;
    private List<ExamQuestionDto> questions;
    private List<ExamAutoRuleDto> autoRules;
}
