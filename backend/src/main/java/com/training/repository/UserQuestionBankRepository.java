package com.training.repository;

import com.training.entity.UserQuestionBank;
import com.training.entity.User;
import com.training.entity.Exam.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface UserQuestionBankRepository extends JpaRepository<UserQuestionBank, Long> {
    List<UserQuestionBank> findByUser(User user);

    boolean existsByUserAndQuestionBank(User user, QuestionBank questionBank);

    Page<UserQuestionBank> findByUser(User user, Pageable pageable);
}
