package com.training.repository;

import com.training.entity.ExamPaperVisibleRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperVisibleRoleRepository extends JpaRepository<ExamPaperVisibleRole, Long> {

}
