package com.talkit.app.domain.chatting.aiChat.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record GeminiRequestDto(List<Content> contents) {

    public record Content(List<Part> parts) {}

    public record Part(String text) {}

    public static GeminiRequestDto fromText(String text) {
        Part part = new Part(text);
        Content content = new Content(List.of(part));
        return new GeminiRequestDto(List.of(content));
    }
}
