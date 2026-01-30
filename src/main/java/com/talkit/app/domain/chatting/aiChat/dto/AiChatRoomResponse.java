package com.talkit.app.domain.chatting.aiChat.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AiChatRoomResponse(
        Long chatRoomId,           // 생성된/조회된 방 번호
        String situationTitle,     // 상황 제목 (예: 첫 데이트)
        String situationDescription, // 상황 설명
        List<ChatMessageDetail> messages // 이전 대화 목록
) {
    @Builder
    public record ChatMessageDetail(
            String role,           // "USER" 또는 "ASSISTANT" (Gemini 방식)
            String content,        // 메시지 내용
            LocalDateTime createdAt // 보낸 시간
    ) {}
}
