package com.talkit.app.security.jwt.service;

import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.global.exception.BusinessLogicException;
import com.talkit.app.global.exception.ExceptionType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtTokenizer jwtTokenizer;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final UserRepository userRepository;

    public String getAccessToken() {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new BusinessLogicException(ExceptionType.ACCESS_TOKEN_NOT_FOUND);
    }

    public Long getUserIdFromAccessToken() {
        String token = getAccessToken();
        return jwtTokenizer.getUserIdFromAccessToken(token);
    }

    public String getRefreshToken() {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new BusinessLogicException(ExceptionType.REFRESH_TOKEN_NOT_FOUND);
    }

    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken();

        if (!jwtTokenizer.validateRefreshToken(refreshToken)) {
            throw new BusinessLogicException(ExceptionType.REFRESH_TOKEN_NOT_FOUND);
        }

        Long userId = jwtTokenizer.getUserIdFromRefreshToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

        String newAccess = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getNickname());

        // 액세스 토큰 수명에 맞춰 쿠키 갱신
        setCookie("accessToken", newAccess, JwtTokenizer.ACCESS_TOKEN_EXPIRE_TIME / 1000);
        response.addHeader("Authorization", "Bearer " + newAccess);
    }

    // 만료 시간(maxAgeInSeconds)을 인자로 받도록 수정
    public void setCookie(String name, String value, long maxAgeInSeconds) {
        String cleanValue = value.replace("Bearer ", "").trim();
        ResponseCookie cookie = ResponseCookie.from(name, cleanValue)
            .path("/")
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .maxAge(maxAgeInSeconds)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void expireCookie(HttpServletResponse response, String name) {
        // 처음 생성할 때와 동일한 path("/") 설정을 유지해야 삭제됨
        ResponseCookie cookie = ResponseCookie.from(name, "")
            .path("/")
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .maxAge(0) // 즉시 삭제 명령
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void createLoginTokens(User user) {
        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getNickname());
        String refreshToken = jwtTokenizer.createRefreshToken(user.getId(), user.getEmail(), user.getNickname());

        // 각 토큰의 실제 만료 시간에 맞춰 쿠키 생성
        setCookie("accessToken", accessToken, JwtTokenizer.ACCESS_TOKEN_EXPIRE_TIME / 1000);
        setCookie("refreshToken", refreshToken, JwtTokenizer.REFRESH_TOKEN_EXPIRE_TIME / 1000);

        response.addHeader("Authorization", "Bearer " + accessToken);
    }
}
