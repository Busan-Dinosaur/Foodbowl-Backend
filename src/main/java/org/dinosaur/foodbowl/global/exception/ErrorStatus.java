package org.dinosaur.foodbowl.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorStatus {

    //JWT code: 1xxx
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.", 1000),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "손상된 토큰입니다.", 1001),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다.", 1002),
    JWT_WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "시그니처 검증에 실패한 토큰입니다.", 1003),
    JWT_UNKNOWN(HttpStatus.UNAUTHORIZED, "알 수 없는 이유로 유효하지 않은 토큰입니다.", 1004),
    JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "로그인 토큰이 존재하지 않습니다.", 1005),

    //Store code: 2xxx
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 가게를 찾을 수 없습니다.", 2000),
    STORE_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 등록된 가게입니다.", 2001),

    //Apple code: 3xxx
    APPLE_INVALID_TOKEN(HttpStatus.BAD_REQUEST, "올바르지 않은 애플 OAuth 토큰입니다.", 3000),
    APPLE_INVALID_HEADER(HttpStatus.BAD_REQUEST, "올바르지 않은 애플 OAuth 토큰 헤더 정보입니다.", 3001),
    APPLE_INVALID_CLAIMS(HttpStatus.INTERNAL_SERVER_ERROR, "올바르지 않은 애플 OAuth 토큰 조각 정보입니다.", 3002),
    APPLE_INVALID_PUBLIC_KEY(HttpStatus.INTERNAL_SERVER_ERROR, "애플 OAuth 퍼블릭 키 생성 중 문제가 발생하였습니다.", 3003),
    APPLE_NOT_REGISTER(HttpStatus.UNAUTHORIZED, "애플 회원가입이 되지 않은 회원입니다.", 3004);

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
