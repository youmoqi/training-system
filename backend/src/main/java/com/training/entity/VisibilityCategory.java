package com.training.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "visibility_categories")
public class VisibilityCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // EXPLOSIVE_FIRST, ENGINEER_CONTINUE, etc.

    @Column(nullable = false, length = 100)
    private String name; // 显示名称

    @Column(nullable = false)
    private Boolean isActive = true;
} 