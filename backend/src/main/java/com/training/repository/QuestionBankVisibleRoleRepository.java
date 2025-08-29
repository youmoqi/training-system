package com.training.repository;

import com.training.entity.QuestionBankVisibleRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBankVisibleRoleRepository extends JpaRepository<QuestionBankVisibleRole, Long> {
    @Modifying
    @Query("delete from QuestionBankVisibleRole q where q.questionBank.id = :qbId")
    void deleteAllByQuestionBankId(Long qbId);
} 