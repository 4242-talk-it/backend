package com.talkit.app.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivity {
    @Id
    private Long userId;

    @OneToOne(fetch= FetchType.LAZY)
    @MapsId
    @JoinColumn(name="user_id")
    private User user;

    //접속 관련
    private LocalDateTime lastLoginAt;
    private int streakDays; //연속접속일수

    //채팅 패턴 카운트
    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int totalChatCount=0; //전체 채팅 횟수

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int nightChatCount=0; //야행성

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int morningChatCount=0; //아침형

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int longChatCount=0; //투머치토커

    //피드백 카운트
    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int yawnFeedbackCount=0; //인간 자장가

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int questionFeedbackCount=0; //탐정

    @Builder.Default
    @Column(columnDefinition = "int default 0")
    private int gagFeedbackCount=0;

    public void incrementGagFeedback() { this.gagFeedbackCount++; }
    public void incrementYawnFeedback() { this.yawnFeedbackCount++; }
    public void incrementQuestionFeedback() { this.questionFeedbackCount++; }

    public void incrementTotalChat() { this.totalChatCount++; }
    public void incrementNightChat() {this.nightChatCount++;}
    public void incrementMorningChat() {this.morningChatCount++;}
    public void incrementLongChat() {this.longChatCount++;}

    //연속 접속 일수, 마지막 로그인 일시 저장
    public void updateLoginStreak() {
        LocalDateTime now=LocalDateTime.now();
        if(lastLoginAt != null&& lastLoginAt.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            this.streakDays++;
        } else if (lastLoginAt==null || !lastLoginAt.toLocalDate().equals(now.toLocalDate())) {
            this.streakDays=1;
        }
        this.lastLoginAt=now;
    }
}
