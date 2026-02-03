package com.talkit.app.domain.chatting.userChat.dto;

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
}
