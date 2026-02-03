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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Value("${custom.gemini.url}")
    private String apiUrl;

    @Value("${custom.gemini.key}")
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
        return AiChatRoomResponseDto.of(aiChatRoom);
    }

    // 2. 메시지 전송 및 AI 답변 저장
    @Transactional
    public String getGeminiReactions(Long chatRoomId, String userMessage, Long userId) {

        AiChatRoom chatRoom = aiChatRoomRepository.findById(chatRoomId)
                .orElseThrow(ExceptionType.NOT_FOUND_AI_CHAT_ROOM::of);

        // 권한 없음 예외 발생
        if (!chatRoom.getUser().getId().equals(userId)) {
            throw ExceptionType.FORBIDDEN_ACCESS.of();
        }

        // 기존 메시지 내역 조회 (시간순 정렬)
        List<AiChatMessage> messageHistory = aiChatMessageRepository.findByAiChatRoomOrderByCreatedAtAsc(chatRoom);
        // 사용자 메시지 먼저 DB 저장
        saveMessage(chatRoom, AiChatMessage.MessageType.USER, userMessage);

        // 프롬프트 생성 (과거 내역 포함)
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("너는 '%s' 상황의 상대방이야. 상황 설명: %s. 이전 대화 맥락을 파악해서 자연스럽게 한국어로 대답해줘.\n\n",
                chatRoom.getAiSituation().getTitle(),
                chatRoom.getAiSituation().getDescription()));

        for (AiChatMessage msg : messageHistory) {
            String role = (msg.getType() == AiChatMessage.MessageType.USER) ? "사용자" : "AI";
            promptBuilder.append(role).append(": ").append(msg.getContent()).append("\n");
        }
        promptBuilder.append("사용자: ").append(userMessage).append("\nAI: ");

        // Gemini API 호출
        String aiAnswer;
        try {
            GeminiRequestDto request = GeminiRequestDto.fromText(promptBuilder.toString());
            GeminiResponseDto response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, request, GeminiResponseDto.class);

            if (response == null || response.getAnswer() == null || response.getAnswer().isBlank()) {
                aiAnswer = "죄송해요, 잠시 대화가 어려워요. 다시 말씀해 주시겠어요?";
            } else {
                aiAnswer = response.getAnswer();
            }
        } catch (Exception e) {
            aiAnswer = "연결이 잠시 원활하지 않습니다. 잠시 후 다시 시도해 주세요.";
        }
        // AI 답변 DB 저장
        saveMessage(chatRoom, AiChatMessage.MessageType.AI, aiAnswer);

        return aiAnswer;
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

}
