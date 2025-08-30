package com.training.entity;

import com.training.entity.Exam.QuestionBank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author YIZ
 */
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

    @Column(name = "purchase_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime purchaseTime;
}
