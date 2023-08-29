package org.dinosaur.foodbowl.global.exception;

import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

public class AuthenticationException extends BaseException {

    public AuthenticationException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public AuthenticationException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
