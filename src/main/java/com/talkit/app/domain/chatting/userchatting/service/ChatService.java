package com.talkit.app.domain.chatting.userchatting.service;

import com.talkit.app.domain.chatting.userchatting.entity.ChatMessage;
import com.talkit.app.domain.chatting.userchatting.entity.ChatRoom;
import com.talkit.app.domain.chatting.userchatting.repository.ChatMessageRepository;
import com.talkit.app.domain.chatting.userchatting.repository.ChatRoomRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    //주제별 채팅방 생성 및 매칭
    public ChatRoom matchOrCreateRoom(String topic, Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 1. 해당 주제로 대기 중인 방(user2가 비어있는 방)이 있는지 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findFirstByTopicAndUser2IsNullOrderByCreatedAtAsc(topic);

        if (existingRoom.isPresent()) {
            ChatRoom room = existingRoom.get();

            // 본인이 만든 방에 다시 들어가는 것 방지
            if (room.getUser1().getId().equals(currentUser.getId())) {
                return room;
            }

            // 2. 방이 있으면 user2로 합류
            room.setUser2(currentUser);
            return room;
        } else {
            // 3. 방이 없으면 새로 생성하여 user1에 할당
            ChatRoom newRoom = ChatRoom.builder()
                    .topic(topic)
                    .user1(currentUser)
                    .createdAt(LocalDateTime.now())
                    .build();
            return chatRoomRepository.save(newRoom);
        }
    }

    public List<ChatMessage> getChatHistory(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        return chatMessageRepository.findByChatRoomOrderByTimestampAsc(room);
    }

    /**
     * 메시지 전송
     */
    public ChatMessage sendMessage(Long roomId, Long userId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .message(content)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        return chatMessageRepository.save(message);
    }
}
