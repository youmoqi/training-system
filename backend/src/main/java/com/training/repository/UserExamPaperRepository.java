package com.training.repository;

import com.training.entity.ExamPaper;
import com.training.entity.UserExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExamPaperRepository extends JpaRepository<UserExamPaper, Long> {

    // 分页查询用户已购买的试卷
    @Query("SELECT uep FROM UserExamPaper uep " +
            "WHERE uep.user.id = :userId")
    Page<UserExamPaper> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // 分页查询用户已购买的试卷（带关键词搜索）
    @Query("SELECT uep FROM UserExamPaper uep " +
            "WHERE uep.user.id = :userId " +
            "AND (uep.examPaper.title LIKE %:keyword% OR uep.examPaper.description LIKE %:keyword%)")
    Page<UserExamPaper> findByUserIdAndExamPaperTitleContaining(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    boolean existsByUserIdAndExamPaperId(Long userId, Long examPaperId);

    // 可购买试卷分页查询方法（用户未购买的试卷）
    @Query("SELECT ep FROM ExamPaper ep " +
            "WHERE ep.isOnline = true " +
            "AND ep.id NOT IN (SELECT uep.examPaper.id FROM UserExamPaper uep WHERE uep.user.id = :userId)")
    Page<ExamPaper> findPurchasableByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ep FROM ExamPaper ep " +
            "WHERE ep.isOnline = true " +
            "AND ep.id NOT IN (SELECT uep.examPaper.id FROM UserExamPaper uep WHERE uep.user.id = :userId) " +
            "AND (ep.title LIKE %:keyword% OR ep.description LIKE %:keyword%)")
    Page<ExamPaper> findPurchasableByUserIdAndTitleContaining(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}
