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
    //채팅 내역 오름차순 조회
    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    //읽지 않은 메시지 개수 카운트
    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User user);

    //채팅방 입장 시 상대방이 보낸 메시지 일괄 읽음 처리
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom = :room AND m.sender != :user")
    void markAsReadByRoomAndUser(@Param("room") ChatRoom room, @Param("user") User user);
}
