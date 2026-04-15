package com.talkit.app.domain.chatting.userChat.service;

import com.talkit.app.domain.chatting.badge.service.BadgeGrantService;
import com.talkit.app.domain.chatting.userChat.entity.ChatFeedback;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.entity.SpecialTagType;
import com.talkit.app.domain.chatting.userChat.repository.ChatFeedbackRepository;
import com.talkit.app.domain.chatting.userChat.repository.ChatRoomRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.entity.UserActivity;
import com.talkit.app.domain.user.repository.UserActivityRepository;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatFeedbackService {

    private final ChatFeedbackRepository chatFeedbackRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;
    private final BadgeGrantService badgeGrantService;

    public void submitFeedback(Long roomId, Long writerId, List<String> tags, String comment) {

        if(chatFeedbackRepository.existsByChatRoomRoomIdAndWriterId(roomId,writerId)) {
            throw new IllegalStateException("이미 피드백을 제출하셨습니다.");
        }

        ChatRoom room=chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 방입니다."));
        User writer = userRepository.findById(writerId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 유저입니다"));

        User target = room.getUser1().getId().equals(writerId) ? room.getUser2() : room.getUser1();
        List<String> filteredTags = tags.stream()

                .filter(t -> t != null && !t.isBlank())
                .toList();

        SpecialTagType tag1 = !filteredTags.isEmpty() ? SpecialTagType.valueOf(filteredTags.get(0).toUpperCase()) : null;
        SpecialTagType tag2 = filteredTags.size() > 1 ? SpecialTagType.valueOf(filteredTags.get(1).toUpperCase()) : null;


        ChatFeedback feedback = ChatFeedback.builder()
                .chatRoom(room)
                .writer(writer)
                .target(target)
                .specialTagType1(tag1)
                .specialTagType2(tag2)
                .comment(comment)
                .build();
        chatFeedbackRepository.save(feedback);

        if(tag1 != null) incrementAndCheck(target, tag1);
        if(tag2 != null) incrementAndCheck(target, tag2);

    }
    private void incrementAndCheck(User target, SpecialTagType tag) {
        UserActivity activity = userActivityRepository.findByUserId(target.getId())
                .orElseThrow(()-> new IllegalStateException(
                        "userActivity not found: userId = "+target.getId()));

        switch (tag) {
            case GAG -> activity.incrementGagFeedback();
            case QUESTION -> activity.incrementQuestionFeedback();
            case ZZZ -> activity.incrementYawnFeedback();
            case REPEAT -> activity.incrementRepeatFeedback();
        }
        userActivityRepository.save(activity);

        badgeGrantService.checkFeedbackSpecialBadge(target);
    }
}
