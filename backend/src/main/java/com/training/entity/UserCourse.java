package com.training.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author 14798
 */
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
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime enrollTime;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column
    private LocalDateTime completeTime;

    @Column
    private Integer watchProgress;
}
