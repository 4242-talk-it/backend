package com.talkit.app.domain.chatting.userChat.dto;


import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.entity.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatHistoryResponse {
    private Long RoomId;
    private String topic;
    private EmotionType emotion;
    private LocalDateTime endedAt;
    private long messageCount;

    public static ChatHistoryResponse of(ChatRoom room, EmotionType emotion, long messageCount) {
        return new ChatHistoryResponse(
                room.getRoomId(),
                room.getTopic(),
                emotion,
                room.getEndedAt(),
                messageCount
        );
    }
}
