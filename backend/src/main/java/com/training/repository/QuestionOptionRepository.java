package com.training.repository;

import com.training.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author YIZ
 */
@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionOption q WHERE q.question.id = :questionId")
    void deleteAllByQuestionId(@Param("questionId") Long questionId);
}
