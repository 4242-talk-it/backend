package com.talkit.app.domain.chatting.userChat.dto;

import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    private Long roomId;
    private String topic;
    private String partnerNickname; // 상대방 이름 (익명 처리용)
    private boolean isMatched;
    private Long userId;

    public static ChatRoomResponse from(ChatRoom room, Long userId) {
        return ChatRoomResponse.builder()
                .roomId(room.getRoomId())
                .topic(room.getTopic())
                .isMatched(room.getUser2() != null)
                .userId(userId)
                .build();
    }
}
