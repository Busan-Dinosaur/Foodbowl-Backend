package org.dinosaur.foodbowl.exception;

import org.springframework.http.HttpStatus;

public class FoodbowlException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;
    private final int code;

    public FoodbowlException(ErrorStatus errorStatus) {
        this.httpStatus = errorStatus.getHttpStatus();
        this.message = errorStatus.getMessage();
        this.code = errorStatus.getCode();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
