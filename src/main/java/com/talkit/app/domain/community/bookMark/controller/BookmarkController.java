package com.talkit.app.domain.community.bookmark.controller;

import com.talkit.app.domain.community.bookmark.dto.BookmarkResponseDto;
import com.talkit.app.domain.community.bookmark.service.BookmarkService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 북마크 API", description = "커뮤니티 '즐겨찾기/북마크' 관련 API")
@RestController
@RequestMapping("/api/community")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "북마크 상태 변경(Toggle)", description = "북마크를 등록하거나 취소합니다.")
    @AuthenticatedUser
    @PostMapping("/{id}/bookmark/toggle")
    public ResponseDto<Boolean> toggleBookMark(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(bookmarkService.toggleBookMark(id, userId));
    }

    @Operation(summary = "게시물 북마크 상태 조회")
    @AuthenticatedUser
    @GetMapping("/{id}/bookmark/status")
    public ResponseDto<Boolean> getBookMarkStatus(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(bookmarkService.isBookmarked(id, userId));
    }

    @Operation(summary = "내 북마크 목록 조회")
    @AuthenticatedUser
    @GetMapping("/bookmark/my-list")
    public ResponseDto<List<BookmarkResponseDto>> getMyBookMarkList() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(bookmarkService.getMyBookMarks(userId));
    }
}
