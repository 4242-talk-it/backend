package com.talkit.app.domain.chatting.aiChat.controller;

import com.talkit.app.domain.chatting.aiChat.dto.AiChatMessageRequest;
import com.talkit.app.domain.chatting.aiChat.service.AiChatService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "AI 채팅 관련 API", description = "AI 채팅 관련 API")
@RequestMapping("/api/ai-chat")
@RestController
public class AiChatController {

    private final AiChatService aiChatService;

    @Operation(summary = "제미나이 AI 채팅 테스트")
    @AuthenticatedUser
    @PostMapping("/test")
    public ResponseDto<String> testChat(@RequestBody AiChatMessageRequest request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        String aiAnswer = aiChatService.getGeminiContents(request.message(), userId);
        return ResponseDto.of(aiAnswer, "AI 답변을 성공적으로 가져왔습니다.");
    }
}
