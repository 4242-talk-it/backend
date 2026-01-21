package com.talkit.app.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRequestDto {

    @Getter
    public static class Signup {
        private String email;
        private String password;
        private String nickname;
        private String birthYear;
        private String gender;
    }

    @Getter
    public static class Login {
        private String email;
        private String password;
    }

    @Getter
    public static class UpdateNickname {
        private String nickname;
    }

    @Getter
    public static class UpdatePassword {
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        private String currentPassword;

        @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
        private String newPassword;
    }
}
