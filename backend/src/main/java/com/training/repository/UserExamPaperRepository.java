package com.training.repository;

import com.training.entity.UserExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExamPaperRepository extends JpaRepository<UserExamPaper, Long> {

    // 根据用户角色分页查询用户已购买的试卷
    @Query("SELECT uep FROM UserExamPaper uep " +
            "WHERE uep.user.id = :userId " +
            "AND (:userRole MEMBER OF uep.examPaper.visibleRoles OR uep.examPaper.visibleRoles IS EMPTY)")
    Page<UserExamPaper> findByUserIdAndUserRole(@Param("userId") Long userId, @Param("userRole") String userRole, Pageable pageable);

    // 根据用户角色和关键词分页查询用户已购买的试卷
    @Query("SELECT uep FROM UserExamPaper uep " +
            "WHERE uep.user.id = :userId " +
            "AND (uep.examPaper.title LIKE %:keyword% OR uep.examPaper.description LIKE %:keyword%) " +
            "AND (:userRole MEMBER OF uep.examPaper.visibleRoles OR uep.examPaper.visibleRoles IS EMPTY)")
    Page<UserExamPaper> findByUserIdAndExamPaperTitleContainingAndUserRole(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("userRole") String userRole, Pageable pageable);

    boolean existsByUserIdAndExamPaperId(Long userId, Long examPaperId);

}
