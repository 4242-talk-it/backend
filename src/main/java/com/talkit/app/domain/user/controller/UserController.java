package com.talkit.app.domain.user.controller;

import com.talkit.app.domain.user.dto.UserLoginRequest;
import com.talkit.app.domain.user.dto.UserLoginResponse;
import com.talkit.app.domain.user.dto.UserSignupRequest;
import com.talkit.app.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        boolean result = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        if(!result) { //false인 경우(이메일, 비밀번호 불일치)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserLoginResponse("fail"));
        }

        return ResponseEntity.ok(
                new UserLoginResponse("success")
        );
    }
}
