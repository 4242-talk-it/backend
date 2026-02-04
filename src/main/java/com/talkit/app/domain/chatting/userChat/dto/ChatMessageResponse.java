package com.talkit.app.domain.chatting.userChat.dto;

import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long cmid;
    private String message;
    private Long senderId;
    private String senderNickname;
    private LocalDateTime timestamp;

    public static ChatMessageResponse from(ChatMessage message) {
        return ChatMessageResponse.builder()
                .cmid(message.getCmid())
                .message(message.getMessage())
                .senderId(message.getSender().getId())
                .senderNickname(message.getSender().getNickname())
                .timestamp(message.getTimestamp())
                .build();
    }
}
