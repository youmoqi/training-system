package com.training.entity;

import javax.persistence.*;

import com.training.entity.Exam.Exam;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * @author YIZ
 */
@Data
@Entity
@Table(name = "user_exams")
public class UserExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @CreationTimestamp
    @Column(name = "purchase_time", nullable = false, updatable = false)
    private LocalDateTime purchaseTime;
}
