package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
@RequiredArgsConstructor
public enum CoordinateExceptionType implements ExceptionType {

    INVALID_X_ERROR("COORDINATE-100", "경도 값의 크기가 잘못되었습니다."),
    INVALID_Y_ERROR("COORDINATE-100", "위도 값의 크기가 잘못되었습니다.");

    private final String errorCode;
    private final String message;
}
