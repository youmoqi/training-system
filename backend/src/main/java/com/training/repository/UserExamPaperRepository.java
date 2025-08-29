package com.training.repository;

import com.training.entity.UserExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 14798
 */
@Repository
public interface UserExamPaperRepository extends JpaRepository<UserExamPaper, Long> {

    // 根据用户角色分页查询用户已购买的试卷
    @Query("SELECT uep FROM UserExamPaper uep " +
            "LEFT JOIN uep.examPaper.visibleRoleIds vc " +
            "WHERE uep.user.id = :userId " +
            "AND (vc.id = :roleId OR vc IS NULL)")
    Page<UserExamPaper> findByUserIdAndRoleId(@Param("userId") Long userId,
                                              @Param("roleId") Long roleId,
                                              Pageable pageable);

    // 根据用户角色和关键词分页查询用户已购买的试卷
    @Query("SELECT uep FROM UserExamPaper uep " +
            "LEFT JOIN uep.examPaper.visibleRoleIds vc " +
            "WHERE uep.user.id = :userId " +
            "AND (uep.examPaper.title LIKE %:keyword% OR uep.examPaper.description LIKE %:keyword%) " +
            "AND (vc.id = :roleId OR vc IS NULL)")
    Page<UserExamPaper> findByUserIdAndKeywordAndRoleId(@Param("userId") Long userId,
                                                        @Param("keyword") String keyword,
                                                        @Param("roleId") Long roleId,
                                                        Pageable pageable);

    boolean existsByUserIdAndExamPaperId(Long userId, Long examPaperId);

}
