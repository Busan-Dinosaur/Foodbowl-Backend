package org.dinosaur.foodbowl.global.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
