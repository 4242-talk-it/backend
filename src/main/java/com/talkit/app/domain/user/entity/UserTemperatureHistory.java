package com.talkit.app.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="user_temperature_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTemperatureHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private double temperature;

    @Column(nullable = false)
    private LocalDateTime recordedAt; //기록 날짜
}
