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
        // 1. 게시글 존재 확인
        Community community = communityRepository.findById(communityId)
                .orElseThrow(NOT_FOUND_COMMUNITY::of);

        // 2. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(NOT_FOUND_USER::of);

        // 3. 댓글 생성 및 저장
        Comment comment = Comment.builder()
                .community(community)
                .user(user)
                .content(requestDto.content())
                .build();

        communityCommentRepository.save(comment);

        // 4. 응답 DTO 반환
        return CommunityCommentResponseDto.of(comment, userId);
    }

    //댓글 불러오기
    public PageResponseDto<CommunityCommentResponseDto> getComments(Long communityId, Long userId, Pageable pageable) {
        // 게시글이 있는지 먼저 확인 (선택 사항)
        if (!communityRepository.existsById(communityId)) {
            throw NOT_FOUND_COMMUNITY.of();
        }

        // DB에서 해당 게시글의 댓글들을 페이징하여 가져와 DTO로 변환
        return PageResponseDto.of(
                communityCommentRepository.findByCommunityId(communityId, pageable)
                        .map(comment -> CommunityCommentResponseDto.of(comment, userId))
        );
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 1. 댓글 존재 확인
        Comment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 2. 작성자 본인인지 권한 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        // 3. 삭제 처리
        communityCommentRepository.delete(comment);
    }
}