package org.dinosaur.foodbowl.global.exception;

public record ExceptionResponse(String errorCode, String message) {

    public static ExceptionResponse from(BaseException e) {
        ExceptionType exceptionType = e.getExceptionType();
        return new ExceptionResponse(exceptionType.getErrorCode(), exceptionType.getMessage());
    }
}
