package org.dinosaur.foodbowl.domain.store.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public enum SchoolExceptionType implements ExceptionType {

    DUPLICATE_SCHOOL("SCHOOL-100", "이미 존재하는 학교입니다."),
    INVALID_SCHOOL_NAME("SCHOOL-101", "학교 이름 형식이 잘못되었습니다."),
    NOT_FOUND("SCHOOL-102", "존재하지 않는 학교입니다.");

    private final String errorCode;
    private final String message;

    SchoolExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
