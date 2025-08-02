package com.training.repository;

import com.training.entity.UserQuestionBank;
import com.training.entity.User;
import com.training.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuestionBankRepository extends JpaRepository<UserQuestionBank, Long> {
    List<UserQuestionBank> findByUser(User user);

    Optional<UserQuestionBank> findByUserAndQuestionBank(User user, QuestionBank questionBank);

    boolean existsByUserAndQuestionBank(User user, QuestionBank questionBank);

    Page<UserQuestionBank> findByUser(User user, Pageable pageable);
}
