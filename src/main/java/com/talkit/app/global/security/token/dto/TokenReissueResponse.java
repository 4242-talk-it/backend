package com.talkit.app.global.security.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueResponse {
    private String accessToken;
    private String refreshToken;
}
