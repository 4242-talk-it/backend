package com.talkit.app.domain.community.service;

import static com.talkit.app.global.exception.ExceptionType.NOT_FOUND_USER;

import com.talkit.app.domain.community.dto.CommunityRequestDto;
import com.talkit.app.domain.community.dto.CommunityResponseDto;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.repository.CommunityRepository;
import com.talkit.app.domain.user.entity.User;
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
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, Long userId) {
        System.out.println("조회하려는 유저 ID: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("DB에서 유저를 찾지 못함. ID: " + userId);
                    return NOT_FOUND_USER.of();
                });
        //.orElseThrow(NOT_FOUND_USER::of);

        Community community = Community.of(
            user,
            requestDto.title(),
            requestDto.content(),
            requestDto.category()
        );

        communityRepository.save(community);
        return CommunityResponseDto.of(community, userId);
    }

}
