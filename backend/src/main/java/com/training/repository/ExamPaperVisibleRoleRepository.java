package com.training.repository;

import com.training.entity.ExamPaperVisibleRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperVisibleRoleRepository extends JpaRepository<ExamPaperVisibleRole, Long> {
    @Modifying
    @Query("delete from ExamPaperVisibleRole e where e.examPaper.id = :examPaperId")
    void deleteAllByExamPaperId(Long examPaperId);
}
