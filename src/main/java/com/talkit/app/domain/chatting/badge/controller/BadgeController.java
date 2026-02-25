package com.talkit.app.domain.chatting.badge.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "채팅 공통 - 뱃지 달성 관련 API", description = "채팅 공통 - 뱃지 달성 관련 API")
@RequestMapping("/api/chatting-badge")
@RestController
public class BadgeController {
}
