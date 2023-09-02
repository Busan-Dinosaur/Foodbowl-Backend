package org.dinosaur.foodbowl.domain.bookmark.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public enum BookmarkExceptionType implements ExceptionType {

    DUPLICATE("BOOKMARK-100", "이미 북마크에 추가된 가게입니다."),
    NOT_FOUND("BOOKMARK-101", "해당 가게는 사용자의 북마크에 추가되어 있지 않습니다.");

    private final String errorCode;
    private final String message;

    BookmarkExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
