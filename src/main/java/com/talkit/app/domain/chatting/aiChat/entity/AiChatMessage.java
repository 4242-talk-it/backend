package com.talkit.app.domain.chatting.aiChat.entity;

import com.talkit.app.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ai_chat_message")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_chatting_id")
    private AiChatRoom aiChatRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType type;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public enum MessageType {
        AI, USER, SYSTEM, NOTICE
    }
}
