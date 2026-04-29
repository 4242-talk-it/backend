package com.talkit.app.domain.chatting.userChat.entity;

import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(
        name="chat_feedback",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id","writer_id"})
        }
)
public class ChatFeedback {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private User writer;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="target_id")
    private User target;

    @Enumerated(EnumType.STRING)
    @Column(name="special_tag1")
    private SpecialTagType specialTagType1;

    @Enumerated(EnumType.STRING)
    @Column(name="special_tag2")
    private SpecialTagType specialTagType2;

    @Column(name="comment", columnDefinition = "TEXT")
    private String comment;

    @CreatedDate
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
}
