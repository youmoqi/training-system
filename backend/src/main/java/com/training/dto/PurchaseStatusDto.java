package com.training.dto;

import lombok.Data;

import java.util.List;

/**
 * @author YIZ
 */
@Data
public class PurchaseStatusDto {
    private boolean hasPurchased;
    private List<ExamResultDto> examResults;
}
