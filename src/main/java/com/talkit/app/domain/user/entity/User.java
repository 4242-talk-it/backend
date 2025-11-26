package com.talkit.app.domain.user.entity;

import com.talkit.app.domain.community.entity.Comment;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.global.BaseEntity;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Table(name = "user")
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickName;

    @Column(name = "birth_year", nullable = false)
    private String birthYear;

    @Column(name = "gender")
    private String gender;

    //oauth에서 제공된 user 식별 아이디
    @Column(name = "provider_id")
    private String providerId;

    //사용된 oauth 이름: kakao, google, saisai
    @Column(name = "sso_provider", length = 50)
    private String ssoProvider;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Community> communityList = new ArrayList<>();




}
