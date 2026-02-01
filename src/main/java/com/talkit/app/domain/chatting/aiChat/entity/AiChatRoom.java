package com.talkit.app.domain.chatting.aiChat.entity;

import com.talkit.app.domain.chatting.aiSituation.entity.AiSituation;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_chat_room")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRoom extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_situation_id", nullable = false)
    private AiSituation aiSituation;

    @Builder.Default
    @OneToMany(mappedBy = "aiChatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiChatMessage> messages = new ArrayList<>();

}
