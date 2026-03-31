package com.talkit.app.domain.user.repository;

import com.talkit.app.domain.user.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    Optional<UserActivity> findByUserId(Long userId);
}
