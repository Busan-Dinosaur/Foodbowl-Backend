package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@RequiredArgsConstructor
@Getter
public enum StoreExceptionType implements ExceptionType {

    INVALID_ADDRESS_ERROR("STORE-100", "가게 주소 형식이 잘못되었습니다.");

    private final String errorCode;
    private final String message;
}
