package com.training.repository;

import com.training.entity.Exam.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    void deleteAllByQuestionId(@Param("questionId") Long questionId);
}
