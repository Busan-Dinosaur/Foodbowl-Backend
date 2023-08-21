package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
@RequiredArgsConstructor
public enum CategoryExceptionType implements ExceptionType {

    NOT_FOUND("CATEGORY-100", "일치하는 카테고리를 찾을 수 없습니다.");

    private final String errorCode;
    private final String message;
}