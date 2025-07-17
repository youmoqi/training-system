package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionBankDto {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Boolean isOnline;
    private List<String> visibleRoles;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}