package com.talkit.app.domain.community.service;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_COMMUNITY;
import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_USER;
import static com.talkit.app.global.exception.ExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;

import com.talkit.app.domain.community.dto.*;
import com.talkit.app.domain.community.entity.Comment;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.repository.CommunityCommentRepository;
import com.talkit.app.domain.community.repository.CommunityRepository;
import com.talkit.app.domain.like.entity.CommunityLike;
import com.talkit.app.domain.like.repository.CommunityLikeRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.global.page.dto.PageRequestVO;
import com.talkit.app.global.page.dto.PageResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserRepository userRepository;

    public CommunityResponseDto getCommunityById(Long id, Long userId) {
        Community community = getCommunity(id);

        return CommunityResponseDto.of(community, userId);
    }

    public PageResponseDto<CommunityListResponseDto> pagesByCommunity(Long userId, PageRequestVO pageRequestVO) {
        List<CommunityLike> communityLikes = getCommunityLikesBy(userId);

        return PageResponseDto.of((communityRepository.findAllByOrderByCreatedAtDesc(pageRequestVO.toPageable()))
            .map(community -> {
                int likeCount = communityLikeRepository.countByCommunityId(community.getId());
                int commentCount = communityCommentRepository.countByCommunityId(community.getId());
                return CommunityListResponseDto.of(community, likeCount, commentCount, communityLikes);
            })
        );
    }

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(NOT_FOUND_USER::of);

        List<String> tagList = (requestDto.tags() != null) ? requestDto.tags() : new ArrayList<>();
        Community community = Community.of(
                user,
                requestDto.title(),
                requestDto.content(),
                requestDto.category(),
                tagList
        );

        communityRepository.save(community);
        return CommunityResponseDto.of(community, userId);
    }


    @Transactional
    public CommunityResponseDto updateCommunity(Long id, CommunityRequestDto requestDto, Long userId) {
        Community community = getCommunity(id);
        validateOwnership(community, userId);
        List<String> tagList = (requestDto.tags() != null) ? requestDto.tags() : new ArrayList<>();

        community.update(
            requestDto.title(),
            requestDto.content(),
            requestDto.category(),
            tagList
        );

        return CommunityResponseDto.of(community, userId);
    }

    public CommunityResponseDto getPostForEdit(Long id, Long userId) {
        Community community = getCommunity(id);
        validateOwnership(community, userId);

        return CommunityResponseDto.of(community, userId);
    }

    private Community getCommunity(Long id) {
        return communityRepository.findWithTagsById(id)
            .orElseThrow(NOT_FOUND_COMMUNITY::of);
    }

    private void validateOwnership(Community community, Long userId) {
        if (!community.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("게시물을 수정/삭제할 권한이 없습니다.");
        }
    }

    private List<CommunityLike> getCommunityLikesBy(Long userId) {
        return userId.equals(User.ANONYMOUS_USER_ID)
            ? new ArrayList<>()
            : communityLikeRepository.findCommunityLikesByUserId(userId);
    }

    @Transactional
    public void deleteCommunity(Long communityId, Long userId) {
        Community community = getCommunity(communityId);
        validateOwnership(community, userId);

        communityRepository.delete(community);
    }


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

    /**
     * 댓글 목록 조회 (페이징 적용)
     */
    public PageResponseDto<CommunityCommentResponseDto> getComments(Long communityId, Long userId, Pageable pageable) {
        // 게시글 존재 확인
        if (!communityRepository.existsById(communityId)) {
            throw NOT_FOUND_COMMUNITY.of();
        }

        // 리포지토리의 findByCommunityId 활용
        return PageResponseDto.of(
                communityCommentRepository.findByCommunityId(communityId, pageable)
                        .map(comment -> CommunityCommentResponseDto.of(comment, userId))
        );
    }

}