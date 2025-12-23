package com.talkit.app.security.jwt.service;

import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.domain.user.service.UserService;
import com.talkit.app.global.exception.BusinessLogicException;
import com.talkit.app.global.exception.ExceptionType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;
    private final UserRepository userRepository;


    // 쿠키에서 AccessToken을 추출
    public String getAccessToken() {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7); // "Bearer " 뒤의 토큰 값 추출
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }else if("refreshToken".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        throw new BusinessLogicException(ExceptionType.ACCESS_TOKEN_NOT_FOUND);
    }

    // 쿠키에서 토큰을 꺼내 디코딩
    public Long getUserIdFromAccessToken() {
        String token = getAccessToken();
        return jwtTokenizer.getUserIdFromAccessToken(token);
    }

    // 요청에서 RefreshToken을 추출
    public String getRefreshToken() {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7); // "Bearer " 뒤의 토큰 값 추출
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new BusinessLogicException(ExceptionType.REFRESH_TOKEN_NOT_FOUND);
    }

    // AccessToken에서 사용자 Email 추출
    public String getEmailFromAccessToken() {
        String token = getAccessToken();
        return jwtTokenizer.getEmailFromAccessToken(token);
    }

    // 특정 쿠키 제거
    public void deleteCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
            .path("/")
            .sameSite("None")
            .secure(true) // 로컬 HTTP 개발 시 false. HTTPS 프로덕션에선 true
            .httpOnly(true)
            .maxAge(0) // 즉시 만료
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    //로그인용 토큰 생성 메서드
    public String createAccessToken(User user) {
        return jwtTokenizer.createAccessToken(
            user.getId(),
            user.getEmail(),
            user.getNickname()
        );
    }

    //임시 토큰 용
    public void createTokenByUserRole() {
        User user = userService.findUserById(1L);
        log.info(user.getNickname());

        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getNickname());

        setCookie("accessToken", accessToken);
        response.addHeader("Authorization", "Bearer " + accessToken);
    }

    //임시 토큰용
    public void createTokenByAdminRole() {
        User user = userService.findUserById(2L);

        userRepository.save(user);
        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getNickname());

        setCookie("accessToken", accessToken);
        response.addHeader("Authorization", "Bearer " + accessToken);
    }

    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .sameSite("None")
            .secure(true) // 로컬 HTTP 개발 시 false. HTTPS 프로덕션에선 true
            .httpOnly(false)
            .maxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_TIME / 1000))
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}
