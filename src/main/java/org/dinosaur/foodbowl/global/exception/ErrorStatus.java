package org.dinosaur.foodbowl.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorStatus {

    //JWT code: 1xxx
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.", 1000),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "손상된 토큰입니다.", 1001),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다.", 1002),
    JWT_WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "시그니처 검증에 실패한 토큰입니다.", 1003),
    JWT_UNKNOWN(HttpStatus.UNAUTHORIZED, "알 수 없는 이유로 유효하지 않은 토큰입니다.", 1004),
    JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인 토큰이 존재하지 않습니다.", 1005);

    private final HttpStatus httpStatus;
    private final String message;
    private final int code;

    ErrorStatus(HttpStatus httpStatus, String message, int code) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.code = code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
