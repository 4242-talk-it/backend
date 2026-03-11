package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    long countByUserIdAndIsSuccessTrue(Long userId);
}
