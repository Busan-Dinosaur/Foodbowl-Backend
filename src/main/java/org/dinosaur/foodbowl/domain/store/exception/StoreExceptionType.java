package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public enum StoreExceptionType implements ExceptionType {

    INVALID_ADDRESS("STORE-100", "가게 주소 형식이 잘못되었습니다."),
    DUPLICATE("STORE-101", "이미 존재하는 가게입니다."),
    NOT_FOUND("STORE-102", "일치하는 가게를 찾을 수 없습니다.");

    private final String errorCode;
    private final String message;

    StoreExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
