package com.training.entity.Exam;

import javax.persistence.*;

import com.training.entity.User;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "exam_results")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "time_taken")
    // 秒
    private Integer timeTaken;

    @Column(name = "exam_time", nullable = false)
    private LocalDateTime examTime;

    @Column(name = "is_passed", nullable = false)
    private Boolean isPassed = false;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    // 题目答题记录
    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamQuestionResult> questionResults;
}
