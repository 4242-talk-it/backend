package com.talkit.app.domain.chatting.aiSituation.service;

import com.talkit.app.domain.chatting.aiSituation.dto.AiSituationResponseDto;
import com.talkit.app.domain.chatting.aiSituation.repository.AiSituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AiSituationService {

    private final AiSituationRepository aiSituationRepository;

    public List<AiSituationResponseDto> findAll() {
        return aiSituationRepository.findAll().stream()
                .map(AiSituationResponseDto::of)
                .toList();
    }

}
