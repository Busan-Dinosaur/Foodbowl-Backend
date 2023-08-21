package org.dinosaur.foodbowl.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예외 응답")
public record ExceptionResponse(
        @Schema(description = "에러 코드", example = "SERVER-100")
        String errorCode,

        @Schema(description = "에러 메시지", example = "알 수 없는 서버 에러가 발생했습니다.")
        String message
) {

    public static ExceptionResponse from(ExceptionType exceptionType) {
        return new ExceptionResponse(exceptionType.getErrorCode(), exceptionType.getMessage());
    }
}
