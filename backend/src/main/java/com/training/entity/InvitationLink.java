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
@Table(name = "invitation_links")
public class InvitationLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String linkCode;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private LocalDateTime expireTime;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "invitation_courses",
            joinColumns = @JoinColumn(name = "invitation_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;
}
