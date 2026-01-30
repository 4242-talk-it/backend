package com.talkit.app.domain.chatting.aiChat.dto;

import lombok.Builder;

@Builder
public record AiChatRoomCreateRequest(Long situationId) {
}
