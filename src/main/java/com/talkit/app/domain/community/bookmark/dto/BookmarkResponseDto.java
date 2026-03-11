package com.talkit.app.domain.community.bookmark.dto;

import com.talkit.app.domain.community.entity.Bookmark;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookmarkResponseDto {

    private final Long bookmarkId;
    private final Long communityId;
    private final String title;
    private final String category;
    private final LocalDateTime createdAt;

    public static BookmarkResponseDto of(Bookmark bookMark) {
        return BookmarkResponseDto.builder()
                .bookmarkId(bookMark.getId())
                .communityId(bookMark.getCommunity().getId())
                .title(bookMark.getCommunity().getTitle())
                .category(bookMark.getCommunity().getCategory())
                .createdAt(bookMark.getCreatedAt())
                .build();
    }
}
