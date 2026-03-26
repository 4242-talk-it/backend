package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.badge.entity.MissionKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MissionKeywordRepository extends JpaRepository<MissionKeyword, Long> {
    @Query(value = "SELECT * FROM mission_keyword ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<MissionKeyword> findRandomKeyword();
}
