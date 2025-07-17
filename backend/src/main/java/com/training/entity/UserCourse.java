package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_courses")
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "createTime", "updateTime"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"createTime", "updateTime"})
    private Course course;

    @Column(nullable = false)
    private LocalDateTime enrollTime;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column
    private LocalDateTime completeTime;

    @Column
    private Integer watchProgress; // 观看进度百分比

    @PrePersist
    protected void onCreate() {
        enrollTime = LocalDateTime.now();
        isCompleted = false;
        watchProgress = 0;
    }
}