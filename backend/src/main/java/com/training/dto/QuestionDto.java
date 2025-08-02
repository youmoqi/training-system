package com.training.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDto {
    private Long id;
    private Long questionBankId;
    private String questionBankTitle;
    private String content;
    private String type;
    private String explanation;
    private List<String> options;
    private List<String> answers;
}
