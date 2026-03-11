package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
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

    @Query("SELECT r FROM ChatRoom r " +
            "LEFT JOIN FETCH r.user1Mission " +
            "LEFT JOIN FETCH r.user2Mission " +
            "WHERE r.roomId = :id")
    Optional<ChatRoom> findByIdWithMissionKeyword(@Param("id") Long id);

    //종료되지 않은 방+같은 토픽을 고른 경우 해당 방 재진입
    @Query("SELECT r FROM ChatRoom r WHERE r.topic = :topic AND r.isOvered = false AND (r.user1 = :user OR r.user2 = :user)")
    Optional<ChatRoom> findFirstByTopicAndIsOveredFalseAndUser1OrUser2(@Param("topic") String topic, @Param("user") User user);

    //userId2가 null인 채팅방의 topic불러오기
    @Query("SELECT DISTINCT r.topic FROM ChatRoom r WHERE r.user2 IS NULL AND r.isOvered = false")
    List<String> findAvailableTopics();

    // 1. 매칭 대기 중인 방 찾기
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatRoom> findFirstByTopicAndUser1NotAndUser2IsNullAndIsOveredFalseOrderByCreatedAtAsc(String topic,User user1);

    // 2. 내가 참여 중인 모든 채팅방 목록 조회
    // user1 혹은 user2가 나인 경우를 모두 찾음
    @Query("SELECT r FROM ChatRoom r WHERE (r.user1.id = :userId OR r.user2.id = :userId) AND r.isOvered = false ORDER BY r.updatedAt DESC")
    List<ChatRoom> findMyActiveRooms(@Param("userId") Long userId);

    // 3. (선택) 특정 주제로 매칭 대기 중인 방이 있는지 확인
    boolean existsByTopicAndUser2IsNull(String topic);
}