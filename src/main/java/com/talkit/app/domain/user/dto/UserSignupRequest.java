package com.talkit.app.domain.user.dto;

import lombok.Getter;

@Getter
public class UserSignupRequest {

    private String email;
    private String password;
    private String nickName;
    private String birthYear;
    private String gender;
}
