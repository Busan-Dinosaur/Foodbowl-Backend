package org.dinosaur.foodbowl.global.exception;

public class FileException extends BaseException {

    public FileException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public FileException(ExceptionType exceptionType, Exception e) {
        super(exceptionType, e);
    }
}
