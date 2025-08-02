package com.training.repository;

import com.training.entity.ExamPaperAutoRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamPaperAutoRuleRepository extends JpaRepository<ExamPaperAutoRule, Long> {

    List<ExamPaperAutoRule> findByExamPaperId(Long examPaperId);

    Optional<ExamPaperAutoRule> findById(Long id);
}
