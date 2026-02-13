package com.talkit.app.domain.chatting.userChat.controller;

import com.talkit.app.domain.chatting.userChat.dto.*;
import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.service.UserChatService;
import com.talkit.app.domain.user.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/user-chat")
public class UserChatController {
    private final UserChatService userChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/topics")
    public ResponseEntity<List<String>> getAvailableTopics() {
        List<String> topics = userChatService.getAvailableTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<List<MyChatRoomResponse>> getMyRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 인증된 객체(userDetails)에서 내 ID를 꺼내 서비스에 전달합니다.
        List<MyChatRoomResponse> response = userChatService.getMyChatRooms(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/match")
    public ResponseEntity<ChatRoomResponse> match(@RequestBody ChatRoomRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ChatRoomResponse response = userChatService.matchOrCreateRoom(request.getTopic(), userDetails.getId());

        return ResponseEntity.ok(response);
    }

    //메세지 전송
    @MessageMapping("/room/{roomId}/message")
    public void sendMessage(@DestinationVariable Long roomId,
                            ChatMessageRequest request,
                            @Header("userId") String userId) {

        try {
            System.out.println("수신 데이터 - roomId: " + roomId + ", userId: " + userId + ", msg: " + request.getMessage());
            Long senderId = Long.parseLong(userId);

            ChatMessage message = userChatService.sendMessage(roomId, senderId, request.getMessage());

            messagingTemplate.convertAndSend("/sub/room/" + roomId, ChatMessageResponse.from(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
