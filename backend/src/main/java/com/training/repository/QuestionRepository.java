package com.training.repository;

import com.training.entity.Question;
import com.training.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionBank(QuestionBank questionBank);
    List<Question> findByQuestionBankOrderById(QuestionBank questionBank);
    List<Question> findByQuestionBankAndType(QuestionBank questionBank, String type);
    long countByQuestionBank(QuestionBank questionBank);
    
    // 分页查询方法
    Page<Question> findByContentContaining(String content, Pageable pageable);
    Page<Question> findByType(String type, Pageable pageable);
    Page<Question> findByContentContainingAndType(String content, String type, Pageable pageable);
    
    // 按题库ID查询
    Page<Question> findByQuestionBankId(Long questionBankId, Pageable pageable);
    Page<Question> findByContentContainingAndQuestionBankId(String content, Long questionBankId, Pageable pageable);
    Page<Question> findByTypeAndQuestionBankId(String type, Long questionBankId, Pageable pageable);
    Page<Question> findByContentContainingAndTypeAndQuestionBankId(String content, String type, Long questionBankId, Pageable pageable);
} 