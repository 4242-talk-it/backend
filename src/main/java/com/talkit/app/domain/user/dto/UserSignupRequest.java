package com.talkit.app.domain.user.dto;

import lombok.Getter;

@Getter
public class UserSignupRequest {

    private String email;
    private String password;
    private String nickname;
    private String birthYear;
    private String gender;
}
