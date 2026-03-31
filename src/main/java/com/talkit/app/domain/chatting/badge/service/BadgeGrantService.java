package com.talkit.app.domain.chatting.badge.service;

import com.talkit.app.domain.chatting.badge.entity.BadgeType;
import com.talkit.app.domain.chatting.badge.entity.UserBadge;
import com.talkit.app.domain.chatting.badge.repository.BadgeRepository;
import com.talkit.app.domain.chatting.badge.repository.UserBadgeRepository;
import com.talkit.app.domain.chatting.userChat.repository.UserMissionRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.entity.UserActivity;
import com.talkit.app.domain.user.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeGrantService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserActivityRepository userActivityRepository;

    // ── MISSION_KEYWORD ───────────────────────────────────────────
    // 채팅방 종료 후, 미션 성공한 유저의 category 전달
    @Transactional
    public void checkMissionKeywordBadge(User user, String category) {
        int count = userMissionRepository
                .countSuccessByUserIdAndCategory(user.getId(), category);
        if (count >= 10) {
            grantIfEligible(user, BadgeType.MISSION_KEYWORD, category);
        }
    }

    // ── FEEDBACK_POSITIVE / NEGATIVE ─────────────────────────────
    // ChatReview 저장 후 temperature 업데이트된 User 전달
    @Transactional
    public void checkTemperatureBadge(User user) {
        double temp = user.getTemperature();
        if (temp >= 60.0) {
            grantIfEligible(user, BadgeType.FEEDBACK_POSITIVE, "warm");
        }
        if (temp <= 20.0) {
            grantIfEligible(user, BadgeType.FEEDBACK_NEGATIVE, "chill");
        }
    }

    // ── CHAT_PATTERN ──────────────────────────────────────────────
    // 채팅방 종료 후 호출
    @Transactional
    public void checkChatPatternBadge(User user) {
        UserActivity activity = getActivity(user);
        checkAndGrant(user, BadgeType.CHAT_PATTERN, "night",
                activity.getNightChatCount(), 20);
        checkAndGrant(user, BadgeType.CHAT_PATTERN, "morning",
                activity.getMorningChatCount(), 20);
        checkAndGrant(user, BadgeType.CHAT_PATTERN, "tmtalk",
                activity.getLongChatCount(), 50);
    }

    // ── ATTENDANCE_STREAK ─────────────────────────────────────────
    // 로그인 후 updateLoginStreak() 호출된 다음에 실행
    @Transactional
    public void checkAttendanceBadge(User user) {
        UserActivity activity = getActivity(user);
        checkAndGrant(user, BadgeType.ATTENDANCE_STREAK, "start",
                activity.getTotalChatCount(), 10);
        checkAndGrant(user, BadgeType.ATTENDANCE_STREAK, "king",
                activity.getTotalChatCount(), 200);
        checkAndGrant(user, BadgeType.ATTENDANCE_STREAK, "routine",
                activity.getStreakDays(), 5);
        checkAndGrant(user, BadgeType.ATTENDANCE_STREAK, "passion",
                activity.getStreakDays(), 21);
    }

    // ── 헬퍼 ──────────────────────────────────────────────────────
    private void grantIfEligible(User user, BadgeType type, String conditionKey) {
        badgeRepository.findByBadgeTypeAndConditionKey(type, conditionKey)
                .ifPresent(badge -> {
                    if (!userBadgeRepository.existsByUserIdAndBadgeId(
                            user.getId(), badge.getId())) {
                        userBadgeRepository.save(
                                UserBadge.builder().user(user).badge(badge).build());
                        log.info("[Badge] 획득 userId={}, badge={}",
                                user.getId(), badge.getName());
                    }
                });
    }

    private void checkAndGrant(User user, BadgeType type, String conditionKey,
                               int current, int goal) {
        if (current >= goal) {
            grantIfEligible(user, type, conditionKey);
        }
    }

    private UserActivity getActivity(User user) {
        return userActivityRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "UserActivity not found: userId=" + user.getId()));
    }
}
