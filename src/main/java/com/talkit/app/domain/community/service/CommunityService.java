package com.talkit.app.domain.community.service;

import com.talkit.app.domain.community.dto.CommunityRequestDto;
import com.talkit.app.domain.community.dto.CommunityResponseDto;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.repository.CommunityRepository;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto) {
/*        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);*/

        Community community = Community.of(
            requestDto.title(),
            requestDto.content(),
            requestDto.category()
        );

        communityRepository.save(community);
        return CommunityResponseDto.of(community);
    }

}
