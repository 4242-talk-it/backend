package com.talkit.app.domain.chatting.aiChat.service;

import com.talkit.app.domain.chatting.aiChat.dto.GeminiRequestDto;
import com.talkit.app.domain.chatting.aiChat.dto.GeminiResponseDto;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AiChatService {

    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    @Value("${custom.gemini.url}")
    private String apiUrl;

    @Value("${custom.gemini.key}")
    private String apiKey;

    @Transactional
    public String getGeminiContents(String prompt, Long userId) {

        User user = userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);

        String requestUrl = apiUrl + "?key=" + apiKey;
        GeminiRequestDto request = GeminiRequestDto.fromText(prompt);
        GeminiResponseDto response = restTemplate.postForObject(requestUrl, request, GeminiResponseDto.class);

        String aiAnswer = (response != null) ? response.getAnswer() : "답변을 가져오지 못했습니다.";
        // saveChatMessage(user, message, aiAnswer);

        return aiAnswer;
    }

}
