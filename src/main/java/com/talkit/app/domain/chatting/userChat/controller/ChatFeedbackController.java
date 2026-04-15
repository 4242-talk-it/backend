package com.talkit.app.domain.chatting.userChat.controller;

import com.talkit.app.domain.chatting.userChat.service.ChatFeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name="채팅 피드백 API")
@RequestMapping("/api/chat-feedback")
public class ChatFeedbackController {

    private final ChatFeedbackService chatFeedbackService;

    @PostMapping
    public ResponseEntity<String> submitFeedback (
            @RequestParam Long roomId,
            @RequestParam Long writerId,
            @RequestParam (required = false, defaultValue = "")List<String> tags,
            @RequestParam (required = false, defaultValue = "")String comment
            ) {
        chatFeedbackService.submitFeedback(roomId, writerId, tags, comment);
        return ResponseEntity.ok("피드백이 제출되었습니다.");
    }
}
