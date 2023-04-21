package org.dinosaur.foodbowl.global.exception;

public class FoodbowlErrorResponse {

    private final String message;
    private final int code;

    public FoodbowlErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
