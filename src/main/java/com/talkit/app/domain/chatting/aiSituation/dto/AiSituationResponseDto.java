package com.talkit.app.domain.chatting.aiSituation.dto;

import com.talkit.app.domain.chatting.aiSituation.entity.AiSituation;
import lombok.Builder;

@Builder
public record AiSituationResponseDto(
        Long id,
        String icon,
        String title,
        String description,
        String status,
        String statusColor
) {
    public static AiSituationResponseDto of(AiSituation situation) {
        return AiSituationResponseDto.builder()
                .id(situation.getId())
                .icon(situation.getIcon())
                .title(situation.getTitle())
                .description(situation.getDescription())
                .status(situation.getStatus())
                .statusColor(situation.getStatusColor())
                .build();
    }
}