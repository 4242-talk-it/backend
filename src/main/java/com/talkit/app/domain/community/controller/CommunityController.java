package com.talkit.app.domain.community.controller;

import com.talkit.app.domain.community.dto.CommunityRequestDto;
import com.talkit.app.domain.community.service.CommunityService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "커뮤니티 '게시물' 관련 API")
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시물 작성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<CommunityRequestDto> create(
        @ModelAttribute @Valid CommunityRequestDto requestDto
    ) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.createCommunity(requestDto, userId), "게시물이 성공적으로 생성되었습니다.");
    }

}
