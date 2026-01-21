package com.talkit.app.domain.user.controller;

import com.talkit.app.domain.user.dto.UserRequestDto;
import com.talkit.app.domain.user.dto.UserResponseDto;
import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.service.UserDetailsImpl;
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
import org.apache.catalina.security.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "회원가입 및 로그인/로그아웃", description = "회원가입 및 로그인/로그아웃 및 프로필 수정 관련 API")
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
    @AuthenticatedUser
    @PatchMapping("/nickname")
    public ResponseDto<String> updateNickname(@RequestBody UserRequestDto.UpdateNickname request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        String updatedNickname = userService.updateNickname(userId, request);

        return ResponseDto.of(updatedNickname, "닉네임이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "현재 비밀번호 확인 (변경 전 검증)")
    @AuthenticatedUser
    @PostMapping("/verify-password")
    public ResponseDto<Boolean> verifyPassword(@RequestBody Map<String, String> request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        String currentPassword = request.get("currentPassword");
        userService.verifyPassword(userId, currentPassword);

        return ResponseDto.of(true, "비밀번호가 일치합니다.");
    }

    @Operation(summary = "비밀번호 변경")
    @AuthenticatedUser
    @PatchMapping("/update-password")
    public ResponseDto<Void> updatePassword(@RequestBody UserRequestDto.UpdatePassword request) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        userService.updatePassword(userId, request);

        return ResponseDto.of(null, "비밀번호가 성공적으로 변경되었습니다.");
    }

    @Operation(summary = "회원 탈퇴")
    @PatchMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletResponse response
    ) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }
        Long userId = userDetails.getId();
        userService.withdraw(userId);
        tokenService.expireCookie(response, "accessToken");
        tokenService.expireCookie(response, "refreshToken");

        return ResponseEntity.ok("회원 탈퇴 요청이 완료되었습니다. 로그아웃 처리되었으며, 한 달 뒤 모든 정보가 삭제됩니다.");
    }

    @Operation(summary="회원 탈퇴 철회")
    @PatchMapping("/restore")
    public ResponseDto<Void> restore(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.restoreUser(email);
        return ResponseDto.of(null,"계정이 복구되었습니다. 다시 로그인해주세요.");
    }

}
