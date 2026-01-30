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
    private Long roomId; //채팅방 식별자

    private String topic; // 사용자가 고른 주제

    @ManyToOne
    @JoinColumn(name = "user1_uid")
    private User user1; // 먼저 들어와서 기다린 사람

    @ManyToOne
    @JoinColumn(name = "user2_uid")
    private User user2; // 나중에 매칭된 사람

    private boolean isMatched; // 매칭 성공 여부
    private LocalDateTime createdAt; //채팅방 생성 일시
}
