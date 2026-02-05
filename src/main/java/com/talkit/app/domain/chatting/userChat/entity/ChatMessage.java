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

public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cmid;

    @ManyToOne
    @JoinColumn(name = "roomId")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "senderUid")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;
}
