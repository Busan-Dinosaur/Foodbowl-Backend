package org.dinosaur.foodbowl.global.exception.type;

import lombok.Getter;

@Getter
public enum ServerExceptionType implements ExceptionType {

    SERVER_ERROR("SERVER-100", "알 수 없는 서버 에러가 발생했습니다."),
    INVALID_APPLE_KEY("SERVER-101", "유효하지 않은 애플 키입니다."),
    INVALID_ALGORITHM("SERVER-102", "존재하지 않는 알고리즘입니다.");

    private final String errorCode;
    private final String message;

    ServerExceptionType(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
