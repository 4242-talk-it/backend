package com.talkit.app.domain.chatting.aiChat.dto;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import com.talkit.app.domain.chatting.aiChat.entity.AiChatRoom;
import com.talkit.app.domain.chatting.aiChat.entity.AiChatMessage;


@Builder
public record AiChatRoomResponseDto(
        Long chatRoomId,
        String situationTitle,
        String situationDescription,
        List<ChatMessageDetail> messages
) {
    // 방 엔티티를 DTO로 변환
    public static AiChatRoomResponseDto of(AiChatRoom aiChatRoom) {
        return AiChatRoomResponseDto.builder()
                .chatRoomId(aiChatRoom.getId())
                .situationTitle(aiChatRoom.getAiSituation().getTitle())
                .situationDescription(aiChatRoom.getAiSituation().getDescription())
                .messages(aiChatRoom.getMessages() == null ? List.of() :
                        aiChatRoom.getMessages().stream()
                                .map(ChatMessageDetail::from)
                                .toList())
                .build();
    }

    @Builder
    public record ChatMessageDetail(
            String type,           // AI, USER, SYSTEM, NOTICE
            String content,
            LocalDateTime createdAt
    ) {
        public static ChatMessageDetail from(AiChatMessage message) {
            return ChatMessageDetail.builder()
                    .type(message.getType().name())
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .build();
        }
    }
}