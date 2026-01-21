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

        // 1. 기초 정보 생성
        CommunityListResponseDto base = from(community);

        // 2. 추가 정보(좋아요 여부) 계산
        boolean isLiked = communityLikesByUserId.stream()
            .anyMatch(like -> community.getId().equals(like.getCommunity().getId()));

        // 3. 전체 조립
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
            .isLiked(isLiked) // 미리 계산한 변수 사용
            .build();
    }

    private static String getPreviewContent(String content, int maxLength) {
        // null 체크 및 빈 문자열 처리 추가
        if (content == null || content.isBlank()) {
            return "";
        }
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "..."; // 줄임표(...)를 넣어주면 UX에 좋습니다.
        }
        return content;
    }
}
