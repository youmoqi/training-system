package com.training.repository;

import com.training.entity.Exam.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author YIZ
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByIsOnlineTrue();

    Page<Exam> findByTitleContaining(String title, Pageable pageable);
    Page<Exam> findByIsOnline(Boolean isOnline, Pageable pageable);
    Page<Exam> findByTitleContainingAndIsOnline(String title, Boolean isOnline, Pageable pageable);

    List<Exam> findByExamCategoryAndIsOnlineTrue(String examCategory);

    @Modifying
    @Query("UPDATE Exam ep SET ep.totalQuestions = :totalQuestions WHERE ep.id = :examId")
    void updateTotalQuestions(Long examId, Integer totalQuestions);

    // 可购买的试卷（未购买）
    @Query("SELECT ep FROM Exam ep " +
            "LEFT JOIN ep.visibleRoles vc " +
            "WHERE ep.isOnline = true " +
            "AND ep.id NOT IN (SELECT uep.exam.id FROM UserExam uep WHERE uep.user.id = :userId) " +
            "AND vc.id = :roleId")
    Page<Exam> findPurchasableByRoleId(Long roleId, Long userId, Pageable pageable);

    // 可购买的试卷 + 关键词
    @Query("SELECT ep FROM Exam ep " +
            "LEFT JOIN ep.visibleRoles vc " +
            "WHERE ep.isOnline = true " +
            "AND ep.title LIKE %:keyword% " +
            "AND ep.id NOT IN (SELECT uep.exam.id FROM UserExam uep WHERE uep.user.id = :userId) " +
            "AND vc.id = :roleId")
    Page<Exam> findPurchasableByKeywordAndRoleId(String keyword, Long roleId, Long userId, Pageable pageable);
}
