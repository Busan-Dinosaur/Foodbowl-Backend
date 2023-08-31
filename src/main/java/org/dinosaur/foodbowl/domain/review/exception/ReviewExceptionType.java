package org.dinosaur.foodbowl.domain.review.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum ReviewExceptionType implements ExceptionType {

    NOT_FOUND("REVIEW-100", "일치하는 리뷰를 찾을 수 없습니다."),
    NOT_OWNER("REVIEW-101", "본인이 작성한 리뷰만 삭제할 수 있습니다.");

    private final String errorCode;
    private final String message;

    ReviewExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
