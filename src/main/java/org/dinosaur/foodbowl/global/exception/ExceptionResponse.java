package org.dinosaur.foodbowl.global.exception;

public record ExceptionResponse(String errorCode, String message) {

    public static ExceptionResponse from(ExceptionType exceptionType) {
        return new ExceptionResponse(exceptionType.getErrorCode(), exceptionType.getMessage());
    }
}
