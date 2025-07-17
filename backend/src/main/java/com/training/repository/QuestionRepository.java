package com.training.repository;

import com.training.entity.Question;
import com.training.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionBank(QuestionBank questionBank);
    List<Question> findByQuestionBankOrderById(QuestionBank questionBank);
    List<Question> findByQuestionBankAndType(QuestionBank questionBank, String type);
    long countByQuestionBank(QuestionBank questionBank);
} 