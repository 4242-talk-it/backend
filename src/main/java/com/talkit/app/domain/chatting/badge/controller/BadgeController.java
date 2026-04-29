package com.talkit.app.domain.chatting.badge.controller;

import com.talkit.app.domain.chatting.badge.dto.BadgeResponse;
import com.talkit.app.domain.chatting.badge.entity.UserBadge;
import com.talkit.app.domain.chatting.badge.repository.UserBadgeRepository;
import com.talkit.app.domain.chatting.badge.service.BadgeGrantService;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "채팅 공통 - 뱃지 달성 관련 API", description = "채팅 공통 - 뱃지 달성 관련 API")
@RequestMapping("/api/chatting-badge")
@RestController
public class BadgeController {
    private final BadgeGrantService badgeGrantService;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;

    @GetMapping("/my")
    public ResponseEntity<List<BadgeResponse.MyBadge>> getMyBadges(@RequestParam Long userId) {
        List<BadgeResponse.MyBadge> response = userBadgeRepository.findAllByUserIdWithBadge(userId)
                .stream()
                .map(BadgeResponse.MyBadge::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
