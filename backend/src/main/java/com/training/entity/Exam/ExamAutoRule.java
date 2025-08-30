package com.training.entity.Exam;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author 14798
 */
@Data
@Entity
@Table(name = "exam_auto_rules")
public class ExamAutoRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount = 0;

    @Column(name = "question_bank_ids", columnDefinition = "TEXT")
    // JSON格式存储题库ID列表
    private String questionBankIds;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    private Exam exam;
}
