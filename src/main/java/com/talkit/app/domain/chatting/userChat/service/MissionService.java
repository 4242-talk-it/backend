package com.talkit.app.domain.chatting.userChat.service;

import com.talkit.app.domain.chatting.badge.entity.MissionKeyword;
import com.talkit.app.domain.chatting.userChat.repository.MissionKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionKeywordRepository missionKeywordRepository;

    public String getRandomMission() {
        return missionKeywordRepository.findRandomKeyword()
                .map(MissionKeyword::getKeyword)
                .orElse("기본");
    }

    public MissionKeyword getRandomMissionEntity() {
        return missionKeywordRepository.findRandomKeyword()
                .orElseThrow(() -> new RuntimeException("DB에 미션 키워드가 없습니다."));
    }
}
