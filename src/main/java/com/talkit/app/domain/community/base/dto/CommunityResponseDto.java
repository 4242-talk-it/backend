package com.talkit.app.domain.community.base.dto;

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
    int viewCount,
    int likeCount,
    boolean isLiked,
    int commentCount,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static CommunityResponseDto of(Community community, Long userId) {
        return CommunityResponseDto.of(community, userId, false, 0, 0);
    }

    public static CommunityResponseDto of(
        Community community, Long userId, boolean isLiked, int likeCount, int commentCount
    ) {
        return CommunityResponseDto.builder()
            .id(community.getId())
            .title(community.getTitle())
            .content(community.getContent())
            .category(community.getCategory())
            .nickname(community.getUser().getNickname())
            .tags(community.getTags())
            .viewCount(community.getViewCount())
            .likeCount(likeCount)
            .isLiked(isLiked)
            .commentCount(commentCount)
            .isOwnedByUser(community.getUser().getId().equals(userId))
            .createdAt(community.getCreatedAt())
            .updatedAt(community.getUpdatedAt())
            .build();
    }
}
