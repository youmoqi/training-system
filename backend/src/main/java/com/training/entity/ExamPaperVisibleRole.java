package com.training.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "exam_paper_visible_roles")
public class ExamPaperVisibleRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    private ExamPaper examPaper;

    @Column(name = "role", nullable = false)
    private String role;
} 