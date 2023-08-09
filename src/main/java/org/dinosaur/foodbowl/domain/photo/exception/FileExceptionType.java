package org.dinosaur.foodbowl.domain.photo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ExceptionType;

@RequiredArgsConstructor
@Getter
public enum FileExceptionType implements ExceptionType {

    FILE_TRANSFER_ERROR("FILE-100", "파일 업로드를 실패했습니다."),
    FILE_FORMAT_ERROR("FILE-101", "파일에 확장자가 존재하지 않습니다."),
    FILE_EXTENSION_ERROR("FILE-102", "이미지 파일만 업로드 가능합니다."),
    FILE_BASE_NAME_ERROR("FILE-103", "파일 이름은 공백이 될 수 없습니다");

    private final String errorCode;
    private final String message;
}