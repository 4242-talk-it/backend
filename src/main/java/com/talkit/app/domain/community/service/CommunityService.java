package com.talkit.app.domain.community.service;

import com.talkit.app.domain.community.dto.CommunityRequestDto;
import com.talkit.app.domain.community.dto.CommunityResponseDto;
import com.talkit.app.domain.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        Community community = Community.of(
            user,
            requestDto.title(),
            requestDto.content(),
            imageUrl,
            requestDto.isPublic()
        );

        communityRepository.save(community);
        return CommunityResponseDto.of(community, userId);
    }

}
