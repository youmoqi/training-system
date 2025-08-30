package com.training.repository;

import com.training.entity.Exam.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author 14798
 */
@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

    @Query("SELECT DISTINCT epq FROM ExamQuestion epq " +
            "JOIN FETCH epq.question q " +
            "LEFT JOIN FETCH q.options " +
            "WHERE epq.exam.id = :examId " +
            "ORDER BY epq.questionOrder")
    List<ExamQuestion> findQuestionsByExamId(@Param("examId") Long examId);

    void deleteByExamId(Long examId);

    @Modifying
    @Query("DELETE FROM ExamQuestion epq " +
            "WHERE epq.exam.id = :examId AND epq.question.id = :questionId")
    void deleteByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(epq) FROM ExamQuestion epq WHERE epq.exam.id = :examId")
    Long countByExamId(@Param("examId") Long examId);

    @Query("SELECT epq FROM ExamQuestion epq " +
            "WHERE epq.exam.id = :examId AND epq.question.id = :questionId")
    Optional<ExamQuestion> findByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);

    @Query("SELECT CASE WHEN COUNT(epq) > 0 THEN true ELSE false END FROM ExamQuestion epq " +
            "WHERE epq.exam.id = :examId AND epq.question.id = :questionId")
    boolean existsByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);
}
