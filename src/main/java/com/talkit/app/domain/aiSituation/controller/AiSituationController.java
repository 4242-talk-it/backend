package com.talkit.app.domain.aiSituation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "AI 채팅 관련 API", description = "AI 채팅 관련 API")
@RequestMapping("/api/cahtting-ai")
@RestController
public class AiSituationController {
}
