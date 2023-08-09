package org.dinosaur.foodbowl.global.exception;

public class AuthenticationException extends BaseException {

    public AuthenticationException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public AuthenticationException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
