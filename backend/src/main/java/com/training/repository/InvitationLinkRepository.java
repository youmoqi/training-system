package com.training.repository;

import com.training.entity.InvitationLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationLinkRepository extends JpaRepository<InvitationLink, Long> {
    Optional<InvitationLink> findByLinkCode(String linkCode);
    
    // 分页查询方法
    Page<InvitationLink> findByTitleContainingOrDescriptionContaining(
        String title, String description, Pageable pageable);
} 