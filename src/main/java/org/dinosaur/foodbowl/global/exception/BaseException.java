package org.dinosaur.foodbowl.global.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public class BaseException extends RuntimeException {

    private final ExceptionType exceptionType;

    public BaseException(ExceptionType exceptionType) {
        super((exceptionType.getMessage()));
        this.exceptionType = exceptionType;
    }

    public BaseException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType.getMessage(), throwable);
        this.exceptionType = exceptionType;
    }
}
