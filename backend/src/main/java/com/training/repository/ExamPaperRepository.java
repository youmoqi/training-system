package com.training.repository;

import com.training.entity.ExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {

    List<ExamPaper> findByIsOnlineTrue();

    @Query("SELECT ep FROM ExamPaper ep " +
           "JOIN ep.visibleRoles vr " +
           "WHERE ep.isOnline = true AND vr = :role")
    List<ExamPaper> findByRoleAndIsOnlineTrue(@Param("role") String role);

    // 分页查询方法
    Page<ExamPaper> findByTitleContaining(String title, Pageable pageable);
    Page<ExamPaper> findByIsOnline(Boolean isOnline, Pageable pageable);
    Page<ExamPaper> findByTitleContainingAndIsOnline(String title, Boolean isOnline, Pageable pageable);

    List<ExamPaper> findByExamCategoryAndIsOnlineTrue(String examCategory);

    @Modifying
    @Query("UPDATE ExamPaper ep SET ep.totalQuestions = :totalQuestions WHERE ep.id = :examPaperId")
    void updateTotalQuestions(@Param("examPaperId") Long examPaperId, @Param("totalQuestions") Integer totalQuestions);

    // 根据用户角色获取可购买的试卷（未购买的）
    @Query("SELECT ep FROM ExamPaper ep " +
           "WHERE ep.isOnline = true " +
           "AND ep.id NOT IN (SELECT uep.examPaper.id FROM UserExamPaper uep WHERE uep.user.id = :userId) " +
           "AND (:userRole MEMBER OF ep.visibleRoles OR ep.visibleRoles IS EMPTY)")
    Page<ExamPaper> findPurchasableByUserRole(@Param("userRole") String userRole, @Param("userId") Long userId, Pageable pageable);

    // 根据关键词和用户角色获取可购买的试卷（未购买的）
    @Query("SELECT ep FROM ExamPaper ep " +
           "WHERE ep.isOnline = true " +
           "AND ep.title LIKE %:keyword% " +
           "AND ep.id NOT IN (SELECT uep.examPaper.id FROM UserExamPaper uep WHERE uep.user.id = :userId) " +
           "AND (:userRole MEMBER OF ep.visibleRoles OR ep.visibleRoles IS EMPTY)")
    Page<ExamPaper> findPurchasableByKeywordAndUserRole(@Param("keyword") String keyword, @Param("userRole") String userRole, @Param("userId") Long userId, Pageable pageable);
}
