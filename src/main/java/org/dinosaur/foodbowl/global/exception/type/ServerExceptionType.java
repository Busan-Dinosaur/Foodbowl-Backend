package org.dinosaur.foodbowl.global.exception.type;

import lombok.Getter;

@Getter
public enum ServerExceptionType implements ExceptionType {

    SERVER_ERROR("SERVER-100", "알 수 없는 서버 에러가 발생하였습니다."),
    INVALID_APPLE_KEY("SERVER-101", "유효하지 않은 애플 키입니다."),
    INVALID_ALGORITHM("SERVER-102", "존재하지 않는 알고리즘입니다."),
    INVALID_COORDINATE_CODE("SERVER-103", "유효하지 않은 좌표 코드입니다."),
    INVALID_COORDINATE_TRANSFORM("SERVER-104", "좌표 거리 계산 중 에러가 발생하였습니다.");

    private final String errorCode;
    private final String message;

    ServerExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
