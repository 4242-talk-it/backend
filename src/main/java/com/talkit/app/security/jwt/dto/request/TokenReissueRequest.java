package com.talkit.app.security.jwt.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenReissueRequest {
    private String refreshToken;
}
