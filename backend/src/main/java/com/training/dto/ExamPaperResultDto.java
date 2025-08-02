package com.training.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author YIZ
 */
@Data
public class ExamPaperResultDto {
    private Long id;
    private Long examPaperId;
    private String examPaperTitle;
    private Long userId;
    private String userName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Integer totalScore;
    private Integer timeTaken; // 考试用时（秒）
    private Boolean isPassed;
    private LocalDateTime submitTime;
    private LocalDateTime examTime;
    private Integer attemptNumber; // 考试次数
    private List<ExamPaperQuestionResultDto> questionResults;
}
