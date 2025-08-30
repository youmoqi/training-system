package com.training.repository;

import com.training.entity.Exam.ExamQuestionResult;
import com.training.entity.Exam.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface ExamQuestionResultRepository extends JpaRepository<ExamQuestionResult, Long> {

    List<ExamQuestionResult> findByExamResultOrderById(ExamResult examResult);
}
