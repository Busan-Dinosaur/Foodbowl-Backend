package org.dinosaur.foodbowl.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("SERVER-100", "알 수 없는 서버 에러가 발생했습니다."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("SERVER-101", e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(ExceptionResponse.from(e.getExceptionType()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.from(e.getExceptionType()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.from(e.getExceptionType()));
    }
}
