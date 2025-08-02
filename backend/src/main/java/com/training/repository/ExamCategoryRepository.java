package com.training.repository;

import com.training.entity.ExamCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamCategoryRepository extends JpaRepository<ExamCategory, Long> {

    Optional<ExamCategory> findByCode(String code);

    List<ExamCategory> findByIsActiveTrueOrderBySortOrderAsc();

    @Query("SELECT ec FROM ExamCategory ec WHERE ec.isActive = true AND ec.parentCode IS NULL ORDER BY ec.sortOrder")
    List<ExamCategory> findActiveParentCategories();

    @Query("SELECT ec FROM ExamCategory ec WHERE ec.isActive = true AND ec.parentCode = ?1 ORDER BY ec.sortOrder")
    List<ExamCategory> findActiveSubCategories(String parentCode);
}
