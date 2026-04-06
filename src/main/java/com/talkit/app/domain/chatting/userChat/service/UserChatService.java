package com.talkit.app.domain.chatting.userChat.service;

import com.talkit.app.domain.chatting.badge.service.BadgeGrantService;
import com.talkit.app.domain.chatting.userChat.dto.ChatRoomResponse;
import com.talkit.app.domain.chatting.userChat.dto.MyChatRoomResponse;
import com.talkit.app.domain.chatting.userChat.entity.ChatMessage;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.badge.entity.MissionKeyword;
import com.talkit.app.domain.chatting.userChat.entity.UserMission;
import com.talkit.app.domain.chatting.userChat.repository.ChatMessageRepository;
import com.talkit.app.domain.chatting.userChat.repository.ChatRoomRepository;
import com.talkit.app.domain.chatting.userChat.repository.MissionKeywordRepository;
import com.talkit.app.domain.chatting.userChat.repository.UserMissionRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.entity.UserActivity;
import com.talkit.app.domain.user.repository.UserActivityRepository;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class UserChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MissionService missionService;
    private final UserMissionRepository userMissionRepository;
    private final MissionKeywordRepository missionKeywordRepository;
    private final BadgeGrantService badgeGrantService;
    private final UserActivityRepository userActivityRepository;

    private final Map<Long,Set<Long>> extendConsensus = new ConcurrentHashMap<>();

    //참여 가능한 채팅방 불러오기
    public List<String> getAvailableTopics() {
        return chatRoomRepository.findAvailableTopics();
    }
    //주제별 채팅방 생성 및 매칭
    public ChatRoomResponse matchOrCreateRoom(String topic, Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        //본인이 참여중이면서 종료되지 않은 대화방
        Optional<ChatRoom> ongoingRoom = chatRoomRepository.findFirstByTopicAndIsOveredFalseAndUser1OrUser2(topic, currentUser);

        if (ongoingRoom.isPresent()) {
            return ChatRoomResponse.from(ongoingRoom.get(), userId);
        }

        //해당 토픽으로 대기 중 대화방
        Optional<ChatRoom> existingRoom = chatRoomRepository.findFirstByTopicAndUser1NotAndUser2IsNullAndIsOveredFalseOrderByCreatedAtAsc(topic, currentUser);

        ChatRoom room;
        if (existingRoom.isPresent()) {
            room = existingRoom.get();
            room.setUser2(currentUser);
            room.setMatched(true);

            room.setUser1Mission(missionService.getRandomMissionEntity());
            room.setUser2Mission(missionService.getRandomMissionEntity());

            // 매칭 완료 신호 전송 (기존 String 대신 JSON 객체로 보내면 프론트 처리가 더 쉬움)
            Map<String, Object> matchSignal = new HashMap<>();
            matchSignal.put("type", "MATCH_COMPLETE");
            matchSignal.put("roomId", room.getRoomId());

            messagingTemplate.convertAndSend("/sub/room/" + room.getRoomId(), "MATCH_COMPLETE");
        } else {
            ChatRoom newRoom = ChatRoom.builder()
                    .topic(topic)
                    .user1(currentUser)
                    .user2(null)
                    .isOvered(false)
                    .isMatched(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            room=chatRoomRepository.save(newRoom);
        }
        return ChatRoomResponse.from(chatRoomRepository.findByIdWithMissionKeyword(room.getRoomId()).get(), userId);
    }

    public ChatRoom getRoom(Long roomId) {
        return chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다. ID: " + roomId));
    }

    public List<MyChatRoomResponse> getMyChatRooms(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findMyActiveRooms(userId);

        return rooms.stream().map(room -> {
            String lastMsg = room.getLastMessage(); // DB에 마지막 메시지를 저장하는 컬럼이 있다고 가정
            if (lastMsg != null && lastMsg.length() > 20) {
                lastMsg = lastMsg.substring(0, 20) + "..."; // 20자 이상이면 생략
            }

            return new MyChatRoomResponse(
                    room.getRoomId(),
                    room.getTopic(),
                    lastMsg != null ? lastMsg : "대화를 시작해보세요!",
                    formatTime(room.getUpdatedAt()), // 시간 포맷팅 유틸 함수 사용
                    userId
            );
        }).collect(Collectors.toList());
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("a hh:mm"));
    }

    public List<ChatMessage> getChatHistory(Long roomId) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        return chatMessageRepository.findByChatRoomOrderByTimestampAsc(room);
    }

    @Transactional
    public boolean checkMissionKeyword(Long roomId, Long myId, String guessedKeyword) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        // 1. 내가 누구인지 확인하고, '상대방'의 정답 정보를 가져옴
        User targetUser;
        MissionKeyword actualMission;

        if (room.getUser1().getId().equals(myId)) {
            // 내가 user1이면, 검증 대상은 user2
            targetUser = room.getUser2();
            actualMission = room.getUser2Mission();
        } else {
            // 내가 user2이면, 검증 대상은 user1
            targetUser = room.getUser1();
            actualMission = room.getUser1Mission();
        }

        // 2. 정답 비교 (공백 제거 및 대소문자 무시하면 더 좋음)
        boolean isSuccess = actualMission.getKeyword().trim().equals(guessedKeyword.trim());

        // 3. UserMission 결과 저장
        UserMission result = UserMission.builder()
                .user(targetUser) // 미션을 부여받았던 당사자
                .chatRoom(room)
                .missionKeyword(actualMission)
                .guessedKeyword(guessedKeyword)
                .isSuccess(isSuccess)
                .build();

        userMissionRepository.save(result);

        return isSuccess;
    }

    public List<String> getMissionOptions(Long roomId, Long myId) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        String myKeyword = "";
        String targetKeyword = "";

        if (room.getUser1().getId().equals(myId)) {
            myKeyword = room.getUser1Mission().getKeyword();
            targetKeyword = room.getUser2Mission().getKeyword();
        } else {
            myKeyword = room.getUser2Mission().getKeyword();
            targetKeyword = room.getUser1Mission().getKeyword();
        }

        // 2. 전체 키워드 중 '내 것'과 '상대방 것'을 제외한 오답 후보들 추출
        final String finalMyKeyword = myKeyword;
        final String finalTargetKeyword = targetKeyword;

        List<String> distractors = missionKeywordRepository.findAll().stream()
                .map(MissionKeyword::getKeyword)
                .filter(k -> !k.equals(finalMyKeyword) && !k.equals(finalTargetKeyword))
                .collect(Collectors.toList());

        // 3. 오답 후보 섞어서 3개 선택
        Collections.shuffle(distractors);
        List<String> options = new ArrayList<>(distractors.subList(0, Math.min(3, distractors.size())));

        // 4. 실제 정답(상대방 키워드) 추가 후 최종 셔플
        options.add(finalTargetKeyword);
        Collections.shuffle(options);

        return options;
    }

    //메세지 전송
    public ChatMessage sendMessage(Long roomId, Long userId, String content) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        //전체 메세지 수 체크 (40개 제한)
        long totalMessages = chatMessageRepository.countByChatRoom(room);
        if (room.isOvered() || totalMessages >= room.getMaxTurns()) {
            if (!room.isOvered()) {
                room.setOvered(true); // 혹시 안 바뀌어있다면 여기서 변경
            }
            throw new IllegalStateException("최대 대화 횟수에 도달했습니다.");
        }

        // 2. 연속 전송 제한 체크 (최근 3개 메시지 조회)
        // Pageable을 사용하여 최신 3개만 가져오는 로직 필요
        List<ChatMessage> lastMessages = chatMessageRepository.findTop3ByChatRoomOrderByTimestampDesc(room);
        long continuousCount = lastMessages.stream()
                .filter(m -> m.getSender().getId().equals(userId))
                .count();

        if (continuousCount >= 3) {
            throw new IllegalStateException("상대방의 답변을 기다려야 합니다.");
        }

        if(room.getUser2() == null) {
            throw new IllegalArgumentException("아직 상대 매칭이 되지 않았습니다.");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // [추가] 보안 로직: 메시지 발신자가 해당 채팅방의 멤버(user1 혹은 user2)인지 확인
        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 채팅방에 참여 권한이 없습니다.");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .message(content)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
        ChatMessage saved = chatMessageRepository.save(message);
        // 투머치토커, 침묵맨
        if (content.length() > 30 || content.length() < 10) {
            UserActivity activity = userActivityRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException(
                            "UserActivity not found: userId=" + userId));
            if (content.length() > 30) activity.incrementLongChat();
            if (content.length() < 10) activity.incrementShortChat();
            userActivityRepository.save(activity);
        }

        if (totalMessages + 1 >= room.getMaxTurns()) {
            room.endChat();

            Map<String, Object> endSignal = new HashMap<>();
            endSignal.put("type", "CHAT_END");
            endSignal.put("maxTurns", room.getMaxTurns());
            endSignal.put("roomId", roomId);

            messagingTemplate.convertAndSend("/sub/room/" + roomId, endSignal);
            handleChatEndBadge(room);
        }
        room.updateLastMessage(content);
        return saved;
    }

    //대화 연장 요청
    public void processExtendRequest(Long roomId, Long userId) {
        // 1. 방 존재 여부 확인
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        // 2. 해당 유저가 이 방의 참여자인지 확인 ---> 굳이 이 로직이 필요한가 싶음
        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 채팅방 참여자가 아닙니다.");
        }

        if (room.getMaxTurns() > 3) {
            return;
        }

        // 3. 동의 목록에 유저 추가
        Set<Long> agreedUsers = extendConsensus.computeIfAbsent(roomId, k -> new HashSet<>());
        agreedUsers.add(userId);

        // 4. 두 명 모두 동의했는지 확인
        if (agreedUsers.size() >= 2) {
            // 합의 완료: 메모리 비우기
            extendConsensus.remove(roomId);

            // [중요] 연장을 위해 방 상태를 다시 활성화 (필요 시)
            room.setOvered(false);
            room.setMaxTurns(room.getMaxTurns() + 25);
            room.setExtended(true);
            // chatRoomRepository.save(room);

            // 5. 클라이언트에 연장 완료 신호 전송
            Map<String, Object> extendSignal = new HashMap<>();
            extendSignal.put("type", "EXTEND_COMPLETE");
            extendSignal.put("roomId", roomId);
            extendSignal.put("newMaxTurns", room.getMaxTurns());
            extendSignal.put("message", "대화가 연장되었습니다! 다시 대화를 시작해보세요.");

            messagingTemplate.convertAndSend("/sub/room/" + roomId, extendSignal);
        }
        // 한 명만 눌렀을 때는 아무 메시지도 보내지 않거나,
        // 상대방 대기 모달을 유지하기 위해 서버에서 기록만 유지합니다.
    }

    //연장 요청을 한 명만 한 경우
    public void processRejectExtension(Long roomId) {
        extendConsensus.remove(roomId);

        chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .ifPresent(room -> {
                    room.endChat(); // endedAt 설정
                    handleChatEndBadge(room);
                });

        Map<String, Object> rejectSignal = new HashMap<>();
        rejectSignal.put("type", "EXTEND_REJECTED");
        rejectSignal.put("message", "상대방이 연장을 원하지 않아 대화가 종료되었습니다.");

        messagingTemplate.convertAndSend("/sub/room/" + roomId, rejectSignal);
    }

    private void handleChatEndBadge(ChatRoom room) {
        User user1 = room.getUser1();
        User user2 = room.getUser2();
        if (user2 == null) return; // 매칭 안 된 방은 스킵

        // UserActivity 카운트 업데이트
        updateChatActivity(room, user1);
        updateChatActivity(room, user2);

        // MISSION_KEYWORD 체크
        if (room.getUser1Mission() != null) {
            badgeGrantService.checkMissionKeywordBadge(
                    user1, room.getUser1Mission().getCategory());
        }
        if (room.getUser2Mission() != null) {
            badgeGrantService.checkMissionKeywordBadge(
                    user2, room.getUser2Mission().getCategory());
        }

        // CHAT_PATTERN 체크
        badgeGrantService.checkChatPatternBadge(user1);
        badgeGrantService.checkChatPatternBadge(user2);

        //새싹/대화왕 뱃지 체크
        badgeGrantService.checkAttendanceBadge(user1);
        badgeGrantService.checkAttendanceBadge(user2);
    }

    //뱃지 : totalChat++, night/morning/longChat++
    private void updateChatActivity(ChatRoom room, User user) {
        UserActivity activity = userActivityRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "userActivity not found: userId= "+user.getId()));

        activity.incrementTotalChat();

        // 야행성/아침형 판단 (채팅방 생성 시간 기준)
        int hour = room.getEndedAt() != null
                ? room.getEndedAt().getHour()
                : LocalDateTime.now().getHour();
        if (hour >= 22 || hour < 6) {
            activity.incrementNightChat();
        } else if (hour >= 7 && hour < 10) {
            activity.incrementMorningChat();
        }

        userActivityRepository.save(activity);
    }
}
