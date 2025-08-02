package com.training.entity;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * @author YIZ
 */
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
}
