package com.talkit.app.domain.chatting.aiSituation.repository;

import com.talkit.app.domain.chatting.aiSituation.entity.AiSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiSituationRepository extends JpaRepository<AiSituation, Long> {
}
