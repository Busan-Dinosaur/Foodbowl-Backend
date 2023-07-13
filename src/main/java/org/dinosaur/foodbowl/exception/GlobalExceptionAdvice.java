package org.dinosaur.foodbowl.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Iterator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "예상치 못한 문제가 발생했습니다.";
    private static final String FIELD_TYPE_ERROR_MESSAGE = "의 타입이 잘못되었습니다.";

    @ExceptionHandler(FoodbowlException.class)
    public ResponseEntity<FoodbowlErrorResponse> handleFoodbowl(FoodbowlException ex) {
        logger.warn(ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new FoodbowlErrorResponse(ex.getMessage(), ex.getCode()));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append("[");
            stringBuilder.append(fieldError.getField());
            stringBuilder.append("] ");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(" 입력된 값 : [");
            stringBuilder.append(fieldError.getRejectedValue());
            stringBuilder.append("]");
        }
        logger.warn(stringBuilder.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new FoodbowlErrorResponse(stringBuilder.toString(), -1000));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<FoodbowlErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Iterator<ConstraintViolation<?>> iterator = ex.getConstraintViolations().iterator();

        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            ConstraintViolation<?> constraintViolation = iterator.next();
            stringBuilder.append(constraintViolation.getMessage());
        }
        logger.warn(stringBuilder.toString());
        return ResponseEntity.badRequest().body(new FoodbowlErrorResponse(stringBuilder.toString(), -1001));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<FoodbowlErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        String parameterName = ex.getParameter().getParameterName();
        logger.warn(ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new FoodbowlErrorResponse(parameterName + FIELD_TYPE_ERROR_MESSAGE, -1002));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FoodbowlErrorResponse> handleException(Exception ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(new FoodbowlErrorResponse(INTERNAL_SERVER_ERROR_MESSAGE, -9999));
    }
}
