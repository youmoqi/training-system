package com.training.repository;

import com.training.entity.UserCourse;
import com.training.entity.User;
import com.training.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUser(User user);
    Optional<UserCourse> findByUserAndCourse(User user, Course course);
    boolean existsByUserAndCourse(User user, Course course);
} 