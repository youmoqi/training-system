package com.training.entity;

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

    @Column(name = "is_random", nullable = false)
    private Boolean isRandom = false;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    // 关联的题目
    @OneToMany(mappedBy = "examPaper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExamPaperQuestion> questions;

    // 可见角色
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exam_paper_visible_roles", joinColumns = @JoinColumn(name = "exam_paper_id"))
    @Column(name = "role")
    private List<String> visibleRoles;
}
