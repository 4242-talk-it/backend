package com.talkit.app.domain.user.controller;

import com.talkit.app.domain.user.dto.UserLoginRequest;
import com.talkit.app.domain.user.dto.UserResponseDto;
import com.talkit.app.domain.user.dto.UserSignupRequest;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserService;
import com.talkit.app.global.exception.BusinessLogicException;
import com.talkit.app.security.jwt.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "회원가입 및 로그인/로그아웃", description = "회원가입 및 로그인/로그아웃 관련 API")
@RestController
@RequestMapping("/api/users")
public class UserAuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @Operation(summary = "사이트 자체 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        System.out.println("회원가입 성공");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인 (Spring Security 처리)", description = "email, password를 넣으면 Spring Security가 자동 인증합니다.")
// UserAuthController.java

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            // 1. 유저 검증
            User user = userService.login(request);

            // 2. 토큰 생성 및 쿠키 설정 (중요: 이제 createLoginTokens 하나로 끝냅니다)
            // 이 메서드 안에서 Access/Refresh 토큰 생성 및 쿠키 설정이 다 이뤄져야 합니다.
            tokenService.createLoginTokens(user);

            // 3. UserResponseDto 사용 (엔티티 직접 반환 방지 -> no Session 에러 해결)
            UserResponseDto userResponse = UserResponseDto.from(user);

            Map<String, Object> responseBody = new HashMap<>();
            // createLoginTokens에서 쿠키와 헤더에 넣어주므로, 클라이언트에 전달할 값만 세팅
            responseBody.put("user", userResponse);

            return ResponseEntity.ok().body(responseBody);

        } catch (BusinessLogicException e) {
            Map<String, String> errorRes = new HashMap<>();
            errorRes.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        tokenService.expireCookie(response, "accessToken");
        tokenService.expireCookie(response, "refreshToken");
        return ResponseEntity.ok().body("로그아웃이 완료되었습니다.");
    }
}
