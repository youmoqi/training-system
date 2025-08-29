package com.training.repository;

import com.training.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 14798
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
