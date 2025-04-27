package com.newbit.newbitgateway.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JwtErrorCode {

    INVALID_TOKEN("10011", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("10012", "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("10013", "지원하지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    EMPTY_CLAIMS("10014", "JWT 클레임이 비어 있습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    JwtErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
