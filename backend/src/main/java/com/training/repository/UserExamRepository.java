package com.training.repository;

import com.training.entity.UserExam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 14798
 */
@Repository
public interface UserExamRepository extends JpaRepository<UserExam, Long> {

    // 根据用户角色分页查询用户已购买的试卷
    Page<UserExam> findByUserIdAndExamVisibleRolesId(Long userId, Long roleId, Pageable pageable);

    // 根据用户角色和试卷名称分页查询用户已购买的试卷
    Page<UserExam> findByUserIdAndExamTitleContainingAndExamVisibleRolesId(Long userId, String keyword, Long roleId, Pageable pageable);

    boolean existsByUserIdAndExamId(Long userId, Long examId);

}
