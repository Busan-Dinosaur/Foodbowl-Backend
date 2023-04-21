package org.dinosaur.foodbowl.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(FoodbowlException.class)
    public ResponseEntity<FoodbowlErrorResponse> foodbowlException(FoodbowlException foodbowlException) {
        return ResponseEntity.status(foodbowlException.getHttpStatus())
                .body(new FoodbowlErrorResponse(foodbowlException.getMessage(), foodbowlException.getCode()));
    }
}
