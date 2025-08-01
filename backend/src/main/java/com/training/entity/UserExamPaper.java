package com.training.entity;

import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_exam_papers")
public class UserExamPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    private ExamPaper examPaper;

    @CreationTimestamp
    @Column(name = "purchase_time", nullable = false, updatable = false)
    private LocalDateTime purchaseTime;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "score")
    private Integer score;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;
} 
