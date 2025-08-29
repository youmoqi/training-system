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

    // 分页查询方法
    Page<ExamPaper> findByTitleContaining(String title, Pageable pageable);
    Page<ExamPaper> findByIsOnline(Boolean isOnline, Pageable pageable);
    Page<ExamPaper> findByTitleContainingAndIsOnline(String title, Boolean isOnline, Pageable pageable);

    List<ExamPaper> findByExamCategoryAndIsOnlineTrue(String examCategory);

    @Modifying
    @Query("UPDATE ExamPaper ep SET ep.totalQuestions = :totalQuestions WHERE ep.id = :examPaperId")
    void updateTotalQuestions(@Param("examPaperId") Long examPaperId, @Param("totalQuestions") Integer totalQuestions);

    // 可购买的试卷（未购买）
    @Query("SELECT ep FROM ExamPaper ep " +
            "LEFT JOIN ep.visibleRoles vc " +
            "WHERE ep.isOnline = true " +
            "AND ep.id NOT IN (SELECT uep.examPaper.id FROM UserExamPaper uep WHERE uep.user.id = :userId) " +
            "AND (vc.id = :roleId OR vc IS NULL)")
    Page<ExamPaper> findPurchasableByRoleId(@Param("roleId") Long roleId,
                                            @Param("userId") Long userId,
                                            Pageable pageable);

    // 可购买的试卷 + 关键词
    @Query("SELECT ep FROM ExamPaper ep " +
            "LEFT JOIN ep.visibleRoles vc " +
            "WHERE ep.isOnline = true " +
            "AND ep.title LIKE %:keyword% " +
            "AND ep.id NOT IN (SELECT uep.examPaper.id FROM UserExamPaper uep WHERE uep.user.id = :userId) " +
            "AND (vc.id = :roleId OR vc IS NULL)")
    Page<ExamPaper> findPurchasableByKeywordAndRoleId(@Param("keyword") String keyword,
                                                      @Param("roleId") Long roleId,
                                                      @Param("userId") Long userId,
                                                      Pageable pageable);
}
