package com.training.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "question_bank_visible_roles")
public class QuestionBankVisibleRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_bank_id", nullable = false)
    private QuestionBank questionBank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visibility_category_id")
    private VisibilityCategory visibilityCategory;
} 