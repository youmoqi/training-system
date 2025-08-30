package com.training.repository;

import com.training.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * @author 14798
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByIdCard(String idCard);
    boolean existsByPhone(String phone);

    // 分页查询方法
    Page<User> findByUsernameContainingOrRealNameContainingOrPhoneContaining(
        String username, String realName, String phone, Pageable pageable);
        
    // 按角色ID查询用户
    List<User> findByRoleId(Long roleId);
}
