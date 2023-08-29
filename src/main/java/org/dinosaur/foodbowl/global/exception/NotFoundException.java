package org.dinosaur.foodbowl.global.exception;

import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

public class NotFoundException extends BaseException {

    public NotFoundException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public NotFoundException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
