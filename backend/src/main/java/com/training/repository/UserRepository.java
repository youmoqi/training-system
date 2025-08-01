package com.training.repository;

import com.training.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByIdCard(String idCard);
    Optional<User> findByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByIdCard(String idCard);
    boolean existsByPhone(String phone);
    
    // 分页查询方法
    Page<User> findByUsernameContainingOrRealNameContainingOrPhoneContaining(
        String username, String realName, String phone, Pageable pageable);
} 