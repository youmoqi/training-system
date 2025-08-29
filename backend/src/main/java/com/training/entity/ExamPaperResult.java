package com.training.entity;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "exam_paper_results")
public class ExamPaperResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    private ExamPaper examPaper;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "time_taken")
    private Integer timeTaken; // 秒

    @Column(name = "exam_time", nullable = false)
    private LocalDateTime examTime;

    @Column(name = "is_passed", nullable = false)
    private Boolean isPassed = false;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // 题目答题记录
    @OneToMany(mappedBy = "examPaperResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamPaperQuestionResult> questionResults;
}
