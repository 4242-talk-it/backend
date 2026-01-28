package com.talkit.app.domain.chatting.userchatting.repository;

import com.talkit.app.domain.chatting.userchatting.entity.ChatRoom;
import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 1. 매칭 대기 중인 방 찾기
    // 같은 주제(topic) + 아직 두 번째 참가자(user2)가 없는 방 중 가장 먼저 생성된 방 하나를 가져옴
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChatRoom> findFirstByTopicAndUser2IsNullOrderByCreatedAtAsc(String topic);

    // 2. 내가 참여 중인 모든 채팅방 목록 조회
    // user1 혹은 user2가 나인 경우를 모두 찾음
    @Query("SELECT r FROM ChatRoom r WHERE r.user1 = :user OR r.user2 = :user ORDER BY r.createdAt DESC")
    List<ChatRoom> findAllMyRooms(@Param("user") User user);

    // 3. (선택) 특정 주제로 매칭 대기 중인 방이 있는지 확인
    boolean existsByTopicAndUser2IsNull(String topic);
}