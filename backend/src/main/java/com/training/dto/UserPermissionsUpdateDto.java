package com.training.dto;

import lombok.Data;

@Data
public class UserPermissionsUpdateDto {
    private Long visibilityCategoryId;
    private Boolean canLearn;
    private Boolean canExam;
    private Long jobCategoryId;
}
