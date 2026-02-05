package com.talkit.app.domain.community.dto;

import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.entity.CommunityLike;
import java.util.List;
import lombok.Builder;

@Builder
public record CommunityListResponseDto(
    String id,
    String title,
    String content,
    String category,
    String nickname,
    List<String> tags,
    boolean isLiked,
    int likeCount,
    int commentCount,
    int viewCount
) {

    public static CommunityListResponseDto from(Community community) {
        return CommunityListResponseDto.builder()
            .id(community.getId().toString())
            .title(community.getTitle())
            .content(getPreviewContent(community.getContent(), 150))
            .nickname(community.getUser().getNickname())
            .category(community.getCategory())
            .tags(community.getTags())
            .viewCount(community.getViewCount())
            .build();
    }

    public static CommunityListResponseDto of(
        Community community,
        int likeCount,
        int commentCount,
        List<CommunityLike> communityLikesByUserId) {

        CommunityListResponseDto base = from(community);

        boolean isLiked = communityLikesByUserId.stream()
            .anyMatch(like -> community.getId().equals(like.getCommunity().getId()));

        return CommunityListResponseDto.builder()
            .id(base.id())
            .title(base.title())
            .content(base.content())
            .nickname(base.nickname())
            .category(base.category())
            .tags(base.tags())
            .viewCount(base.viewCount())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(isLiked)
            .build();
    }

    private static String getPreviewContent(String content, int maxLength) {
        if (content == null || content.isBlank()) {
            return "";
        }
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }
}
