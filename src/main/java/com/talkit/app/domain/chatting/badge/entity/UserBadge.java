package com.talkit.app.domain.chatting.badge.entity;

import com.talkit.app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name="user_badge",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id","badge_id"}) //동일 뱃지 중복 획득 방지
        }
)
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name="badge_id", nullable = false)
    private Badge badge;

    @CreatedDate
    @Column(name="createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserBadge (User user, Badge badge) {
        this.user=user;
        this.badge=badge;
    }
}
