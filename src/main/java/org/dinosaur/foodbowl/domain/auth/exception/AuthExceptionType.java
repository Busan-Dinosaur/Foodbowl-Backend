package org.dinosaur.foodbowl.domain.auth.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum AuthExceptionType implements ExceptionType {

    EXPIRED_JWT("AUTH-100", "만료된 토큰입니다."),
    MALFORMED_JWT("AUTH-101", "손상된 토큰입니다."),
    UNSUPPORTED_JWT("AUTH-102", "지원하지 않은 토큰입니다."),
    SIGNATURE_JWT("AUTH-103", "시그니처 검증에 실패한 토큰입니다."),
    UNKNOWN_JWT("AUTH-104", "알 수 없는 이유로 유효하지 않은 토큰입니다."),
    NOT_AUTHENTICATION("AUTH-105", "인증에 실패하였습니다.");

    private final String errorCode;
    private final String message;

    AuthExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
