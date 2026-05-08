package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatReview;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.entity.EmotionType;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatReviewRepository extends JpaRepository<ChatReview,Long> {
    List<ChatReview> findByChatRoomAndTarget(ChatRoom chatRoom, User target);

    @Query("SELECT cr.emotion FROM ChatReview cr WHERE cr.target.id = :userId")
    List<EmotionType> findEmotionsByTargetId(@Param("userId") Long userid);
}
