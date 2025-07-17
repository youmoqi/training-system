package com.training.repository;

import com.training.entity.InvitationLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationLinkRepository extends JpaRepository<InvitationLink, Long> {
    Optional<InvitationLink> findByLinkCode(String linkCode);
} 