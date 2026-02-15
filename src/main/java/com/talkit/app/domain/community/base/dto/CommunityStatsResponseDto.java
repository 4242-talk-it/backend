package com.talkit.app.domain.community.base.dto;

import lombok.Builder;

@Builder
public record CommunityStatsResponseDto(
        long totalPostCount,     // 전체 게시글 수
        long totalCommentCount,  // 전체 댓글 수
        long activeUserCount     // 활동 멤버 수
) {
    public static CommunityStatsResponseDto of(long totalPostCount, long totalCommentCount, long activeUserCount) {
        return CommunityStatsResponseDto.builder()
                .totalPostCount(totalPostCount)
                .totalCommentCount(totalCommentCount)
                .activeUserCount(activeUserCount)
                .build();
    }
}
