package com.training.repository;

import com.training.entity.CourseVisibleRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseVisibleRoleRepository extends JpaRepository<CourseVisibleRole, Long> {
    @Modifying
    @Query("delete from CourseVisibleRole c where c.course.id = :courseId")
    void deleteAllByCourseId(Long courseId);
} 