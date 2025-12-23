package com.talkit.app.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원정보 관련 API", description = "회원정보 조회, 수정")
@RequestMapping("/api/user")
@RestController
public class UserInfoController {

}
