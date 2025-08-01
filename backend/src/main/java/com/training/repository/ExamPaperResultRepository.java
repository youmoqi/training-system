package com.training.repository;

import com.training.entity.ExamPaperResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperResultRepository extends JpaRepository<ExamPaperResult, Long> {

    @Query("SELECT epr FROM ExamPaperResult epr " +
            "WHERE epr.user.id = :userId AND epr.examPaper.id = :examPaperId " +
            "ORDER BY epr.examTime DESC")
    List<ExamPaperResult> findByUserIdAndExamPaperId(@Param("userId") Long userId, @Param("examPaperId") Long examPaperId);
}
