package com.talkit.app.domain.chatting.badge.repository;

import com.talkit.app.domain.chatting.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
