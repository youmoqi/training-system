package com.training.repository;

import com.training.entity.ExamResult;
import com.training.entity.User;
import com.training.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    
    List<ExamResult> findByUserOrderByExamTimeDesc(User user);
    
    List<ExamResult> findByUserAndQuestionBankOrderByExamTimeDesc(User user, QuestionBank questionBank);
    
    Optional<ExamResult> findTopByUserAndQuestionBankOrderByExamTimeDesc(User user, QuestionBank questionBank);
    
    // 分页查询方法
    Page<ExamResult> findByUser(User user, Pageable pageable);
    
    Page<ExamResult> findByQuestionBankTitleContaining(String title, Pageable pageable);
} 