package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatRoom> findFirstByTopicAndUser2IsNullOrderByCreatedAtAsc(String topic);

    @Query("SELECT r FROM ChatRoom r WHERE r.user1 = :user OR r.user2 = :user ORDER BY r.createdAt DESC")
    List<ChatRoom> findAllMyRooms(@Param("user") User user);
    
    boolean existsByTopicAndUser2IsNull(String topic);
}