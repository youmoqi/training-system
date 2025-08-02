package com.training.repository;

import com.training.entity.QuestionBankQuestionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBankQuestionResultRepository extends JpaRepository<QuestionBankQuestionResult, Long> {
} 