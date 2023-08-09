package org.dinosaur.foodbowl.global.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public BadRequestException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
