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

    private boolean isMatched;
    private LocalDateTime createdAt;
}
