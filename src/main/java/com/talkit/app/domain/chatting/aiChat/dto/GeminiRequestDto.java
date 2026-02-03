package com.talkit.app.domain.chatting.aiChat.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record GeminiRequestDto(List<Content> contents) {

    public record Content(List<Part> parts) {}

    public record Part(String text) {}

    // 편의 메서드: 이 메서드를 호출하면 위 봉투들을 순서대로 조립해줍니다.
    public static GeminiRequestDto fromText(String text) {
        Part part = new Part(text);
        Content content = new Content(List.of(part));
        return new GeminiRequestDto(List.of(content));
    }
}
