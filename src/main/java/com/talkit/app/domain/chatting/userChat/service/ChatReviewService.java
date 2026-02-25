package com.talkit.app.domain.chatting.userChat.service;

import com.talkit.app.domain.chatting.userChat.entity.ChatReview;
import com.talkit.app.domain.chatting.userChat.entity.ChatRoom;
import com.talkit.app.domain.chatting.userChat.entity.EmotionType;
import com.talkit.app.domain.chatting.userChat.repository.ChatReviewRepository;
import com.talkit.app.domain.chatting.userChat.repository.ChatRoomRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatReviewService {
    private final ChatReviewRepository chatReviewRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public void submitReview (Long roomId, Long writerId, String emotionDescription) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 방입니다."));
        User writer = userRepository.findById(writerId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다."));

        User target = room.getUser1().getId().equals(writerId) ? room.getUser2() : room.getUser1();
        if (target == null) {
            throw new IllegalArgumentException("상대방이 존재하지 않습니다.");
        }

        EmotionType emotionType = EmotionType.fromDescription(emotionDescription);
        double delta=emotionType.getTemperature();

        ChatReview review = ChatReview.builder()
                .chatRoom(room)
                .writer(writer)
                .target(target)
                .emotion(emotionType)
                .temperatureDelta(delta)
                .build();
        chatReviewRepository.save(review);

        target.updateTemperature(delta);

    }
}
