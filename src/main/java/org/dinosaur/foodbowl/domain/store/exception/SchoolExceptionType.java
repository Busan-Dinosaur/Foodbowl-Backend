package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
@RequiredArgsConstructor
public enum SchoolExceptionType implements ExceptionType {

    DUPLICATE_ERROR("SCHOOL-100", "이미 존재하는 학교입니다.");

    private final String errorCode;
    private final String message;
}
