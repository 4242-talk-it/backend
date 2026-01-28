package com.talkit.app.global.security.token.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenReissueRequest {
    private String refreshToken;
}
