package com.talkit.app.domain.user.entity;

import com.talkit.app.domain.community.entity.Comment;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.like.entity.CommentLike;
import com.talkit.app.domain.like.entity.CommunityLike;
import com.talkit.app.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    public static final Long ANONYMOUS_USER_ID = -1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", nullable=false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "birth_year", nullable = false)
    private String birthYear;

    @Column(name = "gender")
    private String gender;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "sso_provider", length = 50)
    private String ssoProvider;

    // ===== 게시글 =====
    @OneToMany(mappedBy = "user")
    private List<Community> communityList = new ArrayList<>();

    // ===== 게시글 좋아요 =====
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityLike> communityLikeList = new ArrayList<>();

    // ===== 댓글 =====
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    // ===== 댓글 좋아요 =====
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikeList = new ArrayList<>();
}
