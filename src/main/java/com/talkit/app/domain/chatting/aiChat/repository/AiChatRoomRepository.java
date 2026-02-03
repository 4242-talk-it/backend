package com.talkit.app.domain.chatting.aiChat.repository;

import com.talkit.app.domain.chatting.aiChat.entity.AiChatRoom;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AiChatRoomRepository extends JpaRepository<AiChatRoom, Long> {

    @Query("select r from AiChatRoom r join fetch r.aiSituation where r.user = :user order by r.createdAt desc")
    Slice<AiChatRoom> findAllByUserWithSituation(@Param("user") User user, Pageable pageable);
}
