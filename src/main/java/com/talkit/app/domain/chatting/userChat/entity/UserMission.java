package com.talkit.app.domain.chatting.userChat.entity;

import com.talkit.app.domain.chatting.badge.entity.MissionKeyword;
import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mission_keyword_id")
    private MissionKeyword missionKeyword;

    private String guessedKeyword; //상대가 제출한 키워드

    private boolean isSuccess; //성공 여부

    @Builder
    public UserMission(User user, ChatRoom chatRoom, MissionKeyword missionKeyword, String guessedKeyword, boolean isSuccess) {
        this.user=user;
        this.chatRoom=chatRoom;
        this.missionKeyword=missionKeyword;
        this.guessedKeyword=guessedKeyword;
        this.isSuccess=isSuccess;
    }
}
