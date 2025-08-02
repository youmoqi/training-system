package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "exam_papers")
public class ExamPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore = 100;

    @Column(name = "pass_score", nullable = false)
    private Integer passScore = 60;

    @Column(name = "duration", nullable = false)
    private Integer duration = 120; // 分钟

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = true;

    @Column(name = "exam_category", nullable = false, length = 50)
    private String examCategory = "GENERAL";

    @Column(name = "allow_retake", nullable = false)
    private Boolean allowRetake = true;

    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 3;

    @Column(name = "single_choice_score", nullable = false)
    private Integer singleChoiceScore = 2;

    @Column(name = "multiple_choice_score", nullable = false)
    private Integer multipleChoiceScore = 4;

    @Column(name = "true_false_score", nullable = false)
    private Integer trueFalseScore = 2;

    @Column(name = "fill_blank_score", nullable = false)
    private Integer fillBlankScore = 3;

    @Column(name = "short_answer_score", nullable = false)
    private Integer shortAnswerScore = 5;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    // 关联的题目
    @OneToMany(mappedBy = "examPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ExamPaperQuestion> questions;

    // 可见角色
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exam_paper_visible_roles", joinColumns = @JoinColumn(name = "exam_paper_id"))
    @Column(name = "role")
    private List<String> visibleRoles;
}
