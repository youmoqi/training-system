package com.training.repository;

import com.training.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 14798
 */
@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {
}
