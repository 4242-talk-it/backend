package com.talkit.app.domain.user.controller;

import com.talkit.app.domain.user.dto.UserRequestDto;
import com.talkit.app.domain.user.dto.UserResponseDto;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserService;
import com.talkit.app.global.auth.AuthenticatedUser;
import com.talkit.app.global.auth.AuthenticationHolder;
import com.talkit.app.global.dto.ResponseDto;
import com.talkit.app.security.jwt.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseDto<Void> signup(@RequestBody UserRequestDto.Signup request) {
        userService.signup(request);
        return ResponseDto.of(null, "회원가입이 성공적으로 완료되었습니다.");
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseDto<Map<String, Object>> login(@RequestBody UserRequestDto.Login request) {
        User user = userService.login(request);

        // 토큰 생성 및 쿠키 설정
        tokenService.createLoginTokens(user);

        UserResponseDto userResponse = UserResponseDto.from(user);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("user", userResponse);

        return ResponseDto.of(responseBody, "로그인이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        tokenService.expireCookie(response, "accessToken");
        tokenService.expireCookie(response, "refreshToken");
        return ResponseEntity.ok().body("로그아웃이 완료되었습니다.");
    }

    @Operation(summary = "닉네임 수정")
    @AuthenticatedUser // 커뮤니티 컨트롤러처럼 인증 어노테이션 추가
    @PatchMapping("/nickname")
    public ResponseDto<String> updateNickname(@RequestBody UserRequestDto.UpdateNickname request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        String updatedNickname = userService.updateNickname(userId, request);

        return ResponseDto.of(updatedNickname, "닉네임이 성공적으로 수정되었습니다.");
    }
}
