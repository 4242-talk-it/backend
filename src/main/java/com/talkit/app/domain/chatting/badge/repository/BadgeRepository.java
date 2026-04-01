package com.talkit.app.domain.chatting.badge.repository;

import com.talkit.app.domain.chatting.badge.entity.Badge;
import com.talkit.app.domain.chatting.badge.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findByBadgeTypeAndConditionKey(BadgeType badgeType, String conditionKey);
    List<Badge> findAllByBadgeType(BadgeType badgeType);
}
