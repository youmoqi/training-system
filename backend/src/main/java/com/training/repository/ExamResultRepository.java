package com.training.repository;

import com.training.entity.Exam.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    List<ExamResult> findByUserIdAndExamIdOrderByExamTimeDesc(Long userId, Long examId);

    List<ExamResult> findByUserId(Long userId);

    Page<ExamResult> findByUserIdOrderByExamTimeDesc(Long userId, Pageable pageable);

    Long countByUserIdAndExamId(Long userId, Long examId);

    Page<ExamResult> findByUserIdAndExamTitleContainingOrderByExamTimeDesc(Long userId, String keyword, Pageable pageable);

    Page<ExamResult> findByUserIdAndIsPassedOrderByExamTimeDesc(Long userId, Boolean isPassed, Pageable pageable);

    Page<ExamResult> findByUserIdAndExamTitleContainingAndIsPassedOrderByExamTimeDesc(Long userId, String keyword, Boolean isPassed, Pageable pageable);

    List<ExamResult> findByUserIdAndExamIdOrderByExamTimeAsc(Long userId, Long examId);

    @Query("SELECT er FROM ExamResult er " +
            "JOIN FETCH er.user u " +
            "JOIN FETCH er.exam e " +
            "WHERE er.examTime BETWEEN :startDate AND :endDate " +
            "AND u.role.id IN :roleIds")
    List<ExamResult> findExamResultsByDateRangeAndRoleIds(LocalDateTime startDate, LocalDateTime endDate, List<Long> roleIds);
}
