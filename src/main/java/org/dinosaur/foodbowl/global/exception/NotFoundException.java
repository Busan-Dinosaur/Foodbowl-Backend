package org.dinosaur.foodbowl.global.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
