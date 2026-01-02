package com.talkit.app.domain.user.dto;

import com.talkit.app.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String birthYear;
    private String gender;

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .birthYear(user.getBirthYear())
            .gender(user.getGender())
            .build();
    }

}
