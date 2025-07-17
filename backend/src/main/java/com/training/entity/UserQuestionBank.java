package com.training.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_question_banks")
public class UserQuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_bank_id", nullable = false)
    private QuestionBank questionBank;

    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column
    private LocalDateTime completeTime;

    @Column
    private Integer score; // 答题得分

    @PrePersist
    protected void onCreate() {
        purchaseTime = LocalDateTime.now();
        isCompleted = false;
    }
} 