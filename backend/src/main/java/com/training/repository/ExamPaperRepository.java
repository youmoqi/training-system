package com.training.repository;

import com.training.entity.ExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, Long> {

    List<ExamPaper> findByIsOnlineTrue();

    @Query("SELECT ep FROM ExamPaper ep " +
           "JOIN ep.visibleRoles vr " +
           "WHERE ep.isOnline = true AND vr = :role")
    List<ExamPaper> findByRoleAndIsOnlineTrue(@Param("role") String role);

    // 分页查询方法
    Page<ExamPaper> findByTitleContaining(String title, Pageable pageable);
    Page<ExamPaper> findByIsOnline(Boolean isOnline, Pageable pageable);
    Page<ExamPaper> findByTitleContainingAndIsOnline(String title, Boolean isOnline, Pageable pageable);
}
