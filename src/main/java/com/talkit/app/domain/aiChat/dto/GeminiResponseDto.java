package com.talkit.app.domain.aiChat.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GeminiResponseDto(List<Candidate> candidates) {

    public record Candidate(
            Content content,
            String finishReason
    ) {}

    public record Content(
            List<Part> parts,
            String role
    ) {}

    public record Part(
            String text
    ) {}

    // 응답 결과물에서 AI의 답변 텍스트만 추출
    public String getAnswer() {
        if (candidates == null || candidates.isEmpty()) return "";

        return candidates.get(0).content().parts().get(0).text();
    }
}
