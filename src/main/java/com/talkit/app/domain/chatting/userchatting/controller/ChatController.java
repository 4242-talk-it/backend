package com.talkit.app.domain.chatting.userchatting.controller;

import com.talkit.app.domain.chatting.userchatting.dto.ChatMessageRequest;
import com.talkit.app.domain.chatting.userchatting.dto.ChatMessageResponse;
import com.talkit.app.domain.chatting.userchatting.dto.ChatRoomRequest;
import com.talkit.app.domain.chatting.userchatting.dto.ChatRoomResponse;
import com.talkit.app.domain.chatting.userchatting.entity.ChatMessage;
import com.talkit.app.domain.chatting.userchatting.entity.ChatRoom;
import com.talkit.app.domain.chatting.userchatting.service.ChatService;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/match")
    public ResponseEntity<ChatRoomResponse> match(@RequestBody ChatRoomRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ChatRoom room = chatService.matchOrCreateRoom(request.getTopic(), userDetails.getId());

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

        ChatMessage message = chatService.sendMessage(roomId, userDetails.getId(), request.getMessage());

        return ResponseEntity.ok(ChatMessageResponse.from(message));
    }

    //메세지 받기
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessage> history = chatService.getChatHistory(roomId);

        List<ChatMessageResponse> response = history.stream()
                .map(ChatMessageResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
