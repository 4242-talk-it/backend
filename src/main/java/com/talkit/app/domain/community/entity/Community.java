package com.talkit.app.domain.community.entity;

import com.talkit.app.domain.user.entity.User;
import com.talkit.app.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "community")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Community extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private int viewCount = 0;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityLike> communityLikeList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "community_tags", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "tag_name")
    @Builder.Default // Builder 사용 시 리스트 초기화 보장
    private List<String> tags = new ArrayList<>();

    public void incrementViewCount() {
        this.viewCount++;
    }

    // 생성 메서드 수정
    public static Community of(User user, String title, String content, String category, List<String> tags) {
        return Community.builder()
                .user(user)
                .title(title)
                .content(content)
                .category(category)
                .tags(tags)
                .build();
    }

    public void update(String title, String content, String category,List<String> tags) {

        this.title = title;
        this.content = content;
        this.category = category;

        this.tags.clear(); // 기존 태그 삭제
        if (tags != null) {
            this.tags.addAll(tags); // 새로운 태그 추가
        }
    }

}
