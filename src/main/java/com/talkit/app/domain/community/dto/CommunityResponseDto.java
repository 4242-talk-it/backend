package com.talkit.app.domain.community.dto;

import com.talkit.app.domain.community.entity.Community;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record CommunityResponseDto(
    Long id,
    String title,
    String content,
    String category,
    String nickname,
    List<String> tags,
    int likeCount,
    int commentCount,
    boolean isLiked,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CommunityResponseDto of(Community community, Long userId) {
        return CommunityResponseDto.builder()
            .id(community.getId())
            .title(community.getTitle())
            .content(community.getContent())
            .category(community.getCategory())
            .nickname(community.getUser().getNickname())
            .tags(community.getTags())
            .isOwnedByUser(community.getUser().getId().equals(userId))
            .createdAt(community.getCreatedAt())
            .updatedAt(community.getUpdatedAt())
            .build();
    }
}
