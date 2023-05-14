package org.dinosaur.foodbowl.global.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(FoodbowlException.class)
    public ResponseEntity<FoodbowlErrorResponse> handleException(FoodbowlException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new FoodbowlErrorResponse(e.getMessage(), e.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FoodbowlErrorResponse> handleException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append("[");
            stringBuilder.append(fieldError.getField());
            stringBuilder.append("] 필드는 ");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(" 입력된 값 : [");
            stringBuilder.append(fieldError.getRejectedValue());
            stringBuilder.append("]");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new FoodbowlErrorResponse(stringBuilder.toString(), -1000));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<FoodbowlErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(new FoodbowlErrorResponse(e.getMessage(), -5000));
    }
}
