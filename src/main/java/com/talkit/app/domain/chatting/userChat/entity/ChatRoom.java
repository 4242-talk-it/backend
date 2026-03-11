package com.talkit.app.domain.chatting.userChat.entity;

import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String topic;

    @ManyToOne
    @JoinColumn(name = "user1_uid")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_uid")
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user1_mission_id")
    private MissionKeyword user1Mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user2_mission_id")
    private MissionKeyword user2Mission;

    public void assignMissions(MissionKeyword m1, MissionKeyword m2) {
        this.user1Mission = m1;
        this.user2Mission = m2;
    }

    private boolean isOvered; // 대화 종료 여부
    private LocalDateTime createdAt; //채팅방 생성 일시
    private String lastMessage;
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_matched", nullable = false)
    private boolean isMatched = false;

    @Builder.Default
    private int maxTurns = 3; //최대 대화 가능수
    private boolean isExtended = false; //대화 연장 여부

    public void updateLastMessage(String content) {
        this.lastMessage = content;
        this.updatedAt = LocalDateTime.now();
    }

    //대화 연장 시 호출
    public void extendMaxTurns(int extraTurns) {
        this.maxTurns += extraTurns;
    }
}
