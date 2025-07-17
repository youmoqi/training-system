package com.training.dto;

import lombok.Data;
import java.util.List;
 
@Data
public class QuestionAnswerDto {
    private Long questionId;
    private List<String> userAnswers;
} 