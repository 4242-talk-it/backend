package com.talkit.app.domain.chatting.badge.repository;

import com.talkit.app.domain.chatting.badge.entity.UserBadge;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user.id = :userId")
    List<UserBadge> findAllByUserIdWithBadge(@Param("userId") Long userId);

    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    List<UserBadge> findAllByUserId(Long user);
}
