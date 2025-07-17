package com.training.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String realName;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, unique = true)
    private String idCard;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String workUnit;

    @Column(nullable = false)
    private String trainingType;

    @Column(nullable = false)
    private String jobCategory;

    @Column(nullable = false)
    private String facePhotoUrl;

    @Column(nullable = false)
    private Double paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public enum UserRole {
        SUPER_ADMIN,    // 超级管理员
        ADMIN,          // 管理员
        EXPLOSIVE_USER, // 易制爆人员
        BLAST_USER      // 爆破三大员
    }
} 