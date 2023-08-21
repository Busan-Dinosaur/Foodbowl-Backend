package org.dinosaur.foodbowl.domain.follow.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@Getter
public enum FollowExceptionType implements ExceptionType {

    FOLLOW_ME("FOLLOW-100", "본인을 팔로우할 수 없습니다."),
    DUPLICATE_FOLLOW("FOLLOW-101", "이미 팔로우한 회원입니다."),
    UNFOLLOWED("FOLLOW-102", "팔로우 하지 않은 회원입니다.");

    private String errorCode;
    private String message;

    FollowExceptionType(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
