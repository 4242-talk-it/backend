package com.talkit.app.domain.chatting.userChat.dto;

import com.talkit.app.domain.chatting.userChat.entity.EmotionType;
import com.talkit.app.domain.chatting.userChat.entity.SpecialTagType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDetailResponse {
    private Long myUserId;
    private String myMissionKeyword; //본인의 keyword
    private String opponentGuessedKeyword; //상대가 제출한 guessedKeyword
    private EmotionType opponentEmotion; // 상대가 제출한 emotionType
    private long durationMinutes; //총 대화 시간
    private SpecialTagType specialTag1;
    private SpecialTagType specialTag2;
    private String comment;
}
