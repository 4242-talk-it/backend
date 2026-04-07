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

//    // 테스트용 - 확인 후 삭제
//    @PostMapping("/test/temperature/{userId}")
//    public String testTemperatureBadge(@PathVariable Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
//        badgeGrantService.checkTemperatureBadge(user);
//        return "temperature badge check done. current temp: " + user.getTemperature();
//    }
//
//    //테스트용
//    @PostMapping("/test/mission/{userId}")
//    public String testMissionBadge(
//            @PathVariable Long userId,
//            @RequestParam String category) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
//        badgeGrantService.checkMissionKeywordBadge(user, category);
//        return "mission badge check done. category: " + category;
//    }

    @GetMapping("/my")
    public ResponseEntity<List<BadgeResponse.MyBadge>> getMyBadges(@RequestParam Long userId) {
        List<BadgeResponse.MyBadge> response = userBadgeRepository.findAllByUserIdWithBadge(userId)
                .stream()
                .map(BadgeResponse.MyBadge::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
