package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YIZ
 */
@Data
public class ExamAutoRuleDto {
    private Long id;
    private Long examId;
    private String questionType;
    private Integer questionCount;
    private List<Long> questionBankIds;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
