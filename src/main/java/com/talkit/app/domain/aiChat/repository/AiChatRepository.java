package com.talkit.app.domain.aiChat.repository;

import com.talkit.app.domain.aiChat.entity.AiChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiChatRepository extends JpaRepository<AiChatRoom, Long> {
}
