package com.talkit.app.global.security.token.controller;

import com.talkit.app.domain.user.dto.UserResponseDto;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserService;
import com.talkit.app.global.security.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "토큰 생성 API")
public class TokenController {

    private final TokenService tokenService;
    private final UserService userService;

    @Operation(summary = "현재 로그인 유저 정보 조회")
    @GetMapping("/status")
    public ResponseEntity<UserResponseDto> currentUser() {
        Long userId = tokenService.getUserIdFromAccessToken();
        if (userId == null) {
            return ResponseEntity.noContent().build();
        }

        User user = userService.findUserById(userId);
        return ResponseEntity.ok(UserResponseDto.from(user));
    }

    @Operation(summary = "Access Token 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        tokenService.reissueTokens(request, response);
        return ResponseEntity.ok().build();
    }

}
