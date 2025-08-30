package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YIZ
 */
@Data
public class ExamResultDto {
    private Long id;
    private Long examId;
    private String examTitle;
    private Long userId;
    private String userName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Integer totalScore;
    private Integer timeTaken;
    private Boolean isPassed;
    private LocalDateTime submitTime;
    private LocalDateTime examTime;
    private Integer attemptNumber;
    private List<ExamQuestionResultDto> questionResults;
}
