package com.talkit.app.domain.chatting.userchatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequest {
    private String topic; //사용자가 고른 주제
}
