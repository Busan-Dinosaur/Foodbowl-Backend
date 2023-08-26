package org.dinosaur.foodbowl.domain.bookmark.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum BookmarkExceptionType implements ExceptionType {

    DUPLICATE_ERROR("BOOKMARK-100", "이미 북마크에 추가된 가게입니다.");

    private final String errorCode;
    private final String message;

    BookmarkExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
