package com.training.dto;

import lombok.Data;

import java.util.List;

/**
 * @author YIZ
 */
@Data
public class ExamSubmitDto {
    private Long examId;
    private List<QuestionAnswerDto> answers;
    private Integer timeTaken;
}
