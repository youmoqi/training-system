package com.training.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YIZ
 */
@Data
public class ExamCategoryDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String parentCode;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<ExamCategoryDto> subCategories;
}
