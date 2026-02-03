package com.talkit.app.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {

    // Success(200)
    OK(HttpStatus.OK, "Success"),

    // BAD_REQUEST(400)
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "회원탈퇴 요청이 된 사용자입니다."),

    // UNAUTHORIZED(401)
    // 토큰 누락
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Access Token이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다."),
    // 토큰 유효성 실패
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,  "Access Token이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,  "Refresh Token이 유효하지 않습니다."),
    UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다."),


    // Forbidden(403)
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 콘텐츠에 접근할 권한이 없습니다."),

    // NOT_FOUND(404)
    //NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않음"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_COMMUNITY(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    NOT_FOUND_SITUATION(HttpStatus.NOT_FOUND, "상황을 찾을 수 없습니다."),
    NOT_FOUND_AI_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),

    // Internal Server Error(500)
    //SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    ;

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    public BusinessLogicException of() {
        return new BusinessLogicException(this);
    }

    public BusinessLogicException of(String message) {
        return new BusinessLogicException(this, message);
    }
}
