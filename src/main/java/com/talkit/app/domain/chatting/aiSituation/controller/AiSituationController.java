package com.talkit.app.domain.chatting.aiSituation.controller;

import com.talkit.app.domain.chatting.aiSituation.dto.AiSituationResponseDto;
import com.talkit.app.domain.chatting.aiSituation.service.AiSituationService;
import com.talkit.app.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Tag(name = "AI 채팅 페르소나 관련 API", description = "AI 채팅 페르소나 관련 API")
@RequestMapping("/api/ai-situation")
@RestController
public class AiSituationController {

    private final AiSituationService aiSituationService;

    @Operation(summary = "전체 AI 대화 상황 목록 조회")
    @GetMapping
    public ResponseDto<List<AiSituationResponseDto>> getAllSituations() {
        return ResponseDto.of(aiSituationService.findAll(), "상황 목록을 성공적으로 불러왔습니다.");
    }
}
