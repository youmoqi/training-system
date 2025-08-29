package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "exam_paper_questions")
public class ExamPaperQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    @JsonIgnore
    private ExamPaper examPaper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder = 0;

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
}
