package com.talkit.app.domain.chatting.badge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable=false)
    private String name; //뱃지 이름

    @Column(name="description", nullable=false)
    private String description; //뱃지 설명

    @Column(name="icon")
    private String icon; //뱃지아이콘

    private int requiredSuccessCnt; //획득에 필요한 성공 횟수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType badgeType;

    private String conditionKey; //조건대상 (예: "sports", "NIGHT", "POSITIVE", "LENGTH_20")


    @Builder
    public Badge(String name, String description, String icon, int requiredSuccessCnt, BadgeType badgeType, String conditionKey) {
        this.name=name;
        this.description=description;
        this.icon=icon;
        this.requiredSuccessCnt=requiredSuccessCnt;
        this.badgeType=badgeType;
        this.conditionKey=conditionKey;
    }
}
