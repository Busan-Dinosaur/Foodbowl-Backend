package org.dinosaur.foodbowl.global.exception;

public class GeneralExceptionResponse {

    private final String message;

    public GeneralExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
