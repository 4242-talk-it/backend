package com.talkit.app.domain.chatting.aiSituation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ai_situation")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AiSituation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_situation_id")
    private Long id;

    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "status_color", nullable = false)
    private String statusColor;
}
