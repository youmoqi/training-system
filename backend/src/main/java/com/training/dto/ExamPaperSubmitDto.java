package com.training.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExamPaperSubmitDto {
    private Long examPaperId;
    private List<QuestionAnswerDto> answers;
    private Integer timeTaken; // 用时（秒）
} 