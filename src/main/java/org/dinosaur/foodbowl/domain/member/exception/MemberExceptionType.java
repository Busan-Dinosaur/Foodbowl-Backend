package org.dinosaur.foodbowl.domain.member.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum MemberExceptionType implements ExceptionType {

    NOT_FOUND("MEMBER-100", "등록되지 않은 회원입니다."),
    INVALID_NICKNAME("MEMBER-101", "한글, 영어로 구성된 1글자 이상, 10글자 이하의 닉네임이 아닙니다.");

    private final String errorCode;
    private final String message;

    MemberExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
