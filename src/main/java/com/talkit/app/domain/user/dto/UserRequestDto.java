package com.talkit.app.domain.user.dto;

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
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }
}
