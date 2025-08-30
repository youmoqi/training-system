package com.training.repository;

import com.training.entity.Exam.QuestionBank;
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

    Page<QuestionBank> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);

    Page<QuestionBank> findByIsOnlineTrue(Pageable pageable);

    List<QuestionBank> findByIsOnlineTrueAndVisibleRolesId(Long roleId);

    Page<QuestionBank> findByIsOnlineTrueAndVisibleRolesId(Long roleId, Pageable pageable);
}
