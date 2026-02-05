package com.talkit.app.domain.community.service;

import com.talkit.app.domain.community.dto.CommunityCommentRequestDto;
import com.talkit.app.domain.community.dto.CommunityCommentResponseDto;
import com.talkit.app.domain.community.entity.Comment;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.repository.CommunityCommentRepository;
import com.talkit.app.domain.community.repository.CommunityRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.global.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_COMMUNITY;
import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;


    //댓글 작성
    @Transactional
    public CommunityCommentResponseDto createComment(Long communityId, CommunityCommentRequestDto requestDto, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(NOT_FOUND_COMMUNITY::of);

        User user = userRepository.findById(userId)
                .orElseThrow(NOT_FOUND_USER::of);

        Comment comment = Comment.builder()
                .community(community)
                .user(user)
                .content(requestDto.content())
                .build();

        communityCommentRepository.save(comment);

        return CommunityCommentResponseDto.of(comment, userId);
    }

    public PageResponseDto<CommunityCommentResponseDto> getComments(Long communityId, Long userId, Pageable pageable) {
        if (!communityRepository.existsById(communityId)) {
            throw NOT_FOUND_COMMUNITY.of();
        }

        return PageResponseDto.of(
                communityCommentRepository.findByCommunityId(communityId, pageable)
                        .map(comment -> CommunityCommentResponseDto.of(comment, userId))
        );
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        communityCommentRepository.delete(comment);
    }
}