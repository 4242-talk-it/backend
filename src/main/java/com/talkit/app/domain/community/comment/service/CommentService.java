package com.talkit.app.domain.community.comment.service;

import com.talkit.app.domain.community.comment.dto.CommunityCommentRequestDto;
import com.talkit.app.domain.community.comment.dto.CommunityCommentResponseDto;
import com.talkit.app.domain.community.entity.Comment;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.comment.repository.CommentRepository;
import com.talkit.app.domain.community.base.repository.CommunityRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
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

        commentRepository.save(comment);

        return CommunityCommentResponseDto.of(comment, userId);
    }

    public PageResponseDto<CommunityCommentResponseDto> getComments(Long communityId, Long userId, Pageable pageable) {
        if (!communityRepository.existsById(communityId)) {
            throw NOT_FOUND_COMMUNITY.of();
        }

        return PageResponseDto.of(
                commentRepository.findByCommunityId(communityId, pageable)
                        .map(comment -> CommunityCommentResponseDto.of(comment, userId))
        );
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}