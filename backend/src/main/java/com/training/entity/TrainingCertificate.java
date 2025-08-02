package com.training.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "training_certificates")
public class TrainingCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, unique = true)
    private String certificateNumber; // 证书编号

    @Column(nullable = false)
    private LocalDateTime issueDate; // 颁发日期

    @Column(nullable = false)
    private LocalDateTime completeDate; // 完成日期

    @Column(nullable = false)
    private Boolean isPaid; // 是否收费

    @Column(nullable = false)
    private String certificateType; // 证书类型：EXPLOSIVE_USER(易制爆), BLAST_USER(爆破三大员)

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
} 