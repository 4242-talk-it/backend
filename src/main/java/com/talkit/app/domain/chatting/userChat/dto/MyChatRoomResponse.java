package com.talkit.app.domain.chatting.userChat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyChatRoomResponse {
    private Long roomId;
    private String topic;
    private String lastMessage;
    private String lastTime;
    private Long userId;
}
