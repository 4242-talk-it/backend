package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatFeedback;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatFeedbackRepository extends JpaRepository<ChatFeedback, Long> {
    //상대가 나에게써준 피드백 조회
    Optional<ChatFeedback> findByChatRoomAndTarget (ChatRoom chatRoom, User target);

    boolean existsByChatRoomRoomIdAndWriterId(Long roomId, Long writerId);
}
