package com.talkit.app.domain.community.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 댓글 API", description = "커뮤니티 게시물 '댓글' 관련 API")
@RequestMapping("/api/community/{id}/comment")
@RestController
public class CommunityCommentController {

}
