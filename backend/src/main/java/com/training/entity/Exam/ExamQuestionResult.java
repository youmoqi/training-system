package com.training.entity.Exam;

import javax.persistence.*;

import lombok.Data;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "exam_question_results")
public class ExamQuestionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_result_id", nullable = false)
    private ExamResult examResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 0;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
}
