package com.training.entity;

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
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private boolean canLearn = true;

    @Column(nullable = false)
    private boolean canExam = true;
}
