package com.talkit.app.domain.community.bookmark.service;

import com.talkit.app.domain.community.base.repository.CommunityRepository;
import com.talkit.app.domain.community.bookmark.dto.BookmarkResponseDto;
import com.talkit.app.domain.community.bookmark.repository.BookmarkRepository;
import com.talkit.app.domain.community.entity.Bookmark;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_COMMUNITY;
import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleBookMark(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(NOT_FOUND_COMMUNITY::of);
        User user = userRepository.findById(userId)
                .orElseThrow(NOT_FOUND_USER::of);

        return bookmarkRepository.findByUserAndCommunity(user, community)
                .map(bookMark -> {
                    bookmarkRepository.delete(bookMark);
                    return false;
                })
                .orElseGet(() -> {
                    bookmarkRepository.save(Bookmark.of(user, community));
                    return true;
                });
    }

    public List<BookmarkResponseDto> getMyBookMarks(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NOT_FOUND_USER::of);

        return bookmarkRepository.findAllByUser(user).stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .map(BookmarkResponseDto::of)
                .toList();
    }

    public boolean isBookmarked(Long communityId, Long userId) {
        return bookmarkRepository.existsByCommunityIdAndUserId(communityId, userId);
    }
}
