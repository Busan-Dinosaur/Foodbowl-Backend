package org.dinosaur.foodbowl.global.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public NotFoundException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
