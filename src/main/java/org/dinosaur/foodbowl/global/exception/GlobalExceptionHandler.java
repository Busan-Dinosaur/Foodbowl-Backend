package org.dinosaur.foodbowl.global.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String JOINER_DELIMITER = ", ";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("SERVER-100", "알 수 없는 서버 에러가 발생했습니다."));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String exceptionMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(JOINER_DELIMITER));
        log.error("[" + ex.getClass() + "] " + exceptionMessage);
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-100", exceptionMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationException(ConstraintViolationException e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        Iterator<ConstraintViolation<?>> iterator =
                e.getConstraintViolations().iterator();

        StringJoiner stringJoiner = new StringJoiner(JOINER_DELIMITER);
        while (iterator.hasNext()) {
            ConstraintViolation<?> constraintViolation = iterator.next();
            stringJoiner.add(constraintViolation.getMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("CLIENT-101", stringJoiner.toString()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("CLIENT-102", "이미지의 크기는 최대 " + e.getMaxUploadSize() + "MB 까지 가능합니다."));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionResponse> handleServerException(ServerException e) {
        log.error("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.internalServerError()
                .body(ExceptionResponse.from(e.getExceptionType()));
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

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ExceptionResponse> handleFileException(FileException e) {
        log.warn("[" + e.getClass() + "] " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(e.getExceptionType()));
    }
}
