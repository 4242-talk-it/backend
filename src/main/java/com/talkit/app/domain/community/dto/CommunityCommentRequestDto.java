package com.talkit.app.domain.community.dto;

import jakarta.validation.constraints.NotBlank;

public record CommunityCommentRequestDto(@NotBlank(message = "댓글 내용을 입력해주세요.")
                                        String content) {

}
