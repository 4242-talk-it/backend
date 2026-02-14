package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    //채팅 40개 제한
    long countByChatRoom(ChatRoom chatRoom);

    //연속 3개 초과 전송 제한
    List<ChatMessage> findTop3ByChatRoomOrderByTimestampDesc(ChatRoom chatRoom);
    //채팅 내역 오름차순 조회
    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User user);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom = :room AND m.sender != :user")
    void markAsReadByRoomAndUser(@Param("room") ChatRoom room, @Param("user") User user);
}
