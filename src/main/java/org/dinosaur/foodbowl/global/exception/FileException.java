package org.dinosaur.foodbowl.global.exception;

import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

public class FileException extends BaseException {

    public FileException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public FileException(ExceptionType exceptionType, Exception e) {
        super(exceptionType, e);
    }
}
