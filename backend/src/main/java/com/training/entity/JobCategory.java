package com.training.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "job_categories")
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g. BLASTER, SAFETY, KEEPER, ENGINEER_A/B/C/D

    @Column(nullable = false, length = 100)
    private String name; // 显示名称

    @Column(nullable = false)
    private Boolean isActive = true;
} 