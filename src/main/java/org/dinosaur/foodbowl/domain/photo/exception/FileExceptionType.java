package org.dinosaur.foodbowl.domain.photo.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public enum FileExceptionType implements ExceptionType {

    FILE_TRANSFER_ERROR("FILE-100", "파일 업로드를 실패했습니다."),
    FILE_FORMAT_ERROR("FILE-101", "파일에 확장자가 존재하지 않습니다."),
    FILE_EXTENSION_ERROR("FILE-102", "이미지 파일만 업로드 가능합니다."),
    FILE_BASE_NAME_ERROR("FILE-103", "파일 이름은 공백이 될 수 없습니다"),
    FILE_READ_ERROR("FILE-104", "파일을 정상적으로 읽을 수 없습니다."),
    FILE_WRITE_ERROR("FILE-105", "파일을 정상적으로 생성할 수 없습니다.");

    private final String errorCode;
    private final String message;

    FileExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
