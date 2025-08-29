package com.training.repository;

import com.training.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByIsOnlineTrue();

    // 分页查询方法
    Page<Course> findByTitleContainingOrDescriptionContaining(
            String title, String description, Pageable pageable);

    // 支持角色过滤的分页查询方法
    Page<Course> findByIsOnlineTrue(Pageable pageable);

    Page<Course> findByIsOnlineTrueAndVisibleRolesId(Long roleId, Pageable pageable);
}
