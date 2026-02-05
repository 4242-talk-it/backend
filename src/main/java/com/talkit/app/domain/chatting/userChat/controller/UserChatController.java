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

    @PostMapping("/match")
    public ResponseEntity<ChatRoomResponse> match(@RequestBody ChatRoomRequest request,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ChatRoom room = userChatService.matchOrCreateRoom(request.getTopic(), userDetails.getId());

        boolean isMatched = room.getUser2() != null;

        if (isMatched) {
            messagingTemplate.convertAndSend("/sub/room/" + room.getRoomId(), "MATCH_COMPLETE");
        }

        ChatRoomResponse response = ChatRoomResponse.builder()
                .roomId(room.getRoomId())
                .topic(room.getTopic())
                .isMatched(room.getUser2() != null)
                .userId(userDetails.getId())
                .build();

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
