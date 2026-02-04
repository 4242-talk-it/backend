package com.talkit.app.domain.chatting.userChat.service;

import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.repository.ChatMessageRepository;
import com.talkit.app.domain.chatting.userChat.repository.ChatRoomRepository;
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
public class UserChatService {
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

            // [보완] 이미 user2가 채워진 방인지 다시 한번 확인 (동시성 방어)
            if (room.getUser2() != null) {
                // 이미 다른 사람이 가로챘다면 처음부터 다시 시도하거나 새 방 생성 유도
                // 여기서는 안전하게 새 방을 생성하는 흐름으로 가거나 예외를 던질 수 있습니다.
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

        // [추가] 보안 로직: 메시지 발신자가 해당 채팅방의 멤버(user1 혹은 user2)인지 확인
        if (!room.getUser1().getId().equals(userId) &&
                (room.getUser2() == null || !room.getUser2().getId().equals(userId))) {
            throw new IllegalArgumentException("해당 채팅방에 참여 권한이 없습니다.");
        }

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
