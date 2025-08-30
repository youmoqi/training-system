package com.training.repository;

import com.training.entity.Exam.ExamAutoRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface ExamAutoRuleRepository extends JpaRepository<ExamAutoRule, Long> {
    List<ExamAutoRule> findByExamId(Long examId);
}
