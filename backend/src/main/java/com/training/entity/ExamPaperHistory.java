package com.training.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exam_paper_history")
public class ExamPaperHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "exam_paper_id", nullable = false)
    private Long examPaperId;

    @Column(name = "exam_paper_result_id", nullable = false)
    private Long examPaperResultId;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "time_taken")
    private Integer timeTaken;

    @Column(name = "exam_time", nullable = false)
    private LocalDateTime examTime;

    @Column(name = "is_passed", nullable = false)
    private Boolean isPassed = false;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", insertable = false, updatable = false)
    private ExamPaper examPaper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_result_id", insertable = false, updatable = false)
    private ExamPaperResult examPaperResult;
}
