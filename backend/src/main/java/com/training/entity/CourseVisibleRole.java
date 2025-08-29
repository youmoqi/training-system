package com.training.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "course_visible_roles")
public class CourseVisibleRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visibility_category_id")
    private VisibilityCategory visibilityCategory;
} 