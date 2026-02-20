package com.talkit.app.domain.chatting.aiChat.service;

import com.talkit.app.domain.chatting.aiChat.dto.AiChatRoomCreateRequest;
import com.talkit.app.domain.chatting.aiChat.dto.AiChatRoomResponseDto;
import com.talkit.app.domain.chatting.aiChat.dto.GeminiRequestDto;
import com.talkit.app.domain.chatting.aiChat.dto.GeminiResponseDto;
import com.talkit.app.domain.chatting.aiChat.entity.AiChatMessage;
import com.talkit.app.domain.chatting.aiChat.entity.AiChatRoom;
import com.talkit.app.domain.chatting.aiChat.repository.AiChatMessageRepository;
import com.talkit.app.domain.chatting.aiChat.repository.AiChatRoomRepository;
import com.talkit.app.domain.chatting.aiSituation.entity.AiSituation;
import com.talkit.app.domain.chatting.aiSituation.repository.AiSituationRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AiChatService {

    private final AiSituationRepository aiSituationRepository;
    private final AiChatMessageRepository aiChatMessageRepository;
    private final AiChatRoomRepository aiChatRoomRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${GEMINI_API_URL}")
    private String apiUrl;

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    // 1. 채팅방 생성
    @Transactional
    public AiChatRoomResponseDto createChatRoom(AiChatRoomCreateRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);
        AiSituation aiSituation = aiSituationRepository.findById(request.situationId())
                .orElseThrow(ExceptionType.NOT_FOUND_SITUATION::of);

        AiChatRoom aiChatRoom = AiChatRoom.builder()
                .user(user)
                .aiSituation(aiSituation)
                .build();

        aiChatRoomRepository.save(aiChatRoom);

        String firstGreeting = generateInitialGreeting(aiChatRoom);
        saveMessage(aiChatRoom, AiChatMessage.MessageType.AI, firstGreeting);

        return AiChatRoomResponseDto.of(aiChatRoom);
    }

    // 2. 메시지 전송 및 AI 답변 저장
    @Transactional
    public String getGeminiReactions(Long chatRoomId, String userMessage, Long userId) {

        AiChatRoom chatRoom = aiChatRoomRepository.findById(chatRoomId)
                .orElseThrow(ExceptionType.NOT_FOUND_AI_CHAT_ROOM::of);

        if (!chatRoom.getUser().getId().equals(userId)) {
            throw ExceptionType.FORBIDDEN_ACCESS.of();
        }

        saveMessage(chatRoom, AiChatMessage.MessageType.USER, userMessage);

        List<AiChatMessage> messageHistory = aiChatMessageRepository.findByAiChatRoomOrderByCreatedAtAsc(chatRoom);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("너는 '%s' 상황의 상대방이야. 상황 설명: %s. 이전 대화 맥락을 파악해서 자연스럽게 한국어로 대답해줘.\n\n",
                chatRoom.getAiSituation().getTitle(),
                chatRoom.getAiSituation().getDescription()));

        for (AiChatMessage msg : messageHistory) {
            String role = (msg.getType() == AiChatMessage.MessageType.USER) ? "사용자" : "AI";
            promptBuilder.append(role).append(": ").append(msg.getContent()).append("\n");
        }
        promptBuilder.append("\nAI: ");

        String aiAnswer = callGemini(promptBuilder.toString());
        saveMessage(chatRoom, AiChatMessage.MessageType.AI, aiAnswer);

        return aiAnswer;
    }

    @Retryable(
            retryFor = { HttpServerErrorException.ServiceUnavailable.class },
            maxAttempts = 3, backoff = @Backoff(delay = 2000)
    )
    public String callGemini(String prompt) {
        try {
            GeminiRequestDto request = GeminiRequestDto.fromText(prompt);
            GeminiResponseDto response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, request, GeminiResponseDto.class);

            if (response == null || response.getAnswer() == null || response.getAnswer().isBlank()) {
                return "음... 잠시 생각을 정리 중이에요. 다시 말씀해 주시겠어요?";
            }
            return response.getAnswer();
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            // 503 에러는 다시 throw하여 @Retryable이 작동하게 함
            throw e;
        } catch (Exception e) {
            // 그 외의 일반 에러 처리
            return "연결 실패: " + e.getMessage();
        }
    }


    @Transactional(readOnly = true)
    public List<AiChatRoomResponseDto> getMyAiChatRoom(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);
        return aiChatRoomRepository.findAllByUserWithSituation(user, pageable)
                .stream()
                .map(AiChatRoomResponseDto::of)
                .toList();
    }

    private void saveMessage(AiChatRoom chatRoom, AiChatMessage.MessageType type, String content) {
        AiChatMessage message = AiChatMessage.builder()
                .aiChatRoom(chatRoom)
                .type(type)
                .content(content)
                .build();

        aiChatMessageRepository.save(message);
    }

    private String generateInitialGreeting(AiChatRoom aiChatRoom) {
        String prompt = String.format(
                "너는 '%s'의 상대방이야. 상황 설명: '%s'." +
                        "이 상황에 맞춰서 사용자에게 먼저 첫 인사를 건네줘." +
                        "자연스럽게 대화를 시작할 수 있도록 한국어로 한 문장 혹은 두 문장으로 대화를 시작해줘.",
                aiChatRoom.getAiSituation().getTitle(),
                aiChatRoom.getAiSituation().getDescription()
        );
        return callGemini(prompt);
    }


}
