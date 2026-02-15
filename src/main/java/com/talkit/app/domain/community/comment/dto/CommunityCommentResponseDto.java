package com.talkit.app.domain.community.comment.dto;

import com.talkit.app.domain.community.entity.Comment;

import java.time.LocalDateTime;

public record CommunityCommentResponseDto(
        Long id,
        String content,
        String nickname,
        LocalDateTime createdAt,
        boolean isOwnedByUser,  // 본인 댓글 여부
        int likeCount          // 댓글 좋아요
) {
    public static CommunityCommentResponseDto of(Comment comment, Long currentUserId) {
        return new CommunityCommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt(),
                comment.getUser().getId().equals(currentUserId),
                comment.getCommentLikeList() != null ? comment.getCommentLikeList().size() : 0
        );
    }


}