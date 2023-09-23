package org.dinosaur.foodbowl.domain.photo.exception;

import lombok.Getter;
import org.dinosaur.foodbowl.global.exception.type.ExceptionType;

@Getter
public enum FileExceptionType implements ExceptionType {

    TRANSFER("FILE-100", "파일 업로드를 실패했습니다."),
    FORMAT("FILE-101", "파일에 확장자가 존재하지 않습니다."),
    EXTENSION("FILE-102", "이미지 파일만 업로드 가능합니다."),
    BASE_NAME("FILE-103", "파일 이름은 공백이 될 수 없습니다"),
    READ("FILE-104", "파일을 정상적으로 읽을 수 없습니다."),
    WRITE("FILE-105", "파일을 정상적으로 생성할 수 없습니다."),
    NAME("FILE-106", "파일의 URL 정보를 찾을 수 없습니다."),
    EMPTY("FILE-107", "파일이 존재하지 않습니다.");

    private final String errorCode;
    private final String message;

    FileExceptionType(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
