package com.talkit.app.domain.community.dto;

import java.time.LocalDateTime;

public record CommunityResponseDto(
    Long id,
    String content,
    String category,
    String nickname,
    int likeCount,
    int commentCount,
    boolean isLiked,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
