package com.training.repository;

import com.training.entity.RegistrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationConfigRepository extends JpaRepository<RegistrationConfig, Long> {
} 