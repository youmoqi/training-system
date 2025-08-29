package com.training.repository;

import com.training.entity.ExamPaperQuestionResult;
import com.training.entity.ExamPaperResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface ExamPaperQuestionResultRepository extends JpaRepository<ExamPaperQuestionResult, Long> {

    List<ExamPaperQuestionResult> findByExamPaperResultOrderById(ExamPaperResult examPaperResult);
}
