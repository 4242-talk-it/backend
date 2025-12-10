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
/*    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    BAD_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 요청"),
    BAD_REQUEST_INVALID_COMMENT(HttpStatus.BAD_REQUEST, "댓글 내용은 비어 있을 수 없습니다."),
    BAD_REQUEST_CHECK_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 중복 검사를 해야합니다."),
    BAD_REQUEST_UNUSABLE_NICKNAME(HttpStatus.BAD_REQUEST, "사용할 수 없는 닉네임입니다."),
    BAD_REQUEST_DISCONNECT_THIRD_PARTY(HttpStatus.BAD_REQUEST, "서드 파티 연결에 실패했습니다."),
    BAD_REQUEST_INVALID_EVENTCODE(HttpStatus.BAD_REQUEST, "잘못된 이벤트 코드입니다."),*/

    // UNAUTHORIZED(401)
/*    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 실패"),
    UNAUTHORIZED_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰"),
    UNAUTHORIZED_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "JWT 토큰이 없습니다."),
    UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT(HttpStatus.UNAUTHORIZED, "인증 정보가 존재하지 않습니다."),
    UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN(HttpStatus.UNAUTHORIZED, "Oauth2 프로바이더의 토큰을 발급 받을 수 없습니다."),*/

    // Forbidden(403)
/*    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    FORBIDDEN_UPDATE_COMMENT(HttpStatus.FORBIDDEN, "댓글을 수정할 권한이 없습니다."),
    FORBIDDEN_DELETE_COMMENT(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다."),*/

    // NOT_FOUND(404)
    //NOT_FOUND(HttpStatus.NOT_FOUND, "데이터가 존재하지 않음"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // Internal Server Error(500)
    //SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    ;

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    public Exception of() {
        return new Exception(this);
    }

    public Exception of(String message) {
        return new Exception(this, message);
    }
}
