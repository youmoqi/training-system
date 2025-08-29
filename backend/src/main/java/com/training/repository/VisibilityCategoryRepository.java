package com.training.repository;

import com.training.entity.VisibilityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VisibilityCategoryRepository extends JpaRepository<VisibilityCategory, Long> {
    Optional<VisibilityCategory> findByCode(String code);
}