package com.talkit.app.domain.community.dto;

import lombok.Builder;
import lombok.Getter; // 추가

@Getter
@Builder
public class CommunityLikeResponseDto {
    private final Long communityId;

    public static CommunityLikeResponseDto of(Long communityId) {
        return CommunityLikeResponseDto.builder()
            .communityId(communityId)
            .build();
    }
}
