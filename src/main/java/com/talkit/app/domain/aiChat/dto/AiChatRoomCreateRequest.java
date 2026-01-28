package com.talkit.app.domain.aiChat.dto;

import lombok.Builder;

@Builder
public record AiChatRoomCreateRequest(Long situationId) {
}
