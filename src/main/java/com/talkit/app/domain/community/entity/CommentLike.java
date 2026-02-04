package com.talkit.app.domain.community.entity;

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
    name = "comment_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "comment_id"})
    }
)
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike extends BaseEntity {


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //댓글 좋아요 식별키

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //사용자 식별키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment; //댓글 식별키

}
