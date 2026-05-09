package com.talkit.app.domain.chatting.userChat.service;

import com.talkit.app.domain.chatting.badge.service.BadgeGrantService;
import com.talkit.app.domain.chatting.userChat.dto.ChatHistoryResponse;
import com.talkit.app.domain.chatting.userChat.dto.ChatRoomDetailResponse;
import com.talkit.app.domain.chatting.userChat.dto.ChatRoomResponse;
import com.talkit.app.domain.chatting.userChat.dto.MyChatRoomResponse;
import com.talkit.app.domain.chatting.userChat.entity.*;
import com.talkit.app.domain.chatting.badge.entity.MissionKeyword;
import com.talkit.app.domain.chatting.userChat.repository.*;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.entity.UserActivity;
import com.talkit.app.domain.user.entity.UserTemperatureHistory;
import com.talkit.app.domain.user.repository.UserActivityRepository;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.domain.user.repository.UserTemperatureHistoryRepository;
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
    private final ChatReviewRepository chatReviewRepository;
    private final ChatFeedbackRepository chatFeedbackRepository;
    private final UserTemperatureHistoryRepository userTemperatureHistoryRepository;

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

            // 매칭 완료 신호 전송
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

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return rooms.stream().map(room -> {
            String lastMsg = room.getLastMessage();
            if (lastMsg != null && lastMsg.length() > 20) {
                lastMsg = lastMsg.substring(0, 20) + "...";
            }
            boolean hasUnread = chatMessageRepository
                    .existsByChatRoomAndIsReadFalseAndSenderNot(room, currentUser);

            return new MyChatRoomResponse(
                    room.getRoomId(),
                    room.getTopic(),
                    lastMsg != null ? lastMsg : "대화를 시작해보세요!",
                    formatTime(room.getUpdatedAt()),
                    userId,
                    hasUnread
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        chatMessageRepository.markAsReadByChatRoomAndSenderNot(room, user);
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

        User targetUser;
        MissionKeyword actualMission;

        if (room.getUser1().getId().equals(myId)) {
            targetUser = room.getUser2();
            actualMission = room.getUser2Mission();
        } else {
            targetUser = room.getUser1();
            actualMission = room.getUser1Mission();
        }

        boolean isSuccess = actualMission.getKeyword().trim().equals(guessedKeyword.trim());

        UserMission result = UserMission.builder()
                .user(targetUser)
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

        final String finalMyKeyword = myKeyword;
        final String finalTargetKeyword = targetKeyword;

        List<String> distractors = missionKeywordRepository.findAll().stream()
                .map(MissionKeyword::getKeyword)
                .filter(k -> !k.equals(finalMyKeyword) && !k.equals(finalTargetKeyword))
                .collect(Collectors.toList());

        Collections.shuffle(distractors);
        List<String> options = new ArrayList<>(distractors.subList(0, Math.min(3, distractors.size())));

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
                room.setOvered(true);
            }
            throw new IllegalStateException("최대 대화 횟수에 도달했습니다.");
        }

        List<ChatMessage> lastTalks = chatMessageRepository.findTop3ByChatRoomAndTypeOrderByTimestampDesc(room, MessageType.TALK);

        long continuousCount = lastTalks.stream()
                .filter(m -> m.getSender() != null && m.getSender().getId().equals(userId))
                .count();

        if (continuousCount >= 3) {
            throw new IllegalStateException("상대방의 답변을 기다려야 합니다.");
        }

        if(room.getUser2() == null) {
            throw new IllegalArgumentException("아직 상대 매칭이 되지 않았습니다.");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

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

        Long receiverId = room.getUser1().getId().equals(userId)
                ? room.getUser2().getId()
                : room.getUser1().getId();

        if (totalMessages + 1 >= room.getMaxTurns()) {
            room.endChat();

            Map<String, Object> endSignal = new HashMap<>();
            endSignal.put("type", "CHAT_END");
            endSignal.put("maxTurns", room.getMaxTurns());
            endSignal.put("roomId", roomId);

            messagingTemplate.convertAndSend("/sub/room/" + roomId, endSignal);
            messagingTemplate.convertAndSend("/sub/user/" + userId + "/event", endSignal);
            messagingTemplate.convertAndSend("/sub/user/" + receiverId + "/event", endSignal);



            handleChatEndBadge(room);
        }
        room.updateLastMessage(content);

// 상대방 사이드바 갱신 신호
        Map<String, Object> sidebarSignal = new HashMap<>();
        sidebarSignal.put("type", "SIDEBAR_UPDATE");
        sidebarSignal.put("roomId", roomId);
        sidebarSignal.put("lastMessage", content.length() > 20 ? content.substring(0, 20) + "..." : content);
        sidebarSignal.put("hasUnread", true); // 상대방은 항상 읽지 않은 상태

        messagingTemplate.convertAndSend("/sub/user/" + receiverId + "/sidebar", sidebarSignal);

        return saved;
    }

    //대화 연장 요청
    public void processExtendRequest(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        if (!room.getUser1().getId().equals(userId) && !room.getUser2().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 채팅방 참여자가 아닙니다.");
        }

        if (room.getMaxTurns() > 40) {
            return;
        }

        Set<Long> agreedUsers = extendConsensus.computeIfAbsent(roomId, k -> new HashSet<>());
        agreedUsers.add(userId);

        Long waitingReceiverId = room.getUser1().getId().equals(userId)
                ? room.getUser2().getId()
                : room.getUser1().getId();

        Map<String, Object> waitingSignal = new HashMap<>();
        waitingSignal.put("type", "EXTEND_WAITING");
        waitingSignal.put("roomId", roomId);
        messagingTemplate.convertAndSend("/sub/user/" + waitingReceiverId + "/event", waitingSignal);

        //두 명 모두 동의했는지 확인
        if (agreedUsers.size() >= 2) {
            extendConsensus.remove(roomId);

            room.setOvered(false);
            room.setMaxTurns(room.getMaxTurns() + 25);
            room.setExtended(true);

            saveSystemMessage(room, "💬 대화가 종료되었습니다.");
            saveSystemMessage(room, "🎉 대화가 연장되었습니다! 계속 대화를 나눠보세요.");

            Map<String, Object> extendSignal = new HashMap<>();
            extendSignal.put("type", "EXTEND_COMPLETE");
            extendSignal.put("roomId", roomId);
            extendSignal.put("newMaxTurns", room.getMaxTurns());
            extendSignal.put("message", "대화가 연장되었습니다! 다시 대화를 시작해보세요.");

            messagingTemplate.convertAndSend("/sub/room/" + roomId, extendSignal);

            Long user1Id = room.getUser1().getId();
            Long user2Id = room.getUser2().getId();
            messagingTemplate.convertAndSend("/sub/user/" + user1Id + "/event", extendSignal);
            messagingTemplate.convertAndSend("/sub/user/" + user2Id + "/event", extendSignal);
        }
    }

    //연장 요청을 한 명만 한 경우
    public void processRejectExtension(Long roomId) {
        extendConsensus.remove(roomId);

        chatRoomRepository.findByIdWithMissionKeyword(roomId).ifPresent(room -> {
            room.endChat();
            saveSystemMessage(room, "상대방이 연장을 원하지 않아 대화가 종료되었습니다.");

            // 종료 신호
            Map<String, Object> rejectSignal = new HashMap<>();
            rejectSignal.put("type", "EXTEND_REJECTED");
            rejectSignal.put("roomId", roomId);

            // 두 사용자 모두에게 이벤트 채널로 전송
            messagingTemplate.convertAndSend("/sub/user/" + room.getUser1().getId() + "/event", rejectSignal);
            if (room.getUser2() != null) {
                messagingTemplate.convertAndSend("/sub/user/" + room.getUser2().getId() + "/event", rejectSignal);
            }
        });
    }

    @Transactional
    public void forceEndChat(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        if (!room.getUser1().getId().equals(userId) &&
                (room.getUser2() == null || !room.getUser2().getId().equals(userId))) {
            throw new IllegalArgumentException("해당 채팅방 참여자가 아닙니다.");
        }

        // 채팅방 종료
        room.endChat();

        saveSystemMessage(room, "💬 대화가 종료되었습니다.");

        // 강제 종료한 유저 온도 3도 감소
        User forceEndUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        forceEndUser.decreaseTemperature(3); // User 엔티티에 메서드 필요 (아래 참고)
        userRepository.save(forceEndUser);

        // 상대방에게 종료 신호 전송
        Map<String, Object> endSignal = new HashMap<>();
        endSignal.put("type", "CHAT_END");
        endSignal.put("maxTurns", room.getMaxTurns());
        endSignal.put("roomId", roomId);
        endSignal.put("forced", true);
        messagingTemplate.convertAndSend("/sub/room/" + roomId, endSignal);

        Long otherUserId = room.getUser1().getId().equals(userId)
                ? room.getUser2().getId() : room.getUser1().getId();
        messagingTemplate.convertAndSend("/sub/user/" + userId + "/event", endSignal);
        messagingTemplate.convertAndSend("/sub/user/" + otherUserId + "/event", endSignal);

        forceEndUser.decreaseTemperature(3);
        userRepository.save(forceEndUser);

        UserTemperatureHistory history = UserTemperatureHistory.builder()
                .user(forceEndUser)
                .temperature(forceEndUser.getTemperature())
                .recordedAt(LocalDateTime.now())
                .build();
        userTemperatureHistoryRepository.save(history);
    }

    //종료/연장 시 SystemMessage 저장
    public void saveSystemMessage(ChatRoom room, String content) {
        ChatMessage systemMsg = ChatMessage.builder()
                .chatRoom(room)
                .sender(null) // 시스템은 발신자 없음
                .message(content)
                .type(MessageType.SYSTEM)
                .timestamp(LocalDateTime.now())
                .isRead(true)
                .build();
        chatMessageRepository.save(systemMsg);

        Map<String, Object> sysMsgMap = new HashMap<>();
        sysMsgMap.put("type", "system");
        sysMsgMap.put("message", content);
        sysMsgMap.put("timestamp", LocalDateTime.now());

    }

    private void handleChatEndBadge(ChatRoom room) {
        User user1 = room.getUser1();
        User user2 = room.getUser2();
        if (user2 == null) return;

        updateChatActivity(room, user1);
        updateChatActivity(room, user2);

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

    //MyPage 채팅 List 불러오기
    public List<ChatHistoryResponse> getMyChatHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        List<ChatRoom> endedRooms = chatRoomRepository.findEndedRoomsByUser(user);

        return endedRooms.stream().map(room -> {
            // 상대방이 나를 평가한 emotion 조회
            EmotionType emotion = chatReviewRepository
                    .findByChatRoomAndTarget(room, user)
                    .stream()
                    .findFirst()
                    .map(ChatReview::getEmotion)
                    .orElse(null);

            // TALK 타입 메시지 수
            long messageCount = chatMessageRepository.countNonSystemMessages(room);

            return ChatHistoryResponse.of(room, emotion, messageCount);
        }).collect(Collectors.toList());
    }

    //채팅기록 상세 불러오기
    public ChatRoomDetailResponse getChatRoomDetail(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findByIdWithMissionKeyword(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 내 키워드 미션
        String myMissionKeyword = null;
        if (room.getUser1().getId().equals(userId) && room.getUser1Mission() != null) {
            myMissionKeyword = room.getUser1Mission().getKeyword();
        } else if (room.getUser2() != null && room.getUser2().getId().equals(userId) && room.getUser2Mission() != null) {
            myMissionKeyword = room.getUser2Mission().getKeyword();
        }

        // 상대방이 나에게 준 평가
        EmotionType opponentEmotion = chatReviewRepository
                .findByChatRoomAndTarget(room, user)
                .stream()
                .findFirst()
                .map(ChatReview::getEmotion)
                .orElse(null);

        // 상대방이 제출한 키워드 추측
        String opponentGuessedKeyword = userMissionRepository
                .findByChatRoomAndUser(room, user)
                .map(UserMission::getGuessedKeyword)
                .orElse(null);

        // 총 대화 시간 계산
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomOrderByTimestampAsc(room);
        long durationMinutes = 0;
        if (messages.size() >= 2) {
            LocalDateTime first = messages.get(0).getTimestamp();
            LocalDateTime last = messages.get(messages.size() - 1).getTimestamp();
            durationMinutes = java.time.Duration.between(first, last).toMinutes();
        }

        // 상대방이 나에게 쓴 피드백
        Optional<ChatFeedback> feedback = chatFeedbackRepository
                .findByChatRoomAndTarget(room, user);

        SpecialTagType tag1 = feedback.map(ChatFeedback::getSpecialTagType1).orElse(null);
        SpecialTagType tag2 = feedback.map(ChatFeedback::getSpecialTagType2).orElse(null);
        String comment = feedback.map(ChatFeedback::getComment).orElse(null);


        return new ChatRoomDetailResponse(
                userId,
                myMissionKeyword,
                opponentGuessedKeyword,
                opponentEmotion,
                durationMinutes,
                tag1,
                tag2,
                comment
        );
    }

    public Map<String, Object> getMyPageStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        //EmotionType 집계 (긍정/보통/부정)
        List<EmotionType> emotions = chatReviewRepository.findEmotionsByTargetId(userId);
        long positive = emotions.stream().filter(e -> e == EmotionType.GREAT || e == EmotionType.GOOD).count();
        long normal   = emotions.stream().filter(e -> e == EmotionType.NORMAL).count();
        long negative = emotions.stream().filter(e -> e == EmotionType.BAD   || e == EmotionType.TERRIBLE).count();

        List<Map<String, Object>> monthlyStats =
                userTemperatureHistoryRepository.findMonthlyAverageNative(userId, LocalDateTime.now().minusMonths(5));

        Map<String, Object> result = new HashMap<>();
        result.put("temperature", user.getTemperature());
        result.put("monthlyTemperatures", monthlyStats);
        result.put("positiveCount", positive);
        result.put("normalCount", normal);
        result.put("negativeCount", negative);
        result.put("totalEmotions", emotions.size());
        return result;
    }
}
