package com.training.repository;

import com.training.entity.UserCourse;
import com.training.entity.User;
import com.training.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author YIZ
 */
@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUser(User user);

    Page<UserCourse> findByUser(User user, Pageable pageable);

    boolean existsByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);
}
