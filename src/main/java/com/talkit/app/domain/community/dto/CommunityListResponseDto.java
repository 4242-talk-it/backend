package com.talkit.app.domain.community.dto;

import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.like.entity.CommunityLike;
import java.util.List;
import lombok.Builder;

@Builder
public record CommunityListResponseDto(
    String id,
    String title,
    String content,
    String category,
    String nickname,
    int likeCount,
    int commentCount,
    boolean isLiked
) {

    public static CommunityListResponseDto from(Community community) {

        return CommunityListResponseDto.builder()
            .id(community.getId().toString())
            .title(community.getTitle())
            .content(getPreviewContent(community.getContent(), 250))
            .nickname(community.getUser().getNickname())
            .category(community.getCategory())
            .build();
    }

    public static CommunityListResponseDto of(Community community,
        int likeCount,
        int commentCount,
        List<CommunityLike> communityLikesByUserId) {

        return CommunityListResponseDto.builder()
            .id(community.getId().toString())
            .title(community.getTitle())
            .category(community.getCategory())
            .likeCount(likeCount)
            .nickname(community.getUser().getNickname())
            .commentCount(commentCount)
            .isLiked(communityLikesByUserId.stream()
                .anyMatch(communityLike -> community.getId().equals(communityLike.getCommunity().getId())))
            .content(getPreviewContent(community.getContent(), 150))
            .build();
    }

    private static String getPreviewContent(String content, int maxLength) {
        if (content.length() > maxLength) {
            return content.substring(0, maxLength);
        }
        return content;
    }
}

