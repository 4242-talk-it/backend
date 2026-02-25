package com.talkit.app.domain.chatting.userChat.repository;

import com.talkit.app.domain.chatting.userChat.entity.ChatReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatReviewRepository extends JpaRepository<ChatReview,Long> {
}
