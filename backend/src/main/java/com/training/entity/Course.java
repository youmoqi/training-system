package com.training.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String coverImageUrl;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean isOnline;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_visible_roles", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "role")
    private List<String> visibleRoles;

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