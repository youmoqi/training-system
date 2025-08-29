package com.training.repository;

import com.training.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 14798
 */
@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByIsOnlineTrue();

    // 分页查询方法
    Page<QuestionBank> findByTitleContainingOrDescriptionContaining(
            String title, String description, Pageable pageable);

    // 支持角色过滤的分页查询方法
    Page<QuestionBank> findByIsOnlineTrue(Pageable pageable);

    // 查询在线题库，按 roleId 过滤
    List<QuestionBank> findByIsOnlineTrueAndVisibleRolesId(Long roleId);

    // 分页版本
    Page<QuestionBank> findByIsOnlineTrueAndVisibleRolesId(Long roleId, Pageable pageable);

}
