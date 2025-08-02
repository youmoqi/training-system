package com.training.repository;

import com.training.entity.Course;
import com.training.entity.User;
import com.training.entity.UserCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUser(User user);

    Page<UserCourse> findByUser(User user, Pageable pageable);

    boolean existsByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    // 按用户名或真实姓名搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u WHERE u.username LIKE %:keyword% OR u.realName LIKE %:keyword%")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContaining(
            @Param("keyword") String keyword, Pageable pageable);

    // 按用户名或真实姓名和课程ID搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u WHERE (u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) AND uc.course.id = :courseId")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndCourseId(
            @Param("keyword") String keyword, @Param("courseId") Long courseId, Pageable pageable);

    // 按用户名或真实姓名和完成状态搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u WHERE (u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) AND uc.isCompleted = :isCompleted")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndIsCompleted(
            @Param("keyword") String keyword, @Param("isCompleted") Boolean isCompleted, Pageable pageable);

    // 按用户名或真实姓名、课程ID和完成状态搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u WHERE (u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) AND uc.course.id = :courseId AND uc.isCompleted = :isCompleted")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndCourseIdAndIsCompleted(
            @Param("keyword") String keyword, @Param("courseId") Long courseId, @Param("isCompleted") Boolean isCompleted, Pageable pageable);

    // 按课程ID搜索
    Page<UserCourse> findByCourseId(Long courseId, Pageable pageable);

    // 按完成状态搜索
    Page<UserCourse> findByIsCompleted(Boolean isCompleted, Pageable pageable);

    // 按课程ID和完成状态搜索
    Page<UserCourse> findByCourseIdAndIsCompleted(Long courseId, Boolean isCompleted, Pageable pageable);

}
