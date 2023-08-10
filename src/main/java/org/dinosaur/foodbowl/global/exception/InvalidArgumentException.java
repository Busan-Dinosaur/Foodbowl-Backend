package org.dinosaur.foodbowl.global.exception;

public class InvalidArgumentException extends BaseException {

    public InvalidArgumentException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public InvalidArgumentException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
