package org.dinosaur.foodbowl.domain.auth.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum AuthExceptionType implements ExceptionType {

    EXPIRED_JWT("AUTH-100", "만료된 토큰입니다."),
    UNSUPPORTED_JWT("AUTH-101", "지원하지 않는 토큰입니다."),
    MALFORMED_JWT("AUTH-102", "손상된 토큰입니다."),
    SIGNATURE_JWT("AUTH-103", "시그니처 검증에 실패한 토큰입니다."),
    UNKNOWN_JWT("AUTH-104", "알 수 없는 이유로 유효하지 않은 토큰입니다."),
    INVALID_HEADER_JWT("AUTH-105", "헤더가 유효하지 않은 토큰입니다."),
    INVALID_PAYLOAD_JWT("AUTH-106", "페이로드가 유효하지 않은 토큰입니다."),
    INVALID_APPLE_KEY("AUTH-107", "유효하지 않은 애플 키입니다."),
    NOT_AUTHENTICATION("AUTH-108", "인증에 실패하였습니다."),
    FORBIDDEN("AUTH-109", "권한이 없는 회원입니다."),
    INVALID_BASE64_DECODE("AUTH-110", "Base64로 디코딩할 수 없는 값입니다.");

    private final String errorCode;
    private final String message;

    AuthExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
