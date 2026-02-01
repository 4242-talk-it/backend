package com.talkit.app.domain.chatting.aiChat.controller;

import com.talkit.app.domain.chatting.aiChat.dto.AiChatMessageRequest;
import com.talkit.app.domain.chatting.aiChat.dto.AiChatRoomCreateRequest;
import com.talkit.app.domain.chatting.aiChat.dto.AiChatRoomResponseDto;
import com.talkit.app.domain.chatting.aiChat.service.AiChatService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "AI 채팅 관련 API", description = "AI 채팅 관련 API")
@RequestMapping("/api/ai-chat")
@RestController
public class AiChatController {

    private final AiChatService aiChatService;

    @Operation(summary = "AI 채팅방 생성")
    @AuthenticatedUser
    @PostMapping("/room")
    public ResponseDto<AiChatRoomResponseDto> createAiChatRoom(@RequestBody AiChatRoomCreateRequest request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(aiChatService.createChatRoom(request, userId), "AI 채팅방이 생성되었습니다.");
    }

    @Operation(summary = "AI에게 메시지 전송 및 답변 받음")
    @AuthenticatedUser
    @PostMapping("/message/{chatRoomId}")
    public ResponseDto<String> getGeminiReactions(@PathVariable Long chatRoomId,
                                             @RequestBody AiChatMessageRequest request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        String aiAnswer = aiChatService.getGeminiReactions(chatRoomId, request.message(), userId);
        return ResponseDto.of(aiAnswer, "AI 답변을 성공적으로 가져왔습니다.");
    }

    @Operation(summary = "내 대화 기록 리스트 조회")
    @AuthenticatedUser
    @GetMapping("/my-rooms")
    public ResponseDto<List<AiChatRoomResponseDto>> getMyChatRooms() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(aiChatService.getMyAiChatRoom(userId), "대화 기록을 조회했습니다.");
    }
}
