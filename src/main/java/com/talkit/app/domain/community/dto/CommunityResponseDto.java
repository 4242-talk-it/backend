package com.talkit.app.domain.community.dto;

import com.talkit.app.domain.community.entity.Community;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommunityResponseDto(
    Long id,
    String title,
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
    public static CommunityResponseDto of(Community community) {
        return CommunityResponseDto.builder()
            .id(community.getId())
            .title(community.getTitle())
            .content(community.getContent())
            //.nickname(community.getUser().getNickname())
            .createdAt(community.getCreatedAt())
            .updatedAt(community.getUpdatedAt())
            .build();
    }
}
