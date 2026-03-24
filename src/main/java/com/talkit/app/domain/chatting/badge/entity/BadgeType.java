package com.talkit.app.domain.chatting.badge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeType {
    //키워드 미션관련
    MISSION_KEYWORD("키워드 미션 성공 횟수 기반"),

    //상대방 피드백 관련
    FEEDBACK_POSITIVE ("상대방의 긍정적 피드백 기반"),
    FEEDBACK_NEGATIVE ("상대방의 부정적 피드백 기반"),
    FEEDBACK_SPECIAL ("특정 성향 피드백 기반"),

    //채팅패턴 관련
    CHAT_PATTERN("채팅 길이, 시간대기반"),

    //출석 관련
    ATTENDANCE_STREAK("연속 접속일수 기반");

    private final String description;
}
