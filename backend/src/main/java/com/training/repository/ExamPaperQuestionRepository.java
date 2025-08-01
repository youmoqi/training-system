package com.training.repository;

import com.training.entity.ExamPaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamPaperQuestionRepository extends JpaRepository<ExamPaperQuestion, Long> {

    List<ExamPaperQuestion> findByExamPaperIdOrderByQuestionOrder(Long examPaperId);

    @Query("SELECT epq FROM ExamPaperQuestion epq " +
            "WHERE epq.examPaper.id = :examPaperId " +
            "ORDER BY epq.questionOrder")
    List<ExamPaperQuestion> findQuestionsByExamPaperId(@Param("examPaperId") Long examPaperId);

    void deleteByExamPaperId(Long examPaperId);

    @Query("DELETE FROM ExamPaperQuestion epq " +
            "WHERE epq.examPaper.id = :examPaperId AND epq.question.id = :questionId")
    void deleteByExamPaperIdAndQuestionId(@Param("examPaperId") Long examPaperId, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(epq) FROM ExamPaperQuestion epq WHERE epq.examPaper.id = :examPaperId")
    int countByExamPaperId(@Param("examPaperId") Long examPaperId);

    @Query("SELECT epq FROM ExamPaperQuestion epq " +
            "WHERE epq.examPaper.id = :examPaperId AND epq.question.id = :questionId")
    Optional<ExamPaperQuestion> findByExamPaperIdAndQuestionId(@Param("examPaperId") Long examPaperId, @Param("questionId") Long questionId);

    @Query("SELECT CASE WHEN COUNT(epq) > 0 THEN true ELSE false END FROM ExamPaperQuestion epq " +
            "WHERE epq.examPaper.id = :examPaperId AND epq.question.id = :questionId")
    boolean existsByExamPaperIdAndQuestionId(@Param("examPaperId") Long examPaperId, @Param("questionId") Long questionId);
}
