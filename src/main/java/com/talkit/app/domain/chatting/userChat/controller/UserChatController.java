package com.talkit.app.domain.chatting.userChat.controller;

import com.talkit.app.domain.chatting.userChat.dto.ChatMessageRequest;
import com.talkit.app.domain.chatting.userChat.dto.ChatMessageResponse;
import com.talkit.app.domain.chatting.userChat.dto.ChatRoomRequest;
import com.talkit.app.domain.chatting.userChat.dto.ChatRoomResponse;
import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.service.UserChatService;
import com.talkit.app.domain.user.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/user-chat")
public class UserChatController {
    private final UserChatService userChatService;

    @PostMapping("/match")
    public ResponseEntity<ChatRoomResponse> match(@RequestBody ChatRoomRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ChatRoom room = userChatService.matchOrCreateRoom(request.getTopic(), userDetails.getId());

        // 응답 DTO 변환
        ChatRoomResponse response = ChatRoomResponse.builder()
                .roomId(room.getRoomId())
                .topic(room.getTopic())
                .isMatched(room.getUser2() != null) // 상대방(user2)이 있으면 매칭 성공
                .build();

        return ResponseEntity.ok(response);
    }

    //메세지 전송
    @PostMapping("/room/{roomId}/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(@PathVariable Long roomId,
                                                   @RequestBody ChatMessageRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ChatMessage message = userChatService.sendMessage(roomId, userDetails.getId(), request.getMessage());

        return ResponseEntity.ok(ChatMessageResponse.from(message));
    }

    //메세지 받기
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessage> history = userChatService.getChatHistory(roomId);

        List<ChatMessageResponse> response = history.stream()
                .map(ChatMessageResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
