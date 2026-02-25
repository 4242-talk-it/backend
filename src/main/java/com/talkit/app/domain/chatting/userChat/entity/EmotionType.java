package com.talkit.app.domain.chatting.userChat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum EmotionType {
    GREAT("정말 즐거웠어요", "🥰", 1.5),
    GOOD("편안했어요", "☺️", 1.0),
    NORMAL("평범했어요", "😐", 0.0),
    BAD("아쉬웠어요", "😔", -1.0),
    TERRIBLE("불편했어요", "😣", -1.5);

    private final String description;
    private final String emoji;
    private final double temperature;

    public static EmotionType fromDescription(String description) {
        return Arrays.stream(EmotionType.values())
                .filter(e -> e.getDescription().equals(description))
                .findFirst()
                .orElse(NORMAL); // 기본값 설정
    }

}
