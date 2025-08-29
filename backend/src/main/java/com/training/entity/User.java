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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_category_id")
    private JobCategory jobCategory;

    @Column(nullable = false)
    private String facePhotoUrl;

    @Column(nullable = false)
    private Double paymentAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "visibility_category_id")
    private VisibilityCategory role;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private boolean canLearn = true;

    @Column(nullable = false)
    private boolean canExam = true;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
