package com.talkit.app.domain.community.dto;

public record CommunitySearchConditionDto(
        String searchType, // 드롭다운 선택값
        String keyword     // 입력한 검색어
) {
}
