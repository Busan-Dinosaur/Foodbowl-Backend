package org.dinosaur.foodbowl.global.exception;

import lombok.Getter;

@Getter
public enum ServerExceptionType implements ExceptionType {

    INVALID_APPLE_KEY("SERVER-100", "유효하지 않은 애플 키입니다."),
    INVALID_ALGORITHM("SERVER-101", "존재하지 않는 알고리즘입니다.");

    private final String errorCode;
    private final String message;

    ServerExceptionType(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
