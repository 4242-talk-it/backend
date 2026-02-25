package com.talkit.app.domain.community.like.controller;

import com.talkit.app.domain.community.like.dto.CommunityLikeResponseDto;
import com.talkit.app.domain.community.like.service.CommunityLikeService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 좋아요 API", description = "커뮤니티 게시물 좋아요")
@RequestMapping("/api/community/{id}/like")
@RestController
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @Operation(summary = "게시물 좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/status")
    public ResponseDto<Boolean> getCommunityLikeStatus(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityLikeService.isLiked(id, userId));
    }

    @Operation(summary = "게시물 좋아요 개수 조회")
    @GetMapping("/count")
    public ResponseDto<Long> getCommunityLikeCount(@PathVariable("id") Long id) {
        long likeCount = communityLikeService.getLikeCount(id);
        return ResponseDto.of(likeCount);
    }

    @Operation(summary = "좋아요 상태 변경(Toggle)")
    @AuthenticatedUser
    @PostMapping("/toggle")
    public ResponseDto<CommunityLikeResponseDto> toggleCommunityLike(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityLikeService.toggleLike(id, userId));
    }


}
