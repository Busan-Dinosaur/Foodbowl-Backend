package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@RequiredArgsConstructor
@Getter
public enum StoreExceptionType implements ExceptionType {

    INVALID_ADDRESS_ERROR("STORE-100", "가게 주소 형식이 잘못되었습니다."),
    DUPLICATE_ERROR("STORE-101", "이미 존재하는 가게입니다."),
    NOT_FOUND_ERROR("STORE-102", "일치하는 가게를 찾을 수 없습니다.");

    private final String errorCode;
    private final String message;
}
