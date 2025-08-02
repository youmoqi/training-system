package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamPaperAutoRuleDto {
    private Long id;
    private Long examPaperId;
    private String questionType;
    private Integer questionCount;
    private List<Long> questionBankIds;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 