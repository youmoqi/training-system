package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_courses")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
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
}
