package com.training.dto;

import lombok.Data;

/**
 * @author 14798
 */
@Data
public class UserPermissionsUpdateDto {
    private Long roleId;
    private Boolean canLearn;
    private Boolean canExam;
    private Long jobCategoryId;
}
