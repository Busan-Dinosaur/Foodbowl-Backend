package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum CoordinateExceptionType implements ExceptionType {

    INVALID_X("COORDINATE-100", "경도 값의 크기가 잘못되었습니다."),
    INVALID_Y("COORDINATE-100", "위도 값의 크기가 잘못되었습니다.");

    private final String errorCode;
    private final String message;

    CoordinateExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
