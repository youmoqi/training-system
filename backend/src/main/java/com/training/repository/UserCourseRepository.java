package com.training.repository;

import com.training.entity.Course;
import com.training.entity.User;
import com.training.entity.UserCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author YIZ
 */
@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    boolean existsByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    // 按用户名或真实姓名搜索
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContaining(String userName, String userRealName, Pageable pageable);

    // 按用户名或真实姓名和课程ID搜索
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndCourseId(String userName, String userRealName, Long courseId, Pageable pageable);

    // 按用户名或真实姓名和完成状态搜索
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndIsCompleted(String userName, String userRealName, Boolean isCompleted, Pageable pageable);

    // 按用户名或真实姓名、课程ID和完成状态搜索
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndCourseIdAndIsCompleted(String userName, String userRealName, Long courseId, Boolean isCompleted, Pageable pageable);

    // 添加JOIN FETCH的查询方法
    Page<UserCourse> findByCourseId(Long courseId, Pageable pageable);

    // 查询用户的所有课程
    List<UserCourse> findByUserId(Long userId);

    // 统计用户的课程数量
    long countByUserId(Long userId);

    // 统计用户已完成/未完成的课程数量
    long countByUserIdAndIsCompleted(Long userId, Boolean isCompleted);

    Page<UserCourse> findByIsCompleted(Boolean isCompleted, Pageable pageable);

    Page<UserCourse> findByCourseIdAndIsCompleted(Long courseId, Boolean isCompleted, Pageable pageable);

    Page<UserCourse> findByUser(User user, Pageable pageable);

    List<UserCourse> findByUser(@Param("user") User user);

    List<UserCourse> findByCourseId(Long courseId);

}
