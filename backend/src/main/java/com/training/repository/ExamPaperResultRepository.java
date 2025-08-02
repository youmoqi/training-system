package com.training.repository;

import com.training.entity.ExamPaperResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperResultRepository extends JpaRepository<ExamPaperResult, Long> {

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.examPaper.id = :examPaperId " +
            "ORDER BY epr.examTime DESC")
    List<ExamPaperResult> findByUserIdAndExamPaperId(@Param("userId") Long userId, @Param("examPaperId") Long examPaperId);

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId " +
            "ORDER BY epr.examTime DESC")
    Page<ExamPaperResult> findByUserIdOrderByExamTimeDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(epr) FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.examPaper.id = :examPaperId")
    Long countByUserIdAndExamPaperId(@Param("userId") Long userId, @Param("examPaperId") Long examPaperId);

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.examPaper.title LIKE %:keyword% " +
            "ORDER BY epr.examTime DESC")
    Page<ExamPaperResult> findByUserIdAndExamPaperTitleContainingOrderByExamTimeDesc(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.isPassed = :isPassed " +
            "ORDER BY epr.examTime DESC")
    Page<ExamPaperResult> findByUserIdAndIsPassedOrderByExamTimeDesc(@Param("userId") Long userId, @Param("isPassed") Boolean isPassed, Pageable pageable);

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.examPaper.title LIKE %:keyword% AND epr.isPassed = :isPassed " +
            "ORDER BY epr.examTime DESC")
    Page<ExamPaperResult> findByUserIdAndExamPaperTitleContainingAndIsPassedOrderByExamTimeDesc(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("isPassed") Boolean isPassed, Pageable pageable);

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.examPaper.id = :examPaperId " +
            "ORDER BY epr.examTime ASC")
    List<ExamPaperResult> findByUserIdAndExamPaperIdOrderByExamTimeAsc(@Param("userId") Long userId, @Param("examPaperId") Long examPaperId);
}
