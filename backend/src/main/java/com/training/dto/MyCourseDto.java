package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author YIZ
 */
@Data
public class MyCourseDto {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String coverImageUrl;
    private String videoUrl;
    private Double price;
    private Boolean isOnline;
    private LocalDateTime enrollTime;
    private Boolean isCompleted;
    private LocalDateTime completeTime;
    private Integer watchProgress;
}
