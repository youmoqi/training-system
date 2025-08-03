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

/**
 * @author YIZ
 */
@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    boolean existsByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserAndCourse(User user, Course course);

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    // 按用户名或真实姓名搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE u.username LIKE %:keyword% OR u.realName LIKE %:keyword%")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContaining(
            @Param("keyword") String keyword, Pageable pageable);

    // 按用户名或真实姓名和课程ID搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE (u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) AND uc.course.id = :courseId")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndCourseId(
            @Param("keyword") String keyword, @Param("courseId") Long courseId, Pageable pageable);

    // 按用户名或真实姓名和完成状态搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE (u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) AND uc.isCompleted = :isCompleted")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndIsCompleted(
            @Param("keyword") String keyword, @Param("isCompleted") Boolean isCompleted, Pageable pageable);

    // 按用户名或真实姓名、课程ID和完成状态搜索
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE (u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) AND uc.course.id = :courseId AND uc.isCompleted = :isCompleted")
    Page<UserCourse> findByUserUsernameContainingOrUserRealNameContainingAndCourseIdAndIsCompleted(
            @Param("keyword") String keyword, @Param("courseId") Long courseId, @Param("isCompleted") Boolean isCompleted, Pageable pageable);

    // 添加JOIN FETCH的查询方法
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE uc.course.id = :courseId")
    Page<UserCourse> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE uc.isCompleted = :isCompleted")
    Page<UserCourse> findByIsCompleted(@Param("isCompleted") Boolean isCompleted, Pageable pageable);

    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE uc.course.id = :courseId AND uc.isCompleted = :isCompleted")
    Page<UserCourse> findByCourseIdAndIsCompleted(@Param("courseId") Long courseId, @Param("isCompleted") Boolean isCompleted, Pageable pageable);

    // 添加JOIN FETCH的findAll方法
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c")
    Page<UserCourse> findAllWithJoins(Pageable pageable);

    // 添加JOIN FETCH的findByUser方法
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE uc.user = :user")
    Page<UserCourse> findByUserWithJoins(@Param("user") User user, Pageable pageable);

    // 添加JOIN FETCH的findByUser方法（返回List）
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE uc.user = :user")
    List<UserCourse> findByUserWithJoins(@Param("user") User user);

    // 根据课程ID查找用户课程（返回List）
    @Query("SELECT uc FROM UserCourse uc JOIN uc.user u JOIN uc.course c WHERE uc.course.id = :courseId")
    List<UserCourse> findByCourseId(@Param("courseId") Long courseId);

}
