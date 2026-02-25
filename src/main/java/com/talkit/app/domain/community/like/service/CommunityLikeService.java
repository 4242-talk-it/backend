package com.talkit.app.domain.community.like.service;

import com.talkit.app.domain.community.base.repository.CommunityRepository;
import com.talkit.app.domain.community.base.service.CommunityService;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.like.dto.CommunityLikeResponseDto;
import com.talkit.app.domain.community.entity.CommunityLike;
import com.talkit.app.domain.community.like.repository.CommunityLikeRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_COMMUNITY;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityLikeService {

    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;

    public boolean isLiked(Long id, Long userId) {
        return communityLikeRepository.findByUserIdAndCommunityId(userId, id).isPresent();
    }

    public long getLikeCount(Long id) {
        return communityLikeRepository.countByCommunityId(id);
    }

    @Transactional
    public CommunityLikeResponseDto toggleLike(Long communityId, Long userId) {
        Optional<CommunityLike> like = communityLikeRepository.findByUserIdAndCommunityId(userId, communityId);

        if (like.isPresent()) {
            communityLikeRepository.delete(like.get());
        } else {
            User user = userService.findUserById(userId);
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(NOT_FOUND_COMMUNITY::of);
            communityLikeRepository.save(CommunityLike.of(user, community));
        }
        return CommunityLikeResponseDto.of(communityId);
    }

    public List<CommunityLike> getCommunityLikesByUserId(Long userId) {
        return userId.equals(User.ANONYMOUS_USER_ID)
                ? new ArrayList<>()
                : communityLikeRepository.findCommunityLikesByUserId(userId);
    }

}
