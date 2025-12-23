package com.talkit.app.domain.user.controller;

import com.talkit.app.domain.user.dto.UserLoginRequest;
import com.talkit.app.domain.user.dto.UserSignupRequest;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserService;
import com.talkit.app.security.jwt.dto.response.TokenResponseDto;
import com.talkit.app.security.jwt.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/login")
    public  ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        User user = userService.login(request);
        String accessToken = tokenService.createAccessToken(user);
        return ResponseEntity.ok()
            .header("Authorization", "Bearer " + accessToken)
            .body("로그인 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        request.getSession(false).invalidate();
        return ResponseEntity.ok().build();
    }
}
