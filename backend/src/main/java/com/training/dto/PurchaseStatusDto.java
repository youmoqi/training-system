package com.training.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseStatusDto {
    private boolean hasPurchased;
    private List<ExamPaperResultDto> examResults;
} 