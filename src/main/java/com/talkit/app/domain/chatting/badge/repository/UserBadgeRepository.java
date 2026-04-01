package com.talkit.app.domain.chatting.badge.repository;

import com.talkit.app.domain.chatting.badge.entity.UserBadge;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    List<UserBadge> findAllByUser(User user);
}
