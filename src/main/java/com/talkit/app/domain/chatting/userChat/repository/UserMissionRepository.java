package com.talkit.app.domain.chatting.userChat.repository;


import com.talkit.app.domain.chatting.userChat.entity.UserMission;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    @Query("""
            SELECT COUNT(um)
            FROM UserMission um
            JOIN um.missionKeyword mk
            WHERE um.user.id = :userId
            AND um.isSuccess = true
            And mk.category = :category
            """)
    int countSuccessByUserIdAndCategory(@Param("userId") Long userId,
                                        @Param("category") String category);


    Optional<UserMission> findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
