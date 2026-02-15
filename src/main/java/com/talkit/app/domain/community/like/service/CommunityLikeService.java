package com.talkit.app.domain.community.like.service;

import com.talkit.app.domain.community.base.service.CommunityService;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.like.dto.CommunityLikeResponseDto;
import com.talkit.app.domain.community.entity.CommunityLike;
import com.talkit.app.domain.community.like.repository.CommunityLikeRepository;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityLikeService {

    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityService communityService;
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
            Community community = communityService.getCommunity(communityId);
            communityLikeRepository.save(CommunityLike.of(user, community));
        }
        return CommunityLikeResponseDto.of(communityId);
    }

}
