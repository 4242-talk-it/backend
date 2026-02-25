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
    private User writer; // 평가를 한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_uid")
    private User target; // 점수가 변동될 사람 (상대방)

    @Enumerated(EnumType.STRING)
    private EmotionType emotion; // 선택한 감정 (ENUM 사용 권장)

    private double temperatureDelta; // 해당 감정으로 인해 변동된 점수 (예: +5, -10)

    private LocalDateTime createdAt = LocalDateTime.now();
}
