package com.talkit.app.domain.community.controller;

import com.talkit.app.domain.community.dto.CommunityCommentRequestDto;
import com.talkit.app.domain.community.dto.CommunityCommentResponseDto;
import com.talkit.app.domain.community.service.CommunityCommentService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import com.talkit.app.global.page.dto.PageRequestVO;
import com.talkit.app.global.page.dto.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 댓글 API", description = "커뮤니티 게시물 '댓글' 관련 API")
@RequestMapping("/api/community/{id}/comments")
@RestController
public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;

    @Operation(summary = "댓글 작성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<CommunityCommentResponseDto> createComment(
            @PathVariable("id") Long communityId,
            @RequestBody @Valid CommunityCommentRequestDto requestDto) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(
                communityCommentService.createComment(communityId, requestDto, userId),
                "댓글이 등록되었습니다."
        );
    }

    @Operation(summary = "댓글 목록 조회")
    @AuthenticatedUser
    @GetMapping
    public ResponseDto<PageResponseDto<CommunityCommentResponseDto>> getComments(
            @PathVariable("id") Long communityId,
            @ModelAttribute PageRequestVO pageRequestVO) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(
                communityCommentService.getComments(communityId, userId, pageRequestVO.toPageable()),
                "댓글 목록을 성공적으로 가져왔습니다."
        );
    }

    @Operation(summary = "댓글 삭제")
    @AuthenticatedUser
    @DeleteMapping("/{commentId}") // /api/community/{id}/comments/{commentId}
    public ResponseDto<Void> deleteComment(
            @PathVariable("id") Long communityId,
            @PathVariable("commentId") Long commentId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        communityCommentService.deleteComment(commentId, userId);

        return ResponseDto.of(null, "댓글이 삭제되었습니다.");
    }

}
