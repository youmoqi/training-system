package com.training.repository;

import com.training.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByIsOnlineTrue();
    List<Course> findByIsOnlineTrueAndVisibleRolesContaining(String role);
    
    // 分页查询方法
    Page<Course> findByTitleContainingOrDescriptionContaining(
        String title, String description, Pageable pageable);
} 