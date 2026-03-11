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
    private int maxTurns;
    private String missionKeyword;

    public static ChatRoomResponse from(ChatRoom room, Long userId) {

        String assignedMission = null;

        if (room.getUser1() != null && room.getUser1().getId().equals(userId)) {
            // 내가 user1일 때
            assignedMission = (room.getUser1Mission() != null) ? room.getUser1Mission().getKeyword() : null;
        } else if (room.getUser2() != null && room.getUser2().getId().equals(userId)) {
            // 내가 user2일 때
            assignedMission = (room.getUser2Mission() != null) ? room.getUser2Mission().getKeyword() : null;
        }

        return ChatRoomResponse.builder()
                .roomId(room.getRoomId())
                .topic(room.getTopic())
                .isMatched(room.getUser2() != null)
                .userId(userId)
                .maxTurns(room.getMaxTurns())
                .missionKeyword(assignedMission)
                .build();
    }
}
