package org.dinosaur.foodbowl.domain.photo.vo;

import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_BASE_NAME_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_EXTENSION_ERROR;
import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.FILE_FORMAT_ERROR;

import java.util.Set;
import java.util.UUID;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.springframework.util.StringUtils;

public class PhotoName {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "png", "webp");
    private static final String UNDER_BAR = "_";
    private static final String DOT = ".";

    public static String of(String originalFilename) {
        validateFileName(originalFilename);
        return convertNameToPath(originalFilename);
    }

    private static String convertNameToPath(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (extension == null) {
            throw new FileException(FILE_FORMAT_ERROR);
        }
        String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        validateFileName(fileBaseName);
        validateExtension(extension);

        return fileBaseName + UNDER_BAR + System.currentTimeMillis() + DOT + extension;
    }

    private static void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FileException(FILE_BASE_NAME_ERROR);
        }
    }

    private static void validateExtension(String extension) {
        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileException(FILE_EXTENSION_ERROR);
        }
    }
}
