package com.talkit.app.domain.community.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record CommunityRequestDto(
    @Size(max = 100, message = "게시물 제목은 최대 100자까지 입력할 수 있습니다.")
    String title,
    String content,
    String category,
    List<String> tags
) {
}
