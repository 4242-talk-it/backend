package com.talkit.app.domain.like.entity;

import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "community_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "community_id"})
    }
)
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityLike extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //글 좋아요 식별키

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //사용자 식별키

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community; //글 식별키

    public static CommunityLike of(User user, Community community) {
        return CommunityLike.builder()
            .user(user)
            .community(community)
            .build();
    }
}
