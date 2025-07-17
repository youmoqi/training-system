package com.training.dto;

import lombok.Data;
import java.util.List;
 
@Data
public class QuestionImportDto {
    private Long questionBankId;
    private List<QuestionDto> questions;
} 