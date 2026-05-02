package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.entity.MessageType;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    //채팅 40개 제한
    long countByChatRoom(ChatRoom chatRoom);

    //읽지 않은 메세지 유무
    boolean existsByChatRoomAndIsReadFalseAndSenderNot(ChatRoom chatRoom, User sender);
    //연속 3개 초과 전송 제한
    List<ChatMessage> findTop3ByChatRoomAndTypeOrderByTimestampDesc(ChatRoom chatRoom, MessageType type);
    //채팅 내역 오름차순 조회
    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User user);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom = :room AND m.sender != :user")
    void markAsReadByRoomAndUser(@Param("room") ChatRoom room, @Param("user") User user);

    //읽음 처리
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom = :room AND m.sender != :sender AND m.isRead = false")
    void markAsReadByChatRoomAndSenderNot(@Param("room") ChatRoom room, @Param("sender") User sender);

    @Query("""
        SELECT COUNT(m) > 0
        FROM ChatMessage m
        WHERE m.chatRoom = :room
          AND m.sender = :user
          AND LENGTH(m.message) > :length
    """)
    boolean existsByChatRoomAndSenderAndMessageLengthGreaterThan(
            @Param("room") ChatRoom room,
            @Param("user") User user,
            @Param("length") int length
    );
}
