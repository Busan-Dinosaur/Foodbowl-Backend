package org.dinosaur.foodbowl.global.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dinosaur.foodbowl.global.exception.response.ErrorResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.dinosaur.foodbowl.global.exception.type.ServerExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ExceptionResponse.from(ServerExceptionType.SERVER_ERROR));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> handleServerException(ServerException e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ExceptionResponse.from(e.getExceptionType()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponse> errorResponses = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        log.warn("[" + e.getClass() + "] " + errorResponses);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-100", errorResponses.toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationException(ConstraintViolationException e) {
        List<ErrorResponse> errorResponses = e.getConstraintViolations()
                .stream()
                .map(error -> new ErrorResponse(error.getPropertyPath().toString(), error.getMessage()))
                .toList();
        log.warn("[" + e.getClass() + "] " + errorResponses);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-101", errorResponses.toString()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-102", "이미지의 크기는 최대 " + e.getMaxUploadSize() + "MB 까지 가능합니다."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getName(),
                e.getRequiredType().getSimpleName() + " 타입으로 변환할 수 없는 요청입니다."
        );
        log.warn("[" + e.getClass() + "] " + errorResponse);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-103", errorResponse.toString()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getParameterName(),
                "파라미터가 필요합니다."
        );
        log.warn("[" + e.getClass() + "] " + errorResponse);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-104", errorResponse.toString()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handleBindException(BindException e) {
        List<ErrorResponse> errorResponses = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        log.warn("[" + e.getClass() + "] " + errorResponses);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-105", errorResponses.toString()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getRequestPartName(),
                "파트가 필요합니다."
        );
        log.warn("[" + e.getClass() + "] " + errorResponse);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-106", errorResponse.toString()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(ExceptionResponse.from(e.getExceptionType()));
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidArgumentException(InvalidArgumentException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.badRequest()
                .body(ExceptionResponse.from(e.getExceptionType()));
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ExceptionResponse> handleFileException(FileException e) {
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
