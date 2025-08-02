package com.training.repository;

import com.training.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByIsOnlineTrue();
    List<QuestionBank> findByIsOnlineTrueAndVisibleRolesContaining(String role);
    
    // 分页查询方法
    Page<QuestionBank> findByTitleContainingOrDescriptionContaining(
        String title, String description, Pageable pageable);
    
    // 支持角色过滤的分页查询方法
    Page<QuestionBank> findByIsOnlineTrue(Pageable pageable);
    Page<QuestionBank> findByIsOnlineTrueAndVisibleRolesContaining(String role, Pageable pageable);
    Page<QuestionBank> findByIsOnlineTrueAndVisibleRolesContainingAndTitleContainingOrDescriptionContaining(
        String role, String title, String description, Pageable pageable);
} 