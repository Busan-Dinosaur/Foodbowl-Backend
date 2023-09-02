package org.dinosaur.foodbowl.global.exception;

import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

public class InvalidArgumentException extends BaseException {

    public InvalidArgumentException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public InvalidArgumentException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
