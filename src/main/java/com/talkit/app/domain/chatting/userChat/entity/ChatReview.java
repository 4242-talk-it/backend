package com.talkit.app.domain.chatting.userChat.entity;

import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_uid")
    private User writer; // 본인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_uid")
    private User target; // 온도가 변동될 상대

    @Enumerated(EnumType.STRING)
    private EmotionType emotion; // 선택한 감정

    private double temperatureDelta; // 해당 감정으로 인해 변동된 온도

    private LocalDateTime createdAt = LocalDateTime.now();
}
