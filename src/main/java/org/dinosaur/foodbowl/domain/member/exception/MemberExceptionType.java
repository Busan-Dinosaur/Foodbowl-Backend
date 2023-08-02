package org.dinosaur.foodbowl.domain.member.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum MemberExceptionType implements ExceptionType {

    NOT_FOUND("MEMBER-100", "일치하는 회원을 찾을 수 없습니다.");

    private final String errorCode;
    private final String message;

    MemberExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
