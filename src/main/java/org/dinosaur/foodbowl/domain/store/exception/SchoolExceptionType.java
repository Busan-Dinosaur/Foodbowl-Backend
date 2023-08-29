package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
@RequiredArgsConstructor
public enum SchoolExceptionType implements ExceptionType {

    DUPLICATE_ERROR("SCHOOL-100", "이미 존재하는 학교입니다."),
    INVALID_SCHOOL_NAME_ERROR("SCHOOL-101", "학교 이름 형식이 잘못되었습니다.");

    private final String errorCode;
    private final String message;
}
