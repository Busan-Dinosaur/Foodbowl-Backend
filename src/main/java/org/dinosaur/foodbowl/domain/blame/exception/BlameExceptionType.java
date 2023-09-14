package org.dinosaur.foodbowl.domain.blame.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public enum BlameExceptionType implements ExceptionType {

    BLAME_ME("BLAME-100", "스스로에 대해 신고할 수 없습니다."),
    NOT_EXIST_TARGET("BLAME-101", "존재하지 않는 신고 대상입니다."),
    DUPLICATE_BLAME("BLAME-102", "신고 대상에 대한 신고 이력이 존재합니다."),
    DESCRIPTION_EMPTY("BLAME-103", "신고 내용이 공백이거나 존재하지 않습니다."),
    INVALID_DESCRIPTION_LENGTH("BLAME-104", "신고 내용의 최대 길이를 초과하였습니다.");

    private final String errorCode;
    private final String message;

    BlameExceptionType(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
