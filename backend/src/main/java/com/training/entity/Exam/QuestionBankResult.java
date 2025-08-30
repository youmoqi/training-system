package com.training.entity.Exam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.entity.User;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "question_bank_results")
public class QuestionBankResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_bank_id", nullable = false)
    @JsonIgnore
    private QuestionBank questionBank;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column
    private Integer timeTaken;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime submitTime;

    @Column(nullable = false)
    private Boolean isPassed;

    @Column(name = "create_time", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "questionBankResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<QuestionBankQuestionResult> questionResults;
}
