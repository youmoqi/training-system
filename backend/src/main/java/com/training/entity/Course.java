package com.training.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 14798
 */
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_visible_roles",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "visibility_role_id")
    )
    private List<Role> visibleRoles;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
