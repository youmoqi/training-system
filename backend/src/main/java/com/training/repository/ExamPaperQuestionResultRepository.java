package com.training.repository;

import com.training.entity.ExamPaperQuestionResult;
import com.training.entity.ExamPaperResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperQuestionResultRepository extends JpaRepository<ExamPaperQuestionResult, Long> {
    
    @Query("SELECT eqr FROM ExamPaperQuestionResult eqr WHERE eqr.examPaperResult.id = :resultId ORDER BY eqr.id")
    List<ExamPaperQuestionResult> findByExamPaperResultId(@Param("resultId") Long resultId);
    
    List<ExamPaperQuestionResult> findByExamPaperResultOrderById(ExamPaperResult examPaperResult);
} 