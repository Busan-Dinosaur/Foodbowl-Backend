package org.dinosaur.foodbowl.global.exception;

public class ServerException extends BaseException {

    public ServerException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public ServerException(ExceptionType exceptionType, Throwable throwable) {
        super(exceptionType, throwable);
    }
}
