package com.talkit.app.domain.community.base.controller;

import com.talkit.app.domain.community.base.dto.CommunityListResponseDto;
import com.talkit.app.domain.community.base.dto.CommunityRequestDto;
import com.talkit.app.domain.community.base.dto.CommunityResponseDto;
import com.talkit.app.domain.community.base.dto.CommunitySearchConditionDto;
import com.talkit.app.domain.community.base.service.CommunityService;
import com.talkit.app.domain.community.base.dto.CommunityStatsResponseDto;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import com.talkit.app.global.dto.PageRequestVO;
import com.talkit.app.global.dto.PageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "커뮤니티 '게시물' 관련 API")
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시물 단일 조회")
    @GetMapping("/{id}")
    public ResponseDto<CommunityResponseDto> getCommunityById(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.getCommunityById(id, userId));
    }

    @Operation(summary = "게시물 목록 조회")
    @GetMapping("/list")
    public ResponseDto<PageResponseDto<CommunityListResponseDto>> list(
            CommunitySearchConditionDto condition, PageRequestVO pageRequestVO
    ) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.pagesByCommunity(condition, userId, pageRequestVO));
    }

    @Operation(summary = "수정 페이지용 게시물 조회")
    @AuthenticatedUser
    @GetMapping("/edit/{id}")
    public ResponseDto<CommunityResponseDto> getPostForEdit(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.getPostForEdit(id, userId), "게시물을 성공적으로 가져왔습니다.");
    }

    @Operation(summary = "게시물 작성")
    @AuthenticatedUser
    @PostMapping("/create")
    public ResponseDto<CommunityResponseDto> communityCreate(@RequestBody @Valid CommunityRequestDto requestDto) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.createCommunity(requestDto, userId), "게시물이 성공적으로 생성되었습니다.");
    }

    @Operation(summary = "게시물 수정")
    @AuthenticatedUser
    @PutMapping("/{id}")
    public ResponseDto<CommunityResponseDto> update(
        @RequestBody @Valid CommunityRequestDto requestDto,
        @PathVariable("id") Long communityId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.updateCommunity(communityId, requestDto, userId), "게시물이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "게시물 삭제")
    @AuthenticatedUser // 인증 체크 어노테이션
    @DeleteMapping("/{id}")
    public ResponseDto<Void> delete(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        communityService.deleteCommunity(id, userId);
        return ResponseDto.of(null, "게시물이 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "커뮤니티 통계 조회", description = "메인 상단 Hero Section에 표시될 게시글, 댓글, 활동멤버 수를 조회합니다.")
    @GetMapping("/stats")
    public ResponseDto<CommunityStatsResponseDto> getCommunityStats() {
        return ResponseDto.of(communityService.getCommunityStats());
    }

}
